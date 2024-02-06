package rs.ac.uns.ftn.nwt.simulator_server.dto.response;

import lombok.*;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.vehicle_gate.constants.VehicleStatus;

import java.util.Date;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleGateResponse {
    private Long id;
    private boolean isActive;
    private String lastPlate;
    private String plateOut = null;
    private VehicleStatus vehicleStatus = null;
    private Date timestamp;
}
