package nvt.project.smart_home.main.feature.device.solar_panel_system.web_dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import nvt.project.smart_home.main.core.dto.response.SmartDeviceResponseDto;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public class SolarPanelSystemResponseDto extends SmartDeviceResponseDto {

    private List<SolarPanelResponseDto> solarPanels;

}
