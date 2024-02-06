package nvt.project.smart_home.main.feature.online_offline.web_dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnlineOfflineWebResponse {
    private boolean failed;
    private String timestamp;
}
