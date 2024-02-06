package nvt.project.smart_home.main.feature.device.solar_panel_system.web_dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolarPanelRequestDto {

    private double area;
    private double efficiency;

}
