package nvt.project.smart_home.main.feature.device.electric_vehicle_charger.mqtt_dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingVehicleMqtt {
    private Long id;
    private double currentPower;
    private double maxPower;
}
