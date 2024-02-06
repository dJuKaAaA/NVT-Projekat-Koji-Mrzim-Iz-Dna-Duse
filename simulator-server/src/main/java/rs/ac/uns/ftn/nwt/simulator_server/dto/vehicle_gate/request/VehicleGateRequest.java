package rs.ac.uns.ftn.nwt.simulator_server.dto.vehicle_gate.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleGateRequest {
    private long id;
    private boolean isAlwaysOpen;
    private boolean isPrivateMode;
    private List<String> allowedLicencePlates; // null if no changes
    private String triggeredBy;
}
