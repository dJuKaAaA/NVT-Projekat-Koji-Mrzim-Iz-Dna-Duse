import { Component } from '@angular/core';
import { AuthService } from 'src/app/service/auth.service';
import { ResetPasswordRequest } from 'src/app/model/request/reset-password.model';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-reset-passowrd',
  templateUrl: './reset-passowrd.component.html',
  styleUrls: ['./reset-passowrd.component.css'],
})
export class ResetPassowrdComponent {
  public newPassword: string = '';
  public confirmNewPassword: string = '';
  public email: string = this.authService.getEmail();

  constructor(private authService: AuthService) {}

  public resetPassword() {
    let resetPasswordRequest: ResetPasswordRequest = {
      email: this.email,
      newPassword: this.newPassword,
      confirmNewPassword: this.confirmNewPassword,
    };
    this.authService.resetPassword(resetPasswordRequest).subscribe({
      next: (response: void) => alert(`Password successfully reseted!`),
      error: (error: HttpErrorResponse) =>
        alert(`Status Code: ${error.status}\nMessage: ${error.error?.message}`),
    });
  }
}
