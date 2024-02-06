import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-start-end-date-input',
  templateUrl: './start-end-date-input.component.html',
  styleUrls: ['./start-end-date-input.component.css']
})
export class StartEndDateInputComponent {
  dateFrom: any
  dateTo: any
  isDateError: boolean = false
  dateErrorMessage = ''
  @Input() isButtonDisabled = false
  @Output() onShow = new EventEmitter<StartEndDateRange>
  @Output() dateChanged = new EventEmitter<StartEndDateRange>

  onClick(isButton: boolean = false) {
    let startDateString = this.dateFrom + 'T00:00'
    let endDateString = this.dateTo + 'T23:59'
    let startDate = new Date(startDateString);
    let endDate = new Date(endDateString);
    //check if some field empty
    if (this.dateFrom == undefined || this.dateTo == undefined) {
      this.isDateError = true
      this.dateErrorMessage = 'Date fields are required.'
      return;
    }
    else this.isDateError = false

    // @ts-ignore
    if (Math.ceil(Math.abs(startDate - endDate) / (1000 * 60 * 60 * 24)) > 30) {
      this.isDateError = true
      this.dateErrorMessage = 'Range can not be larger than 30 days.'
      return
    }
    else this.isDateError = false

    // check of end before start
    if (endDate < startDate) {
      this.isDateError = true
      this.dateErrorMessage = 'Start date must be before end date.'
      return
    }
    else this.isDateError = false

    this.onShow.emit({start: startDateString, end: endDateString})
  }

  sendDate() {
    let startDateString = this.dateFrom + 'T00:00'
    let endDateString = this.dateTo + 'T23:59'
    this.dateChanged.emit({start: startDateString, end: endDateString})
  }

}

export interface StartEndDateRange {
  start: string,
  end: string
}
