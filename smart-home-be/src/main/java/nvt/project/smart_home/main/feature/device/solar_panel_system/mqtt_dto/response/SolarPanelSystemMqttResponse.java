package nvt.project.smart_home.main.feature.device.solar_panel_system.mqtt_dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolarPanelSystemMqttResponse {
    private Long id;
    private double energy;  // kW
    private Date timestamp;
}
