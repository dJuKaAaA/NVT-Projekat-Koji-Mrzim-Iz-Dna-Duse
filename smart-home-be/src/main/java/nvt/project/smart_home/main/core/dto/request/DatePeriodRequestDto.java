package nvt.project.smart_home.main.core.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DatePeriodRequestDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
