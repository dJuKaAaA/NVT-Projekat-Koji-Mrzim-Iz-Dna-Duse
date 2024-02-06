package nvt.project.smart_home.main.feature.device.vehicle_gate.mapper;

import nvt.project.smart_home.main.feature.device.vehicle_gate.entity.VehicleGateEntity;
import nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.request.VehicleGateWebRequestDto;
import nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.response.VehicleGateWebResponseDto;
import org.mapstruct.Mapper;

@Mapper
public interface VehicleGateMapper {

    VehicleGateWebResponseDto entityToResponseDto(VehicleGateEntity entity);
    VehicleGateEntity requestDtoToEntity(VehicleGateWebRequestDto requestDto);

}
