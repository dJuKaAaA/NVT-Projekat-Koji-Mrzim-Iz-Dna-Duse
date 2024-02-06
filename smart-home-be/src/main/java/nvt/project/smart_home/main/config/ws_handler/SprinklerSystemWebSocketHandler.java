package nvt.project.smart_home.main.config.ws_handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.feature.device.sprinkler_system.mqtt_dto.response.SprinklerSystemMqttResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SprinklerSystemWebSocketHandler implements WebSocketHandler {

    private final Map<Long, Set<WebSocketSession>> sessions = new HashMap<>();
    private final WebSocketHandlerHelper webSocketHandlerHelper;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, String> params = webSocketHandlerHelper.extractParametersFromUri(session.getUri());
        if (params.containsKey("deviceId")) {
            long deviceId = Long.parseLong(params.get("deviceId"));
            if (!sessions.containsKey(deviceId)) {
                sessions.put(deviceId, new HashSet<>());
            }
            sessions.get(deviceId).add(session);
        } else {
            throw new RuntimeException("URI must contain query parameter \"deviceId\"");
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String messagePayload = message.getPayload().toString();
        SprinklerSystemMqttResponse response = objectMapper.readValue(messagePayload, SprinklerSystemMqttResponse.class);
        broadcastMessage(response.getId(), messagePayload);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {}

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        for (Long conversationId : sessions.keySet()) {
            if (sessions.get(conversationId).contains(session)) {
                sessions.get(conversationId).remove(session);
                break;
            }
        }
    }

    @Override
    public boolean supportsPartialMessages() { return false; }

    @SneakyThrows
    public void broadcastMessage(long deviceId, String messagePayload) {
        if (sessions.containsKey(deviceId)) {
            for (WebSocketSession wss : sessions.get(deviceId)) {
                wss.sendMessage(new TextMessage(messagePayload));
            }
        } else {
            throw new RuntimeException("No topic for deviceId=%d found".formatted(deviceId));
        }
    }
}
