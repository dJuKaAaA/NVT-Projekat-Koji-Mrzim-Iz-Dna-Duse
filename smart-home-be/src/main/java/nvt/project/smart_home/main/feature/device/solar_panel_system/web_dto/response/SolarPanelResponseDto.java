package nvt.project.smart_home.main.feature.device.solar_panel_system.web_dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolarPanelResponseDto {

    private Long id;
    private double area;
    private double efficiency;

}
