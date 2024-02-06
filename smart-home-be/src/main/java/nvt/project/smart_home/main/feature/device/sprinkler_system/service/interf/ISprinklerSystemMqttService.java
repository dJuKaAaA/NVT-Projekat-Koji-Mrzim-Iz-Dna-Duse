package nvt.project.smart_home.main.feature.device.sprinkler_system.service.interf;

import nvt.project.smart_home.main.feature.device.sprinkler_system.entity.SprinklerSystemEntity;
import nvt.project.smart_home.main.feature.device.sprinkler_system.mqtt_dto.response.SprinklerSystemMqttResponse;

public interface ISprinklerSystemMqttService {
    void sendWsMessage(SprinklerSystemMqttResponse response);
}
