package rs.ac.uns.ftn.nwt.simulator_server.dto.request.vehicle_gate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.vehicle_gate.constants.VehicleGateCommand;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.vehicle_gate.constants.VehicleStatus;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleGateRequest {
    private long id;
    private boolean isOpened;
    private boolean privateMode;
    private List<String> allowedLicencePlates;
    private String lastPlate;
}
