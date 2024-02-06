package nvt.project.smart_home.main.feature.device.ambient_sensor.service.interf;

import nvt.project.smart_home.main.feature.device.ambient_sensor.web_dto.request.AmbientSensorHistoryWebRequestDto;
import nvt.project.smart_home.main.feature.device.ambient_sensor.web_dto.response.AmbientSensorHistoryWebResponseDto;

import java.util.List;

public interface IAmbientSensorHistoryService {

    List<AmbientSensorHistoryWebResponseDto> getValues(long deviceId, AmbientSensorHistoryWebRequestDto requestDto);

}
