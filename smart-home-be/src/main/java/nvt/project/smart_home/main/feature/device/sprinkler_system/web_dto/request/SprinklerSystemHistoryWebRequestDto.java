package nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.request;

import lombok.*;
import nvt.project.smart_home.main.feature.device.ambient_sensor.constants.PredefinedHistoryPeriod;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SprinklerSystemHistoryWebRequestDto {
    private PredefinedHistoryPeriod period;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String triggeredBy;
}
