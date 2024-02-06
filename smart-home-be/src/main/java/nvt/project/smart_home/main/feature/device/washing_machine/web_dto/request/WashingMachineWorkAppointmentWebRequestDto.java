package nvt.project.smart_home.main.feature.device.washing_machine.web_dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import nvt.project.smart_home.main.feature.device.washing_machine.constants.WashingMachineCommand;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WashingMachineWorkAppointmentWebRequestDto {
    @NotBlank(message = "Field bookedByEmail must not be blank")
    @Email(message = "Field bookedByEmail must be a valid email address")
    private String bookedByEmail;

    @NotBlank(message = "Field startTime must not be blank")
    private String startTime;

    @NotNull(message = "Field command must not be null")
    private WashingMachineCommand command;
}
