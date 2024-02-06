package rs.ac.uns.ftn.nwt.simulator_server.dto.response;

import lombok.*;
import rs.ac.uns.ftn.nwt.simulator_server.dto.request.electric_vehicle_charger.ChargingVehicle;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElectricVehicleChargerResponse {
    private Long id;
    private ChargingVehicle chargingVehicle;
    private double chargeAmount;
    private Date timestamp;
}
