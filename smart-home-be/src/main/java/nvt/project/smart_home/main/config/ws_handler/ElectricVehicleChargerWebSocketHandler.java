package nvt.project.smart_home.main.config.ws_handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.config.ws_handler.WebSocketHandlerHelper;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.mqtt_dto.response.ElectricVehicleChargerMqttResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class ElectricVehicleChargerWebSocketHandler implements WebSocketHandler {

    private final Map<String, Set<WebSocketSession>> sessions = new HashMap<>();
    private final WebSocketHandlerHelper webSocketHandlerHelper;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, String> params = webSocketHandlerHelper.extractParametersFromUri(session.getUri());
        if (params.containsKey("deviceId") && params.containsKey("chargingVehicleId")) {
            String id = params.get("deviceId") + "_" + params.get("chargingVehicleId");
            if (!sessions.containsKey(id)) {
                sessions.put(id, new HashSet<>());
            }

            sessions.get(id).add(session);
        } else {
            throw new RuntimeException("URI must contain query parameter \"deviceId\" and \"chargingVehicleId\"");
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String messagePayload = message.getPayload().toString();
        ElectricVehicleChargerMqttResponse response = objectMapper.readValue(messagePayload, ElectricVehicleChargerMqttResponse.class);

        broadcastMessage(response.getId() + "_" + response.getChargingVehicle().getId(), messagePayload);
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
    public void broadcastMessage(String id, String messagePayload) {
        if (sessions.containsKey(id)) {
            for (WebSocketSession wss : sessions.get(id)) {
                wss.sendMessage(new TextMessage(messagePayload));
            }
        } else {
            String[] splitId = id.split("_");
            throw new RuntimeException("No topic for deviceId=%s and chargingVehicleId=%s found".formatted(splitId[0], splitId[1]));
        }
    }
}
