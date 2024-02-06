import {Component, EventEmitter, Input, Output} from '@angular/core';
import {SprinklingSystemScheduleRequest} from "../../../../model/sprinkler-system/request/sprinkling-system-schedule-request.model";

@Component({
  selector: 'app-schedule-card',
  templateUrl: './schedule-card.component.html',
  styleUrls: ['./schedule-card.component.css']
})
export class ScheduleCardComponent {
  @Input() schedule: {index: number, item: SprinklingSystemScheduleRequest}
  @Input() showInfoText: boolean = true
  @Output() changedSchedule = new EventEmitter<{index: number, item: SprinklingSystemScheduleRequest}>
  @Output() clickedClear = new EventEmitter<number>

  clickDay(day: string) {
    if (this.schedule.item.days.includes(day)) this.schedule.item.days = this.schedule.item.days.filter(d => d != day)
    else this.schedule.item.days.push(day)
    this.sendEmitter()
  }

  sendEmitter() { this.changedSchedule.emit(this.schedule) }

  clear() { this.clickedClear.emit(this.schedule.index) }
}
