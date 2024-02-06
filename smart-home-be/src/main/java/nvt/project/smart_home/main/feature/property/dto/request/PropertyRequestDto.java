package nvt.project.smart_home.main.feature.property.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import nvt.project.smart_home.main.core.dto.CityDto;
import nvt.project.smart_home.main.core.dto.request.ImageRequestDto;
import nvt.project.smart_home.main.feature.property.constant.PropertyType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyRequestDto {
    @NotBlank(message = "Name not provided!") private String name;
    @NotBlank private String ownerEmail;
    @Min(value = 1, message = "Must be more than 0!") private Integer floors;
    @Min(value = 1, message = "Must be more than 0!") private Double area;
    private Double longitude;
    private Double latitude;
    @NotBlank(message = "Address not provided!") private String address;
    @NotNull private CityDto city;
    private PropertyType type;
    @NotNull private ImageRequestDto image;
}
