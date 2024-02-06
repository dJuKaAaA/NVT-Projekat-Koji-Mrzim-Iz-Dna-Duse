package nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nvt.project.smart_home.main.feature.device.air_conditioner.constants.AirConditionerCurrentWorkMode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AirCSetWorkModeWebRequestDto {

    @NotBlank(message = "Field canceledByEmail must not be blank")
    @Email(message = "Field canceledByEmail must be a valid email address")
    private String setByUserEmail;

    @NotNull(message = "Field workMode must not be null")
    private AirConditionerCurrentWorkMode workMode;
    private double wantedTemperature;
}
