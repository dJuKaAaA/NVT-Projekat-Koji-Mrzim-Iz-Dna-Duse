package nvt.project.smart_home.main.feature.device.electric_vehicle_charger.service.interf;

import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.dto.request.ChargingVehicleRequestDto;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.dto.request.ElectricVehicleChargerRequestDto;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.dto.response.ElectricVehicleChargerResponseDto;

public interface IElectricVehicleChargerService {

    ElectricVehicleChargerResponseDto create(ElectricVehicleChargerRequestDto request);
    ElectricVehicleChargerResponseDto getById(long id);
    ElectricVehicleChargerResponseDto setChargeLimit(long id, double chargeLimit);
    ElectricVehicleChargerResponseDto setActive(long id, boolean active);
    ElectricVehicleChargerResponseDto startCharging(long id, ChargingVehicleRequestDto chargingVehicleRequest);
    ElectricVehicleChargerResponseDto stopCharging(long id, long chargingVehicleId);
    ElectricVehicleChargerResponseDto addPowerToVehicle(long id, long chargingVehicleId, double power);

}
