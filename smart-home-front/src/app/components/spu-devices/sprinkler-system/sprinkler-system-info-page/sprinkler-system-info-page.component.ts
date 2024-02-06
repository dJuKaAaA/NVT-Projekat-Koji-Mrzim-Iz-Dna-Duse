import { Component } from '@angular/core';
import {DomSanitizer, SafeUrl} from "@angular/platform-browser";
import {environment} from "../../../../environments/environment";
import {HttpErrorResponse} from "@angular/common/http";
import {ImageService} from "../../../../service/image.service";
import {SprinklerSystemService} from "../../../../service/sprinkler-system.service";
import {ActivatedRoute} from "@angular/router";
import {SprinklerSystemResponse} from "../../../../model/sprinkler-system/response/sprinkler-system-response.model";
import {SprinklerSystemScheduleResponse} from "../../../../model/sprinkler-system/response/sprinkler-system-schedule-response.model";
import {SprinklingSystemScheduleRequest} from "../../../../model/sprinkler-system/request/sprinkling-system-schedule-request.model";
import {SprinklerSystemSocketService} from "../../../../service/socket/sprinkler-system-socket.service";
import {AuthService} from "../../../../service/auth.service";
import {SprinklerSystemHistoryResponse} from "../../../../model/sprinkler-system/response/sprinkler-system-history-response.model";

@Component({
  selector: 'app-sprinkler-system-info-page',
  templateUrl: './sprinkler-system-info-page.component.html',
  styleUrls: ['./sprinkler-system-info-page.component.css']
})
export class SprinklerSystemInfoPageComponent {

  sprinklerSystemId: number
  sprinklerSystem: SprinklerSystemResponse
  actionTableItem: SprinklerSystemHistoryResponse

  constructor(
    private activatedRoute: ActivatedRoute,
    private imageService: ImageService,
    private sprinklerSystemService: SprinklerSystemService,
    private sanitizer: DomSanitizer,
    private sprinklerSystemSocketService: SprinklerSystemSocketService,
    private authService: AuthService
  ) {
    this.sprinklerSystemId = Number(this.activatedRoute.snapshot.paramMap.get("sprinklerSystemId"));
    this.sprinklerSystemService.getById(this.sprinklerSystemId).subscribe({
      next: (response: SprinklerSystemResponse) => {
        this.sprinklerSystem = response
        this.editedSchedules = response.schedule
        if (this.sprinklerSystem.deviceActive) this.startSocket()
        this.getImage(response.image.name + "." + response.image.format)
      },
      error: (error) => console.log(error.error.message)
    })
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
      error: (err: HttpErrorResponse) => console.error(err)
    });
  }

  startSocket() {
    this.sprinklerSystemSocketService.connect(this.sprinklerSystemId, (message: string) => {
      const response = JSON.parse(message);
      this.sprinklerSystem.systemOn = response.systemOn
      if (response.triggeredBy != null) this.changeTableItem(response)
      console.log(response);
    });
  }

  changeTableItem(response: any) {
    let status = "OFF"
    if (response.systemOn) status = "ON"
    this.actionTableItem = {
      status: status,
      triggeredBy: response.triggeredBy,
      timestamp: response.timestamp
    }
  }

  // Schedule
  editedSchedules: SprinklerSystemScheduleResponse[]
  scheduleChanged: boolean = false
  addSchedule() {
    let now = new Date()
    let startHour = now.getHours()
    if (startHour == 24) startHour = 0
    let endHour = startHour + 1
    if (endHour == 24) endHour = 0
    this.editedSchedules.push({
      startTime: startHour.toString().padStart(2, '0') + ":" + now.getMinutes().toString().padStart(2, '0'),
      endTime: endHour.toString().padStart(2, '0') + ":" + now.getMinutes().toString().padStart(2, '0'),
      days: this.getDays()
    })
    this.scheduleChanged = true
  }
  getDays() { return ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY']}
  editSchedule(schedule: {index: number, item: SprinklingSystemScheduleRequest}) {
    this.editedSchedules[schedule.index] = schedule.item
    this.scheduleChanged = true
  }
  removeSchedule(index: number) {
    this.editedSchedules.splice(index, 1)
    this.scheduleChanged = true
  }

  saveSchedules() {
    this.sprinklerSystem.schedule = this.editedSchedules
    this.scheduleChanged = false
    this.sprinklerSystemService.setSchedules({id: this.sprinklerSystemId, schedule: this.editedSchedules}).subscribe({
      next: (response: SprinklerSystemResponse) => console.log("Changes Saved"),
      error: (err: HttpErrorResponse) => console.error(err)
    })
  }

  turnOn() {
    this.sprinklerSystemService.setSystemOnOff(this.sprinklerSystemId,{systemOn: true, userEmail: this.authService.getEmail()}).subscribe({
      next: (response: SprinklerSystemResponse) => console.log("System On"),
      error: (err: HttpErrorResponse) => console.error(err)
    })
  }

  turnOff() {
    this.sprinklerSystemService.setSystemOnOff(this.sprinklerSystemId,{systemOn: false, userEmail: this.authService.getEmail()}).subscribe({
      next: (response: SprinklerSystemResponse) => console.log("System Off"),
      error: (err: HttpErrorResponse) => console.error(err)
    })
  }
}
