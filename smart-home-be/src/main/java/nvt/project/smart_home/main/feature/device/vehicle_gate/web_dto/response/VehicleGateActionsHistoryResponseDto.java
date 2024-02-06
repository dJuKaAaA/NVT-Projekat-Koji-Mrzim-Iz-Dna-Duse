package nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleGateActionsHistoryResponseDto {
    private String triggeredBy;
    private String mode;
    private String command;
    private String timestamp;
}
