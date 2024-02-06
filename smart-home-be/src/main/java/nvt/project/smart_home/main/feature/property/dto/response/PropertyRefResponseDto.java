package nvt.project.smart_home.main.feature.property.dto.response;

import lombok.*;
import nvt.project.smart_home.main.feature.property.entity.Property;

@Getter
@Setter
@Builder
public class PropertyRefResponseDto {
    private Long id;
    private String name;
    private String address;

}
