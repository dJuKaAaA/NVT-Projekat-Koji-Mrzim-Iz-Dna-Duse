package nvt.project.smart_home.main.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SmartDeviceResponseDto {

    private Long id;
    private String name;
    private String groupType;
    private boolean deviceActive;
    private boolean usesBatteries;
    private double powerConsumption;
    private String deviceType;
    private ImageResponseDto image;

}
