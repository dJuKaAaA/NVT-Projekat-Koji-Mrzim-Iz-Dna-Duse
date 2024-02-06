package nvt.project.smart_home.main.feature.device.sprinkler_system.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
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
import nvt.project.smart_home.main.core.mapper.ScheduledWorkMapper;
import nvt.project.smart_home.main.core.repository.SmartDeviceRepository;
import nvt.project.smart_home.main.core.service.interf.IImageService;
import nvt.project.smart_home.main.feature.device.sprinkler_system.entity.SprinklerSystemEntity;
import nvt.project.smart_home.main.feature.device.sprinkler_system.entity.SprinklerSystemScheduleEntity;
import nvt.project.smart_home.main.feature.device.sprinkler_system.mapper.SprinklerSystemMapper;
import nvt.project.smart_home.main.feature.device.sprinkler_system.mqtt_dto.request.SprinklerSystemMqttRequestDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.mqtt_dto.request.SprinklerSystemScheduleMqttRequestDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.repository.SprinklerSystemRepository;
import nvt.project.smart_home.main.feature.device.sprinkler_system.repository.SprinklerSystemScheduleRepository;
import nvt.project.smart_home.main.feature.device.sprinkler_system.service.interf.ISprinklerSystemService;
import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.request.SetScheduleRequestDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.request.SetSystemOnOffRequestDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.request.SprinklerSystemRequestWebDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.request.SprinklerSystemScheduleWebRequestDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.response.SprinklerSystemResponseWebDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.response.SprinklingSystemScheduleWebResponseDto;
import nvt.project.smart_home.main.feature.property.constant.PropertyStatus;
import nvt.project.smart_home.main.feature.property.entity.Property;
import nvt.project.smart_home.main.feature.property.exception.PropertyNotExistsException;
import nvt.project.smart_home.main.feature.property.repository.PropertyRepository;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.I_AM_ALIVE_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.RECEIVE_SPRINKLER_SYSTEM_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.SEND_SPRINKLER_SYSTEM_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.START_HEARTBEAT;

@RequiredArgsConstructor
@Service
public class SprinklerSystemService implements ISprinklerSystemService {

    private final SprinklerSystemRepository sprinklerSystemRepository;
    private final SprinklerSystemScheduleRepository scheduledWorkRepository;
    private final SprinklerSystemMapper sprinklerSystemMapper;
    private final ImageMapper imageMapper;
    private final IImageService imageService;
    private final PropertyRepository propertyRepository;
    private final ScheduledWorkMapper scheduledWorkMapper;
    private final IMqttClient mqttClient;
    private final ObjectMapper objectMapper;
    private final SmartDeviceRepository smartDeviceRepository;

    @SneakyThrows
    @Transactional
    @Override
    public SprinklerSystemResponseWebDto create(SprinklerSystemRequestWebDto request) {
        Property property = propertyRepository.findById(request.getPropertyId()).orElseThrow(PropertyNotExistsException::new);
        if (property.getStatus() != PropertyStatus.APPROVED) throw new BadRequestException("You can only add devices to APPROVED properties!");

        Optional<SmartDeviceEntity> optional = smartDeviceRepository.findByPropertyIdAndName(property.getId(), request.getName());
        if (optional.isPresent()) throw new BadRequestException("Device with this name already exists on this property!");


        ImageRequestDto image = request.getImage();
        image.setName(request.getName() + property.getId());
        imageService.saveDeviceImageToFileSystem(image);

        SprinklerSystemEntity newEntity = sprinklerSystemMapper.requestDtoToEntity(request);
        newEntity.setDeviceActive(true);
        newEntity.setImageFormat(image.getFormat());
        newEntity.setDeviceType(DeviceType.SPRINKLER_SYSTEM);
        newEntity.setGroupType(DeviceGroupType.SPU);
        newEntity.setProperty(property);

        for (SprinklerSystemScheduleEntity schedule: newEntity.getSchedule()) scheduledWorkRepository.save(schedule);
        newEntity = sprinklerSystemRepository.save(newEntity);

        HeartbeatDto heartbeatRequest = HeartbeatDto.builder()
                .deviceId(newEntity.getId())
                .failed(false)
                .build();
        mqttClient.publish(START_HEARTBEAT, new MqttMessage(objectMapper.writeValueAsBytes(heartbeatRequest)));
        mqttClient.subscribe(I_AM_ALIVE_TOPIC + newEntity.getId(), 2);

        // TODO: Send mqtt message
        return sprinklerSystemMapper.entityToResponseDto(newEntity);
    }

    @Override
    public SprinklerSystemResponseWebDto getById(Long id) {
        SprinklerSystemEntity entity = sprinklerSystemRepository.findById(id)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Sprinkler system not found!"));

        SprinklerSystemResponseWebDto response = sprinklerSystemMapper.entityToResponseDto(entity);
        response.setImage(ImageResponseDto.builder()
                .name(entity.getName() + entity.getProperty().getId())
                .format(entity.getImageFormat())
                .build());

        List<SprinklingSystemScheduleWebResponseDto> scheduleWebResponseDtos = new ArrayList<>();
        for (SprinklerSystemScheduleEntity schedule: entity.getSchedule()) {
            scheduleWebResponseDtos.add(SprinklingSystemScheduleWebResponseDto.builder()
                    .startTime(schedule.getStartTime().toString())
                    .endTime(schedule.getEndTime().toString())
                    .days(schedule.getDays())
                    .build());
        }
        response.setSchedule(scheduleWebResponseDtos);

        return response;
    }

    @SneakyThrows
    @Override
    public SprinklerSystemResponseWebDto setSystemOn(Long id, SetSystemOnOffRequestDto systemOnOffRequestDto) {
        SprinklerSystemEntity entity = sprinklerSystemRepository.findById(id)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Sprinkler system not found!"));
        
        entity.setSystemOn(systemOnOffRequestDto.isSystemOn());
        entity = sprinklerSystemRepository.save(entity);

        SprinklerSystemMqttRequestDto request = SprinklerSystemMqttRequestDto.builder()
                .id(entity.getId())
                .systemOn(systemOnOffRequestDto.isSystemOn())
                .userEmail(systemOnOffRequestDto.getUserEmail())
                .schedule(null)
                .build();

        mqttClient.subscribe(RECEIVE_SPRINKLER_SYSTEM_TOPIC + entity.getId(), 2);
        mqttClient.publish(SEND_SPRINKLER_SYSTEM_TOPIC, new MqttMessage(objectMapper.writeValueAsBytes(request)));

        return sprinklerSystemMapper.entityToResponseDto(entity);
    }

    @SneakyThrows
    @Override
    public SprinklerSystemResponseWebDto setSchedule(SetScheduleRequestDto setScheduleRequestDto) {
        SprinklerSystemEntity entity = sprinklerSystemRepository.findById(setScheduleRequestDto.getId())
                .orElseThrow(() -> new SmartDeviceNotFoundException("Sprinkler system not found!"));

        List<SprinklerSystemScheduleEntity> schedules = new ArrayList<>();
        List<SprinklerSystemScheduleMqttRequestDto> scheduleMqtt = new ArrayList<>();

        for (SprinklerSystemScheduleWebRequestDto schedule: setScheduleRequestDto.getSchedule()) {
            SprinklerSystemScheduleEntity edited = SprinklerSystemScheduleEntity.builder()
                    .startTime(LocalTime.parse(schedule.getStartTime()))
                    .endTime(LocalTime.parse(schedule.getEndTime()))
                    .days(schedule.getDays())
                    .build();
            scheduledWorkRepository.save(edited);
            schedules.add(edited);

            scheduleMqtt.add(SprinklerSystemScheduleMqttRequestDto.builder()
                    .startTime(schedule.getStartTime())
                    .endTime(schedule.getEndTime())
                    .days(schedule.getDays())
                    .build());
        }

        entity.setSchedule(schedules);
        entity = sprinklerSystemRepository.save(entity);

        SprinklerSystemMqttRequestDto request = SprinklerSystemMqttRequestDto.builder()
                .id(entity.getId())
                .systemOn(entity.isSystemOn())
                .userEmail(null)
                .schedule(scheduleMqtt)
                .build();

        mqttClient.subscribe(RECEIVE_SPRINKLER_SYSTEM_TOPIC + entity.getId(), 2);
        mqttClient.publish(SEND_SPRINKLER_SYSTEM_TOPIC, new MqttMessage(objectMapper.writeValueAsBytes(request)));

        return sprinklerSystemMapper.entityToResponseDto(entity);
    }
}
