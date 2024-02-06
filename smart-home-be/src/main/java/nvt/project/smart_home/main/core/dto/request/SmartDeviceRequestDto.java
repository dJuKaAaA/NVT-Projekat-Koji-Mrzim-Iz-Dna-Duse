package nvt.project.smart_home.main.core.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmartDeviceRequestDto {

    @NotBlank(message = "Name cannot be blank!")
    @Length(max = 100, message = "Name cannot surpass 100 characters!")
    private String name;

    @NotNull(message = "Property not assigned to device!")
    private Long propertyId;

    private boolean usesBatteries;

    private ImageRequestDto image;

    @Min(value = 0, message = "Power consumption cannot be a negative number!")
    private double powerConsumption;

}
