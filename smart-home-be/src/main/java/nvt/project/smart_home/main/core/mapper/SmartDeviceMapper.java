package nvt.project.smart_home.main.core.mapper;

import nvt.project.smart_home.main.core.dto.request.SmartDeviceRequestDto;
import nvt.project.smart_home.main.core.dto.response.SmartDeviceResponseDto;
import nvt.project.smart_home.main.core.entity.SmartDeviceEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = {ImageMapper.class})
public interface SmartDeviceMapper {

    SmartDeviceEntity requestDtoToEntity(SmartDeviceRequestDto requestDto);
    SmartDeviceResponseDto entityToResponseDto(SmartDeviceEntity entity);

    List<SmartDeviceResponseDto> entitiesToResponseDtos(List<SmartDeviceEntity> entities);
}
