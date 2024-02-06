package nvt.project.smart_home.main.feature.device.vehicle_gate.web_dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import nvt.project.smart_home.main.core.dto.response.SmartDeviceResponseDto;
import nvt.project.smart_home.main.feature.device.vehicle_gate.constants.VehicleGateSystemCommand;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@SuperBuilder
public class VehicleGateWebResponseDto extends SmartDeviceResponseDto {
    private boolean isOpen;
    private boolean isAlwaysOpen;
    private boolean isPrivateMode;
    private String lastLicencePlateIn;
    private Date lastInDate;
    private String lastLicencePlateOut;
    private Date lastOutDate;
    private List<String> allowedLicencePlates;
    private VehicleGateSystemCommand lastInCommand;
}
