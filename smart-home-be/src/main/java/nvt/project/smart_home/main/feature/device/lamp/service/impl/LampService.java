package nvt.project.smart_home.main.feature.device.lamp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.core.constant.devices.DeviceGroupType;
import nvt.project.smart_home.main.core.constant.devices.DeviceType;
import nvt.project.smart_home.main.core.dto.HeartbeatDto;
import nvt.project.smart_home.main.core.dto.request.ImageRequestDto;
import nvt.project.smart_home.main.core.dto.response.ImageResponseDto;
import nvt.project.smart_home.main.core.entity.SmartDeviceEntity;
import nvt.project.smart_home.main.core.exception.BadRequestException;
import nvt.project.smart_home.main.core.exception.SmartDeviceNotFoundException;
import nvt.project.smart_home.main.core.mapper.ImageMapper;
import nvt.project.smart_home.main.core.repository.SmartDeviceRepository;
import nvt.project.smart_home.main.core.service.interf.IImageService;
import nvt.project.smart_home.main.feature.device.lamp.entity.LampEntity;
import nvt.project.smart_home.main.feature.device.lamp.mapper.LampMapper;
import nvt.project.smart_home.main.feature.device.lamp.mqtt_dto.request.LampMqttRequest;
import nvt.project.smart_home.main.feature.device.lamp.repository.LampRepository;
import nvt.project.smart_home.main.feature.device.lamp.service.interf.ILampService;
import nvt.project.smart_home.main.feature.device.lamp.web_dto.request.LampWebRequestDto;
import nvt.project.smart_home.main.feature.device.lamp.web_dto.response.LampWebResponseDto;
import nvt.project.smart_home.main.feature.property.constant.PropertyStatus;
import nvt.project.smart_home.main.feature.property.entity.Property;
import nvt.project.smart_home.main.feature.property.exception.PropertyNotExistsException;
import nvt.project.smart_home.main.feature.property.repository.PropertyRepository;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Service;

import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.I_AM_ALIVE_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.RECEIVE_LAMP_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.SEND_LAMP_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.START_HEARTBEAT;

import java.util.Optional;
import java.util.Optional;

import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.RECEIVE_LAMP_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.SEND_LAMP_TOPIC;

@RequiredArgsConstructor
@Service
public class LampService implements ILampService {

    private final LampRepository lampRepository;
    private final LampMapper lampMapper;
    private final PropertyRepository propertyRepository;
    private final ImageMapper imageMapper;
    private final IImageService imageService;
    private final IMqttClient mqttClient;
    private final SmartDeviceRepository smartDeviceRepository;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public LampWebResponseDto create(LampWebRequestDto request) {
        Property property = propertyRepository.findById(request.getPropertyId()).orElseThrow(PropertyNotExistsException::new);
        if (property.getStatus() != PropertyStatus.APPROVED) {
            throw new BadRequestException("You can only add devices to APPROVED properties!");
        }

        Optional<SmartDeviceEntity> optional = smartDeviceRepository.findByPropertyIdAndName(property.getId(), request.getName());
        if (optional.isPresent()) {
            throw new BadRequestException("Device with this name already exists on this property!");
        }

        LampEntity newEntity = lampMapper.requestDtoToEntity(request);

        ImageRequestDto image = request.getImage();
        image.setName(request.getName() + property.getId());
        imageService.saveDeviceImageToFileSystem(image);

        newEntity.setImageFormat(image.getFormat());
        newEntity.setDeviceType(DeviceType.LAMP);
        newEntity.setGroupType(DeviceGroupType.SPU);
        newEntity.setLightLevel(0.0);
        newEntity.setAutoModeOn(false);

        newEntity.setProperty(property);
        newEntity = lampRepository.save(newEntity);

        HeartbeatDto heartbeatRequest = HeartbeatDto.builder()
                .deviceId(newEntity.getId())
                .failed(false)
                .build();
        mqttClient.publish(START_HEARTBEAT, new MqttMessage(objectMapper.writeValueAsBytes(heartbeatRequest)));
        mqttClient.subscribe(I_AM_ALIVE_TOPIC + newEntity.getId(), 2);

        LampMqttRequest lampRequest = LampMqttRequest.builder()
                .id(newEntity.getId())
                .bulbOn(newEntity.isBulbOn())
                .autoModeOn(newEntity.isAutoModeOn())
                .build();
        mqttClient.subscribe(RECEIVE_LAMP_TOPIC + newEntity.getId(), 2);
        mqttClient.publish(SEND_LAMP_TOPIC, new MqttMessage(objectMapper.writeValueAsBytes(lampRequest)));

        return lampMapper.entityToResponseDto(newEntity);
    }

    @Override
    public LampWebResponseDto getById(Long id) {
        LampEntity entity = lampRepository.findById(id).orElseThrow(() -> new SmartDeviceNotFoundException("Lamp not found!"));
        LampWebResponseDto response = lampMapper.entityToResponseDto(entity);
        response.setImage(ImageResponseDto.builder()
                            .name(entity.getName() + entity.getProperty().getId())
                            .format(entity.getImageFormat())
                            .build());
        return response;
    }

    @SneakyThrows
    @Override
    public LampWebResponseDto setBulb(Long id, boolean bulbOn, String triggeredBy) {
        LampEntity entity = lampRepository.findById(id).orElseThrow(() -> new SmartDeviceNotFoundException("Lamp not found!"));
        if (!entity.isDeviceActive()) throw new BadRequestException("Can not change bulb status for inactive lamp!");
        if (entity.isAutoModeOn()) throw new BadRequestException("Can not change bulb status when lamp is in Auto Mode!");

//        entity.setAutoModeOn(false);
//        entity.setBulbOn(bulbOn);
//        entity = lampRepository.save(entity);

        LampMqttRequest request = LampMqttRequest.builder()
                .id(entity.getId())
                .bulbOn(bulbOn)
                .autoModeOn(false)
                .triggeredBy(triggeredBy)
                .build();

        mqttClient.subscribe(RECEIVE_LAMP_TOPIC + entity.getId(), 2);
        mqttClient.publish(SEND_LAMP_TOPIC, new MqttMessage(objectMapper.writeValueAsBytes(request)));
        return lampMapper.entityToResponseDto(entity);
    }

    @SneakyThrows
    @Override
    public LampWebResponseDto setAuto(Long id, boolean autoOn, String triggeredBy) {
        LampEntity entity = lampRepository.findById(id).orElseThrow(() -> new SmartDeviceNotFoundException("Lamp not found!"));
        if (!entity.isDeviceActive()) throw new BadRequestException("Can not change auto mode for inactive lamp!");

//        entity.setAutoModeOn(autoOn);
//        entity = lampRepository.save(entity);

        LampMqttRequest request = LampMqttRequest.builder()
                .id(entity.getId())
                .bulbOn(entity.isBulbOn())
                .autoModeOn(autoOn)
                .triggeredBy(triggeredBy)
                .build();
        mqttClient.subscribe(RECEIVE_LAMP_TOPIC + entity.getId(), 2);
        mqttClient.publish(SEND_LAMP_TOPIC, new MqttMessage(objectMapper.writeValueAsBytes(request)));

        return lampMapper.entityToResponseDto(entity);
    }
}