import { HttpErrorResponse } from '@angular/common/http';
import { AfterViewInit, Component, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { GraphComponent } from 'src/app/components/graph/graph.component';
import { environment } from 'src/app/environments/environment';
import { PredefinedHistoryPeriod } from 'src/app/model/ambient-sensor/constants/ambient-sensor-predefined-history-period.enum';
import { GraphDataSeries } from 'src/app/model/graph-data-series.model';
import { LampHistoryRequest } from 'src/app/model/lamp/request/lamp-history-request.model';
import { LampResponse } from 'src/app/model/lamp/response/lamp-response.model';
import { ImageService } from 'src/app/service/image.service';
import { LampService } from 'src/app/service/lamp.service';
import { LampSocketService } from 'src/app/service/socket/lamp-socket.service';
import {LampCommandHistoryResponse} from "../../../../model/lamp/response/lamp-command-history-response.model";
import {AuthService} from "../../../../service/auth.service";

@Component({
  selector: 'app-lamp-info-page',
  templateUrl: './lamp-info-page.component.html',
  styleUrls: ['./lamp-info-page.component.css']
})
export class LampInfoPageComponent implements OnDestroy{
  lampId: number = -1
  lamp: LampResponse = {} as LampResponse
  // statistics
  lightLevelGraphItem: GraphDataSeries = {} as GraphDataSeries
  bulbOnGraphItem: GraphDataSeries = {} as GraphDataSeries
  commandTableItem: LampCommandHistoryResponse

  constructor(
    private activatedRoute: ActivatedRoute,
    private lampService: LampService,
    private lampSocketService: LampSocketService,
    private imageService: ImageService,
    private sanitizer: DomSanitizer,
    private authService: AuthService
    ) {
      this.lampId = Number(this.activatedRoute.snapshot.paramMap.get("lampId"));
      this.lampService.getById(this.lampId).subscribe({
        next: (response: LampResponse) => {
          this.lamp = response
          if (this.lamp.deviceActive) this.startSocket()
          this.getImage(response.image.name + "." + response.image.format)
        },
        error: (error) => console.log(error.error.message)
      })
    }

  ngOnDestroy(): void {
    this.lampSocketService.closeConnection(this.lampId, "Closed lamp info page!")
  }

  public imageBlob: SafeUrl = {} as SafeUrl
  public imgPresent = false

  getImage(name: string) {
    this.imageService.getImage(name, environment.nginxDeviceDirBaseUrl).subscribe({
      next: (response: Blob) => {
        const objectURL = URL.createObjectURL(response);
        this.imageBlob = this.sanitizer.bypassSecurityTrustUrl(objectURL);
        this.imgPresent = true;
      },
      error: (err: HttpErrorResponse) => console.error(err),
    });
  }

  private startSocket() {
    this.lampSocketService.connect(this.lampId, (message: string) => {
      const response = JSON.parse(message)
      this.lamp.lightLevel = response.lightLevel
      this.lamp.bulbOn = response.bulbOn
      this.lamp.autoModeOn = response.autoModeOn
      let date = new Date(response.timestamp)
      this.lightLevelGraphItem = {
        date: date,
        value: response.lightLevel
      }
      this.bulbOnGraphItem = {
        date: date,
        value: response.bulbOn ? 1 : 0
      }
      if (response.command == undefined) return
      let mode = 'MANUAL_MODE'
      if (response.autoModeOn) mode = 'AUTO_MODE'
      this.commandTableItem = {
        command: response.command,
        triggeredBy: response.triggeredBy,
        mode: mode,
        timestamp: response.timestamp
      }
    });
  }

  setBulb() {
    if (this.lamp.bulbOn) {
      this.lampService.setBulbOn(this.lamp?.id, this.authService.getEmail()).subscribe({
        next: (response: LampResponse) => console.log("Turn off bulb."),
        error: (error) => console.log(error.error.message)
      });
    } else {
      this.lampService.setBulbOff(this.lamp?.id, this.authService.getEmail()).subscribe({
        next: (response: LampResponse) => console.log("Turn off bulb."),
        error: (error) => console.log(error.error.message)
      });
    }
  }

  setAuto() {
    if (!this.lamp.autoModeOn) {
      this.lampService.setAutoOn(this.lamp?.id, this.authService.getEmail()).subscribe({
        next: (response: LampResponse) => console.log("Auto Mode on bulb."),
        error: (error) => console.log(error.error.message)
      });
    } else {
      this.lampService.setAutoOff(this.lamp?.id, this.authService.getEmail()).subscribe({
        next: (response: LampResponse) => console.log("Auto Mode off bulb."),
        error: (error) => console.log(error.error.message)
      });
    }
  }
}
