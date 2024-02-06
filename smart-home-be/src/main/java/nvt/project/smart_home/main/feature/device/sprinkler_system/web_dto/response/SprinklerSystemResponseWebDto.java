package nvt.project.smart_home.main.feature.device.sprinkler_system.web_dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import nvt.project.smart_home.main.core.dto.response.SmartDeviceResponseDto;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public class SprinklerSystemResponseWebDto extends SmartDeviceResponseDto {
    private boolean systemOn;
    private List<SprinklingSystemScheduleWebResponseDto> schedule;
}
