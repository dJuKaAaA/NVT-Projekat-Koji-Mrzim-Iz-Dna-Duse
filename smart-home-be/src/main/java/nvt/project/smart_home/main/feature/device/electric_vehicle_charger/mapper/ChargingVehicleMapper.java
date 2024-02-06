package nvt.project.smart_home.main.feature.device.electric_vehicle_charger.mapper;

import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.dto.request.ChargingVehicleRequestDto;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.dto.response.ChargingVehicleResponseDto;
import nvt.project.smart_home.main.feature.device.electric_vehicle_charger.entity.ChargingVehicleEntity;
import org.mapstruct.Mapper;

@Mapper
public interface ChargingVehicleMapper {

    ChargingVehicleResponseDto entityToResponseDto(ChargingVehicleEntity entity);

    ChargingVehicleEntity requestDtoToEntity(ChargingVehicleRequestDto requestDto);

}
