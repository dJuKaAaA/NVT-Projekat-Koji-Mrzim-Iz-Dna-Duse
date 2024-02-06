package nvt.project.smart_home.main.feature.device.ambient_sensor.service.interf;

import nvt.project.smart_home.main.feature.device.ambient_sensor.mqtt_dto.response.AmbientSensorMqttResponse;

public interface IAmbientSensorMqttService {

    void sendWsMessage(AmbientSensorMqttResponse mqttResponse);
}
