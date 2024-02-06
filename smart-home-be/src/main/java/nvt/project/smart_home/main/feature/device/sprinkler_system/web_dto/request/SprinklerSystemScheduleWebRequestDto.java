package nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.DayOfWeek;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SprinklerSystemScheduleWebRequestDto {
    @NotBlank(message = "Start date cannot be blank!")
    private String startTime;

    @NotBlank(message = "End date cannot be blank!")
    private String endTime;

    private List<DayOfWeek> days;
}
