package nvt.project.smart_home.main.feature.device.vehicle_gate.mqtt_dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nvt.project.smart_home.main.feature.device.vehicle_gate.constants.VehicleGateSystemCommand;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleGateMqttResponse {
    private Long id;
    private boolean isOpen;
    private boolean isAlwaysOpen;
    private boolean isPrivateMode;
    private String plate;
    private VehicleGateSystemCommand command;
    private Date timestamp;
    private String triggeredBy;
}
