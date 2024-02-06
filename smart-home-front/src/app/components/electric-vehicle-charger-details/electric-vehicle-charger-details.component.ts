import {Component, OnDestroy, OnInit} from '@angular/core';
import {ElectricVehicleChargerService} from "../../service/electric-vehicle-charger.service";
import {ActivatedRoute} from "@angular/router";
import {ElectricVehicleChargerSocketService} from "../../service/socket/electric-vehicle-charger-socket.service";
import {
  ElectricVehicleChargerResponse
} from "../../model/electric_vehicle_charger/response/electric-vehicle-charger-response.model";
import {HttpErrorResponse} from "@angular/common/http";
import {ChargingVehicleRequest} from "../../model/electric_vehicle_charger/request/charging-vehicle-request.model";
import {DatePeriodRequest} from "../../model/request/date-period-request.model";
import {FluxResultWithTags} from "../../model/response/flux-result-with-tags.model";
import {UserRefResponse} from "../../model/response/user-ref-response,model";
import {UserResponse} from "../../model/response/user-response.model";
import {AuthService} from "../../service/auth.service";

interface ElectricVehicleChargerWSResponse {
  id: number,
  chargeAmount: number,
  timestamp: Date,
  chargingVehicle: { id: number, currentPower: number, maxPower: number }
}

@Component({
  selector: 'app-electric-vehicle-charger-details',
  templateUrl: './electric-vehicle-charger-details.component.html',
  styleUrls: ['./electric-vehicle-charger-details.component.css']
})
export class ElectricVehicleChargerDetailsComponent implements OnInit, OnDestroy {

  deviceId: number = -1;
  device: ElectricVehicleChargerResponse;
  changeChargeLimitVisible: boolean = false;
  changedChargeLimit: number = 0;
  availableSpacesForCharging: Array<ChargingVehicleRequest> = [];

  dialog: HTMLDialogElement | null = null;
  dialogMessage: string = "";

  users: Array<UserRefResponse> = [];

  constructor(
    private electricVehicleChargerService: ElectricVehicleChargerService,
    private route: ActivatedRoute,
    private electricVehicleChargerSocketService: ElectricVehicleChargerSocketService,
    private authService: AuthService
  ) {
  }

  ngOnInit(): void {
    this.users.push({ id: this.authService.getId(), email: this.authService.getEmail()} )
    this.dialog = document.getElementById('dialog') as HTMLDialogElement;
    this.route.params.subscribe(params => {
      const id = params['deviceId'];
      this.deviceId = Number(id);
      this.electricVehicleChargerService.getById(id).subscribe({
        next: (response: ElectricVehicleChargerResponse) => {
          this.setDevice(response);
        },
        error: (error) => {
          if (error instanceof HttpErrorResponse) {
            console.log(error.error.message);
            this.dialogMessage = error.error.message;
          }
        }
      })
    });
  }

  setDevice(device: ElectricVehicleChargerResponse) {
    this.device = device;
    this.changedChargeLimit = this.device.chargeLimit;

    this.availableSpacesForCharging = [];
    for (let i = 0; i < this.device.chargerCount - this.device.chargersOccupied; i++) {
      this.availableSpacesForCharging.push({currentPower: 0.0, maxPower: 0.0});
    }

    for (let vehicle of this.device.vehiclesCharging) {
      this.electricVehicleChargerSocketService.closeConnection(this.device.id, vehicle.id);
      this.electricVehicleChargerSocketService.connect(this.device.id, vehicle.id, (message: string) => {
        const response: ElectricVehicleChargerWSResponse = JSON.parse(message);
        for (let vehicle of this.device.vehiclesCharging) {
          console.log(message);
          if (response.chargingVehicle.id == vehicle.id) {
            vehicle.currentPower = response.chargingVehicle.currentPower;
            this.electricVehicleChargerService.addPowerToVehicle(this.deviceId, vehicle.id, response.chargeAmount).subscribe({
              next: (response: ElectricVehicleChargerResponse) => {
                console.log("Successfully saved the current power of the vehicle");
                console.log(response);
              }, error: (error) => {
                if (error instanceof HttpErrorResponse) {
                  console.log(error.error.message);
                  this.dialogMessage = error.error.message;
                }
              }
            })
          }
        }
      });
    }

    // this.simulateCharging();
    console.log(this.device);
  }

  simulateCharging() {
    setInterval(() => {
      for (let vehicle of this.device.vehiclesCharging) {
        if (vehicle.currentPower < vehicle.maxPower) {
          vehicle.currentPower += 1
          if (vehicle.currentPower > vehicle.maxPower * (this.device.chargeLimit / 100)) {
            vehicle.currentPower = vehicle.maxPower * (this.device.chargeLimit / 100);
          }
        }
      }
    }, 100);
  }

  setChangeChargeLimitVisible() {
    this.changeChargeLimitVisible = !this.changeChargeLimitVisible;
  }

  setChargeLimitClickable: boolean = true;
  setChargeLimitClickableToTrueId: any;

  setChargeLimit() {
    this.setChargeLimitClickable = false;
    this.setChargeLimitClickableToTrueId = setTimeout(() => {
      this.setChargeLimitClickable = true;
    }, 20000);
    this.electricVehicleChargerService.setChargeLimit(this.device.id, this.changedChargeLimit).subscribe({
      next: (response: ElectricVehicleChargerResponse) => {
        this.device = response;
        this.setChargeLimitClickable = true;
        this.changeChargeLimitVisible = false;
        clearTimeout(this.setChargeLimitClickableToTrueId);
      }, error: (error) => {
        if (error instanceof HttpErrorResponse) {
          console.log(error.error.message);
          this.dialogMessage = error.error.message;
        }
      }
    })
  }

  ngOnDestroy(): void {
    for (let vehicle of this.device.vehiclesCharging) {
      this.electricVehicleChargerSocketService.closeConnection(this.device.id, vehicle.id, "Component was destroyed");
    }
  }

  startCharging(vehicle: ChargingVehicleRequest) {
    if (vehicle.maxPower <= 0 || vehicle.currentPower < 0) {
      console.log("Max power must be greater than 0 and current power cannot be a negative number!");
      this.dialogMessage = "Max power must be greater than 0 and current power cannot be a negative number!";
      this.dialog?.showModal();
      return;
    }
    if (vehicle.maxPower < vehicle.currentPower) {
      console.log("Current power must be lesser than max power!");
      this.dialogMessage = "Current power must be lesser than max power!";
      this.dialog?.showModal();
      return;
    }
    console.log(vehicle);

    this.electricVehicleChargerService.startCharging(this.device.id, vehicle).subscribe({
      next: (response: ElectricVehicleChargerResponse) => {
        this.setDevice(response);
      }, error: (error) => {
        if (error instanceof HttpErrorResponse) {
          console.log(error.error.message);
          this.dialogMessage = error.error.message;
          this.dialog?.showModal()
        }
      }
    })
  }

  stopCharging(vehicleId: number) {
    this.electricVehicleChargerService.stopCharging(this.device.id, vehicleId).subscribe({
      next: (response: ElectricVehicleChargerResponse) => {
        // closing the websocket connection for all vehicles including the one that was removed
        for (let vehicle of this.device.vehiclesCharging) {
          this.electricVehicleChargerSocketService.closeConnection(this.device.id, vehicle.id);
        }

        this.setDevice(response);
      }, error: (error) => {
        if (error instanceof HttpErrorResponse) {
          console.log(error.error.message);
          this.dialogMessage = error.error.message;
          this.dialog?.showModal();
        }
      }
    })
  }

  dataSource: Array<{ timestamp: Date, action: string, user: string, }> = [];

  startDateTime: string;
  endDateTime: string;

  userType: string = "N/A";

  onUserChange(event: any): void {
    this.userType = event.target.value;
  }

  search() {
    if (this.userType == "N/A") {
      console.log("Please select a valid user type for searching!");
      this.dialogMessage = "Please select a valid user type for searching!";
      this.dialog?.showModal();
      return;
    }

    const datePeriod: DatePeriodRequest = {
      startDate: new Date(this.startDateTime),
      endDate: new Date(this.endDateTime)
    };
    if (datePeriod.startDate.getTime() >= datePeriod.endDate.getTime()) {
      console.log("Start date must be before end date!");
      this.dialogMessage = "Start date must be before end date!";
      this.dialog?.showModal();
      return;
    }

    this.fetchActions(datePeriod);
  }

  private fetchActions(datePeriod: DatePeriodRequest) {
    if (this.userType == "ALL") {
      this.electricVehicleChargerService.getAllActions(this.device.id, datePeriod).subscribe({
        next: (response: Array<FluxResultWithTags>) => {
          this.dataSource = [];
          for (let el of response) {
            let action: string = this.getActionFromNumber(el.value);
            this.dataSource.push({
              timestamp: new Date(el.timestamp * 1000),
              action: action,
              user: el.tags["userId"]
            })
          }
        }, error: (error) => {
          if (error instanceof HttpErrorResponse) {
            console.log(error);
            this.dialogMessage = error.error.message;
            this.dialog?.showModal();
          }
        }
      });
    } else {
      this.electricVehicleChargerService.getActionsByUser(this.device.id, this.users[0].id, datePeriod).subscribe({
        next: (response: Array<FluxResultWithTags>) => {
          this.dataSource = [];
          for (let el of response) {
            let action: string = this.getActionFromNumber(el.value);
            this.dataSource.push({
              timestamp: new Date(el.timestamp * 1000),
              action: action,
              user: el.tags["userId"]
            })
          }
        }, error: (error) => {
          if (error instanceof HttpErrorResponse) {
            console.log(error);
            this.dialogMessage = error.error.message;
            this.dialog?.showModal();
          }
        }
      });
    }
  }

  private getActionFromNumber(action: number) {
    let actualAction = "N/A";
    switch (action) {
      case 0:
        actualAction = "STOPPED CHARGING";
        break;
      case 1:
        actualAction = "STARTED CHARGING";
        break;
      case 2:
        actualAction = "CHANGED CHARGE LIMIT";
        break;
    }

    return actualAction;
  }

  closeDialog() {
    this.dialog?.close()
  }
}

