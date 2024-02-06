package rs.ac.uns.ftn.nwt.simulator_server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.nwt.simulator_server.configuration.mqtt.MessageCallback;
import rs.ac.uns.ftn.nwt.simulator_server.dto.sprinkler_system.request.SprinklerSystemRequest;
import rs.ac.uns.ftn.nwt.simulator_server.dto.sprinkler_system.request.SprinklerSystemScheduleRequest;
import rs.ac.uns.ftn.nwt.simulator_server.dto.sprinkler_system.response.SprinklerSystemResponse;
import rs.ac.uns.ftn.nwt.simulator_server.dto.sprinkler_system.state.SprinklerSystemState;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import static rs.ac.uns.ftn.nwt.simulator_server.constants.topics.SendTopicConstants.SEND_SPRINKLER_SYSTEM_TOPIC;

@RequiredArgsConstructor
@Service
public class SprinklerSystemService {
    private final IMqttClient mqttClient;
    private final MessageCallback messageCallback;
    private final RunnableManager runnableManager;
    private final ObjectMapper mapper;

    private ConcurrentHashMap<Long, SprinklerSystemState> states = new ConcurrentHashMap<>();
    private final int GENERATE_DATA_TIME = 45;

    @PostConstruct
    public void init() {
        messageCallback.setSetSprinklerSystemCommand(this::changeState);
    }

    @FunctionalInterface
    public interface Callback { void apply(SprinklerSystemRequest request); }

    private void changeState(SprinklerSystemRequest request) {
        SprinklerSystemState state = states.get(request.getId());
        if (state == null) {
            setInitState(request);
            Runnable runnable = () -> simulation(request.getId());
            runnableManager.startRunnable(request.getId(), runnable, 0, GENERATE_DATA_TIME);
        } else changeState(request, state);
    }

    private void setInitState(SprinklerSystemRequest request) {
        SprinklerSystemState state = SprinklerSystemState.builder()
                .id(request.getId())
                .systemOn(request.isSystemOn())
                .schedule(request.getSchedule())
                .build();
        states.put(request.getId(), state);
    }

    private void changeState(SprinklerSystemRequest request, SprinklerSystemState state) {
        if (request.getSchedule() != null) state.setSchedule(request.getSchedule());
        else {
            state.setSystemOn(request.isSystemOn());
            sendMqttMessage(request.getId(), request.isSystemOn(), request.getUserEmail());
        }
        states.put(request.getId(), state);
    }

    private void simulation(long id) {
        SprinklerSystemState state = states.get(id);
        boolean isMessageSent = checkDatesAndIsMessageSent(state);
        if (!isMessageSent) sendMqttMessage(state, null);
    }

    private boolean checkDatesAndIsMessageSent(SprinklerSystemState state) {
        LocalDateTime date = LocalDateTime.now();
        boolean isStartMessageSent = false;
        boolean isEndMessageSent = false;
        if (date.getSecond() < GENERATE_DATA_TIME) {
            isStartMessageSent = checkStartDatesAndIsMessageSent(state, date);
            isEndMessageSent = checkEndDatesAndIsMessageSent(state, date);
        }
        return isStartMessageSent || isEndMessageSent;
    }

    private boolean checkStartDatesAndIsMessageSent(SprinklerSystemState state, LocalDateTime date) {
        for (SprinklerSystemScheduleRequest scheduleRequest: state.getSchedule()) {
            LocalTime time = LocalTime.parse(scheduleRequest.getStartTime());
            for (DayOfWeek day: scheduleRequest.getDays()) {
                if (date.getDayOfWeek() == day
                        && date.getHour() == time.getHour()
                        && date.getMinute() == time.getMinute()) {
                    if (!state.isSystemOn()) {
                        state.setSystemOn(true);
                        states.put(state.getId(), state);
                        sendMqttMessage(state, "SYSTEM");
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

    private boolean checkEndDatesAndIsMessageSent(SprinklerSystemState state, LocalDateTime date) {
        for (SprinklerSystemScheduleRequest scheduleRequest: state.getSchedule()) {
            LocalTime time = LocalTime.parse(scheduleRequest.getEndTime());
            for (DayOfWeek day: scheduleRequest.getDays()) {
                if (date.getDayOfWeek() == day
                        && date.getHour() == time.getHour()
                        && date.getMinute() == time.getMinute()) {
                    if (state.isSystemOn()) {
                        state.setSystemOn(false);
                        states.put(state.getId(), state);
                        sendMqttMessage(state, "SYSTEM");
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

    @SneakyThrows
    private void sendMqttMessage(SprinklerSystemState state, String triggeredBy) {
        SprinklerSystemResponse response = SprinklerSystemResponse.builder()
                .id(state.getId())
                .systemOn(state.isSystemOn())
                .triggeredBy(triggeredBy)
                .timestamp(new Date())
                .build();
        mqttClient.publish(SEND_SPRINKLER_SYSTEM_TOPIC + state.getId(), new MqttMessage(mapper.writeValueAsBytes(response)));
    }

    @SneakyThrows
    private void sendMqttMessage(long id, boolean systemOn, String triggeredBy) {
        SprinklerSystemResponse response = SprinklerSystemResponse.builder()
                .id(id)
                .systemOn(systemOn)
                .triggeredBy(triggeredBy)
                .timestamp(new Date())
                .build();
        mqttClient.publish(SEND_SPRINKLER_SYSTEM_TOPIC + id, new MqttMessage(mapper.writeValueAsBytes(response)));
    }

}
