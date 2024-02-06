package nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import nvt.project.smart_home.main.core.dto.response.SmartDeviceResponseDto;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public class AirCWebResponseDto extends SmartDeviceResponseDto {

    private double maxTemperature;
    private double minTemperature;
    private Double currentWorkTemperature;
    private String workMode;
    private List<AirCWorkAppointmentWebResponseDto> workPlan;

}
