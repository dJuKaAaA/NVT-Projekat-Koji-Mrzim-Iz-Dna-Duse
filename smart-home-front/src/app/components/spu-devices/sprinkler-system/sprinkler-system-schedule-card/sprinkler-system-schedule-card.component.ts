import {Component, EventEmitter, Output} from '@angular/core';
import {SprinklingSystemScheduleRequest} from "../../../../model/sprinkler-system/request/sprinkling-system-schedule-request.model";

@Component({
  selector: 'app-sprinkler-system-schedule-card',
  templateUrl: './sprinkler-system-schedule-card.component.html',
  styleUrls: ['./sprinkler-system-schedule-card.component.css']
})
export class SprinklerSystemScheduleCardComponent {
  @Output() onChangedSchedules = new EventEmitter<SprinklingSystemScheduleRequest[]>
  schedules: SprinklingSystemScheduleRequest[] = []
  addSchedule() {
    let now = new Date()
    let startHour = now.getHours()
    if (startHour == 24) startHour = 0
    let endHour = startHour + 1
    if (endHour == 24) endHour = 0
    this.schedules.push({
      startTime: startHour.toString().padStart(2, '0') + ":" + now.getMinutes().toString().padStart(2, '0'),
      endTime: endHour.toString().padStart(2, '0') + ":" + now.getMinutes().toString().padStart(2, '0'),
      days: this.getDays()
    })
    this.sendEmitter()
  }

  getDays() { return ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY']}

  editSchedule(schedule: {index: number, item: SprinklingSystemScheduleRequest}) {
    this.schedules[schedule.index] = schedule.item
    this.sendEmitter()
  }

  removeSchedule(index: number) {
    this.schedules.splice(index, 1)
    this.sendEmitter()
  }

  sendEmitter() { this.onChangedSchedules.emit(this.schedules) }
}
