package nvt.project.smart_home.main.feature.device.home_battery.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import nvt.project.smart_home.main.core.dto.response.SmartDeviceResponseDto;

@Getter
@Setter
@SuperBuilder
public class HomeBatteryResponseDto extends SmartDeviceResponseDto {

    private double capacity;

}
