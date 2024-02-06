package nvt.project.smart_home.main.feature.device.solar_panel_system.web_dto.request;

import lombok.Getter;
import lombok.Setter;
import nvt.project.smart_home.main.core.dto.request.SmartDeviceRequestDto;

import java.util.List;

@Getter
@Setter
public class SolarPanelSystemRequestDto extends SmartDeviceRequestDto {

    private List<SolarPanelRequestDto> solarPanels;

}
