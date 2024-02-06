package nvt.project.smart_home.main.feature.device.ambient_sensor.mapper;

import nvt.project.smart_home.main.feature.device.ambient_sensor.entity.AmbientSensorEntity;
import nvt.project.smart_home.main.feature.device.ambient_sensor.web_dto.request.AmbientSensorWebRequestDto;
import nvt.project.smart_home.main.feature.device.ambient_sensor.web_dto.response.AmbientSensorWebResponseDto;
import org.mapstruct.Mapper;

@Mapper
public interface AmbientSensorMapper {

    AmbientSensorWebResponseDto entityToResponseDto(AmbientSensorEntity entity);
    AmbientSensorEntity requestDtoToEntity(AmbientSensorWebRequestDto requestDto);

}
