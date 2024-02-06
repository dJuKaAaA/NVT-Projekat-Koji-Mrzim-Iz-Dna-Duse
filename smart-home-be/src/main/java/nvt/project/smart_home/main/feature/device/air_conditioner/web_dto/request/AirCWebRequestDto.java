package nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.request;

import lombok.*;
import lombok.experimental.SuperBuilder;
import nvt.project.smart_home.main.core.dto.request.SmartDeviceRequestDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirCWebRequestDto extends SmartDeviceRequestDto {

    private double maxTemperature;
    private double minTemperature;
}
