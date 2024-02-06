import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {LampService} from "../../../../service/lamp.service";
import {LampCommandHistoryResponse} from "../../../../model/lamp/response/lamp-command-history-response.model";
import {PredefinedHistoryPeriod} from "../../../../model/ambient-sensor/constants/ambient-sensor-predefined-history-period.enum";
import {HttpErrorResponse} from "@angular/common/http";
import {StartEndDateRange} from "../../helpers/start-end-date-input/start-end-date-input.component";
import {LampActionsHistoryRequest} from "../../../../model/lamp/request/lamp-actions-history-request.model";

@Component({
  selector: 'app-lamp-actions-table',
  templateUrl: './lamp-actions-table.component.html',
  styleUrls: ['./lamp-actions-table.component.css']
})
export class LampActionsTableComponent implements OnChanges{

  @Input() lampId: number
  @Input('tableItem') tableItem: LampCommandHistoryResponse
  commands: Array<LampCommandHistoryResponse> = []
  selectedTime = ''
  isDateRange: boolean = false
  haveError: boolean = false
  errorMessage: string = 'Period field is required.'
  range: StartEndDateRange

  constructor(private lampService: LampService) {}

  onTimeSelect(event: string) {
    this.selectedTime = event
    this.isDateRange = this.selectedTime == "CUSTOM_DATE";
    this.haveError = false
  }

  public onCustomDate(range: StartEndDateRange) {
    this.range = range
    this.displayData()
  }

  public onDateChange(range: StartEndDateRange) {
    this.range = range
  }

  displayData() {
    let request: LampActionsHistoryRequest

    this.haveError = false
    this.oldTime = this.selectedTime
    this.oldMode = this.modeFilter
    this.oldTriggeredBy = this.triggeredBy
    this.oldRange = this.range

    if (this.selectedTime == 'REAL_TIME') {
      request = {
        period:this.stringToEnum('H1'),
        startDateTime: '',
        endDateTime: '',
        mode: this.modeFilter == '' ? null : this.modeFilter,
        triggeredBy: this.triggeredBy == "" ? null : this.triggeredBy
      };
    }  else if (this.selectedTime == 'CUSTOM_DATE') {
      request = {
        period:this.stringToEnum(this.selectedTime),
        startDateTime: this.range.start,
        endDateTime: this.range.end,
        mode: this.modeFilter == '' ? null : this.modeFilter,
        triggeredBy: this.triggeredBy == "" ? null : this.triggeredBy
      }
    }else if(this.selectedTime == '') {
      this.haveError = true
      return;
    } else {
      request = {
        period:this.stringToEnum(this.selectedTime),
        startDateTime: null,
        endDateTime: null,
        mode: this.modeFilter == '' ? null : this.modeFilter,
        triggeredBy: this.triggeredBy == "" ? null : this.triggeredBy
      }
    }
    this.getHistory(request)
  }

  private getHistory(request: LampActionsHistoryRequest) {
    this.lampService.getActionsHistory(this.lampId, request).subscribe({
      next: response => this.commands = response.sort((a, b) => {
        const dateA = new Date(a.timestamp);
        const dateB = new Date(b.timestamp);
        // @ts-ignore
        return dateA - dateB;
      }).reverse(),
      error: (error: HttpErrorResponse) => { console.log(error) }
    })
  }

  private stringToEnum(value: string): PredefinedHistoryPeriod {
    return value as PredefinedHistoryPeriod;
  }

  public liveStream(command: LampCommandHistoryResponse) {
    let period:PredefinedHistoryPeriod = this.stringToEnum(this.selectedTime)
    if(period == 'REAL_TIME'
      && (command.triggeredBy == this.oldTriggeredBy || this.triggeredBy == '')
      && (command.mode == this.modeFilter || this.modeFilter == '')) this.commands.unshift(command)
  }

  ngOnChanges(changes: SimpleChanges): void {
    let newTableItem = changes['tableItem'].currentValue;
    this.liveStream(newTableItem)
  }

  public getTableString(item: string) {
    if (item == 'USER') return 'User'
    else if (item == 'AUTO_MODE') return 'Auto Mode'
    else if (item == 'AUTO_MODE_OFF') return 'Auto Mode Off'
    else if (item == 'AUTO_MODE_ON') return 'Auto Mode On'
    else if (item == 'ON_BULB') return 'Bulb On'
    else if (item == 'OFF_BULB') return 'Bulb Off'
    else if (item == 'MANUAL_MODE') return 'Manual Mode'
    else if (item == 'SYSTEM') return 'System'
    else return  item
  }

  // triggered by
  triggeredBy = ''
  populateInput() {this.triggeredBy = 'SYSTEM'}
  clearInput() { this.triggeredBy = '' }

  // mode
  openedDropdown = false
  modeFilter = ''
  modeFilterShow = 'Mode'
  onSelect(selected: string) {
    if (selected == '') this.modeFilterShow = "Mode"
    else if (selected == 'MANUAL_MODE') this.modeFilterShow = "Manual Mode"
    else if (selected == 'AUTO_MODE') this.modeFilterShow = "Auto Mode"
    this.modeFilter = selected
    this.openedDropdown = false
  }
  onDropdownInputClick() { this.openedDropdown = !this.openedDropdown }

  // button enabled
  oldTime = ''
  oldMode = ''
  oldTriggeredBy = ''
  oldRange: StartEndDateRange | undefined = undefined
  isButtonEnabled() {
    let isEnabled = false
    if (this.selectedTime == 'CUSTOM_DATE') {
      if (this.oldRange != this.range) isEnabled = true
      else {
        if (this.oldMode != this.modeFilter || this.oldTriggeredBy != this.triggeredBy) isEnabled = true
      }
    }
    else {
      if (this.oldTime != this.selectedTime || this.oldMode != this.modeFilter || this.oldTriggeredBy != this.triggeredBy)
        isEnabled = true
    }
    return isEnabled
  }
}
