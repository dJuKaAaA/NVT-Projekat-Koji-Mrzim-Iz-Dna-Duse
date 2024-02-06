package nvt.project.smart_home.main.feature.device.lamp.mapper;

import nvt.project.smart_home.main.feature.device.lamp.entity.LampEntity;
import nvt.project.smart_home.main.feature.device.lamp.web_dto.request.LampWebRequestDto;
import nvt.project.smart_home.main.feature.device.lamp.web_dto.response.LampWebResponseDto;
import org.mapstruct.Mapper;

@Mapper
public interface LampMapper {

    LampWebResponseDto entityToResponseDto(LampEntity entity);
    LampEntity requestDtoToEntity(LampWebRequestDto requestDto);

}

