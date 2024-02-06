package rs.ac.uns.ftn.nwt.simulator_server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.nwt.simulator_server.configuration.mqtt.MessageCallback;
import rs.ac.uns.ftn.nwt.simulator_server.constants.washing_machine.WashingMachineCommand;
import rs.ac.uns.ftn.nwt.simulator_server.constants.washing_machine.WashingMachineCurrentWorkMode;
import rs.ac.uns.ftn.nwt.simulator_server.constants.washing_machine.WashingTime;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.washing_machine.WashingMachineRequest;
import rs.ac.uns.ftn.nwt.simulator_server.dto.response.WashingMachineResponse;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static rs.ac.uns.ftn.nwt.simulator_server.constants.topics.SendTopicConstants.SEND_WASHING_MACHINE_TOPIC;


@RequiredArgsConstructor
@Service
public class WashingMachineService {

    private final IMqttClient mqttClient;
    private final MessageCallback messageCallback;
    private final ObjectMapper mapper;
    private final RunnableManager runnableManager;
    int EXECUTION_TIME_IN_SECONDS = 3;
    int WASHING_TIME_IN_SECONDS = 5;

    // Active
    // <deviceId, runnableId>
    private Map<Long, Boolean> isWorkingPeriodicAppointment = new ConcurrentHashMap<>();

    // <appointmentId, runnable>
    private Map<Long, Long> allAppointments = new ConcurrentHashMap<>();
    private Map<Long, Long> oneTimeRunnable = new ConcurrentHashMap<>();

    @PostConstruct
    public void setCommand() {
        messageCallback.setStartWashingMachineOneTimeSim(this::startOneTimeSim);
        messageCallback.setScheduleWashingMachineSim(this::scheduleAppointment);
        messageCallback.setCancelWashingMachineSim(this::cancelAppointment);
    }


    @SneakyThrows
    public void startOneTimeSim(WashingMachineRequest request) {
        WashingMachineCurrentWorkMode workMode;
        LocalDateTime endTime;
        if(request.getCommand() == WashingMachineCommand.STANDARD_WASH_PROGRAM) {
            workMode = WashingMachineCurrentWorkMode.STANDARD_WASH_PROGRAM;
            endTime = LocalDateTime.now().plusSeconds(WashingTime.STANDARD_WASH_PROGRAM_IN_SECONDS);
        }
        else if(request.getCommand() == WashingMachineCommand.COLOR_WASH_PROGRAM) {
            workMode = WashingMachineCurrentWorkMode.COLOR_WASH_PROGRAM;
            endTime = LocalDateTime.now().plusSeconds(WashingTime.COLOR_WASH_PROGRAM_IN_SECONDS);
        }
        else {
            workMode = WashingMachineCurrentWorkMode.WASH_PROGRAM_FOR_DELICATES;
             endTime = LocalDateTime.now().plusSeconds(WashingTime.WASH_PROGRAM_FOR_DELICATES_IN_SECONDS);
        }
        Runnable runnable = () -> task(request.getId(), workMode, endTime);
        long runnableId = runnableManager.startRunnable(request.getId(), runnable, 0, EXECUTION_TIME_IN_SECONDS);

        oneTimeRunnable.put(request.getId(),runnableId);
        WashingMachineResponse response = WashingMachineResponse.builder()
                .id(request.getId())
                .appointmentId(-1000L)
                .workMode(workMode)
                .timestamp(new Date())
                .build();
        mqttClient.publish(SEND_WASHING_MACHINE_TOPIC + request.getId(), new MqttMessage(mapper.writeValueAsBytes(response)));

    }

    @SneakyThrows
    private void task(long deviceId, WashingMachineCurrentWorkMode workMode, LocalDateTime endTime) {

        System.out.println("Perem bato:" + deviceId + String.valueOf(workMode));
        if(LocalDateTime.now().isAfter(endTime)) {
            cancelWithNotify(deviceId);
        }
    }

    @SneakyThrows
    public void scheduleAppointment(WashingMachineRequest request) {
        WashingMachineCurrentWorkMode workMode;
        LocalTime startTime = LocalTime.parse(request.getPeriodicCommand().getStartTime());
        LocalTime endTime;
        if(request.getCommand() == WashingMachineCommand.SCHEDULED_STANDARD_WASH_PROGRAM) {
            workMode = WashingMachineCurrentWorkMode.SCHEDULED_STANDARD_WASH_PROGRAM;
            endTime = startTime.plusSeconds(WashingTime.STANDARD_WASH_PROGRAM_IN_SECONDS);
        }
        else if(request.getCommand() == WashingMachineCommand.SCHEDULED_COLOR_WASH_PROGRAM) {
            workMode = WashingMachineCurrentWorkMode.SCHEDULED_COLOR_WASH_PROGRAM;
            endTime = startTime.plusSeconds(WashingTime.COLOR_WASH_PROGRAM_IN_SECONDS);
        }
        else {
            workMode = WashingMachineCurrentWorkMode.SCHEDULED_WASH_PROGRAM_FOR_DELICATES;
            endTime = startTime.plusSeconds(WashingTime.WASH_PROGRAM_FOR_DELICATES_IN_SECONDS);
        }

        isWorkingPeriodicAppointment.put(request.getPeriodicCommand().getAppointmentId(), false);
        Runnable runnable = () -> scheduleTask(
                request.getId(),
                request.getPeriodicCommand().getAppointmentId(),
                startTime,
                endTime,
                workMode);
        long runnableId = runnableManager.startRunnable(request.getId(), runnable, 0, EXECUTION_TIME_IN_SECONDS);

        allAppointments.put(request.getId(),runnableId);
    }

    @SneakyThrows
    private void scheduleTask(long deviceId, long appointmentId, LocalTime startTime, LocalTime endTime,
                              WashingMachineCurrentWorkMode workMode) {
        LocalTime now = LocalTime.now();
        if(isBetween(now, startTime, endTime)) {
            System.out.println("(deviceId = " + deviceId + "ZAKAZONO PRANJE" + ")") ; // TODO remove
            isWorkingPeriodicAppointment.put(appointmentId, true);

            WashingMachineResponse response = WashingMachineResponse.builder()
                    .id(deviceId)
                    .appointmentId(appointmentId)
                    .workMode(workMode)
                    .timestamp(new Date())
                    .build();
            mqttClient.publish(SEND_WASHING_MACHINE_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(response)));
        } else {
            if(isWorkingPeriodicAppointment.get(appointmentId)) {
                isWorkingPeriodicAppointment.put(appointmentId, false);
                WashingMachineResponse response = WashingMachineResponse.builder()
                        .id(deviceId)
                        .appointmentId(appointmentId)
                        .workMode(WashingMachineCurrentWorkMode.OFF)
                        .timestamp(new Date())
                        .build();
                mqttClient.publish(SEND_WASHING_MACHINE_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(response)));
            }
        }
    }

    @SneakyThrows
    public void cancelAppointment(WashingMachineRequest request) {
        long deviceId = request.getId();
        long appointmentId = request.getPeriodicCommand().getAppointmentId();
        long runnableId = allAppointments.get(appointmentId);
        runnableManager.cancelRunnable(deviceId, runnableId);
        allAppointments.remove(appointmentId);
        isWorkingPeriodicAppointment.remove(appointmentId);
    }

    @SneakyThrows
    public void cancelWithNotify(long deviceId) {
        long runnableId = oneTimeRunnable.get(deviceId);
        runnableManager.cancelRunnable(deviceId, runnableId);

        WashingMachineResponse response = WashingMachineResponse.builder()
                .id(deviceId)
                .workMode(WashingMachineCurrentWorkMode.OFF)
                .timestamp(new Date())
                .build();
        System.out.println("ZAVRSENO PRANJE");
        mqttClient.publish(SEND_WASHING_MACHINE_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(response)));
    }

    private boolean isBetween(LocalTime timeToCheck, LocalTime startTime, LocalTime endTime) {
        return !timeToCheck.isBefore(startTime) && !timeToCheck.isAfter(endTime);
    }


    @FunctionalInterface
    public interface Callback {
        void apply(WashingMachineRequest request);
    }

}
