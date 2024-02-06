package rs.ac.uns.ftn.nwt.simulator_server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.nwt.simulator_server.configuration.mqtt.MessageCallback;
import rs.ac.uns.ftn.nwt.simulator_server.constants.air_conditioner.AirConditionerCurrentWorkMode;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.air_condtioner.AirConditionerRequest;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.air_condtioner.command_type.NormalCommand;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.air_condtioner.command_type.PeriodicCommand;
import rs.ac.uns.ftn.nwt.simulator_server.dto.response.AirConditionerResponse;
import rs.ac.uns.ftn.nwt.simulator_server.utils.SimulationMath;

import java.time.LocalTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static rs.ac.uns.ftn.nwt.simulator_server.constants.air_conditioner.AirConditionerCurrentWorkMode.*;
import static rs.ac.uns.ftn.nwt.simulator_server.constants.topics.SendTopicConstants.SEND_AIR_CONDITIONER_TOPIC;

@RequiredArgsConstructor
@Service
public class AirConditionerService {

    private final RunnableManager runnableManager = new RunnableManager();
    private final IMqttClient mqttClient;
    private final MessageCallback messageCallback;
    private final ObjectMapper mapper;

    // Active
    // <deviceId <enumWorkMode, runnableId>>
    private final Map<Long, Map<AirConditionerCurrentWorkMode, Long>> devices = new ConcurrentHashMap<>();

    // <appointmentId, runnable>
    private Map<Long, Long> allAppointments = new ConcurrentHashMap<>();
    private Map<Long, Boolean> isWorkingPeriodicAppointment = new ConcurrentHashMap<>();

    // <deviceId, temp>
    private final Map<Long, Double> temperatures = new ConcurrentHashMap<>();
    private final int PERIOD_IN_SECONDS = 5;
    private final double  LOWER_BOUND_FOR_RANDOM_GENERATION = 0.1;
    private final double UPPER_BOUND_FOR_RANDOM_GENERATION = 0.2;
    private final double LOWER_BOUND_FOR_RANDOM_TEMPERATURE = 19;
    private final double UPPER_BOUND_FOR_RANDOM_TEMPERATURE = 20;


    @PostConstruct
    public void setCommand() {
        messageCallback.setStarAirConditionerNormalHeatingSim(this::turnOnHeating);
        messageCallback.setStarAirConditionerNormalCoolingSim(this::turnOnCooling);
        messageCallback.setStartAirConditionerNormalMaintenanceSim(this::turnOnTemperatureMaintenance);

        messageCallback.setStartAirConditionerPeriodicHeatingSim(this::turnOnHeatingPeriodic);
        messageCallback.setStartAirConditionerPeriodicCollingSim(this::turnOnCoolingPeriodic);
        messageCallback.setStartAirConditionerPeriodicMaintenanceSim(this::turnOnPeriodicTemperatureMaintenance);

        messageCallback.setOffAirConditioner(this::cancelWithNotify);
    }

    public void turnOnHeating(AirConditionerRequest request) {
        addTemperature(request.getId(), 0);
        cancelWithNotify(request.getId(), COOLING);
        NormalCommand normalCommand = request.getNormalCommand();
        Runnable runnable = () -> heatingTask(request.getId(), normalCommand.getWantedTemperature());
        long runnableId = runnableManager.startRunnable(request.getId(), runnable, 0, PERIOD_IN_SECONDS);
        addNewRunnableIntoDevices(request.getId(), HEATING, runnableId);
    }

    @SneakyThrows
    private void heatingTask(long deviceId, double wantedTemp) {
        double currentTemp = temperatures.get(deviceId);
        if(currentTemp >= wantedTemp) {
            cancelWithNotify(deviceId, HEATING);

        } else {
            double temperatureChange = SimulationMath.generateRandomDouble(LOWER_BOUND_FOR_RANDOM_GENERATION, UPPER_BOUND_FOR_RANDOM_GENERATION);
//            System.out.println("(deviceId = " + deviceId + " temp = " + currentTemp + ")") ; // TODO remove
            addTemperature(deviceId, temperatureChange);

            AirConditionerResponse response = AirConditionerResponse.builder()
                    .id(deviceId)
                    .appointmentId(-1000L)
                    .workMode(HEATING)
                    .temperature(currentTemp)
                    .timestamp(new Date())
                    .build();
            mqttClient.publish(SEND_AIR_CONDITIONER_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(response)));
        }
    }

    public void turnOnCooling(AirConditionerRequest request) {
        NormalCommand normalCommand = request.getNormalCommand();
        addTemperature(request.getId(), 0);
        cancelWithNotify(request.getId(), HEATING);
        Runnable runnable = () -> coolingTask(request.getId(), normalCommand.getWantedTemperature());
        long runnableId = runnableManager.startRunnable(request.getId(), runnable, 0, PERIOD_IN_SECONDS);
        addNewRunnableIntoDevices(request.getId(), COOLING, runnableId);
    }

    @SneakyThrows
    private void coolingTask(long deviceId, double wantedTemp) {
        double currentTemp = temperatures.get(deviceId);
        if(currentTemp <= wantedTemp) {
            cancelWithNotify(deviceId, COOLING);
        } else {
//            System.out.println("(deviceId = " + deviceId + " temp = " + currentTemp + ")") ; // TODO remove
            double temperatureChange = - SimulationMath.generateRandomDouble(LOWER_BOUND_FOR_RANDOM_GENERATION, UPPER_BOUND_FOR_RANDOM_GENERATION);
            addTemperature(deviceId, temperatureChange);

            AirConditionerResponse response = AirConditionerResponse.builder()
                    .id(deviceId)
                    .appointmentId(-1000L)
                    .workMode(COOLING)
                    .temperature(currentTemp)
                    .timestamp(new Date())
                    .build();
            mqttClient.publish(SEND_AIR_CONDITIONER_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(response)));
        }
    }

    public void turnOnTemperatureMaintenance(AirConditionerRequest request) {
        addTemperature(request.getId(), 0);
        cancelWithNotify(request.getId(), HEATING);
        cancelWithNotify(request.getId(), COOLING);
        NormalCommand normalCommand = request.getNormalCommand();
        Runnable runnable = () -> maintenanceTask(request.getId(), normalCommand.getWantedTemperature());
        long runnableId = runnableManager.startRunnable(request.getId(), runnable, 0, PERIOD_IN_SECONDS);
        addNewRunnableIntoDevices(request.getId(), TEMPERATURE_MAINTENANCE, runnableId);
    }

    @SneakyThrows
    private void maintenanceTask(long deviceId, double wantedTemp) {
        double currentTemp = temperatures.get(deviceId);
        if (currentTemp < wantedTemp) {
//            System.out.println("(deviceId = " + deviceId + " temp = " + currentTemp + ")") ; // TODO remove
            double temperatureChange = SimulationMath.generateRandomDouble(LOWER_BOUND_FOR_RANDOM_GENERATION, UPPER_BOUND_FOR_RANDOM_GENERATION);
            addTemperature(deviceId, temperatureChange);

            AirConditionerResponse response = AirConditionerResponse.builder()
                    .id(deviceId)
                    .appointmentId(-1000L)
                    .workMode(TEMPERATURE_MAINTENANCE)
                    .temperature(currentTemp)
                    .timestamp(new Date())
                    .build();
            mqttClient.publish(SEND_AIR_CONDITIONER_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(response)));

        } else if(currentTemp > wantedTemp ) {
//            System.out.println("(deviceId = " + deviceId + " temp = " + currentTemp + ")") ; // TODO remove
            double temperatureChange = -SimulationMath.generateRandomDouble(LOWER_BOUND_FOR_RANDOM_GENERATION, UPPER_BOUND_FOR_RANDOM_GENERATION);
            addTemperature(deviceId, temperatureChange);

            AirConditionerResponse response = AirConditionerResponse.builder()
                    .id(deviceId)
                    .workMode(TEMPERATURE_MAINTENANCE)
                    .temperature(currentTemp)
                    .timestamp(new Date())
                    .build();
            mqttClient.publish(SEND_AIR_CONDITIONER_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(response)));
        }
    }

    public void turnOnHeatingPeriodic(AirConditionerRequest request) {
        addTemperature(request.getId(), 0);
        PeriodicCommand periodicCommand = request.getPeriodicCommand();
        Runnable runnable = () -> periodicHeatingTask(
                request.getId(),
                request.getPeriodicCommand().getAppointmentId(),
                LocalTime.parse(request.getPeriodicCommand().getStartTime()),
                LocalTime.parse(request.getPeriodicCommand().getEndTime()),
                periodicCommand.getWantedTemperature());

        long runnableId = runnableManager.startRunnable(request.getId(), runnable, 0, PERIOD_IN_SECONDS);
        addNewRunnableIntoAppointments(request.getPeriodicCommand().getAppointmentId(), runnableId);
        isWorkingPeriodicAppointment.put(request.getPeriodicCommand().getAppointmentId(), false);
    }

    @SneakyThrows
    private void periodicHeatingTask(long deviceId, long appointmentId, LocalTime startTime, LocalTime endTime,
                                     double wantedTemp) {
        LocalTime now = LocalTime.now();
        double currentTemp = temperatures.get(deviceId);

        if(isBetween(now, startTime, endTime) && currentTemp < wantedTemp) {
////            System.out.println("(deviceId = " + deviceId + " temp = " + currentTemp + ")") ; // TODO remove
            isWorkingPeriodicAppointment.put(appointmentId, true);
            cancelWithoutNotify(deviceId, HEATING);
            cancelWithNotify(deviceId, COOLING);
            double temperatureChange = SimulationMath.generateRandomDouble(LOWER_BOUND_FOR_RANDOM_GENERATION, UPPER_BOUND_FOR_RANDOM_GENERATION);
            addTemperature(deviceId, temperatureChange);

            AirConditionerResponse response = AirConditionerResponse.builder()
                    .id(deviceId)
                    .appointmentId(appointmentId)
                    .workMode(PERIODIC_HEATING)
                    .temperature(currentTemp)
                    .timestamp(new Date())
                    .build();
            mqttClient.publish(SEND_AIR_CONDITIONER_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(response)));
        } else {
            if(isWorkingPeriodicAppointment.get(appointmentId)) {
                isWorkingPeriodicAppointment.put(appointmentId, false);
                AirConditionerResponse response = AirConditionerResponse.builder()
                        .id(deviceId)
                        .appointmentId(appointmentId)
                        .workMode(OFF)
                        .temperature(-1000.0)
                        .timestamp(new Date())
                        .build();
                mqttClient.publish(SEND_AIR_CONDITIONER_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(response)));
            }
        }
    }

    public void turnOnCoolingPeriodic(AirConditionerRequest request) {
        addTemperature(request.getId(), 0);
        PeriodicCommand periodicCommand = request.getPeriodicCommand();
        Runnable runnable = () -> periodicCoolingTask(
                request.getId(),
                request.getPeriodicCommand().getAppointmentId(),
                LocalTime.parse(request.getPeriodicCommand().getStartTime()),
                LocalTime.parse(request.getPeriodicCommand().getEndTime()),
                periodicCommand.getWantedTemperature());
        isWorkingPeriodicAppointment.put(request.getPeriodicCommand().getAppointmentId(), false);
        long runnableId = runnableManager.startRunnable(request.getId(), runnable, 0, PERIOD_IN_SECONDS);
        addNewRunnableIntoAppointments(request.getPeriodicCommand().getAppointmentId(), runnableId);
    }

    @SneakyThrows
    private void periodicCoolingTask(long deviceId, long appointmentId, LocalTime startTime, LocalTime endTime,
                                     double wantedTemp){
        LocalTime now = LocalTime.now();
        double currentTemp = temperatures.get(deviceId);

        if(isBetween(now, startTime, endTime) && currentTemp > wantedTemp) {
            isWorkingPeriodicAppointment.put(appointmentId,true);
//            System.out.println("(deviceId = " + deviceId + " temp = " + currentTemp + ")") ; // TODO remove
            cancelWithNotify(deviceId, HEATING);
            cancelWithoutNotify(deviceId, COOLING);
            double temperatureChange = SimulationMath.generateRandomDouble(LOWER_BOUND_FOR_RANDOM_GENERATION, UPPER_BOUND_FOR_RANDOM_GENERATION);
            subtractTemperature(deviceId, temperatureChange);

            AirConditionerResponse response = AirConditionerResponse.builder()
                    .id(deviceId)
                    .workMode(PERIODIC_COOLING)
                    .temperature(currentTemp)
                    .timestamp(new Date())
                    .build();

            mqttClient.publish(SEND_AIR_CONDITIONER_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(response)));
        } else {
            if(isWorkingPeriodicAppointment.get(appointmentId)) {
                isWorkingPeriodicAppointment.put(appointmentId, false);
                AirConditionerResponse response = AirConditionerResponse.builder()
                        .id(deviceId)
                        .appointmentId(appointmentId)
                        .workMode(OFF)
                        .temperature(-1000.0)
                        .timestamp(new Date())
                        .build();
                mqttClient.publish(SEND_AIR_CONDITIONER_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(response)));
            }
        }
    }

    public void turnOnPeriodicTemperatureMaintenance(AirConditionerRequest request) {
        addTemperature(request.getId(), 0);
        PeriodicCommand periodicCommand = request.getPeriodicCommand();
        Runnable runnable = () -> periodicMaintenanceTask(
                request.getId(),
                request.getPeriodicCommand().getAppointmentId(),
                LocalTime.parse(request.getPeriodicCommand().getStartTime()),
                LocalTime.parse(request.getPeriodicCommand().getEndTime()),
                periodicCommand.getWantedTemperature());
        isWorkingPeriodicAppointment.put(request.getPeriodicCommand().getAppointmentId(), false);
        long runnableId = runnableManager.startRunnable(request.getId(), runnable, 0, PERIOD_IN_SECONDS);
        addNewRunnableIntoAppointments(request.getPeriodicCommand().getAppointmentId(), runnableId);
    }

    @SneakyThrows
    private void periodicMaintenanceTask(long deviceId, long appointmentId, LocalTime startTime, LocalTime endTime,
                                         double wantedTemp) {

        LocalTime now = LocalTime.now();
        if(isBetween(now, startTime, endTime)) {

            double currentTemp = temperatures.get(deviceId);
            if (currentTemp < wantedTemp) {
                isWorkingPeriodicAppointment.put(appointmentId, true);
//                System.out.println("(deviceId = " + deviceId + " temp = " + currentTemp + ")") ; // TODO remove
                double temperatureChange = SimulationMath.generateRandomDouble(LOWER_BOUND_FOR_RANDOM_GENERATION, UPPER_BOUND_FOR_RANDOM_GENERATION);
                addTemperature(deviceId, temperatureChange);

                AirConditionerResponse response = AirConditionerResponse.builder()
                        .id(deviceId)
                        .appointmentId(appointmentId)
                        .workMode(PERIODIC_TEMPERATURE_MAINTENANCE)
                        .temperature(currentTemp)
                        .timestamp(new Date())
                        .build();

                mqttClient.publish(SEND_AIR_CONDITIONER_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(response)));


            } else if(currentTemp > wantedTemp ) {
                isWorkingPeriodicAppointment.put(appointmentId, true);
//                System.out.println("(deviceId = " + deviceId + " temp = " + currentTemp + ")") ; // TODO remove
                double temperatureChange = SimulationMath.generateRandomDouble(LOWER_BOUND_FOR_RANDOM_GENERATION, UPPER_BOUND_FOR_RANDOM_GENERATION);
                subtractTemperature(deviceId, temperatureChange);

                AirConditionerResponse response = AirConditionerResponse.builder()
                        .id(deviceId)
                        .appointmentId(appointmentId)
                        .workMode(PERIODIC_TEMPERATURE_MAINTENANCE)
                        .temperature(currentTemp)
                        .timestamp(new Date())
                        .build();
                mqttClient.publish(SEND_AIR_CONDITIONER_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(response)));
            }
        } else {
            if(isWorkingPeriodicAppointment.get(appointmentId)) {
                isWorkingPeriodicAppointment.put(appointmentId, false);
                AirConditionerResponse response = AirConditionerResponse.builder()
                        .id(deviceId)
                        .appointmentId(appointmentId)
                        .workMode(OFF)
                        .temperature(-1000.0)
                        .timestamp(new Date())
                        .build();
                mqttClient.publish(SEND_AIR_CONDITIONER_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(response)));
            }
        }
    }

    @SneakyThrows
    public void cancelWithNotify(AirConditionerRequest request) {
        long deviceId = request.getId();
        switch (request.getCommand()) {
            case OFF_HEATING -> cancelWithNotify(deviceId, HEATING);
            case OFF_COOLING -> cancelWithNotify(deviceId, COOLING);
            case OFF_TEMPERATURE_MAINTENANCE -> cancelWithNotify(deviceId, TEMPERATURE_MAINTENANCE);
            case OFF_PERIODIC_HEATING -> cancelAppointmentWithNotify(deviceId, request.getPeriodicCommand().getAppointmentId());
            case OFF_PERIODIC_COOLING -> cancelAppointmentWithNotify(deviceId, request.getPeriodicCommand().getAppointmentId());
            case OFF_PERIODIC_TEMPERATURE_MAINTENANCE -> cancelAppointmentWithNotify(deviceId, request.getPeriodicCommand().getAppointmentId());
        }
    }

    @SneakyThrows
    private void cancelWithNotify(long deviceId, AirConditionerCurrentWorkMode workingMode) {
        if (devices.get(deviceId) == null) return;
        var runnables = devices.get(deviceId);

        if(runnables.get(workingMode) == null) return;

        long runnableId = runnables.get(workingMode);
        runnableManager.cancelRunnable(deviceId, runnableId);

        AirConditionerResponse response = AirConditionerResponse.builder()
                .id(deviceId)
                .workMode(OFF)
                .temperature(-1000.0)
                .timestamp(new Date())
                .build();
        mqttClient.publish(SEND_AIR_CONDITIONER_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(response)));
    }

    @SneakyThrows
    private void cancelAppointmentWithNotify(long deviceId, long appointmentId) {
        long runnableId = allAppointments.get(appointmentId);
        runnableManager.cancelRunnable(deviceId, runnableId);
        allAppointments.remove(appointmentId);

        AirConditionerResponse response = AirConditionerResponse.builder()
                .id(deviceId)
                .appointmentId(appointmentId)
                .workMode(OFF)
                .temperature(-1000.0)
                .timestamp(new Date())
                .build();
        mqttClient.publish(SEND_AIR_CONDITIONER_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(response)));

    }

    private void cancelWithoutNotify(long deviceId, AirConditionerCurrentWorkMode workingMode) {
        if (devices.get(deviceId) == null) return;
        var runnables = devices.get(deviceId);

        if(runnables.get(workingMode) == null) return;

        long runnableId = runnables.get(workingMode);
        runnableManager.cancelRunnable(deviceId, runnableId);
    }

    private void addNewRunnableIntoDevices(long deviceId, AirConditionerCurrentWorkMode workMode, long runnableId) {
        var runnables = this.devices.get(deviceId);
        if(runnables == null)  runnables = new ConcurrentHashMap<>();

        runnables.put(workMode, runnableId);
        this.devices.put(deviceId, runnables);
    }

    private void addNewRunnableIntoAppointments(long appointmentId, long runnableId) {
        this.allAppointments.put(appointmentId, runnableId);
    }

    private void addTemperature(long deviceId, double temperatureChange) {
        if(temperatures.get(deviceId) == null) {
            temperatures.put(deviceId, SimulationMath.generateRandomDouble(LOWER_BOUND_FOR_RANDOM_TEMPERATURE , UPPER_BOUND_FOR_RANDOM_TEMPERATURE));
        }
        temperatures.put(deviceId, temperatures.get(deviceId) + temperatureChange);
    }

    private void subtractTemperature(long deviceId, double temperatureChange) {
        if(temperatures.get(deviceId) == null) {
            temperatures.put(deviceId, SimulationMath.generateRandomDouble(LOWER_BOUND_FOR_RANDOM_TEMPERATURE , UPPER_BOUND_FOR_RANDOM_TEMPERATURE));
        }
        temperatures.put(deviceId, temperatures.get(deviceId) - temperatureChange);
    }

    private boolean isBetween(LocalTime timeToCheck, LocalTime startTime, LocalTime endTime) {
        return !timeToCheck.isBefore(startTime) && !timeToCheck.isAfter(endTime);
    }

    @FunctionalInterface
    public interface Callback {
        void apply(AirConditionerRequest request);
    }
}
