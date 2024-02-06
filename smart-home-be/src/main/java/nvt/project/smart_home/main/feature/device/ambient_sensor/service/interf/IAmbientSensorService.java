package nvt.project.smart_home.main.feature.device.ambient_sensor.service.interf;

import lombok.SneakyThrows;
import nvt.project.smart_home.main.feature.device.ambient_sensor.web_dto.request.AmbientSensorWebRequestDto;
import nvt.project.smart_home.main.feature.device.ambient_sensor.web_dto.response.AmbientSensorWebResponseDto;

public interface IAmbientSensorService {

    @SneakyThrows
    AmbientSensorWebResponseDto create(AmbientSensorWebRequestDto request);

    AmbientSensorWebResponseDto getById(Long id);
    AmbientSensorWebResponseDto setActivity(Long id, boolean active);

}
