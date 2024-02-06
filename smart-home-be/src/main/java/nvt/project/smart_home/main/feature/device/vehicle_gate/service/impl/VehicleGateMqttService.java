package nvt.project.smart_home.main.feature.device.vehicle_gate.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.config.ws_handler.VehicleGateWebSocketHandler;
import nvt.project.smart_home.main.feature.device.vehicle_gate.entity.VehicleGateEntity;
import nvt.project.smart_home.main.feature.device.vehicle_gate.mqtt_dto.request.VehicleGateMqttRequest;
import nvt.project.smart_home.main.feature.device.vehicle_gate.mqtt_dto.response.VehicleGateMqttResponse;
import nvt.project.smart_home.main.feature.device.vehicle_gate.service.interf.IVehicleGateMqttService;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Service;

import static nvt.project.smart_home.main.core.constant.topics.ReceiveTopicsConstants.RECEIVE_VEHICLE_GATE_TOPIC;
import static nvt.project.smart_home.main.core.constant.topics.SendTopicConstants.SEND_VEHICLE_GATE_TOPIC;

@Service
@RequiredArgsConstructor
public class VehicleGateMqttService implements IVehicleGateMqttService {

    private final VehicleGateWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public void sendWsMessage(VehicleGateMqttResponse response) {
        String message = objectMapper.writeValueAsString(response);
        webSocketHandler.broadcastMessage(response.getId(), message);
    }

}
