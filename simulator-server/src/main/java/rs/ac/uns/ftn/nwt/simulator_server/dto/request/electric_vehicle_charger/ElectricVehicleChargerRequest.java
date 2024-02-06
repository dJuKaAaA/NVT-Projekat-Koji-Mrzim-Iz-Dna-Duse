package rs.ac.uns.ftn.nwt.simulator_server.dto.request.electric_vehicle_charger;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectricVehicleChargerRequest {
    private long id;
    private double chargeLimit;     // percentage (from 0.0 to 100.0)
    private double chargePower;
    private ElectricVehicleChargerCommand command;
    private ChargingVehicle chargingVehicle;
}
