package nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SprinklerSystemHistoryResponseWebDto {
    private String status;
    private String triggeredBy;
    private String timestamp;
}
