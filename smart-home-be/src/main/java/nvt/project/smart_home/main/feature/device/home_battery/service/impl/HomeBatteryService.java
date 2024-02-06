package nvt.project.smart_home.main.feature.device.home_battery.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.core.constant.devices.DeviceGroupType;
import nvt.project.smart_home.main.core.constant.devices.DeviceType;
import nvt.project.smart_home.main.core.dto.HeartbeatDto;
import nvt.project.smart_home.main.core.dto.request.ImageRequestDto;
import nvt.project.smart_home.main.core.entity.SmartDeviceEntity;
import nvt.project.smart_home.main.core.exception.BadRequestException;
import nvt.project.smart_home.main.core.exception.SmartDeviceNotFoundException;
import nvt.project.smart_home.main.core.repository.SmartDeviceRepository;
import nvt.project.smart_home.main.core.service.interf.IImageService;
import nvt.project.smart_home.main.feature.device.home_battery.dto.request.HomeBatteryRequestDto;
import nvt.project.smart_home.main.feature.device.home_battery.dto.response.HomeBatteryResponseDto;
import nvt.project.smart_home.main.feature.device.home_battery.entity.HomeBatteryEntity;
import nvt.project.smart_home.main.feature.device.home_battery.mapper.HomeBatteryMapper;
import nvt.project.smart_home.main.feature.device.home_battery.repository.HomeBatteryRepository;
import nvt.project.smart_home.main.feature.device.home_battery.service.HomeBatterySimulatorService;
import nvt.project.smart_home.main.feature.device.home_battery.service.interf.IHomeBatteryService;
import nvt.project.smart_home.main.feature.property.constant.PropertyStatus;
import nvt.project.smart_home.main.feature.property.entity.Property;
import nvt.project.smart_home.main.feature.property.exception.PropertyNotExistsException;
import nvt.project.smart_home.main.feature.property.repository.PropertyRepository;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.I_AM_ALIVE_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.START_HEARTBEAT;

@RequiredArgsConstructor
@Service
public class HomeBatteryService implements IHomeBatteryService {

    private final HomeBatteryRepository homeBatteryRepository;
    private final HomeBatteryMapper homeBatteryMapper;
    private final IImageService imageService;
    private final PropertyRepository propertyRepository;
    private final SmartDeviceRepository smartDeviceRepository;
    private final HomeBatterySimulatorService homeBatterySimulatorService;
    private final ObjectMapper objectMapper;
    private final IMqttClient mqttClient;

    @SneakyThrows
    @Override
    public HomeBatteryResponseDto create(HomeBatteryRequestDto request) {
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

        HomeBatteryEntity newEntity = homeBatteryMapper.requestDtoToEntity(request);

        newEntity.setImageFormat(image.getFormat());
        newEntity.setDeviceType(DeviceType.HOME_BATTERY);
        newEntity.setGroupType(DeviceGroupType.VEU);

        newEntity.setProperty(property);
        newEntity.setDeviceActive(true);
        newEntity.setCurrent(newEntity.getCapacity());
        newEntity = homeBatteryRepository.save(newEntity);

        HeartbeatDto heartbeatRequest = HeartbeatDto.builder()
                .deviceId(newEntity.getId())
                .failed(false)
                .build();
        mqttClient.publish(START_HEARTBEAT, new MqttMessage(objectMapper.writeValueAsBytes(heartbeatRequest)));
        mqttClient.subscribe(I_AM_ALIVE_TOPIC + newEntity.getId(), 2);

        homeBatterySimulatorService.addBattery(property.getId(), newEntity);

        return homeBatteryMapper.entityToResponseDto(newEntity);
    }

    @Override
    public HomeBatteryResponseDto getById(Long id) {
        HomeBatteryEntity entity = homeBatteryRepository.findById(id)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Home battery not found!"));
        return homeBatteryMapper.entityToResponseDto(entity);
    }
}
