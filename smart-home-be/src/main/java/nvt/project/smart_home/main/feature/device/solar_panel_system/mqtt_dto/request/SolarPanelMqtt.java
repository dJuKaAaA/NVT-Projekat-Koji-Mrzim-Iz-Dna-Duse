package nvt.project.smart_home.main.feature.device.solar_panel_system.mqtt_dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolarPanelMqtt {
    private double area;
    private double efficiency;
}
