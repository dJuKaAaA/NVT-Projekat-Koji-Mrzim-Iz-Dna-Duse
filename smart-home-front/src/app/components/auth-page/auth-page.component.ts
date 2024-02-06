import { Component } from '@angular/core';

@Component({
  selector: 'app-auth-page',
  templateUrl: './auth-page.component.html',
  styleUrls: ['./auth-page.component.css'],
})
export class AuthPageComponent {
  public isLogin = true;

  public createAccountView(): void {
    this.isLogin = false;
  }

  public loginView(): void {
    this.isLogin = true;
  }
}
