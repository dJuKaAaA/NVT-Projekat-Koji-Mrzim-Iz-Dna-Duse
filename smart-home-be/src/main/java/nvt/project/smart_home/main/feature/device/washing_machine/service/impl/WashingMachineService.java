package nvt.project.smart_home.main.feature.device.washing_machine.service.impl;

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
import nvt.project.smart_home.main.feature.device.washing_machine.constants.WashingMachineCommand;
import nvt.project.smart_home.main.feature.device.washing_machine.constants.WashingMachineCurrentWorkMode;
import nvt.project.smart_home.main.feature.device.washing_machine.constants.WashingTime;
import nvt.project.smart_home.main.feature.device.washing_machine.entity.WashingMachineAppointmentHistoryEntity;
import nvt.project.smart_home.main.feature.device.washing_machine.entity.WashingMachineEntity;
import nvt.project.smart_home.main.feature.device.washing_machine.entity.WashingMachineWorkAppointmentEntity;
import nvt.project.smart_home.main.feature.device.washing_machine.mapper.WashingMachineAppointmentMapper;
import nvt.project.smart_home.main.feature.device.washing_machine.mapper.WashingMachineHistoryMapper;
import nvt.project.smart_home.main.feature.device.washing_machine.mapper.WashingMachineMapper;
import nvt.project.smart_home.main.feature.device.washing_machine.mqtt_dto.command.PeriodicCommand;
import nvt.project.smart_home.main.feature.device.washing_machine.mqtt_dto.reqeust.WashingMachineMqttRequest;
import nvt.project.smart_home.main.feature.device.washing_machine.repository.WashingMachineRepository;
import nvt.project.smart_home.main.feature.device.washing_machine.repository.WashingMachineSchedulingRepository;
import nvt.project.smart_home.main.feature.device.washing_machine.service.interf.IWashingMachineHistoryService;
import nvt.project.smart_home.main.feature.device.washing_machine.service.interf.IWashingMachineService;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.request.WashingMachineCancelAppointmentWebRequestDto;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.request.WashingMachineSetWorkModeWebRequestDto;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.request.WashingMachineWebRequestDto;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.request.WashingMachineWorkAppointmentWebRequestDto;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.response.WashingMachineHistoryWebResponseDto;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.response.WashingMachineWebResponseDto;
import nvt.project.smart_home.main.feature.device.washing_machine.web_dto.response.WashingMachineWorkAppointmentWebResponseDto;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.I_AM_ALIVE_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.RECEIVE_WASHING_MACHINE_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.SEND_WASHING_MACHINE_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.START_HEARTBEAT;

@RequiredArgsConstructor
@Service
public class WashingMachineService implements IWashingMachineService {

    private final WashingMachineMapper washingMachineMapper;
    private final WashingMachineAppointmentMapper appointmentMapper;
    private final WashingMachineHistoryMapper historyMapper;

    private final WashingMachineSchedulingRepository washingMachineAppointmentRepository;
    private final PropertyRepository propertyRepository;
    private final WashingMachineRepository washingMachineRepository;
    private final SmartDeviceRepository smartDeviceRepository;

    private final SmartDeviceService smartDeviceService;
    private final IWashingMachineHistoryService historyService;
    private final IUserService userService;
    private final IImageService imageService;

    private final ImageMapper imageMapper;
    private final IMqttClient mqttClient;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Transactional
    @Override
    public WashingMachineWebResponseDto create(WashingMachineWebRequestDto request) {

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

        WashingMachineEntity newEntity = washingMachineMapper.dtoToEntity(request);
        newEntity.setDeviceType(DeviceType.WASHING_MACHINE);
        newEntity.setGroupType(DeviceGroupType.PKA);
        newEntity.setDeviceActive(false);
        newEntity.setWorkMode(WashingMachineCurrentWorkMode.OFF);
        newEntity.setProperty(property);
        newEntity = washingMachineRepository.save(newEntity);

        ImageRequestDto imageRequest = request.getImage();
        imageRequest.setName(request.getName() + property.getId());
        imageService.saveDeviceImageToFileSystem(imageRequest);
        newEntity.setImageFormat(imageRequest.getFormat());
        washingMachineRepository.save(newEntity);

        HeartbeatDto heartbeatRequest = HeartbeatDto.builder()
                .deviceId(newEntity.getId())
                .failed(false)
                .build();
        mqttClient.publish(START_HEARTBEAT, new MqttMessage(objectMapper.writeValueAsBytes(heartbeatRequest)));
        mqttClient.subscribe(I_AM_ALIVE_TOPIC + newEntity.getId(), 2);

        mqttClient.subscribe(RECEIVE_WASHING_MACHINE_TOPIC + newEntity.getId(), 2);
        var response = washingMachineMapper.entityToDto(newEntity);

        response.setImage(imageMapper.requestToResponse(imageRequest));
        return response;
    }


    @SneakyThrows
    @Transactional
    @Override
    public WashingMachineWorkAppointmentWebResponseDto schedule(long deviceId, WashingMachineWorkAppointmentWebRequestDto request) {
        var entity = washingMachineRepository
                .findById(deviceId)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Washing machine not found!"));

        WashingMachineWorkAppointmentEntity newAppointment = appointmentMapper.dtoToEntity(request);
        UserEntity user =  userService
                .findByEmail(request.getBookedByEmail())
                .orElseThrow(UserNotFoundException::new);
        newAppointment.setBookedByUser(user);

        LocalTime scheduleWorkStartTime = LocalTime.parse(request.getStartTime());
        var scheduledAppointments = entity.getWorkPlan();

        LocalTime scheduleWorkEndTime;
        if(request.getCommand() == WashingMachineCommand.STANDARD_WASH_PROGRAM
                || request.getCommand() == WashingMachineCommand.SCHEDULED_STANDARD_WASH_PROGRAM) {
            scheduleWorkEndTime =
                    scheduleWorkStartTime.plusSeconds(WashingTime.STANDARD_WASH_PROGRAM_IN_SECONDS);
        } else if (request.getCommand() == WashingMachineCommand.COLOR_WASH_PROGRAM
                || request.getCommand() == WashingMachineCommand.SCHEDULED_COLOR_WASH_PROGRAM) {
            scheduleWorkEndTime =
                    scheduleWorkStartTime.plusSeconds(WashingTime.COLOR_WASH_PROGRAM_IN_SECONDS);
        } else if (request.getCommand() == WashingMachineCommand.WASH_PROGRAM_FOR_DELICATES
                || request.getCommand() == WashingMachineCommand.SCHEDULED_WASH_PROGRAM_FOR_DELICATES) {
            scheduleWorkEndTime =
                    scheduleWorkStartTime.plusSeconds(WashingTime.WASH_PROGRAM_FOR_DELICATES_IN_SECONDS);
        } else {
            throw new BadRequestException("Not valid wash program!");
        }
        newAppointment.setEndTime(scheduleWorkEndTime);

        for (var appointment: scheduledAppointments) {

            if(DateTimeUtility.isTimeInInterval(scheduleWorkStartTime, appointment.getStartTime(), appointment.getEndTime())
                    || DateTimeUtility.isTimeInInterval(scheduleWorkEndTime, appointment.getStartTime(), appointment.getEndTime())) {
                throw new ScheduledPlanAlreadyExistForPeriodException();
            }
        }

        washingMachineAppointmentRepository.save(newAppointment);
        entity.getWorkPlan().add(newAppointment);
        entity = washingMachineRepository.save(entity);

        var history = WashingMachineAppointmentHistoryEntity.
                builder()
                .executor(request.getBookedByEmail())
                .action(String.valueOf(request.getCommand()))
                .timestamp(DateTimeUtility.convertToLocalDateTime(Instant.now()))
                .device(entity)
                .build();
        historyService.save(history);

        var mqttRequest = WashingMachineMqttRequest.builder()
                .id(entity.getId())
                .command(newAppointment.getCommand())
                .periodicCommand(new PeriodicCommand(
                        newAppointment.getId(),
                        String.valueOf(scheduleWorkStartTime)))
                .build();
        mqttClient.publish(SEND_WASHING_MACHINE_TOPIC, new MqttMessage(objectMapper.writeValueAsBytes(mqttRequest)));

        var response =  appointmentMapper.entityToDto(newAppointment);
        response.setEndTime(scheduleWorkEndTime.format(DateTimeFormatter.ofPattern("HH::mm:ss")));
        return response;
    }

    @SneakyThrows
    @Transactional
    @Override
    public void cancelAppointment(long deviceId, long appointmentId, WashingMachineCancelAppointmentWebRequestDto request) {
        var appointment = washingMachineAppointmentRepository.findById(appointmentId)
                .orElseThrow(SchedulingPlanNotFoundException::new);
        var wmEntity = washingMachineRepository.findById(deviceId)
                .orElseThrow(SmartDeviceNotFoundException::new);

        userService.findByEmail(request.getCanceledByEmail()).orElseThrow(UserNotFoundException::new);

        // WHY
        var workPlan = wmEntity.getWorkPlan();
        workPlan = workPlan.stream()
                .filter(el -> el.getId() != appointmentId)
                .collect(Collectors.toList());

        System.out.println(workPlan);

        wmEntity.setWorkPlan(workPlan);
        washingMachineAppointmentRepository.delete(appointment);
        washingMachineRepository.save(wmEntity);

        var history = WashingMachineAppointmentHistoryEntity.
                builder()
                .executor(request.getCanceledByEmail())
                .action("CANCEL_" + String.valueOf(appointment.getCommand()))
                .timestamp(DateTimeUtility.convertToLocalDateTime(Instant.now()))
                .device(wmEntity)
                .build();
        historyService.save(history);


        var mqttRequest = WashingMachineMqttRequest.builder()
                .id(wmEntity.getId())
                .command(WashingMachineCommand.CANCEL)
                .periodicCommand(new PeriodicCommand(
                        appointment.getId(),
                        String.valueOf(appointment.getStartTime())))
                .build();
        mqttClient.publish(SEND_WASHING_MACHINE_TOPIC, new MqttMessage(objectMapper.writeValueAsBytes(mqttRequest)));
    }

    @SneakyThrows
    @Transactional
    @Override
    public WashingMachineWebResponseDto setCurrentWorkMode(long deviceId, WashingMachineSetWorkModeWebRequestDto request) {

        var wmEntity = washingMachineRepository.findById(deviceId)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Washing machine not found!"));

        if(wmEntity.getWorkMode() != WashingMachineCurrentWorkMode.OFF)
            throw new BadRequestException("Washing machine already working, can not be turn on!");


        // STANDARD_WASH_PROGRAM, COLOR_WASH_PROGRAM, WASH_PROGRAM_FOR_DELICATES,
        var newWorkMode = request.getWorkMode();
        String command = String.valueOf(newWorkMode);
        wmEntity.setDeviceActive(true);
        WashingMachineAppointmentHistoryEntity history = WashingMachineAppointmentHistoryEntity.
                builder()
                .executor(request.getSetByUserEmail())
                .action("ON_" + command)
                .timestamp(DateTimeUtility.convertToLocalDateTime(Instant.now()))
                .device(wmEntity)
                .build();
        historyService.save(history);

        wmEntity.setWorkMode(newWorkMode);
        wmEntity = washingMachineRepository.save(wmEntity);


        var mqttRequest = WashingMachineMqttRequest.builder()
                .id(wmEntity.getId())
                .command(WashingMachineCommand.valueOf(command))
                .periodicCommand(null)
                .build();

        mqttClient.publish(
                SEND_WASHING_MACHINE_TOPIC,
                new MqttMessage(objectMapper.writeValueAsBytes(mqttRequest)));

        return washingMachineMapper.entityToDto(wmEntity);
    }

    // DONE
    @Override
    public List<WashingMachineHistoryWebResponseDto> getHistory(long deviceId, Pageable pageable) {
        List<WashingMachineAppointmentHistoryEntity> history = historyService.findByDeviceId(deviceId, pageable);
        return history
                .stream()
                .map(historyMapper::entityToDto)
                .toList();
    }

    // DONE
    @Override
    public WashingMachineWebResponseDto getById(Long id) {
        var entity = washingMachineRepository.findById(id)
                .orElseThrow(() -> new SmartDeviceNotFoundException("Washing machine not found!"));
        ImageResponseDto image = smartDeviceService.getImage(entity);
        var response = washingMachineMapper.entityToDto(entity);
        response.setImage(image);
        return response;
    }
}
