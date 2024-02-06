package nvt.project.smart_home.main.feature.device.electric_vehicle_charger.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargingVehicleRequestDto {

    @Min(value = 0, message = "Current power cannot be negative!")
    private double currentPower;
    @Min(value = 0, message = "Max power cannot be negative!")
    private double maxPower;

}
