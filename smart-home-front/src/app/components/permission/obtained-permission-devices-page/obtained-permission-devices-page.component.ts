import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SmartDeviceResponse } from 'src/app/model/response/smart-device-response.model';
import { AuthService } from 'src/app/service/auth.service';
import { PermissionService } from 'src/app/service/permission.service';

@Component({
  selector: 'app-obtained-permission-devices-page',
  templateUrl: './obtained-permission-devices-page.component.html',
  styleUrls: ['./obtained-permission-devices-page.component.css']
})
export class ObtainedPermissionDevicesPageComponent implements OnInit{
  
  devices: SmartDeviceResponse[] = []
  propertyId: number = -1;


  constructor(
    private router: Router,
    private authService: AuthService,
    private permissionService: PermissionService,
    private activatedRoute: ActivatedRoute) {
  }

     ngOnInit(): void {
    this.propertyId = Number(this.activatedRoute.snapshot.paramMap.get("propertyId"));
    this.permissionService.getAllObtainedDevicesByProperty(
      this.authService.getEmail(), this.propertyId).subscribe({
      next: (response: Array<SmartDeviceResponse>) => {
        this.devices = response;
        console.log(this.devices);
      }, error: (error) => {
        if (error instanceof HttpErrorResponse) {
          console.log(error.error.message);
          // TODO: Show error message properly
        }
      }
    })
  }


}
