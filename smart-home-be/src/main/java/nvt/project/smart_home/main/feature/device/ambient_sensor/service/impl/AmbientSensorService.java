package nvt.project.smart_home.main.feature.device.ambient_sensor.service.impl;

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
import nvt.project.smart_home.main.core.exception.DeviceIsAlreadyOffException;
import nvt.project.smart_home.main.core.exception.DeviceIsAlreadyOnException;
import nvt.project.smart_home.main.core.exception.SmartDeviceNotFoundException;
import nvt.project.smart_home.main.core.mapper.ImageMapper;
import nvt.project.smart_home.main.core.repository.SmartDeviceRepository;
import nvt.project.smart_home.main.core.service.impl.SmartDeviceService;
import nvt.project.smart_home.main.core.service.interf.IImageService;
import nvt.project.smart_home.main.feature.device.ambient_sensor.constants.AmbientSensorCommand;
import nvt.project.smart_home.main.feature.device.ambient_sensor.entity.AmbientSensorEntity;
import nvt.project.smart_home.main.feature.device.ambient_sensor.mapper.AmbientSensorMapper;
import nvt.project.smart_home.main.feature.device.ambient_sensor.mqtt_dto.request.AmbientSensorMqttRequest;
import nvt.project.smart_home.main.feature.device.ambient_sensor.repository.AmbientSensorRepository;
import nvt.project.smart_home.main.feature.device.ambient_sensor.service.interf.IAmbientSensorService;
import nvt.project.smart_home.main.feature.device.ambient_sensor.web_dto.request.AmbientSensorWebRequestDto;
import nvt.project.smart_home.main.feature.device.ambient_sensor.web_dto.response.AmbientSensorWebResponseDto;
import nvt.project.smart_home.main.feature.property.constant.PropertyStatus;
import nvt.project.smart_home.main.feature.property.entity.Property;
import nvt.project.smart_home.main.feature.property.exception.PropertyNotExistsException;
import nvt.project.smart_home.main.feature.property.repository.PropertyRepository;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.I_AM_ALIVE_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.RECEIVE_AMBIENT_SENSOR_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.SEND_AMBIENT_SENSOR_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.START_HEARTBEAT;

@RequiredArgsConstructor
@Service
public class AmbientSensorService implements IAmbientSensorService {

    private final AmbientSensorRepository ambientSensorRepository;
    private final AmbientSensorMapper ambientSensorMapper;
    private final PropertyRepository propertyRepository;
    private final IImageService imageService;
    private final IMqttClient mqttClient;
    private final ObjectMapper objectMapper;
    private final SmartDeviceRepository smartDeviceRepository;
    private final ImageMapper imageMapper;

    private final SmartDeviceService smartDeviceService;

    @SneakyThrows
    @Override
    public AmbientSensorWebResponseDto create(AmbientSensorWebRequestDto request) {
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(PropertyNotExistsException::new);
        if (property.getStatus() != PropertyStatus.APPROVED) {
            throw new BadRequestException("You can only add devices to APPROVED properties!");
        }

        Optional<SmartDeviceEntity> optional = smartDeviceRepository
                .findByPropertyIdAndName(property.getId(), request.getName());
        if (optional.isPresent()) {
            throw new BadRequestException("Device with this name already exists on this property!");
        }

        AmbientSensorEntity newEntity = ambientSensorMapper.requestDtoToEntity(request);

        newEntity.setDeviceType(DeviceType.AMBIENT_SENSOR);
        newEntity.setGroupType(DeviceGroupType.PKA);
        newEntity.setProperty(property);

        newEntity = ambientSensorRepository.save(newEntity);

        ImageRequestDto imageRequest = request.getImage();
        imageRequest.setName(request.getName() + newEntity.getId());
        imageService.saveDeviceImageToFileSystem(imageRequest);

        newEntity.setImageFormat(imageRequest.getFormat());
        newEntity = ambientSensorRepository.save(newEntity);

        HeartbeatDto heartbeatRequest = HeartbeatDto.builder()
                .deviceId(newEntity.getId())
                .failed(false)
                .build();
        mqttClient.publish(START_HEARTBEAT, new MqttMessage(objectMapper.writeValueAsBytes(heartbeatRequest)));
        mqttClient.subscribe(I_AM_ALIVE_TOPIC + newEntity.getId(), 2);

        ImageResponseDto imageResponseDto = imageMapper.requestToResponse(imageRequest);
        var response = ambientSensorMapper.entityToResponseDto(newEntity);
        response.setImage(imageResponseDto);
        return response;
    }

    @SneakyThrows
    @Override
    public AmbientSensorWebResponseDto getById(Long id) {
        AmbientSensorEntity entity = ambientSensorRepository.findById(id)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Ambient sensor not found!"));

        ImageResponseDto image = smartDeviceService.getImage(entity);
        var response = ambientSensorMapper.entityToResponseDto(entity);
        response.setImage(image);
        return response;
    }

    @SneakyThrows
    @Override
    public AmbientSensorWebResponseDto setActivity(Long id, boolean active) {
        AmbientSensorEntity entity = ambientSensorRepository.findById(id)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Ambient sensor not found!"));

        if(entity.isDeviceActive() == active) {
            if(entity.isDeviceActive()) throw new DeviceIsAlreadyOnException();
            else throw new DeviceIsAlreadyOffException();
        }
        entity.setDeviceActive(active);
        entity = ambientSensorRepository.save(entity);

        AmbientSensorMqttRequest mqttRequest = AmbientSensorMqttRequest.builder()
                .id(entity.getId())
                .build();

        if (entity.isDeviceActive()) {
            mqttRequest.setCommand(AmbientSensorCommand.ON);
            mqttClient.subscribe(RECEIVE_AMBIENT_SENSOR_TOPIC + entity.getId(), 2);
        } else {
            mqttRequest.setCommand(AmbientSensorCommand.OFF);
        }
        mqttClient.publish(SEND_AMBIENT_SENSOR_TOPIC, new MqttMessage(objectMapper.writeValueAsBytes(mqttRequest)));

        return ambientSensorMapper.entityToResponseDto(entity);
    }
}
