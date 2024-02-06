package nvt.project.smart_home.main.feature.device.electric_vehicle_charger.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import nvt.project.smart_home.main.core.dto.response.SmartDeviceResponseDto;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public class ElectricVehicleChargerResponseDto extends SmartDeviceResponseDto {

    private List<ChargingVehicleResponseDto> vehiclesCharging;
    private double chargePower;
    private int chargerCount;
    private int chargersOccupied;
    private int chargeLimit;
}
