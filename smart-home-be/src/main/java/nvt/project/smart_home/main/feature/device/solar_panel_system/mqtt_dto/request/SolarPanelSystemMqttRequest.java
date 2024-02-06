package nvt.project.smart_home.main.feature.device.solar_panel_system.mqtt_dto.request;

import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolarPanelSystemMqttRequest {
    private long id;
    private SolarPanelSystemCommand command;
    private Collection<SolarPanelMqtt> panels = new ArrayList<>();
    private double latitude;
    private double longitude;
}
