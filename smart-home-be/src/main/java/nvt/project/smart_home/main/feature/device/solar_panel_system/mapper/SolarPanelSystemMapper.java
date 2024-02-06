package nvt.project.smart_home.main.feature.device.solar_panel_system.mapper;

import nvt.project.smart_home.main.feature.device.solar_panel_system.entity.SolarPanelSystemEntity;
import nvt.project.smart_home.main.feature.device.solar_panel_system.web_dto.request.SolarPanelSystemRequestDto;
import nvt.project.smart_home.main.feature.device.solar_panel_system.web_dto.response.SolarPanelSystemResponseDto;
import org.mapstruct.Mapper;

@Mapper(uses = {SolarPanelMapper.class})
public interface SolarPanelSystemMapper {

    SolarPanelSystemResponseDto entityToResponseDto(SolarPanelSystemEntity entity);

    SolarPanelSystemEntity requestDtoToEntity(SolarPanelSystemRequestDto requestDto);

}
