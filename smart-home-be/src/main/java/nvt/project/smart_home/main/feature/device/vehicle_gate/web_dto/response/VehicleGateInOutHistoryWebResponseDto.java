package nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleGateInOutHistoryWebResponseDto {
    private boolean isVehicleIn;
    private String timestamp;
}
