package nvt.project.smart_home.main.feature.device.ambient_sensor.web_dto.request;

import lombok.Getter;
import lombok.Setter;
import nvt.project.smart_home.main.feature.device.ambient_sensor.constants.PredefinedHistoryPeriod;

import java.time.LocalDateTime;

@Getter
@Setter
public class AmbientSensorHistoryWebRequestDto {

    private PredefinedHistoryPeriod period;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

}
