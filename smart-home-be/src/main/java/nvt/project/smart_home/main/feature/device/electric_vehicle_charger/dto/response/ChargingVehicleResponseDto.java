package nvt.project.smart_home.main.feature.device.electric_vehicle_charger.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargingVehicleResponseDto {
    private Long id;
    private double currentPower;
    private double maxPower;
}
