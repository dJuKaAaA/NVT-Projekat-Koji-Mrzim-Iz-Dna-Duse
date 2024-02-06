package rs.ac.uns.ftn.nwt.simulator_server.dto.vehicle_gate.response;

import lombok.*;
import rs.ac.uns.ftn.nwt.simulator_server.dto.vehicle_gate.constants.VehicleGateCommand;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleGateResponse {
    private Long id;
    private boolean isOpen;
    private boolean isAlwaysOpen;
    private boolean isPrivateMode;
    private String plate;
    private VehicleGateCommand command;
    private Date timestamp;
    private String triggeredBy;
}
