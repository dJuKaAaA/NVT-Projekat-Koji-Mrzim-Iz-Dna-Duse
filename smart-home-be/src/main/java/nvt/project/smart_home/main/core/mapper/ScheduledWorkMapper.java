package nvt.project.smart_home.main.core.mapper;

import nvt.project.smart_home.main.core.dto.request.ScheduledWorkRequestDto;
import nvt.project.smart_home.main.core.dto.response.ScheduledWorkResponseDto;
import nvt.project.smart_home.main.core.entity.ScheduledWorkEntity;
import org.mapstruct.Mapper;

@Mapper
public interface ScheduledWorkMapper {

    ScheduledWorkEntity requestDtoToEntity(ScheduledWorkRequestDto requestDto);
    ScheduledWorkResponseDto entityToResponseDto(ScheduledWorkEntity entity);

}
