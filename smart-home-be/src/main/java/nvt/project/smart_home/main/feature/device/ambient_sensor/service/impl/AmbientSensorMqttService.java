package nvt.project.smart_home.main.feature.device.ambient_sensor.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.config.ws_handler.AmbientSensorWebSocketHandler;
import nvt.project.smart_home.main.feature.device.ambient_sensor.mqtt_dto.response.AmbientSensorMqttResponse;
import nvt.project.smart_home.main.feature.device.ambient_sensor.service.interf.IAmbientSensorMqttService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AmbientSensorMqttService implements IAmbientSensorMqttService {

    private final AmbientSensorWebSocketHandler ambientSensorWebSocketHandler;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public void sendWsMessage(AmbientSensorMqttResponse mqttResponse) {
        String message = objectMapper.writeValueAsString(mqttResponse);
        ambientSensorWebSocketHandler.broadcastMessage(mqttResponse.getId(), message);
    }

}
