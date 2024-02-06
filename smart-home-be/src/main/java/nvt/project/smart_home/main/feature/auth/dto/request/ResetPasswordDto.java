package nvt.project.smart_home.main.feature.auth.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDto {
    private String email;
    private String newPassword;
    private String confirmNewPassword;
}
