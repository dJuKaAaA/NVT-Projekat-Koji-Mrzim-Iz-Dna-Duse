import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { environment } from 'src/app/environments/environment';
import { VehicleGateResponse } from 'src/app/model/vehicle_gate/response/vehicle-gate-response.model';
import { ImageService } from 'src/app/service/image.service';
import { VehicleGateSocketService } from 'src/app/service/socket/vehicle-gate-socket.service';
import { VehicleGateService } from 'src/app/service/vehicle-gate.service';
import {VehicleGateActionsHistoryResponse} from "../../../../model/vehicle_gate/response/vehicle-gate-actions-history-response.model";
import {AuthService} from "../../../../service/auth.service";
import {VehicleGateInOutResponse} from "../../../../model/vehicle_gate/response/vehicle-gate-in-out-response.model";
import {GraphDataSeries} from "../../../../model/graph-data-series.model";

@Component({
  selector: 'app-gate-info-page',
  templateUrl: './gate-info-page.component.html',
  styleUrls: ['./gate-info-page.component.css']
})
export class GateInfoPageComponent implements OnDestroy{

  gateId: number = -1
  gate: VehicleGateResponse = {} as VehicleGateResponse
  plateInfoItem: VehicleGateActionsHistoryResponse = {} as VehicleGateActionsHistoryResponse
  inOutGraphItem: {data: GraphDataSeries, triggeredBy: string}
  constructor(
    private activatedRoute: ActivatedRoute,
    private gateService: VehicleGateService,
    private imageService: ImageService,
    private sanitizer: DomSanitizer,
    private gateSocketService: VehicleGateSocketService,
    private authService: AuthService
  ) {
      this.gateId = Number(this.activatedRoute.snapshot.paramMap.get("gateId"));
      this.gateService.getById(this.gateId).subscribe({
        next: (response: VehicleGateResponse) => {
          this.gate = response
          this.setCommand(response.lastInCommand)
          if (this.gate.deviceActive) this.startSocket()
          this.getImage(response.image.name + "." + response.image.format)
          this.editedPlates = response.allowedLicencePlates.slice()
        },
        error: (error) => console.log(error.error.message)
      })
    }

    ngOnDestroy(): void {
      this.gateSocketService.closeConnection(this.gate.id, "Closed gate info page!")
    }

  public imageBlob: SafeUrl = {} as SafeUrl
  public imgPresent = false

  getImage(name: string) {
    this.imageService.getImage(name, environment.nginxDeviceDirBaseUrl).subscribe({
      next: (response: Blob) => {
        const objectURL = URL.createObjectURL(response)
        this.imageBlob = this.sanitizer.bypassSecurityTrustUrl(objectURL)
        this.imgPresent = true;
      },
      error: (err: HttpErrorResponse) => console.error(err),
    });
  }

  startSocket() {
    this.gateSocketService.connect(this.gateId, (message: string) => {
      const response = JSON.parse(message);
      console.log(response);
      this.setVehicleGate(response)
    });
  }

  lastCommand = 'No Data'
  setCommand(command: string) {
    if (command == 'IN') this.lastCommand = 'Go Inside'
    else if (command == 'DENIED') this.lastCommand = 'Entry Denied'
  }
  setVehicleGate(response: any) {
    this.gate.open = response.open
    this.gate.privateMode = response.privateMode
    this.gate.alwaysOpen = response.alwaysOpen

    if (response.command != null) this.setCommand(response.command)
    if (response.command == "IN" || response.command == 'DENIED') {
      this.gate.lastLicencePlateIn = response.plate
      this.gate.lastInDate = response.timestamp
    } else if (response.command == "OUT") {
      this.gate.lastLicencePlateOut = response.plate
      this.gate.lastOutDate = response.timestamp
    }
    if (response.command != "NO_CHANGES") {
      let mode = "ALWAYS_OPEN"
      if (!response.alwaysOpen) {
        if (response.privateMode) mode = "PRIVATE_MODE"
        else mode = "PUBLIC_MODE"
      }
      this.plateInfoItem = {
        triggeredBy: response.triggeredBy,
        timestamp: response.timestamp,
        command: response.command,
        mode: mode
      }
    }

    if (response.command == 'IN' || response.command == 'OUT') {
      this.inOutGraphItem = {
        data : {
          value: response.command == 'IN' ? 1 : 0,
          date: new Date(response.timestamp)
        },
        triggeredBy: response.triggeredBy
      }
    }
  }

  setMode() {
    if (this.gate.privateMode) {
      this.gateService.setPrivateMode(this.gate.id, this.authService.getEmail()).subscribe({
        next: (response: VehicleGateResponse) => console.log("Turn on private mode."),
        error: (error) => console.log(error.error.message)
      })
    } else {
      this.gateService.setPublicMode(this.gate.id, this.authService.getEmail()).subscribe({
        next: (response: VehicleGateResponse) => console.log("Turn on public mode."),
        error: (error) => console.log(error.error.message)
      })
    }
  }

  changeIsOpen() {
    if (!this.gate.alwaysOpen) {
      this.gateService.open(this.gate.id, this.authService.getEmail()).subscribe({
        next: (response: VehicleGateResponse) => console.log("Open gate."),
        error: (error) => console.log(error.error.message)
      });
    } else {
      this.gateService.close(this.gate.id, this.authService.getEmail()).subscribe({
        next: (response: VehicleGateResponse) => console.log("Close gate."),
        error: (error) => console.log(error.error.message)
      });
    }
  }

  // Change allowed plates
  platesChanges: boolean = false
  editedPlates: string[]
  changePlates(plates: string[]) {
    this.editedPlates = plates
    this.checkIfPlatesChanges()
  }

  checkIfPlatesChanges() {
    if (this.editedPlates.length != this.gate.allowedLicencePlates.length) {
      this.platesChanges = true
      return
    }
    const sortedArr1 = this.editedPlates.slice().sort();
    const sortedArr2 = this.gate.allowedLicencePlates.slice().sort();
    for (let i= 0; i < sortedArr1.length; i++) {
      if (sortedArr1[i] == sortedArr2[i]) continue
      else {
        this.platesChanges = true
        return
      }
    }
    this.platesChanges = false
  }

  changeVehiclePlates() {
    this.gateService.setAllowedVehiclePlates(this.gate.id, this.editedPlates).subscribe({
      next: (response: VehicleGateResponse) => {
        this.gate.allowedLicencePlates = response.allowedLicencePlates
        this.platesChanges = false
      },
      error: (error) => console.log(error.error.message)
    });
  }
}
