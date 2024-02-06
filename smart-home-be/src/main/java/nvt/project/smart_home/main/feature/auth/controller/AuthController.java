package nvt.project.smart_home.main.feature.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.dto.request.UserRequestDto;
import nvt.project.smart_home.main.core.dto.response.UserResponseDto;
import nvt.project.smart_home.main.feature.auth.dto.request.LoginRequestDto;
import nvt.project.smart_home.main.feature.auth.dto.request.ResetPasswordDto;
import nvt.project.smart_home.main.feature.auth.dto.resposnse.LoginResponseDto;
import nvt.project.smart_home.main.feature.auth.service.interf.IAuthService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AuthController {

    @Qualifier("AuthService")
    private final IAuthService authService;

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/auth/add-user")
    public UserResponseDto createAccount(@Valid @RequestBody UserRequestDto userRequest) {
        return authService.createAccount(userRequest);
    }

    @GetMapping("/auth/activate/{email}")
    public String activateProfile(@PathVariable("email") String email) {
        authService.activateProfile(email);
        return """
                <!DOCTYPE html>
               <html lang="en">
               <head>
                   <meta charset="UTF-8">
                   <meta name="viewport" content="width=device-width, initial-scale=1.0">
                   <title>Account Activation</title>
                   <style>
                       body {
                           font-family: 'Arial', sans-serif;
                           background-color: #f4f4f4;
                           text-align: center;
                           margin: 50px;
                       }
                       .container {
                           background-color: rgb(219, 117, 83);
                           padding: 20px;
                           border-radius: 10px;
                           box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
                           max-width: 400px;
                           margin: 0 auto;
                       }
                       h1 {
                           color: #fff;
                       }
                       p {
                           color: #eee;
                       }
                   </style>
               </head>
               <body>
                   <div class="container">
                       <h1>Successfully activated your account!</h1>
                       <p>You may close this page.</p>
                   </div>
               </body>
               </html>
               """;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/add-admin")
    public UserResponseDto addNewAdmin(@Valid @RequestBody UserRequestDto userRequest)  {
        return authService.saveNewAdmin(userRequest);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto){
        authService.resetPassword(resetPasswordDto);
    }

}
