package nvt.project.smart_home.main.feature.device.lamp.web_dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import nvt.project.smart_home.main.core.dto.response.ImageResponseDto;
import nvt.project.smart_home.main.core.dto.response.SmartDeviceResponseDto;

@Getter
@Setter
@SuperBuilder
public class LampWebResponseDto extends SmartDeviceResponseDto {
    private double lightLevel;
    private boolean autoModeOn;
    private boolean bulbOn;
    private ImageResponseDto image;
}
