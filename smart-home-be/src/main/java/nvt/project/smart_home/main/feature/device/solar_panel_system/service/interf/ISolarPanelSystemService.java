package nvt.project.smart_home.main.feature.device.solar_panel_system.service.interf;

import nvt.project.smart_home.main.feature.device.solar_panel_system.web_dto.request.SolarPanelRequestDto;
import nvt.project.smart_home.main.feature.device.solar_panel_system.web_dto.request.SolarPanelSystemRequestDto;
import nvt.project.smart_home.main.feature.device.solar_panel_system.web_dto.response.SolarPanelSystemResponseDto;

public interface ISolarPanelSystemService {

    SolarPanelSystemResponseDto create(SolarPanelSystemRequestDto request);
    SolarPanelSystemResponseDto addPanel(Long id, SolarPanelRequestDto panelRequest);
    SolarPanelSystemResponseDto removePanel(Long id, Long panelId);
    SolarPanelSystemResponseDto getById(Long id);
    SolarPanelSystemResponseDto setActive(Long id, boolean active);
}
