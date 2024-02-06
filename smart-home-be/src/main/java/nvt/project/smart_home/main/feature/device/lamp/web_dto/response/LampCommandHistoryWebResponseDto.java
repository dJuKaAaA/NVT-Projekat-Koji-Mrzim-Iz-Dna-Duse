package nvt.project.smart_home.main.feature.device.lamp.web_dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LampCommandHistoryWebResponseDto {
    private String command;
    private String triggeredBy;
    private String mode;
    private String timestamp;
}
