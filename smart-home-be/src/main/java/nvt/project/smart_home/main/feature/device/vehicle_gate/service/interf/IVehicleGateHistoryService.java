package nvt.project.smart_home.main.feature.device.vehicle_gate.service.interf;

import nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.request.VehicleGateHistoryWebRequestDto;
import nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.response.VehicleGateActionsHistoryResponseDto;
import nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.response.VehicleGateInOutHistoryWebResponseDto;

import java.util.List;

public interface IVehicleGateHistoryService {
    List<VehicleGateActionsHistoryResponseDto> getActionsHistory(long deviceId, VehicleGateHistoryWebRequestDto request);
    List<VehicleGateInOutHistoryWebResponseDto> getInOutHistory(long deviceId,VehicleGateHistoryWebRequestDto request);
}
