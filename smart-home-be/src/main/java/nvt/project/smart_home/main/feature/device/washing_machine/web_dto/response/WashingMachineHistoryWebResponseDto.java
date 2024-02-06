package nvt.project.smart_home.main.feature.device.washing_machine.web_dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WashingMachineHistoryWebResponseDto {
        String executor;
        private String action;
        private String timestamp;
}
