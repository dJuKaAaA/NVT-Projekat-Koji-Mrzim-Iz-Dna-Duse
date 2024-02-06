package nvt.project.smart_home.main.feature.device.washing_machine.web_dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import nvt.project.smart_home.main.core.dto.response.SmartDeviceResponseDto;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WashingMachineWebResponseDto extends SmartDeviceResponseDto {

    private String workMode;
    private List<WashingMachineWorkAppointmentWebResponseDto> workPlan;

}
