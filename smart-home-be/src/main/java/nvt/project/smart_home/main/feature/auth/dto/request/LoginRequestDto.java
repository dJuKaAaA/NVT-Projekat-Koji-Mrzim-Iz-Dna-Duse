package nvt.project.smart_home.main.feature.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "Email not provided!")
    private String email;
    @NotBlank(message = "Password not provided")
    private String password;

}
