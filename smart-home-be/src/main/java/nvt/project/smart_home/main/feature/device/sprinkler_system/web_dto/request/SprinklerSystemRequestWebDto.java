package nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.request;

import lombok.Getter;
import lombok.Setter;
import nvt.project.smart_home.main.core.dto.request.SmartDeviceRequestDto;

import java.util.List;

@Getter
@Setter
public class SprinklerSystemRequestWebDto extends SmartDeviceRequestDto {
    private List<SprinklerSystemScheduleWebRequestDto> schedule;
}
