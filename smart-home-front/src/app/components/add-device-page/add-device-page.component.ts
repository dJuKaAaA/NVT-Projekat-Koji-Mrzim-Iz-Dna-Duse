import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ImageService} from "../../service/image.service";
import {ActivatedRoute, Router} from "@angular/router";
import {HttpErrorResponse} from "@angular/common/http";
import {LampRequest} from "../../model/lamp/request/lamp-request.model";
import {LampService} from "../../service/lamp.service";
import {LampResponse} from "../../model/lamp/response/lamp-response.model";
import {ImgRequest} from "../../model/request/img-request.model";
import {SolarPanelRequest} from "../../model/request/solar-panel-request.model";
import {SolarPanelSystemRequest} from "../../model/solar-panel-system-request.model";
import {SolarPanelSystemService} from "../../service/solar-panel-system.service";
import {HomeBatteryRequest} from "../../model/request/home-battery-request.model";
import {HomeBatteryService} from "../../service/home-battery.service";
import {SolarPanelSystemResponse} from "../../model/response/solar-panel-system-response.model";
import {HomeBatteryResponse} from "../../model/response/home-battery-response.model";
import { VehicleGateRequest } from 'src/app/model/vehicle_gate/request/vehicle-gate-request.model';
import { VehicleGateService } from 'src/app/service/vehicle-gate.service';
import { VehicleGateResponse } from 'src/app/model/vehicle_gate/response/vehicle-gate-response.model';
import { AirConditionerMoreInfoCardComponent } from '../pka-devices/air-condition/air-conditioner-more-info-card/air-conditioner-more-info-card.component';
import { AirConditionerService } from 'src/app/service/air-conditioner.service';
import { AmbientSensorService } from 'src/app/service/ambient-sensor.service';
import { AirConditionerRequest } from 'src/app/model/air_conditioner/request/air-conditioner-request.model';
import { AmbientSensorRequest } from 'src/app/model/ambient-sensor/request/ambient-sensor-request.model';
import {ScheduleWorkRequest} from "../../model/request/schedule-work-request.model";
import {SprinklerSystemRequest} from "../../model/sprinkler-system/request/sprinkler-system-request.model";
import {SprinklerSystemService} from "../../service/sprinkler-system.service";
import {SprinklerSystemResponse} from "../../model/sprinkler-system/response/sprinkler-system-response.model";
import {
  ElectricVehicleChargerRequest
} from "../../model/electric_vehicle_charger/request/electric-vehicle-charger-request.model";
import {ElectricVehicleChargerService} from "../../service/electric-vehicle-charger.service";
import {
  ElectricVehicleChargerResponse
} from "../../model/electric_vehicle_charger/response/electric-vehicle-charger-response.model";

@Component({
  selector: 'app-add-device-page',
  templateUrl: './add-device-page.component.html',
  styleUrls: ['./add-device-page.component.css'],
})
export class AddDevicePageComponent implements OnInit {
  constructor(
    private ambientSensorService:AmbientSensorService,
    private airConditionerService:AirConditionerService,
    private imageService: ImageService,
    private activatedRoute: ActivatedRoute,
    private lampService: LampService,
    private solarPanelSystemService: SolarPanelSystemService,
    private homeBatteryService: HomeBatteryService,
    private vehicleGateService: VehicleGateService,
    private sprinklerSystemService: SprinklerSystemService,
    private electricVehicleChargerService: ElectricVehicleChargerService,
    private router: Router,
  ) {}

  @ViewChild(AirConditionerMoreInfoCardComponent)
  childComponent!: AirConditionerMoreInfoCardComponent;

  // form
  showError = false;
  deviceName: string = '';
  usesBatteries: boolean = false;
  propertyId: number = -1;

  //image
  public uploadedImage: File = {} as File;
  public imageUrl: string = '';
  public isImageUploaded: boolean = false;

  deviceTypesMap: Map<string, string> = new Map<string, string>();

  powerConsumption: number = 0.0;

  dialog: HTMLDialogElement | null = null;
  dialogMessage: string = "";

    ngOnInit() {
      this.dialog = document.getElementById("dialog") as HTMLDialogElement;
      this.propertyId = Number(
        this.activatedRoute.snapshot.paramMap.get('propertyId')
      );
      this.propertyId = Number(this.activatedRoute.snapshot.paramMap.get("propertyId"));
      this.deviceTypesMap.set("Air-Conditioner", "AIR_CONDITIONER");
      this.deviceTypesMap.set('Lamp', 'LAMP');
      this.deviceTypesMap.set("Vehicle Gate", "VEHICLE_GATE");
      this.deviceTypesMap.set("Sprinkler System", "SPRINKLER_SYSTEM");
      this.deviceTypesMap.set("Home Battery", "HOME_BATTERY");
      this.deviceTypesMap.set('Ambient Sensor', 'AMBIENT_SENSOR');
      this.deviceTypesMap.set('Air-Conditioner', 'AIR_CONDITIONER');
      this.deviceTypesMap.set('Solar Panel System', 'SOLAR_PANEL_SYSTEM');
      this.deviceTypesMap.set('Electric Vehicle Charger', 'ELECTRIC_VEHICLE_CHARGER');
    }

  @ViewChild('fileInput') fileInput!: ElementRef;

  openFileInput() {
    this.fileInput.nativeElement.click();
  }

  public async onImageUpload(event: any) {
    this.uploadedImage = event.target.files[0];
    try {
      this.imageUrl = await this.imageService.convertImageForDisplayOnUpload(
        this.uploadedImage
      );
      this.isImageUploaded = true;
      console.log(this.uploadedImage);
    } catch (error: any) {
      console.log('Upload image failed.');
    }
  }

  isInputErr(inputName: string) {
    return this.showError;
  }

  async submit() {
    if (this.powerConsumption <= 0 && this.selectedDeviceType != "SOLAR_PANEL_SYSTEM" && this.selectedDeviceType != "HOME_BATTERY" && this.selectedDeviceType != "ELECTRIC_VEHICLE_CHARGER") {
      this.dialogMessage = "Power consumption must be greater than 0";
      this.dialog?.showModal();
      console.log("Power consumption must be greater than 0");
      return;
    }

    switch (this.selectedDeviceType) {
      case "LAMP":
        await this.createLamp()
        break
      case 'SOLAR_PANEL_SYSTEM':
        await this.createSolarPanelSystem();
        break;
      case "HOME_BATTERY":
        await this.createHomeBattery();
        break
      case 'VEHICLE_GATE':
        await this.createVehicleGate()
        break
      case 'SPRINKLER_SYSTEM':
        await this.createSprinklerSystem()
        break
      case 'AMBIENT_SENSOR':
        await this.createAmbientSensor();
        break;
      case 'AIR_CONDITIONER':
        await this.createAirConditioner();
        break;
      case 'ELECTRIC_VEHICLE_CHARGER':
        await this.createElectricVehicleCharger();
        break;
    }
  }

  async createLamp() {
    let img: ImgRequest = await this.imageService.convertImageForSending(
      this.uploadedImage
    );
    const request: LampRequest = {
      name: this.deviceName,
      propertyId: this.propertyId,
      usesBatteries: this.usesBatteries,
      image: img,
      powerConsumption: this.powerConsumption
    }
    this.lampService.create(request).subscribe({
      next: (response: LampResponse) => {
        console.log('Lamp successfully created!');
        this.router.navigate([`/devices/${this.propertyId}`]);
      },
      error: (error) => {
        if (error instanceof HttpErrorResponse) {
          this.dialogMessage = error.error.message;
          this.dialog?.showModal();
          console.log(error.error.message);
        }
      },
    });
  }

  async createSolarPanelSystem() {
    let img: ImgRequest = await this.imageService.convertImageForSending(
      this.uploadedImage
    );
    if (this.panels.length > 0) {
      const request: SolarPanelSystemRequest = {
        name: this.deviceName,
        propertyId: this.propertyId,
        usesBatteries: false,
        image: img,
        solarPanels: this.panels,
        powerConsumption: 0
      }

      this.solarPanelSystemService.create(request).subscribe({
        next: (response: SolarPanelSystemResponse) => {
          console.log("Solar panel system successfully created!");
          this.router.navigate([`/devices/${this.propertyId}`]);
        }, error: (error) => {
          if (error instanceof HttpErrorResponse) {
            this.dialogMessage = error.error.message;
            this.dialog?.showModal();
            console.log(error.error.message);
          }
        }
      });
    } else {
      this.dialogMessage = "Solar panel system must contain at least one solar panel";
      this.dialog?.showModal();
      console.log("Solar panel system must contain at least one solar panel");
    }
  }

  solarPanelArea: number = 0.0;
  solarPanelEfficiency: number = 0.0;
  panels: Array<SolarPanelRequest> = [];

  addPanels() {
    if (this.solarPanelArea <= 0.0) {
      this.dialogMessage = 'Solar panel area must be greater than 0';
      this.dialog?.showModal();
      console.log('Solar panel area must be greater than 0');
      return;
    }
    if (this.solarPanelEfficiency > 1.0 || this.solarPanelEfficiency <= 0.0) {
      this.dialogMessage = 'Solar panel efficiency must be between greater than 0.0 and lesser than 1.0';
      this.dialog?.showModal();
      console.log('Solar panel efficiency must be between greater than 0.0 and lesser than 1.0');
      return;
    }
    this.panels.push({
      area: this.solarPanelArea,
      efficiency: this.solarPanelEfficiency,
    });
    this.solarPanelArea = 0.0;
    this.solarPanelEfficiency = 0.0;

    // TODO: Make an actual info popup
    console.log("Successfully added a solar panel");
  }

  openedDropdown = false;
  selectedDevice = 'Ambient sensor';
  selectedDeviceType = 'AMBIENT_SENSOR';

  // selectedDeviceType = "VEHICLE_GATE";

  onDropdownInputClick() {
    this.openedDropdown = !this.openedDropdown;
  }
  onSelect(select: string) {
    this.openedDropdown = !this.openedDropdown;
    this.selectedDeviceType = String(this.deviceTypesMap.get(select));
    console.log(this.selectedDeviceType);
    this.selectedDevice = select;
  }

  batteryCapacity: number = 0.0;

  async createHomeBattery() {
    if (this.batteryCapacity <= 0) {
      console.log("Battery capacity must be greater than 0");
      return;
    }

    let img: ImgRequest = await this.imageService.convertImageForSending(this.uploadedImage)
    const request: HomeBatteryRequest = {
      name: this.deviceName,
      propertyId: this.propertyId,
      usesBatteries: false,
      image: img,
      capacity: this.batteryCapacity,
      powerConsumption: 0
    }

    this.homeBatteryService.create(request).subscribe({
      next: (response: HomeBatteryResponse) => {
        console.log("Home battery successfully created!");
        this.router.navigate([`/devices/${this.propertyId}`]);
      }, error: (error) => {
        if (error instanceof HttpErrorResponse) {
          this.dialogMessage = error.error.message;
          this.dialog?.showModal();
          console.log(error.error.message);
        }
      }
    });


  }

  //Vehicle Gate
  carPlates: string[] = [];
  onReceivedPlates(plates: string[]) { this.carPlates = plates; }

  async createVehicleGate() {
    let img: ImgRequest = await this.imageService.convertImageForSending(
      this.uploadedImage
    );
    const request: VehicleGateRequest = {
      name: this.deviceName,
      propertyId: this.propertyId,
      usesBatteries: this.usesBatteries,
      image: img,
      allowedLicencePlates: this.carPlates,
      powerConsumption: this.powerConsumption
    };
    console.log(request);

    this.vehicleGateService.create(request).subscribe({
      next: (response: VehicleGateResponse) => {
        console.log('Gate successfully created!');
        this.router.navigate([`/devices/${this.propertyId}`]);
      },
      error: (error) => console.log(error.error.message),
    });
  }

  // Sprinkler System
  schedules: ScheduleWorkRequest[] = []
  onChangeSchedules(schedules: ScheduleWorkRequest[]) { this.schedules = schedules }

  async createSprinklerSystem() {
    let img: ImgRequest = await this.imageService.convertImageForSending(
      this.uploadedImage
    );
    const request: SprinklerSystemRequest = {
      name: this.deviceName,
      propertyId: this.propertyId,
      usesBatteries: this.usesBatteries,
      image: img,
      schedule: this.schedules,
      powerConsumption: this.powerConsumption
    };

    this.sprinklerSystemService.create(request).subscribe({
      next: (response: SprinklerSystemResponse) => {
        console.log('Sprinkler System successfully created!');
        this.router.navigate([`/devices/${this.propertyId}`]);
      },
      error: (error) => console.log(error.error.message),
    });
  }


  async createAmbientSensor() {
    let img: ImgRequest = await this.imageService.convertImageForSending(
      this.uploadedImage
    );

    let request:AmbientSensorRequest = {
      name:this.deviceName,
      image:img,
      propertyId:this.propertyId,
      usesBatteries:this.usesBatteries,
      powerConsumption:this.powerConsumption
    }

    this.ambientSensorService.create(request).subscribe(response => {
      alert("You have successfully created device!")
      this.router.navigate([`/devices/${this.propertyId}`])
    })
  }

  async createAirConditioner()  {
    let img: ImgRequest = await this.imageService.convertImageForSending(
      this.uploadedImage
    );
    let minTemperature = this.childComponent.minTemperature;
    let maxTemp = this.childComponent.maxTemperature;

    let request:AirConditionerRequest = {
      minTemperature:minTemperature,
      maxTemperature:maxTemp,
      name:this.deviceName,
      image: img,
      propertyId:this.propertyId,
      usesBatteries:this.usesBatteries,
      powerConsumption:this.powerConsumption
    }
    this.airConditionerService.create(request).subscribe(response => {
      alert("You have successfully created device!")
      this.router.navigate([`/devices/${this.propertyId}`])
    });
  }

  chargePower: number = 0;
  chargerCount: number = 0;

  async createElectricVehicleCharger() {
    let img: ImgRequest = await this.imageService.convertImageForSending(
      this.uploadedImage
    );
    const request: ElectricVehicleChargerRequest = {
      name: this.deviceName,
      propertyId: this.propertyId,
      usesBatteries: false,
      image: img,
      powerConsumption: 0,
      chargerCount: this.chargerCount,
      chargePower: this.chargePower
    }

    this.electricVehicleChargerService.create(request).subscribe({
      next: (response: ElectricVehicleChargerResponse) => {
        console.log("Electric vehicle charger successfully created!");
        this.router.navigate([`/devices/${this.propertyId}`]);
      }, error: (error) => {
        if (error instanceof HttpErrorResponse) {
          this.dialogMessage = error.error.message;
          this.dialog?.showModal();
          console.log(error.error.message);
        }
      }
    });
  }

  closeDialog() {
    this.dialog?.close()
    this.dialogMessage = "";
  }
}
