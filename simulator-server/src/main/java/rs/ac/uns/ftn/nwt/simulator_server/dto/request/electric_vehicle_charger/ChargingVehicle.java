package rs.ac.uns.ftn.nwt.simulator_server.dto.request.electric_vehicle_charger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingVehicle {
    private Long id;
    private double currentPower;
    private double maxPower;
}
