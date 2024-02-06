package nvt.project.smart_home.main.feature.device.sprinkler_system.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.config.ws_handler.SprinklerSystemWebSocketHandler;
import nvt.project.smart_home.main.feature.device.sprinkler_system.entity.SprinklerSystemEntity;
import nvt.project.smart_home.main.feature.device.sprinkler_system.entity.SprinklerSystemScheduleEntity;
import nvt.project.smart_home.main.feature.device.sprinkler_system.mqtt_dto.request.SprinklerSystemMqttRequestDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.mqtt_dto.request.SprinklerSystemScheduleMqttRequestDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.mqtt_dto.response.SprinklerSystemMqttResponse;
import nvt.project.smart_home.main.feature.device.sprinkler_system.service.interf.ISprinklerSystemMqttService;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.RECEIVE_SPRINKLER_SYSTEM_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.SEND_SPRINKLER_SYSTEM_TOPIC;

@Service
@RequiredArgsConstructor
public class SprinklerSystemMqttService implements ISprinklerSystemMqttService {

    private final SprinklerSystemWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public void sendWsMessage(SprinklerSystemMqttResponse response) {
        String message = objectMapper.writeValueAsString(response);
        webSocketHandler.broadcastMessage(response.getId(), message);
    }
}
