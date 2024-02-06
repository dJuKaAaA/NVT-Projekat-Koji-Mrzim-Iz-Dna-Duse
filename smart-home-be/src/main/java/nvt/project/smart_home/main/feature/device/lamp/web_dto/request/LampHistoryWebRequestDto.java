package nvt.project.smart_home.main.feature.device.lamp.web_dto.request;

import lombok.Getter;
import lombok.Setter;
import nvt.project.smart_home.main.feature.device.ambient_sensor.constants.PredefinedHistoryPeriod;

import java.time.LocalDateTime;

@Getter
@Setter
public class LampHistoryWebRequestDto {
    private PredefinedHistoryPeriod period;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
