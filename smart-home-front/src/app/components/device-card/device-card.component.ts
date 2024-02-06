import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output
} from '@angular/core';
import {ImageService} from "../../service/image.service";
import {AuthService} from "../../service/auth.service";
import {PropertyResponseDto} from "../../model/response/property-response.model";
import {HttpErrorResponse} from "@angular/common/http";
import {SmartDeviceResponse} from "../../model/response/smart-device-response.model";
import {LampService} from "../../service/lamp.service";
import {environment} from "../../environments/environment";
import {DomSanitizer, SafeUrl} from "@angular/platform-browser";
import {AmbientSensorService} from "../../service/ambient-sensor.service";
import {AmbientSensorResponse} from "../../model/response/ambient-sensor-response.model";
import {MyWebSocketService} from "../../service/my-web-socket.service";
import {SolarPanelSystemService} from "../../service/solar-panel-system.service";
import {SolarPanelSystemResponse} from "../../model/response/solar-panel-system-response.model";
import {SolarPanelSystemWebSocketService} from "../../service/socket/solar-panel-system-web-socket.service";
import { LampSocketService } from 'src/app/service/socket/lamp-socket.service';
import { Router } from '@angular/router';
import { LampResponse } from 'src/app/model/lamp/response/lamp-response.model';

@Component({
  selector: 'app-device-card',
  templateUrl: './device-card.component.html',
  styleUrls: ['./device-card.component.css']
})
export class DeviceCardComponent implements OnInit, AfterViewInit, OnDestroy {

  deviceTypesMap: Map<string, string> = new Map<string, string>();

  dialog: HTMLDialogElement | null = null;
  dialogMessage: string = "";

  ngOnInit(): void {
    this.deviceActive = this.device.deviceActive;

    this.deviceTypesMap.set("AIR_CONDITIONER", "Air-Conditioner");
    this.deviceTypesMap.set('LAMP', 'Lamp');
    this.deviceTypesMap.set("VEHICLE_GATE", "Vehicle Gate");
    this.deviceTypesMap.set("SPRINKLER_SYSTEM", "Sprinkler System");
    this.deviceTypesMap.set("HOME_BATTERY", "Home Battery");
    this.deviceTypesMap.set('AMBIENT_SENSOR', 'Ambient Sensor');
    this.deviceTypesMap.set('AIR_CONDITIONER', 'Air-Conditioner');
    this.deviceTypesMap.set('SOLAR_PANEL_SYSTEM', 'Solar Panel System');
    this.deviceTypesMap.set('WASHING_MACHINE', 'Washing Machine');
    this.deviceTypesMap.set('ELECTRIC_VEHICLE_CHARGER', 'Electric Vehicle Charger');
  }

  ngOnDestroy(): void {
    this.solarPanelSystemWebSocketService.closeConnection(this.device.id, "Called ngOnDestroy off component");
  }

  constructor(
    private imageService: ImageService,
    authService: AuthService,
    private lampService: LampService,
    private ambientSensorService: AmbientSensorService,
    private solarPanelSystemService: SolarPanelSystemService,
    private solarPanelSystemWebSocketService: SolarPanelSystemWebSocketService,
    private sanitizer: DomSanitizer,
    private webSocketService: MyWebSocketService,
    private lampSocketService: LampSocketService,
    private router: Router
  ) {}

  public imageBlob: SafeUrl = {} as SafeUrl;
  public imgPresent = false;

  ngAfterViewInit(): void {
    this.dialog = document.getElementById("dialog") as HTMLDialogElement;
    console.log(this.device.image.name + "." + this.device.image.format)
    this.imageService.getImage(this.device.image.name + "." + this.device.image.format, environment.nginxDeviceDirBaseUrl).subscribe({
      next: (response: Blob) => {
        const objectURL = URL.createObjectURL(response);
        this.imageBlob = this.sanitizer.bypassSecurityTrustUrl(objectURL);
        this.imgPresent = true;
      },
      error: (err: HttpErrorResponse) => console.error(err),
    });
  }

  @Input() device: any = {} as SmartDeviceResponse;
  @Input() index: number | null = null
  @Output() forRemove = new EventEmitter<PropertyResponseDto | null>();
  @Input() canTurnOffDevice:boolean = true

  deviceActive: boolean = this.device == null ? false : this.device.deviceActive;
  setActive() {
    console.log("Changed to ", this.deviceActive);
    switch (this.device?.deviceType) {
      case "WASHING_MACHINE":
        break;
      case "AMBIENT_SENSOR":
        this.changeAmbientSensorActiveState();
        break;
      case "SOLAR_PANEL_SYSTEM":
        this.changeSolarPanelSystemActiveState();
        break;
    }
  }

  private changeAmbientSensorActiveState() {
    if (this.deviceActive) {
      this.ambientSensorService.setActive(this.device?.id).subscribe({
        next: (response: AmbientSensorResponse) => {
          console.log('Changed active state to active')
        }, error: (error) => {
          if (error instanceof HttpErrorResponse) {
            this.dialogMessage = error.error.message;
            this.dialog?.showModal();
            console.log(error.error.message);
          }
        }
      });
    } else {
      this.ambientSensorService.setInactive(this.device?.id).subscribe({
        next: (response: AmbientSensorResponse) => {
          console.log('Changed active state to inactive')
        }, error: (error) => {
          if (error instanceof HttpErrorResponse) {
            this.dialogMessage = error.error.message;
            this.dialog?.showModal();
            console.log(error.error.message);
          }
        }
      });
    }
  }

  private changeSolarPanelSystemActiveState() {
    if (this.deviceActive) {
      this.solarPanelSystemService.setActive(this.device?.id).subscribe({
        next: (response: SolarPanelSystemResponse) => {
          console.log('Changed active state to active')

          // opening WebSocket connection
          this.solarPanelSystemWebSocketService.connect(response.id, (message: string) => {
            console.log(message);
          })

        }, error: (error) => {
          if (error instanceof HttpErrorResponse) {
            this.dialogMessage = error.error.message;
            this.dialog?.showModal();
            console.log(error.error.message);
          }
        }
      });
    } else {
      this.solarPanelSystemService.setInactive(this.device?.id).subscribe({
        next: (response: SolarPanelSystemResponse) => {
          console.log('Changed active state to inactive')
        }, error: (error) => {
          if (error instanceof HttpErrorResponse) {
            this.dialogMessage = error.error.message;
            this.dialog?.showModal();
            this.solarPanelSystemWebSocketService.closeConnection(this.device.id, "Turned off device");
            console.log(error.error.message);
          }
        }
      });
    }
  }

  openDevicePage() {
    switch (this.device?.deviceType) {
      case "AMBIENT_SENSOR":
        this.router.navigate(['/ambient-sensor-history', this.device.id]);
        break;
      case "AIR_CONDITIONER":
        this.router.navigate(['/air-conditioner-manager', this.device.id]);
        break;
      case "WASHING_MACHINE":
        this.router.navigate(['/washing-machine-manager', this.device.id]);
        break;
      case "AMBIENT_SENSOR":
        this.changeAmbientSensorActiveState();
        break;
      case "SOLAR_PANEL_SYSTEM":
        this.router.navigate([`/solar-panel/${this.device.id}`])
        break;
      case 'LAMP':
        this.router.navigate([`lamp-info-page/${this.device.id}`])
        break
      case 'SPRINKLER_SYSTEM':
        this.router.navigate([`sprinkler-system-info-page/${this.device.id}`])
        break
      case 'HOME_BATTERY':
        this.router.navigate([`home-battery-info-page/${this.device.id}`])
        break
      case 'VEHICLE_GATE':
        this.router.navigate([`gate-info-page/${this.device.id}`])
        break
      case 'ELECTRIC_VEHICLE_CHARGER':
        this.router.navigate([`electric-vehicle-charger-details/${this.device.id}`])
    }
  }

  closeDialog() {
    this.dialog?.close()
  }
}
