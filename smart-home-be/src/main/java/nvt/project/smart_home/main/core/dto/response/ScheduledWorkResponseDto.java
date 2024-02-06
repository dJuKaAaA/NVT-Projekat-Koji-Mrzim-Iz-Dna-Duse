package nvt.project.smart_home.main.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledWorkResponseDto {

    public Long id;
    public String startTime;
    public String endTime;

}
