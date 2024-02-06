package nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import nvt.project.smart_home.main.feature.device.air_conditioner.constants.AirConditionerCommand;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirCWorkAppointmentWebRequestDto {

    @NotBlank(message = "Field bookedByEmail must not be blank")
    @Email(message = "Field bookedByEmail must be a valid email address")
    private String bookedByEmail;

    @NotBlank(message = "Field startTime must not be blank")
    private String startTime;

    @NotBlank(message = "Field startTime must not be blank")
    private String endTime;
    private double wantedTemperature;

    @NotNull(message = "Field command must not be null")
    private AirConditionerCommand command;
}
