import { Injectable } from '@angular/core';
import { LoginRequest } from '../model/request/login-request.model';
import { TokenResponse } from '../model/response/token-response.model';
import { UserResponse } from '../model/response/user-response.model';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { HttpClient } from '@angular/common/http';
import { JwtHelperService } from '@auth0/angular-jwt';
import { UserRequest } from '../model/request/user-request.model';
import { Route, Router } from '@angular/router';
import { ResetPasswordRequest } from '../model/request/reset-password.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private httpClient: HttpClient, private router: Router) {}

  public login(loginRequest: LoginRequest): Observable<TokenResponse> {
    return this.httpClient.post<TokenResponse>(
      `${environment.beBaseUrl}/auth/login`,
      loginRequest
    );
  }

  public creteUserAccount(
    createAccountRequest: UserRequest
  ): Observable<UserResponse> {
    return this.httpClient.post<UserResponse>(
      `${environment.beBaseUrl}/auth/add-user`,
      createAccountRequest
    );
  }

  public creteAdminAccount(
    createAccountRequest: UserRequest
  ): Observable<UserResponse> {
    return this.httpClient.post<UserResponse>(
      `${environment.beBaseUrl}/add-admin`,
      createAccountRequest
    );
  }

  public resetPassword(
    resetPasswordRequest: ResetPasswordRequest
  ): Observable<void> {
    return this.httpClient.post<void>(
      `${environment.beBaseUrl}/reset-password`,
      resetPasswordRequest
    );
  }

  public signOut(): void {
    localStorage.removeItem('jwt');
    this.router.navigate(['']);
  }

  public getRole(): string {
    if (this.isLoggedIn()) {
      const accessToken: any = localStorage.getItem('jwt');
      const helper = new JwtHelperService();
      const role = helper.decodeToken(accessToken).roles;
      return role;
    }
    return '';
  }

  public getId(): number {
    if (this.isLoggedIn()) {
      const accessToken: any = localStorage.getItem('jwt');
      const helper = new JwtHelperService();
      const id = helper.decodeToken(accessToken).id;
      return id;
    }
    return NaN;
  }

  public getEmail(): string {
    if (this.isLoggedIn()) {
      const accessToken: any = localStorage.getItem('jwt');
      const helper = new JwtHelperService();
      const email = helper.decodeToken(accessToken).sub;
      return email;
    }
    return '';
  }

  public isLoggedIn(): boolean {
    return localStorage.getItem('jwt') != null;
  }

  private getToken(): string {
    if (this.isLoggedIn()) {
      const accessToken: any = localStorage.getItem('jwt');
      const decodedItem = JSON.parse(accessToken);
      return decodedItem.accessToken;
    }
    return '';
  }
}
