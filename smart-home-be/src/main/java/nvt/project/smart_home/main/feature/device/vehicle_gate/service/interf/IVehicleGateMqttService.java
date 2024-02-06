package nvt.project.smart_home.main.feature.device.vehicle_gate.service.interf;

import nvt.project.smart_home.main.feature.device.vehicle_gate.entity.VehicleGateEntity;
import nvt.project.smart_home.main.feature.device.vehicle_gate.mqtt_dto.response.VehicleGateMqttResponse;

public interface IVehicleGateMqttService {
    void sendWsMessage(VehicleGateMqttResponse response);
}
