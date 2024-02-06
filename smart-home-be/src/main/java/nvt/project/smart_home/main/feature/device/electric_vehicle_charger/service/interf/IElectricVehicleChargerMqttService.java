package nvt.project.smart_home.main.feature.device.electric_vehicle_charger.service.interf;

import nvt.project.smart_home.main.core.influxdb.fluxResult.FluxResultWithTagsDto;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.mqtt_dto.response.ElectricVehicleChargerMqttResponse;

import java.time.LocalDateTime;
import java.util.Collection;

public interface IElectricVehicleChargerMqttService {
    void sendWsMessage(ElectricVehicleChargerMqttResponse mqttResponse);
    Collection<FluxResultWithTagsDto<Integer>> getAllActions(long deviceId, LocalDateTime startDate, LocalDateTime endDate);
    Collection<FluxResultWithTagsDto<Integer>> getActionsByUser(long deviceId, long userId, LocalDateTime startDate, LocalDateTime endDate);
}
