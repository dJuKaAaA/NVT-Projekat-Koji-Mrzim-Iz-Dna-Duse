package nvt.project.smart_home.main.feature.device.electric_vehicle_charger.mqtt_dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.mqtt_dto.request.ChargingVehicleMqtt;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElectricVehicleChargerMqttResponse {
    private Long id;
    private ChargingVehicleMqtt chargingVehicle;
    private double chargeAmount;
    private Date timestamp;
}
