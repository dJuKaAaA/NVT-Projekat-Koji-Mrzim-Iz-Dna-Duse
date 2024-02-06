package nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nvt.project.smart_home.main.feature.device.ambient_sensor.constants.PredefinedHistoryPeriod;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleGateHistoryWebRequestDto {
    private PredefinedHistoryPeriod period;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String triggeredBy;
    private String mode;
}
