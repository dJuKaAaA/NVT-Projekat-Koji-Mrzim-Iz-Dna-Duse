package nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.response;

import lombok.*;

import java.time.DayOfWeek;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SprinklingSystemScheduleWebResponseDto {
    private String startTime;
    private String endTime;
    private List<DayOfWeek> days;
}
