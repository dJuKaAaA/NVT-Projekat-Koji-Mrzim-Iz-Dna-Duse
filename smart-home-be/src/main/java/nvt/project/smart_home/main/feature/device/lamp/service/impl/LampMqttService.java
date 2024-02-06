package nvt.project.smart_home.main.feature.device.lamp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.config.ws_handler.LampWebSocketHandler;
import nvt.project.smart_home.main.feature.device.lamp.mqtt_dto.response.LampMqttResponse;
import nvt.project.smart_home.main.feature.device.lamp.service.interf.ILampMqttService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LampMqttService implements ILampMqttService {

    private final LampWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public void sendWsMessage(LampMqttResponse response) {
        String message = objectMapper.writeValueAsString(response);
        webSocketHandler.broadcastMessage(response.getId(), message);
    }
}
