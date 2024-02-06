package nvt.project.smart_home.main.feature.device.washing_machine.service.interf;

import nvt.project.smart_home.main.feature.device.washing_machine.mqtt_dto.response.WashingMachineMqttResponse;

public interface IWashingMachineMqttService {
    void handleMqttResponse(WashingMachineMqttResponse response);
}
