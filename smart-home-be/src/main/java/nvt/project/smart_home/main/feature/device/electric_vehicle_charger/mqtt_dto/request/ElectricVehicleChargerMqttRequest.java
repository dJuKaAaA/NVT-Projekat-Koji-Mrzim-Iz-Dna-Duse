package nvt.project.smart_home.main.feature.device.electric_vehicle_charger.mqtt_dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectricVehicleChargerMqttRequest {
    private long id;
    private double chargeLimit;     // percentage (from 0.0 to 100.0)
    private double chargePower;
    private ElectricVehicleChargerCommand command;
    private ChargingVehicleMqtt chargingVehicle;
}
