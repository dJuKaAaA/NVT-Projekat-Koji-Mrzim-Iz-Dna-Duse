package nvt.project.smart_home.main.feature.device.vehicle_gate.service.impl;

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
import nvt.project.smart_home.main.core.repository.SmartDeviceRepository;
import nvt.project.smart_home.main.core.service.interf.IImageService;
import nvt.project.smart_home.main.feature.device.vehicle_gate.entity.VehicleGateEntity;
import nvt.project.smart_home.main.feature.device.vehicle_gate.mapper.VehicleGateMapper;
import nvt.project.smart_home.main.feature.device.vehicle_gate.mqtt_dto.request.VehicleGateMqttRequest;
import nvt.project.smart_home.main.feature.device.vehicle_gate.repository.VehicleGateRepository;
import nvt.project.smart_home.main.feature.device.vehicle_gate.service.interf.IVehicleGateService;
import nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.request.VehicleGateWebRequestDto;
import nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.response.VehicleGateWebResponseDto;
import nvt.project.smart_home.main.feature.property.constant.PropertyStatus;
import nvt.project.smart_home.main.feature.property.entity.Property;
import nvt.project.smart_home.main.feature.property.exception.PropertyNotExistsException;
import nvt.project.smart_home.main.feature.property.repository.PropertyRepository;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.I_AM_ALIVE_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.RECEIVE_VEHICLE_GATE_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.SEND_VEHICLE_GATE_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.START_HEARTBEAT;

@RequiredArgsConstructor
@Service
public class VehicleGateService implements IVehicleGateService {

    private final VehicleGateRepository vehicleGateRepository;
    private final VehicleGateMapper vehicleGateMapper;
    private final IImageService imageService;
    private final PropertyRepository propertyRepository;
    private final IMqttClient mqttClient;
    private final ObjectMapper objectMapper;
    private final SmartDeviceRepository smartDeviceRepository;

    @SneakyThrows
    @Override
    public VehicleGateWebResponseDto create(VehicleGateWebRequestDto request) {
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

        ImageRequestDto image = request.getImage();
        image.setName(request.getName() + property.getId());
        imageService.saveDeviceImageToFileSystem(image);

        VehicleGateEntity newEntity = vehicleGateMapper.requestDtoToEntity(request);

        newEntity.setDeviceActive(true);
        newEntity.setImageFormat(image.getFormat());
        newEntity.setDeviceType(DeviceType.VEHICLE_GATE);
        newEntity.setGroupType(DeviceGroupType.SPU);

        newEntity.setProperty(property);
        newEntity = vehicleGateRepository.save(newEntity);

        HeartbeatDto heartbeatRequest = HeartbeatDto.builder()
                .deviceId(newEntity.getId())
                .failed(false)
                .build();
        mqttClient.publish(START_HEARTBEAT, new MqttMessage(objectMapper.writeValueAsBytes(heartbeatRequest)));
        mqttClient.subscribe(I_AM_ALIVE_TOPIC + newEntity.getId(), 2);

        // Send mqtt message
        VehicleGateMqttRequest gateRequest = VehicleGateMqttRequest.builder()
                .id(newEntity.getId())
                .isAlwaysOpen(newEntity.isAlwaysOpen())
                .isPrivateMode(newEntity.isPrivateMode())
                .allowedLicencePlates(newEntity.getAllowedLicencePlates())
                .build();
        mqttClient.subscribe(RECEIVE_VEHICLE_GATE_TOPIC + newEntity.getId(), 2);
        mqttClient.publish(SEND_VEHICLE_GATE_TOPIC, new MqttMessage(objectMapper.writeValueAsBytes(gateRequest)));

        return vehicleGateMapper.entityToResponseDto(newEntity);
    }

    @SneakyThrows
    @Override
    public VehicleGateWebResponseDto changeIsAlwaysOpen(Long id, boolean isOpen, String triggeredBy) {
        VehicleGateEntity entity = vehicleGateRepository.findById(id).orElseThrow(() -> new SmartDeviceNotFoundException("Vehicle gate not found!"));
        if (!entity.isDeviceActive()) throw new BadRequestException("Can not change mode for inactive vehicle gate!");

        VehicleGateMqttRequest request = VehicleGateMqttRequest.builder()
                .id(entity.getId())
                .isAlwaysOpen(isOpen)
                .isPrivateMode(entity.isPrivateMode())
                .allowedLicencePlates(null)
                .triggeredBy(triggeredBy)
                .build();
        mqttClient.subscribe(RECEIVE_VEHICLE_GATE_TOPIC + entity.getId(), 2);
        mqttClient.publish(SEND_VEHICLE_GATE_TOPIC, new MqttMessage(objectMapper.writeValueAsBytes(request)));

        return vehicleGateMapper.entityToResponseDto(entity);
    }

    @Override
    public VehicleGateWebResponseDto getById(Long id) {
        VehicleGateEntity entity = vehicleGateRepository.findById(id).orElseThrow(() -> new SmartDeviceNotFoundException("Vehicle gate not found!"));
        VehicleGateWebResponseDto response = vehicleGateMapper.entityToResponseDto(entity);
        response.setImage(ImageResponseDto.builder()
                .name(entity.getName() + entity.getProperty().getId())
                .format(entity.getImageFormat())
                .build());
        response.setOpen(entity.isOpen());
        response.setAlwaysOpen(entity.isAlwaysOpen());
        response.setPrivateMode(entity.isPrivateMode());
        response.setLastInCommand(entity.getLastInCommand());
        return response;
    }

    @SneakyThrows
    @Override
    public VehicleGateWebResponseDto setAllowedLicencePlate(Long id, List<String> licencePlate) {
        VehicleGateEntity entity = vehicleGateRepository.findById(id).orElseThrow(() -> new SmartDeviceNotFoundException("Vehicle gate not found!"));
        entity.setAllowedLicencePlates(licencePlate);
        entity = vehicleGateRepository.save(entity);

        VehicleGateMqttRequest request = VehicleGateMqttRequest.builder().id(entity.getId()).allowedLicencePlates(entity.getAllowedLicencePlates()).build();
        mqttClient.subscribe(RECEIVE_VEHICLE_GATE_TOPIC + entity.getId(), 2);
        mqttClient.publish(SEND_VEHICLE_GATE_TOPIC, new MqttMessage(objectMapper.writeValueAsBytes(request)));

        return vehicleGateMapper.entityToResponseDto(entity);
    }

    @SneakyThrows
    @Override
    public VehicleGateWebResponseDto setMode(Long id, boolean isPrivate, String triggeredBy) {
        VehicleGateEntity entity = vehicleGateRepository.findById(id).orElseThrow(() -> new SmartDeviceNotFoundException("Vehicle gate not found!"));
        if (!entity.isDeviceActive()) throw new BadRequestException("Can not change mode for inactive vehicle gate!");

        VehicleGateMqttRequest request = VehicleGateMqttRequest.builder()
                .id(entity.getId())
                .isPrivateMode(isPrivate)
                .isAlwaysOpen(false)
                .allowedLicencePlates(null)
                .triggeredBy(triggeredBy)
                .build();
        mqttClient.subscribe(RECEIVE_VEHICLE_GATE_TOPIC + entity.getId(), 2);
        mqttClient.publish(SEND_VEHICLE_GATE_TOPIC, new MqttMessage(objectMapper.writeValueAsBytes(request)));

        return vehicleGateMapper.entityToResponseDto(entity);
    }
}
