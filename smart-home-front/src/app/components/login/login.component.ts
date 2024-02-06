import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Output } from '@angular/core';
import { Router } from '@angular/router';
import { LoginRequest } from '../../model/request/login-request.model';
import { TokenResponse } from '../../model/response/token-response.model';
import { AuthService } from '../../service/auth.service';
import { SharedService } from 'src/app/service/shared.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  public email: string = '';
  public password: string = '';
  @Output() onCreateAccountEvent = new EventEmitter<string>();

  constructor(
    private router: Router,
    private authService: AuthService,
    private sharedService: SharedService
  ) {}

  public login(): void {
    localStorage.removeItem('jwt');
    console.log(this.email, this.password);
    let loginRequest: LoginRequest = {
      email: this.email,
      password: this.password,
    };
    localStorage.removeItem('jwt');
    this.authService.login(loginRequest).subscribe({
      next: (response: TokenResponse) => {
        console.log(response);
        localStorage.setItem('jwt', JSON.stringify(response.token));
        this.sharedService.setIsSignIn(true);
        this.router.navigate(['properties']); // TODO add route
      },
      error: (error) => {
        if (error instanceof HttpErrorResponse) {
          alert(
            `Status Code: ${error.status}\nMessage: ${error.error.message}`
          );
        } else {
          alert('Error, check console!');
          console.log(error);
        }
      },
    });
  }

  public onCreateAccount() {
    this.onCreateAccountEvent.emit();
  }
}
