package nvt.project.smart_home.main.feature.device.solar_panel_system.mapper;

import nvt.project.smart_home.main.feature.device.solar_panel_system.entity.SolarPanelEntity;
import nvt.project.smart_home.main.feature.device.solar_panel_system.web_dto.request.SolarPanelRequestDto;
import nvt.project.smart_home.main.feature.device.solar_panel_system.web_dto.response.SolarPanelResponseDto;
import org.mapstruct.Mapper;

@Mapper
public interface SolarPanelMapper {

    SolarPanelResponseDto entityToResponseDto(SolarPanelEntity entity);
    SolarPanelEntity requestDtoToEntity(SolarPanelRequestDto requestDto);

}
