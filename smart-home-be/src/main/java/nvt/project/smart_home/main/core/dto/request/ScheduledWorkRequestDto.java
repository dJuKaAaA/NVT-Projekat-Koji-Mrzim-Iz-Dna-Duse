package nvt.project.smart_home.main.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledWorkRequestDto {

    // TODO: Add pattern for start and end date times
    @NotBlank(message = "Start date cannot be blank!")
    private String startTime;

    @NotBlank(message = "End date cannot be blank!")
    private String endTime;

}
