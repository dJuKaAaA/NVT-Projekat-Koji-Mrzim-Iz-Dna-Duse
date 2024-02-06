package nvt.project.smart_home.main.feature.device.electric_vehicle_charger.mapper;

import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.dto.request.ElectricVehicleChargerRequestDto;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.dto.response.ElectricVehicleChargerResponseDto;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.entity.ElectricVehicleChargerEntity;
import org.mapstruct.Mapper;

@Mapper(uses = {ChargingVehicleMapper.class})
public interface ElectricVehicleChargerMapper {

    ElectricVehicleChargerResponseDto entityToResponseDto(ElectricVehicleChargerEntity entity);
    ElectricVehicleChargerEntity requestDtoToEntity(ElectricVehicleChargerRequestDto requestDto);

}
