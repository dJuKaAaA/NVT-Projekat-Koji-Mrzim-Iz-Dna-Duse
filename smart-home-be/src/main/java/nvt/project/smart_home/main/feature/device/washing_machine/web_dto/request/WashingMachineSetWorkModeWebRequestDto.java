package nvt.project.smart_home.main.feature.device.washing_machine.web_dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import nvt.project.smart_home.main.feature.device.washing_machine.constants.WashingMachineCurrentWorkMode;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WashingMachineSetWorkModeWebRequestDto {
    @NotBlank(message = "Field canceledByEmail must not be blank")
    @Email(message = "Field canceledByEmail must be a valid email address")
    private String setByUserEmail;

    @NotNull(message = "Field workMode must not be null")
    private WashingMachineCurrentWorkMode workMode;
}
