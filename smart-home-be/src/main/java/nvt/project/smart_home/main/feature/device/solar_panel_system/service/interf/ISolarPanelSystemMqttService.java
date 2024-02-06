package nvt.project.smart_home.main.feature.device.solar_panel_system.service.interf;

import nvt.project.smart_home.main.core.influxdb.fluxResult.FluxResultDto;
import nvt.project.smart_home.main.feature.device.solar_panel_system.mqtt_dto.response.SolarPanelSystemMqttResponse;

import java.time.LocalDateTime;
import java.util.Collection;

public interface ISolarPanelSystemMqttService {
    void sendWsMessage(SolarPanelSystemMqttResponse mqttResponse);
    Collection<FluxResultDto<Integer>> getActions(long id, String userEmail, LocalDateTime startDate, LocalDateTime endDate);
    Collection<FluxResultDto<Double>> getEnergyProduced(long id, int minutesInPast);
}
