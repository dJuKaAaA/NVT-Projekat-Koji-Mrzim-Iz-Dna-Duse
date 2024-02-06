package nvt.project.smart_home.main.feature.device.home_battery.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import nvt.project.smart_home.main.core.dto.request.SmartDeviceRequestDto;

@Getter
@Setter
public class HomeBatteryRequestDto extends SmartDeviceRequestDto {

    @Min(value = 0, message = "Capacity cannot be a negative number!")
    private double capacity;

}
