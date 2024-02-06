package rs.ac.uns.ftn.nwt.simulator_server.dto.vehicle_gate.state;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleGateState {
    private long id;
    //private boolean isOpen;
    private boolean isAlwaysOpen;//
    private boolean isPrivateMode;//
    private List<String> allowedLicencePlates;//
    private List<String> licencePlatesIn;
}