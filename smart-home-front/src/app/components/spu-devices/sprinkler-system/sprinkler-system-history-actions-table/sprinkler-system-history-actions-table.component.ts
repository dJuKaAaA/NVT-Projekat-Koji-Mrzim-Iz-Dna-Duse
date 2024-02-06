import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {SprinklerSystemService} from "../../../../service/sprinkler-system.service";
import {SprinklerSystemHistoryOfActionsRequest} from "../../../../model/sprinkler-system/request/sprinkler-system-history-of-actions-request.model";
import {StartEndDateRange} from "../../helpers/start-end-date-input/start-end-date-input.component";
import {PredefinedHistoryPeriod} from "../../../../model/ambient-sensor/constants/ambient-sensor-predefined-history-period.enum";
import {SprinklerSystemHistoryResponse} from "../../../../model/sprinkler-system/response/sprinkler-system-history-response.model";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-sprinkler-system-history-actions-table',
  templateUrl: './sprinkler-system-history-actions-table.component.html',
  styleUrls: ['./sprinkler-system-history-actions-table.component.css']
})
export class SprinklerSystemHistoryActionsTableComponent implements OnChanges{
  @Input() sprinklerSystemId: number
  @Input('tableItem') tableItem: SprinklerSystemHistoryResponse
  sprinklerSystemActions: SprinklerSystemHistoryResponse[] = []
  selectedTime = ''
  isDateRange: boolean = false
  haveError: boolean = false
  errorMessage: string = 'Period field is required.'
  range: StartEndDateRange

  constructor(private sprinklerSystemService: SprinklerSystemService) {}

  onTimeSelect(event: string) {
    this.selectedTime = event
    this.isDateRange = this.selectedTime == "CUSTOM_DATE";
    this.haveError = false
    console.log(this.range)
  }

  public onCustomDate(range: StartEndDateRange) {
    this.range = range
    this.displayData()
  }

  public onDateChange(range: StartEndDateRange) {
    this.range = range
  }

  displayData() {
    let request: SprinklerSystemHistoryOfActionsRequest
    this.oldTime = this.selectedTime
    this.oldTriggeredBy = this.triggeredBy
    this.haveError = false
    if (this.selectedTime == 'REAL_TIME') {
      request = {
        period:this.stringToEnum('H1'),
        startDateTime: '',
        endDateTime: '',
        triggeredBy: this.triggeredBy == "" ? null : this.triggeredBy
      };
    } else if (this.selectedTime == 'CUSTOM_DATE')  {
      request = {
        period:this.stringToEnum(this.selectedTime),
        startDateTime: this.range.start,
        endDateTime: this.range.end,
        triggeredBy: this.triggeredBy == "" ? null : this.triggeredBy
      }
    } else if (this.selectedTime == '') {
      this.haveError = true
      return
    } else {
      request = {
        period:this.stringToEnum(this.selectedTime),
        startDateTime: null,
        endDateTime: null,
        triggeredBy: this.triggeredBy == "" ? null : this.triggeredBy
      }
    }
    this.getHistory(request)
  }

  private getHistory(request: SprinklerSystemHistoryOfActionsRequest) {
    this.sprinklerSystemService.getHistoryOfActions(this.sprinklerSystemId, request).subscribe({
      next: response => this.sprinklerSystemActions = response.sort((a, b) => {
        const dateA = new Date(a.timestamp);
        const dateB = new Date(b.timestamp);
        // @ts-ignore
        return dateA - dateB;
      }).reverse(),
      error: (error: HttpErrorResponse) => { console.log(error) }
    })
  }

  ngOnChanges(changes: SimpleChanges): void {
    let newTableItem = changes['tableItem'].currentValue;
    this.liveStream(newTableItem)
  }

  public liveStream(command: SprinklerSystemHistoryResponse) {
    let period:PredefinedHistoryPeriod = this.stringToEnum(this.oldTime)
    if(period == 'REAL_TIME' && (command.triggeredBy == this.oldTriggeredBy || this.triggeredBy == ''))
      this.sprinklerSystemActions.unshift(command)
  }

  private stringToEnum(value: string): PredefinedHistoryPeriod {
    return value as PredefinedHistoryPeriod;
  }

  public getTableString(item: string) {
    if (item == 'ON') return 'System On'
    else if (item == 'OFF') return 'System Off'
    else return item
  }

  // triggered by
  triggeredBy = ''
  populateInput() {this.triggeredBy = 'SYSTEM'}
  clearInput() { this.triggeredBy = '' }

  // button enabled
  oldTime = ''
  oldTriggeredBy = ''
  oldRange: StartEndDateRange | undefined = undefined
  isButtonEnabled() {
    let isEnabled = false
    if (this.selectedTime == 'CUSTOM_DATE') {
      if (this.oldRange != this.range) isEnabled = true
      else {
        if (this.oldTriggeredBy != this.triggeredBy) isEnabled = true
      }
    }
    else {
      if (this.oldTime != this.selectedTime || this.oldTriggeredBy != this.triggeredBy)
        isEnabled = true
    }
    return isEnabled
  }
}
