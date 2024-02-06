package nvt.project.smart_home.main.feature.device.air_conditioner.web_dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirCCancelAppointmentWebRequestDto {

    @NotBlank(message = "Field canceledByEmail must not be blank")
    @Email(message = "Field canceledByEmail must be a valid email address")
    private String canceledByEmail;
}
