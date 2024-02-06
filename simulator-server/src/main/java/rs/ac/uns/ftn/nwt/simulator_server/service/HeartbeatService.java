package rs.ac.uns.ftn.nwt.simulator_server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.nwt.simulator_server.configuration.mqtt.MessageCallback;
import rs.ac.uns.ftn.nwt.simulator_server.dto.HeartbeatDto;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static rs.ac.uns.ftn.nwt.simulator_server.constants.topics.SendTopicConstants.I_AM_ALIVE_TOPIC;

@RequiredArgsConstructor
@Service
public class HeartbeatService {

    private final IMqttClient mqttClient;
    private final MessageCallback messageCallback;
    private final ObjectMapper mapper;
    private final RunnableManager runnableManager;
    private final Map<Long, Long> devices = new ConcurrentHashMap<>();
    private final Random random;

    // if the boolean value is true, then the device is broken
    private final Map<Long, Boolean> devicesFailed = new ConcurrentHashMap<>();
    private final double chanceToFail = 0.000;


    @PostConstruct
    public void init() {
        messageCallback.setStartHeartbeatSim(this::startSimulate);
    }

    public void startSimulate(long deviceId) {
        // TODO if needed add to map subscribedDevices
        Runnable runnable = () -> generateMeasurements(deviceId);
        long runnableId = runnableManager.startRunnable(deviceId, runnable, 0, 10);
        devices.put(deviceId, runnableId);
        devicesFailed.put(deviceId, false);
    }

    public void cancelSimulation(long deviceId) {
        // TODO if needed remove from map subscribedDevices
        long runnableId = devices.get(deviceId);
        runnableManager.cancelRunnable(deviceId, runnableId);
        devices.remove(deviceId);
    }

    @SneakyThrows
    private void generateMeasurements(long deviceId) {
        HeartbeatDto heartbeatDto = HeartbeatDto.builder()
                .deviceId(deviceId)
                .build();
//        System.out.println("deviceId=%d is %s".formatted(heartbeatDto.getDeviceId(), heartbeatDto.isFailed() ? "not alive" : "alive"));
        if (devicesFailed.get(deviceId)) {
            heartbeatDto.setFailed(true);
            mqttClient.publish(I_AM_ALIVE_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(heartbeatDto)));
        } else {
            heartbeatDto.setFailed(random.nextDouble() < chanceToFail);
            mqttClient.publish(I_AM_ALIVE_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(heartbeatDto)));
        }
    }

    @FunctionalInterface
    public interface Callback {
        void apply(long deviceId);
    }
}
