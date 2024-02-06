package nvt.project.smart_home.main.feature.device.lamp.web_dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LampValueHistoryWebResponseDto {
    private double value;
    private String timestamp;
}
