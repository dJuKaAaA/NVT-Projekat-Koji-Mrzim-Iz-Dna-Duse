package nvt.project.smart_home.main.feature.online_offline.web_dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nvt.project.smart_home.main.feature.device.ambient_sensor.constants.PredefinedHistoryPeriod;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnlineOfflineWebRequest {
    private PredefinedHistoryPeriod period;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
