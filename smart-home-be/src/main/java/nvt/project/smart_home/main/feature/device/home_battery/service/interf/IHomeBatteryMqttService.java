package nvt.project.smart_home.main.feature.device.home_battery.service.interf;

import nvt.project.smart_home.main.core.influxdb.fluxResult.FluxResultDto;
import nvt.project.smart_home.main.feature.device.home_battery.mqtt_dto.HomeBatteryMqttDto;

import java.time.LocalDateTime;
import java.util.Collection;

public interface IHomeBatteryMqttService {
    void sendWsMessage(HomeBatteryMqttDto mqttResponse);
    Collection<FluxResultDto<Double>> getConsumedEnergy(Long propertyId, int minutesInPast, String powerConsumptionTypeString);
    Collection<FluxResultDto<Double>> getConsumedEnergy(Long propertyId, LocalDateTime startDate, LocalDateTime endDate, String powerConsumptionTypeString);
}
