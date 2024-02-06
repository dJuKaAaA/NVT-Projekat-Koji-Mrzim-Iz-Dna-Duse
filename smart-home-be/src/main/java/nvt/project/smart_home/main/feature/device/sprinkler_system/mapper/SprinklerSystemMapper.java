package nvt.project.smart_home.main.feature.device.sprinkler_system.mapper;

import nvt.project.smart_home.main.core.mapper.ScheduledWorkMapper;
import nvt.project.smart_home.main.feature.device.sprinkler_system.entity.SprinklerSystemEntity;
import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.request.SprinklerSystemRequestWebDto;
import nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.response.SprinklerSystemResponseWebDto;
import org.mapstruct.Mapper;

@Mapper(uses = {ScheduledWorkMapper.class})
public interface SprinklerSystemMapper {

    SprinklerSystemResponseWebDto entityToResponseDto(SprinklerSystemEntity entity);
    SprinklerSystemEntity requestDtoToEntity(SprinklerSystemRequestWebDto requestDto);

}
