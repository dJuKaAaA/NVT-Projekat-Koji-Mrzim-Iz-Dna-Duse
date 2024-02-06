package nvt.project.smart_home.main.config.ws_handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.feature.device.home_battery.mqtt_dto.HomeBatteryMqttDto;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class HomeBatteryWebSocketHandler implements WebSocketHandler {

    private final Map<Long, Set<WebSocketSession>> sessions = new HashMap<>();
    private final WebSocketHandlerHelper webSocketHandlerHelper;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, String> params = webSocketHandlerHelper.extractParametersFromUri(session.getUri());
        if (params.containsKey("propertyId")) {
            long deviceId = Long.parseLong(params.get("propertyId"));
            if (!sessions.containsKey(deviceId)) {
                sessions.put(deviceId, new HashSet<>());
            }

            sessions.get(deviceId).add(session);
        } else {
            throw new RuntimeException("URI must contain query parameter \"propertyId\"");
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String messagePayload = message.getPayload().toString();
        HomeBatteryMqttDto response = objectMapper.readValue(messagePayload, HomeBatteryMqttDto.class);

        broadcastMessage(response.getPropertyId(), messagePayload);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    @SneakyThrows
    public void broadcastMessage(long propertyId, String messagePayload) {
        if (sessions.containsKey(propertyId)) {
            for (WebSocketSession wss : sessions.get(propertyId)) {
                wss.sendMessage(new TextMessage(messagePayload));
            }
        } else {
            System.out.println("\nNo topic for propertyId=%d found\n".formatted(propertyId));
        }
    }
}
