import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/service/auth.service';
import { SharedService } from 'src/app/service/shared.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit {
  public isSignIn: boolean = false;
  public role : string = ''
  constructor(
    public authService: AuthService,
    private sharedService: SharedService,
    private router: Router
  ) {
    this.sharedService.isSignIn$.subscribe((value) => {
      this.isSignIn = value;
      this.isSignIn = this.authService.isLoggedIn()
    });
  }

  ngOnInit(): void {}

  public signOut(): void {
    this.isSignIn = false;
    this.authService.signOut();
  }

  isPropertiesActive(): boolean {
    const currentRoute = this.router.url
    return currentRoute.includes('/properties') ||
          currentRoute.includes('/add-property') ||
          currentRoute.includes('graph-ambient-sensor') ||
          currentRoute.includes('air-condition-history') ||
          currentRoute.includes('devices') ||
          currentRoute.includes('add-device') ||
          currentRoute.includes('lamp-info-page')
  }

  isPermissionsActive(): boolean {
    const currentRoute = this.router.url
    return currentRoute.includes('/permission-management')
  }

  isPowerConsumptionActive(): boolean {
    const currentRoute = this.router.url
    return currentRoute.includes("power-consumption");
  }

}
