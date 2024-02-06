package nvt.project.smart_home.main.feature.device.air_conditioner.service.interf;

import nvt.project.smart_home.main.feature.device.air_conditioner.mqtt_dto.response.AirConditionerMqttResponse;

public interface IAirConditionerMqttService {
    void handleMqttResponse(AirConditionerMqttResponse response);
}
