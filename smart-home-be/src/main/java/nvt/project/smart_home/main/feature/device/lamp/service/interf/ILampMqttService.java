package nvt.project.smart_home.main.feature.device.lamp.service.interf;

import nvt.project.smart_home.main.feature.device.lamp.mqtt_dto.response.LampMqttResponse;

public interface ILampMqttService {
    void sendWsMessage(LampMqttResponse response);
}
