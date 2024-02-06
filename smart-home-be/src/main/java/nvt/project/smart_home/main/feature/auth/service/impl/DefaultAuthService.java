package nvt.project.smart_home.main.feature.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nvt.project.smart_home.main.core.constant.Role;
import nvt.project.smart_home.main.core.dto.request.UserRequestDto;
import nvt.project.smart_home.main.core.dto.response.UserResponseDto;
import nvt.project.smart_home.main.core.entity.UserEntity;
import nvt.project.smart_home.main.core.exception.UserNotFoundException;
import nvt.project.smart_home.main.core.service.interf.IMailService;
import nvt.project.smart_home.main.core.service.interf.IUserService;
import nvt.project.smart_home.main.feature.auth.dto.request.LoginRequestDto;
import nvt.project.smart_home.main.feature.auth.dto.request.ResetPasswordDto;
import nvt.project.smart_home.main.feature.auth.dto.resposnse.LoginResponseDto;
import nvt.project.smart_home.main.feature.auth.exception.InvalidCredentialsException;
import nvt.project.smart_home.main.feature.auth.service.interf.IAuthService;
import nvt.project.smart_home.main.websecurity.JwtService;
import nvt.project.smart_home.main.websecurity.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service("AuthService")
public class DefaultAuthService implements IAuthService {


    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IMailService mailService;


    @Override
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException(e.getMessage());
        }

        UserEntity user = userService.findByEmail(loginRequest.getEmail()).orElseThrow(UserNotFoundException::new);
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", user.getId());
        extraClaims.put("roles", user.getRole());
        String jwt = jwtService.generateToken(extraClaims, new UserDetailsImpl(user));

        return new LoginResponseDto(jwt);

    }

    @Override
    public UserResponseDto createAccount(UserRequestDto userRequestDto)  {
        UserResponseDto userResponseDto = userService.createUser(userRequestDto, Role.USER, false);
        Map<String, Object> model = new HashMap<>();
        model.put("userEmail", userRequestDto.getEmail());
        mailService.sendConfirmationEmail("Activation", userRequestDto.getEmail(), model);
        return userResponseDto;
    }

    @Override
    public void activateProfile(String email) {
        UserEntity user = userService.findByEmail(email).orElseThrow(UserNotFoundException::new);
        user.setEnabled(true);
        userService.update(user);
    }

    @Override
    public UserResponseDto saveNewAdmin(UserRequestDto userRequestDto) {
        return userService.createUser(userRequestDto, Role.ADMIN, true);
    }

    @SneakyThrows
    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        userService.resetPassword(resetPasswordDto);
    }




}
