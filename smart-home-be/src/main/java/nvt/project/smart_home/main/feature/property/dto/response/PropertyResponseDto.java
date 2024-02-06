package nvt.project.smart_home.main.feature.property.dto.response;

import lombok.*;
import nvt.project.smart_home.main.core.dto.CityDto;
import nvt.project.smart_home.main.core.dto.request.ImageRequestDto;
import nvt.project.smart_home.main.feature.property.constant.PropertyStatus;
import nvt.project.smart_home.main.feature.property.constant.PropertyType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyResponseDto {
    private Long id;
    private String name;
    private String ownerEmail;
    private Integer floors;
    private Double area;
    private String address;
    private CityDto city;
    private PropertyType type;
    private ImageRequestDto image;
    private PropertyStatus status;
}
