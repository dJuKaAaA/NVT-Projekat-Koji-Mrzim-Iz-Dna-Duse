package nvt.project.smart_home.main.feature.device.vehicle_gate.mqtt_dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleGateMqttRequest {
    private long id;
    private boolean isAlwaysOpen;
    private boolean isPrivateMode;
    private List<String> allowedLicencePlates; // null if no changes
    private String triggeredBy;
}
