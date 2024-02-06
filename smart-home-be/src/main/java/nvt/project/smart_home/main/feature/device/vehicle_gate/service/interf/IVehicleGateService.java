package nvt.project.smart_home.main.feature.device.vehicle_gate.service.interf;

import nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.request.VehicleGateWebRequestDto;
import nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.response.VehicleGateWebResponseDto;

import java.util.List;

public interface IVehicleGateService {

    VehicleGateWebResponseDto create(VehicleGateWebRequestDto request);
    VehicleGateWebResponseDto changeIsAlwaysOpen(Long id, boolean isOpen, String triggeredBy);
    VehicleGateWebResponseDto setMode(Long id, boolean isPrivate, String triggeredBy);
    VehicleGateWebResponseDto getById(Long id);
    VehicleGateWebResponseDto setAllowedLicencePlate(Long id, List<String> licencePlate);
}
