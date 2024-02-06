package rs.ac.uns.ftn.nwt.simulator_server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.nwt.simulator_server.configuration.mqtt.MessageCallback;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.electric_vehicle_charger.ElectricVehicleChargerRequest;
import rs.ac.uns.ftn.nwt.simulator_server.dto.response.ElectricVehicleChargerResponse;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static rs.ac.uns.ftn.nwt.simulator_server.constants.topics.SendTopicConstants.SEND_ELECTRIC_VEHICLE_CHARGER_TOPIC;

@RequiredArgsConstructor
@Service
public class ElectricVehicleChargerService {

    private final IMqttClient mqttClient;
    private final MessageCallback messageCallback;
    private final ObjectMapper mapper;
    private final RunnableManager runnableManager;
    private final Map<Long, Long> devices = new ConcurrentHashMap<>();
    private final Map<String, ElectricVehicleChargerRequest> chargingVehicles = new ConcurrentHashMap<>();
    private final Map<String, Long> deviceSimIds = new ConcurrentHashMap<>();
    private final AtomicLong deviceIdCounter = new AtomicLong(1L);
    private final int PERIOD_IN_SECONDS = 5;

    @PostConstruct
    public void init() {
        messageCallback.setStartElectricVehicleChargerSim(this::startSimulate);
        messageCallback.setCancelElectricVehicleChargerSim(this::cancelSimulation);
    }

    public void startSimulate(ElectricVehicleChargerRequest request) {
        // TODO if needed add to map subscribedDevices
        long simId = deviceIdCounter.getAndIncrement();
        String vehicleChargingSimId = request.getId() + "_" + request.getChargingVehicle().getId();
        Runnable runnable = () -> generateMeasurements(vehicleChargingSimId);
        long runnableId = runnableManager.startRunnable(simId, runnable, 0, PERIOD_IN_SECONDS);
        devices.put(simId, runnableId);
        chargingVehicles.put(vehicleChargingSimId, request);
        deviceSimIds.put(vehicleChargingSimId, simId);
    }

    public void cancelSimulation(ElectricVehicleChargerRequest request) {
        // TODO if needed remove from map subscribedDevices
        System.out.println("Canceling charging simulation");
        String vehicleChargingSimId = request.getId() + "_" + request.getChargingVehicle().getId();
        long simId = deviceSimIds.get(vehicleChargingSimId);
        long runnableId = devices.get(simId);
        runnableManager.cancelRunnable(simId, runnableId);
        devices.remove(simId);
        chargingVehicles.remove(vehicleChargingSimId);
        deviceSimIds.remove(vehicleChargingSimId);
    }

    @SneakyThrows
    private void generateMeasurements(String vehicleChargingSimId) {
        ElectricVehicleChargerRequest request = chargingVehicles.get(vehicleChargingSimId);

        System.out.println(request.toString());
        double currVehiclePower = request.getChargingVehicle().getCurrentPower();
        double chargeValue = getChargeValue(
                request.getChargeLimit(),
                currVehiclePower,
                request.getChargingVehicle().getMaxPower(),
                request.getChargePower() * (PERIOD_IN_SECONDS / 60.0));

        request.getChargingVehicle().setCurrentPower(currVehiclePower + chargeValue);
        System.out.println(chargeValue);

        ElectricVehicleChargerResponse response = ElectricVehicleChargerResponse.builder()
                .id(request.getId())
                .chargingVehicle(request.getChargingVehicle())
                .chargeAmount(chargeValue)
                .timestamp(new Date())
                .build();
        mqttClient.publish(SEND_ELECTRIC_VEHICLE_CHARGER_TOPIC + "-" + vehicleChargingSimId,
                new MqttMessage(mapper.writeValueAsBytes(response)));
    }

    private double getChargeValue(double chargeLimitPercentage, double currPower, double maxPower, double chargePower) {
        double chargeLimit = (chargeLimitPercentage / 100.0) * maxPower;
        double chargeValue;
        if (currPower + chargePower > chargeLimit) {
            double excess = currPower + chargePower - chargeLimit;
            chargeValue = chargePower - excess;
        } else {
            chargeValue = chargePower;
        }

        return chargeValue;
    }

    @FunctionalInterface
    public interface Callback {
        void apply(ElectricVehicleChargerRequest request);
    }
}
