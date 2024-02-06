package nvt.project.smart_home.main.feature.device.electric_vehicle_charger.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import nvt.project.smart_home.main.core.dto.request.SmartDeviceRequestDto;

@Getter
@Setter
public class ElectricVehicleChargerRequestDto extends SmartDeviceRequestDto {

    @Min(value = 0, message = "Charge power cannot be negative!")
    private double chargePower;
    @Min(value = 1, message = "Must have at least one charger!")
    private int chargerCount;

}
