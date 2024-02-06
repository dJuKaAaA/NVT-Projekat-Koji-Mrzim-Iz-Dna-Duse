package rs.ac.uns.ftn.nwt.simulator_server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.nwt.simulator_server.configuration.mqtt.MessageCallback;
import rs.ac.uns.ftn.nwt.simulator_server.dto.lamp.request.LampRequest;
import rs.ac.uns.ftn.nwt.simulator_server.dto.lamp.state.LampState;
import rs.ac.uns.ftn.nwt.simulator_server.dto.lamp.constants.LampCommand;
import rs.ac.uns.ftn.nwt.simulator_server.dto.lamp.response.LampResponse;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import static rs.ac.uns.ftn.nwt.simulator_server.constants.topics.SendTopicConstants.SEND_LAMP_TOPIC;

@Service
@RequiredArgsConstructor
public class LampService {

    private final IMqttClient mqttClient;
    private final MessageCallback messageCallback;
    private final RunnableManager runnableManager;
    private final ObjectMapper mapper;

    private final AtomicInteger illumination = new AtomicInteger(100); // 0-100
    private final AtomicBoolean isBrighter = new AtomicBoolean(false);
    private ConcurrentHashMap<Long, LampState> states = new ConcurrentHashMap<>();
    private final int GENERATE_DATA_TIME = 45;
    private final int MAX_ILLUMINATION_OFFSET = 3;
    private final Integer TURN_OFF_THRESHOLD = 50;

    @PostConstruct
    public void init() {
        messageCallback.setSetLampCommand(this::setCommand);
        Runnable runnable = this::changeIllumination;
        runnableManager.startNonEndingRunnable(runnable, 0, GENERATE_DATA_TIME);
    }

    @FunctionalInterface
    public interface Callback { void apply(LampRequest request); }

    public void setCommand(LampRequest request) {
        if (states.get(request.getId()) == null) {
            setInitState(request);
            Runnable runnable = () -> simulation(request.getId());
            runnableManager.startRunnable(request.getId(), runnable, 0, GENERATE_DATA_TIME);
        } else {
            changeCommand(request);
            changeState(request);
        }
    }

    @SneakyThrows
    private void changeCommand(LampRequest request) {
        System.out.println(request.getTriggeredBy());
        LampState state = states.get(request.getId());
        LampCommand command = LampCommand.AUTO_MODE_OFF;
        if (request.getAutoModeOn() != state.getAutoModeOn()) {
            if (request.getAutoModeOn()) command = LampCommand.AUTO_MODE_ON;
        } else {
            if (request.getBulbOn()) command = LampCommand.ON_BULB;
            else command = LampCommand.OFF_BULB;
        }
        LampResponse response = LampResponse.builder()
                .id(request.getId())
                .lightLevel((double)illumination.get())
                .bulbOn(request.getBulbOn())
                .autoModeOn(request.getAutoModeOn())
                .command(command)
                .triggeredBy(request.getTriggeredBy())
                .timestamp(new Date())
                .build();
        mqttClient.publish(SEND_LAMP_TOPIC + request.getId(), new MqttMessage(mapper.writeValueAsBytes(response)));
    }

    private void setInitState(LampRequest request) {
        LampState state = LampState.builder()
                .bulbOn(request.getBulbOn())
                .autoModeOn(request.getAutoModeOn())
                .build();
        states.put(request.getId(), state);
    }

    private void changeState(LampRequest request) {
        LampState state = states.get(request.getId());
        if (!request.getAutoModeOn()) state.setBulbOn(request.getBulbOn());
        state.setAutoModeOn(request.getAutoModeOn());
        states.put(request.getId(), state);
    }

    private void changeBulbOn(long lampId,boolean bulbOn) {
        LampState state = states.get(lampId);
        state.setBulbOn(bulbOn);
        states.put(lampId, state);
    }

    @SneakyThrows
    private void simulation(Long lampId) {
        LampState state = states.get(lampId);
        if (state.getAutoModeOn()) {
            boolean bulbOn = illumination.get() <= TURN_OFF_THRESHOLD;
            autoModeTask(lampId, bulbOn, bulbOn != state.getBulbOn());
        }
        else defaultModeTask(lampId, state.getBulbOn());
    }

    @SneakyThrows
    private void defaultModeTask(long deviceId, boolean bulbOn) {
        LampResponse response = LampResponse.builder()
                .id(deviceId)
                .bulbOn(bulbOn)
                .lightLevel((double)illumination.get())
                .autoModeOn(false)
                .timestamp(new Date())
                .build();
        mqttClient.publish(SEND_LAMP_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(response)));
    }

    @SneakyThrows
    private void autoModeTask(long deviceId, boolean bulbOn, boolean isBulbOnChanged) {
        LampCommand command = null;
        String triggeredBy = null;
        if (isBulbOnChanged) {
            changeBulbOn(deviceId, bulbOn);
            if (bulbOn) command = LampCommand.ON_BULB;
            else command = LampCommand.OFF_BULB;
            triggeredBy = "SYSTEM";
        }

        LampResponse response = LampResponse.builder()
                .id(deviceId)
                .bulbOn(bulbOn)
                .lightLevel((double) illumination.get())
                .autoModeOn(true)
                .timestamp(new Date())
                .command(command)
                .triggeredBy(triggeredBy)
                .build();
        mqttClient.publish(SEND_LAMP_TOPIC + deviceId, new MqttMessage(mapper.writeValueAsBytes(response)));
    }

    private void changeIllumination() {
        Random rand = new Random();
        int num = rand.nextInt(MAX_ILLUMINATION_OFFSET);
        if (isBrighter.get()) {
            illumination.getAndAdd(num);
            if (illumination.get() >= 100) {
                illumination.set(100);
                isBrighter.set(false);
            }
        } else {
            illumination.getAndAdd(-num);
            if (illumination.get() <= 0) {
                illumination.set(0);
                isBrighter.set(true);
            }
        }
    }
}