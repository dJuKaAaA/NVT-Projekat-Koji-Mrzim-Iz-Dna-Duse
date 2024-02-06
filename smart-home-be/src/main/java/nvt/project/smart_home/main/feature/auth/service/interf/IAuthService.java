package nvt.project.smart_home.main.feature.auth.service.interf;

import nvt.project.smart_home.main.core.dto.request.UserRequestDto;
import nvt.project.smart_home.main.core.dto.response.UserResponseDto;
import nvt.project.smart_home.main.feature.auth.dto.request.LoginRequestDto;
import nvt.project.smart_home.main.feature.auth.dto.request.ResetPasswordDto;
import nvt.project.smart_home.main.feature.auth.dto.resposnse.LoginResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface IAuthService {

    LoginResponseDto login(LoginRequestDto loginRequest);

    UserResponseDto createAccount(UserRequestDto userRequestDto);

    void activateProfile(String email);

    UserResponseDto saveNewAdmin(UserRequestDto userRequestDto);

    void resetPassword(ResetPasswordDto resetPasswordDto);
}
