package nvt.project.smart_home.main.feature.device.air_conditioner.service.impl;

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
import nvt.project.smart_home.main.core.entity.UserEntity;
import nvt.project.smart_home.main.core.exception.*;
import nvt.project.smart_home.main.core.mapper.ImageMapper;
import nvt.project.smart_home.main.core.repository.SmartDeviceRepository;
import nvt.project.smart_home.main.core.service.impl.SmartDeviceService;
import nvt.project.smart_home.main.core.service.interf.IImageService;
import nvt.project.smart_home.main.core.service.interf.IUserService;
import nvt.project.smart_home.main.core.utils.DateTimeUtility;
import nvt.project.smart_home.main.feature.device.air_conditioner.constants.AirConditionerCommand;
import nvt.project.smart_home.main.feature.device.air_conditioner.constants.AirConditionerCurrentWorkMode;
import nvt.project.smart_home.main.feature.device.air_conditioner.entity.AirConditionerAppointmentHistoryEntity;
import nvt.project.smart_home.main.feature.device.air_conditioner.entity.AirConditionerEntity;
import nvt.project.smart_home.main.feature.device.air_conditioner.entity.AirConditionerWorkAppointmentEntity;
import nvt.project.smart_home.main.feature.device.air_conditioner.mapper.AirCHistoryMapper;
import nvt.project.smart_home.main.feature.device.air_conditioner.mapper.AirConditionerAppointmentMapper;
import nvt.project.smart_home.main.feature.device.air_conditioner.mapper.AirConditionerMapper;
import nvt.project.smart_home.main.feature.device.air_conditioner.mqtt_dto.command_type.NormalCommand;
import nvt.project.smart_home.main.feature.device.air_conditioner.mqtt_dto.command_type.PeriodicCommand;
import nvt.project.smart_home.main.feature.device.air_conditioner.mqtt_dto.request.AirConditionerMqttRequest;
import nvt.project.smart_home.main.feature.device.air_conditioner.repository.AirConditionerRepository;
import nvt.project.smart_home.main.feature.device.air_conditioner.repository.AirConditionerSchedulingRepository;
import nvt.project.smart_home.main.feature.device.air_conditioner.service.interf.IAirConditionerHistoryService;
import nvt.project.smart_home.main.feature.device.air_conditioner.service.interf.IAirConditionerService;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.request.AirCCancelAppointmentWebRequestDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.request.AirCSetWorkModeWebRequestDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.request.AirCWebRequestDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.request.AirCWorkAppointmentWebRequestDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.response.AirCHistoryResponseWebDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.response.AirCWebResponseDto;
import nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.response.AirCWorkAppointmentWebResponseDto;
import nvt.project.smart_home.main.feature.property.constant.PropertyStatus;
import nvt.project.smart_home.main.feature.property.entity.Property;
import nvt.project.smart_home.main.feature.property.exception.PropertyNotExistsException;
import nvt.project.smart_home.main.feature.property.repository.PropertyRepository;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.I_AM_ALIVE_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.RECEIVE_AIR_CONDITIONER_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.SEND_AIR_CONDITIONER_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.START_HEARTBEAT;

@RequiredArgsConstructor
@Service
public class AirConditionerService implements IAirConditionerService {

    private final AirConditionerMapper airConditionerMapper;
    private final AirConditionerAppointmentMapper appointmentMapper;
    private final AirCHistoryMapper historyMapper;

    private final AirConditionerSchedulingRepository airCWorkAppointmentRepository;
    private final PropertyRepository propertyRepository;
    private final AirConditionerRepository airConditionerRepository;
    private final SmartDeviceRepository smartDeviceRepository;

    private final SmartDeviceService smartDeviceService;
    private final IAirConditionerHistoryService historyService;
    private final IUserService userService;
    private final IImageService imageService;

    private final ImageMapper imageMapper;
    private final IMqttClient mqttClient;
    private final ObjectMapper objectMapper;


    @SneakyThrows
    @Transactional
    @Override
    public AirCWebResponseDto create(AirCWebRequestDto request) {
        if (request.getMinTemperature() >= request.getMaxTemperature()) {
            throw new BadRequestException("Minimum temperature must be less than max temperature!");
        }

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

        AirConditionerEntity newEntity = airConditionerMapper.dtoToEntity(request);
        newEntity.setDeviceType(DeviceType.AIR_CONDITIONER);
        newEntity.setGroupType(DeviceGroupType.PKA);
        newEntity.setDeviceActive(false);
        newEntity.setCurrentWorkTemperature(null);
        newEntity.setWorkMode(AirConditionerCurrentWorkMode.OFF);
        newEntity.setProperty(property);
        newEntity = airConditionerRepository.save(newEntity);

        ImageRequestDto imageRequest = request.getImage();
        imageRequest.setName(request.getName() + property.getId());
        imageService.saveDeviceImageToFileSystem(imageRequest);
        newEntity.setImageFormat(imageRequest.getFormat());
        airConditionerRepository.save(newEntity);

        HeartbeatDto heartbeatRequest = HeartbeatDto.builder()
                .deviceId(newEntity.getId())
                .failed(false)
                .build();
        mqttClient.publish(START_HEARTBEAT, new MqttMessage(objectMapper.writeValueAsBytes(heartbeatRequest)));
        mqttClient.subscribe(I_AM_ALIVE_TOPIC + newEntity.getId(), 2);

        mqttClient.subscribe(RECEIVE_AIR_CONDITIONER_TOPIC + newEntity.getId(), 2);
        var response = airConditionerMapper.entityToDto(newEntity);
        response.setImage(imageMapper.requestToResponse(imageRequest));
        return response;
    }



    @SneakyThrows
    @Transactional
    @Override
    public AirCWebResponseDto setCurrentWorkMode(long deviceId, AirCSetWorkModeWebRequestDto request) {

        AirConditionerEntity airConditionerEntity = airConditionerRepository.findById(deviceId)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Air conditioner not found!"));

        if(airConditionerEntity.getWorkMode().equals(request.getWorkMode()))
            throw new BadRequestException("This work mode is already set!");


        // HEATING, COOLING, TEMP_MAINTENANCE, OFF_HEATING, OFF_COOLING, OFF_TEMP_MAINTENANCE
        AirConditionerCurrentWorkMode newWorkMode = request.getWorkMode();
        String command;

        if (newWorkMode == AirConditionerCurrentWorkMode.OFF) {

            command = "OFF_%s".formatted(String.valueOf(airConditionerEntity.getWorkMode()));
            airConditionerEntity.setDeviceActive(false);
            airConditionerEntity.setCurrentWorkTemperature(null);
            airConditionerRepository.updateCurrentWorkTemperatureById(airConditionerEntity.getId(), null);
            AirConditionerAppointmentHistoryEntity history = AirConditionerAppointmentHistoryEntity.
                    builder()
                    .executor(request.getSetByUserEmail())
                    .action(command)
                    .temperature(null)
                    .timestamp(DateTimeUtility.convertToLocalDateTime(Instant.now()))
                    .device(airConditionerEntity)
                    .build();
            historyService.save(history);
        } else {

            if(airConditionerEntity.getMinTemperature() > request.getWantedTemperature() || airConditionerEntity.getMaxTemperature() < request.getWantedTemperature())
                throw new BadRequestException("Temperature must be in valid range!");

            command = String.valueOf(newWorkMode);
            airConditionerEntity.setDeviceActive(true);
            airConditionerRepository.updateCurrentWorkTemperatureById(
                    airConditionerEntity.getId(),
                    request.getWantedTemperature());
            AirConditionerAppointmentHistoryEntity history = AirConditionerAppointmentHistoryEntity.
                    builder()
                    .executor(request.getSetByUserEmail())
                    .action("ON_" + command)
                    .temperature(request.getWantedTemperature())
                    .timestamp(DateTimeUtility.convertToLocalDateTime(Instant.now()))
                    .device(airConditionerEntity)
                    .build();
            historyService.save(history);
        }

        airConditionerEntity.setWorkMode(newWorkMode);
        airConditionerRepository.save(airConditionerEntity);
        airConditionerRepository.updateWorkModeById(airConditionerEntity.getId(), newWorkMode);



        AirConditionerMqttRequest mqttRequest = AirConditionerMqttRequest.builder()
                .id(airConditionerEntity.getId())
                .minTemperature(airConditionerEntity.getMinTemperature())
                .maxTemperature(airConditionerEntity.getMaxTemperature())
                .command(AirConditionerCommand.valueOf(command))
                .normalCommand(new NormalCommand(request.getWantedTemperature()))
                .periodicCommand(null)
                .build();

        mqttClient.publish(
                SEND_AIR_CONDITIONER_TOPIC,
                new MqttMessage(objectMapper.writeValueAsBytes(mqttRequest)));

        return airConditionerMapper.entityToDto(airConditionerEntity);
    }


    // DONE
    @SneakyThrows
    @Transactional
    @Override
    public AirCWorkAppointmentWebResponseDto schedule(long deviceId, AirCWorkAppointmentWebRequestDto request) {
        AirConditionerEntity airConditionerEntity = airConditionerRepository
                .findById(deviceId)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Air conditioner not found!"));

        if(airConditionerEntity.getMinTemperature() > request.getWantedTemperature() || airConditionerEntity.getMaxTemperature() < request.getWantedTemperature())
            throw new BadRequestException("Temperature must be in valid range!");

        AirConditionerWorkAppointmentEntity newAppointment = appointmentMapper.dtoToEntity(request);

        UserEntity user =  userService
                .findByEmail(request.getBookedByEmail())
                .orElseThrow(UserNotFoundException::new);
        newAppointment.setBookedByUser(user);

        LocalTime scheduleWorkStartTime = LocalTime.parse(request.getStartTime());
        LocalTime scheduleWorkEndTime = LocalTime.parse(request.getEndTime());

        if(DateTimeUtility.isStartTimeAfterEndDate(scheduleWorkStartTime, scheduleWorkEndTime)) {
            throw new StartTimeIsAfterEndTimeException();
        }
        var scheduledAppointments = airConditionerEntity.getWorkPlan();

        for(var appointment: scheduledAppointments) {
        if(DateTimeUtility.isTimeInInterval(scheduleWorkStartTime, appointment.getStartTime(), appointment.getEndTime())
        || DateTimeUtility.isTimeInInterval(scheduleWorkEndTime, appointment.getStartTime(), appointment.getEndTime())) {
            throw new ScheduledPlanAlreadyExistForPeriodException();
            }
        }


        airConditionerEntity.getWorkPlan().add(newAppointment);
        airConditionerEntity = airConditionerRepository.save(airConditionerEntity);

        AirConditionerAppointmentHistoryEntity history = AirConditionerAppointmentHistoryEntity.
                builder()
                .executor(request.getBookedByEmail())
                .action("SCHEDULE_" + String.valueOf(request.getCommand()))
                .temperature(request.getWantedTemperature())
                .timestamp(DateTimeUtility.convertToLocalDateTime(Instant.now()))
                .device(airConditionerEntity)
                .build();
        historyService.save(history);

        AirConditionerMqttRequest mqttRequest = AirConditionerMqttRequest.builder()
                .id(airConditionerEntity.getId())
                .minTemperature(airConditionerEntity.getMinTemperature())
                .maxTemperature(airConditionerEntity.getMaxTemperature())
                .command(newAppointment.getCommand())
                .normalCommand(null)
                .periodicCommand(new PeriodicCommand(
                        newAppointment.getId(),
                        String.valueOf(scheduleWorkStartTime),
                        String.valueOf(scheduleWorkEndTime),
                        request.getWantedTemperature()))
                .build();
        mqttClient.publish(SEND_AIR_CONDITIONER_TOPIC, new MqttMessage(objectMapper.writeValueAsBytes(mqttRequest)));

        return appointmentMapper.entityToDto(newAppointment);
    }

    @SneakyThrows
    @Transactional
    @Override
    public void cancelAppointment(long deviceId, long appointmentId, AirCCancelAppointmentWebRequestDto request) {
        AirConditionerWorkAppointmentEntity appointment = airCWorkAppointmentRepository.findById(appointmentId)
                .orElseThrow(SchedulingPlanNotFoundException::new);
        AirConditionerEntity airConditionerEntity = airConditionerRepository.findById(deviceId)
                        .orElseThrow(SmartDeviceNotFoundException::new);

        userService.findByEmail(request.getCanceledByEmail()).orElseThrow(UserNotFoundException::new);

        // WHY
        var workPlan = airConditionerEntity.getWorkPlan();
        workPlan = workPlan.stream()
                .filter(el -> el.getId() != appointmentId)
                .collect(Collectors.toList());

        System.out.println(workPlan);

        airConditionerEntity.setWorkPlan(workPlan);
        airCWorkAppointmentRepository.delete(appointment);


        AirConditionerAppointmentHistoryEntity history = AirConditionerAppointmentHistoryEntity.
                builder()
                .executor(request.getCanceledByEmail())
                .action("CANCEL_" + String.valueOf(appointment.getCommand()))
                .temperature(null)
                .timestamp(DateTimeUtility.convertToLocalDateTime(Instant.now()))
                .device(airConditionerEntity)
                .build();
        historyService.save(history);


        String command = "OFF_" + String.valueOf(appointment.getCommand());
        AirConditionerMqttRequest mqttRequest = AirConditionerMqttRequest.builder()
                .id(airConditionerEntity.getId())
                .minTemperature(airConditionerEntity.getMinTemperature())
                .maxTemperature(airConditionerEntity.getMaxTemperature())
                .command(AirConditionerCommand.valueOf(command))
                .normalCommand(null)
                .periodicCommand(new PeriodicCommand(
                        appointment.getId(),
                        String.valueOf(appointment.getStartTime()),
                        String.valueOf(appointment.getEndTime()),
                        appointment.getWantedTemperature()))
                .build();
        mqttClient.publish(SEND_AIR_CONDITIONER_TOPIC, new MqttMessage(objectMapper.writeValueAsBytes(mqttRequest)));
    }

    @Override
    public List<AirCHistoryResponseWebDto> getHistory(long deviceId, Pageable pageable) {
        List<AirConditionerAppointmentHistoryEntity> history = historyService.findByDeviceId(deviceId, pageable);
        return history.stream().map(historyMapper::entityToDto)
                .toList();
    }

    // DONE
    @Override
    public AirCWebResponseDto getById(Long id) {
        AirConditionerEntity entity = airConditionerRepository.findById(id)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Air conditioner not found!"));
        ImageResponseDto image = smartDeviceService.getImage(entity);
        var response = airConditionerMapper.entityToDto(entity);
        response.setImage(image);
        return response;
    }

    private void update(AirConditionerEntity airConditionerEntity) {
        airConditionerRepository.save(airConditionerEntity);
    }
}
