package nvt.project.smart_home.main.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SmartDeviceRefResponseDto {
    private Long id;
    private String name;
}
