package nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.request;

import lombok.Getter;
import lombok.Setter;
import nvt.project.smart_home.main.core.dto.request.SmartDeviceRequestDto;

import java.util.List;

@Getter
@Setter
public class VehicleGateWebRequestDto extends SmartDeviceRequestDto {
    private List<String> allowedLicencePlates;
}