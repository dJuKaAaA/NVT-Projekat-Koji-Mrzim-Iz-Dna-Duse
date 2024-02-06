import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {AuthService} from "../../service/auth.service";
import {PropertyResponseDto} from "../../model/response/property-response.model";
import {HttpErrorResponse} from "@angular/common/http";
import {SmartDeviceResponse} from "../../model/response/smart-device-response.model";
import {SmartDeviceService} from "../../service/smart-device.service";

@Component({
  selector: 'app-devices-page',
  templateUrl: './devices-page.component.html',
  styleUrls: ['./devices-page.component.css']
})
export class DevicesPageComponent implements OnInit {
  devices: SmartDeviceResponse[] = []
  propertyId: number = -1;

  constructor(
    private router: Router,
    public authService: AuthService,
    private smartDeviceService: SmartDeviceService,
    private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.propertyId = Number(this.activatedRoute.snapshot.paramMap.get("propertyId"));
    this.smartDeviceService.getByPropertyId(this.propertyId).subscribe({
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

  removeElementEvent(forRemove: PropertyResponseDto | null) {

  }

  addNewButtonClicked() {
    this.router.navigate([`/add-device/${this.propertyId}`]);
  }

  goToPowerConsumption() {
    this.router.navigate([`power-consumption/${this.propertyId}`]);
  }

}
