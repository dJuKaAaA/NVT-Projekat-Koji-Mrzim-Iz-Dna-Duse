import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {StartEndDateRange} from "../../helpers/start-end-date-input/start-end-date-input.component";
import {LampHistoryRequest} from "../../../../model/lamp/request/lamp-history-request.model";
import {HttpErrorResponse} from "@angular/common/http";
import {PredefinedHistoryPeriod} from "../../../../model/ambient-sensor/constants/ambient-sensor-predefined-history-period.enum";
import {VehicleGateActionsHistoryResponse} from "../../../../model/vehicle_gate/response/vehicle-gate-actions-history-response.model";
import {VehicleGateService} from "../../../../service/vehicle-gate.service";
import {VehicleGateHistoryRequest} from "../../../../model/vehicle_gate/request/vehicle-gate-history-request.model";

@Component({
  selector: 'app-gate-actions-history-table',
  templateUrl: './gate-actions-history-table.component.html',
  styleUrls: ['./gate-actions-history-table.component.css']
})
export class GateActionsHistoryTableComponent implements OnChanges{
  @Input() vehicleGateId: number
  @Input('tableItem') tableItem: VehicleGateActionsHistoryResponse
  platesInfo: Array<VehicleGateActionsHistoryResponse> = []
  selectedTime = ''
  isDateRange: boolean = false
  range: StartEndDateRange
  haveError: boolean = false
  errorMessage: string = 'Period field is required.'

  constructor(private vehicleGateService: VehicleGateService) {}

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

  public displayData() {
    let request: VehicleGateHistoryRequest

    this.haveError = false
    this.oldTime = this.selectedTime
    this.oldMode = this.modeFilter
    this.oldTriggeredBy = this.triggeredBy
    this.oldRange = this.range

    if (this.selectedTime == 'REAL_TIME') {
      request = {
        period:this.stringToEnum('H1'),
        startDateTime: null,
        endDateTime: null,
        mode: this.modeFilter == '' ? null : this.modeFilter,
        triggeredBy: this.triggeredBy == "" ? null : this.triggeredBy
      }
    } else if (this.selectedTime == 'CUSTOM_DATE')  {
      request = {
        period:this.stringToEnum(this.selectedTime),
        startDateTime: this.range.start,
        endDateTime: this.range.end,
        triggeredBy: this.triggeredBy == "" ? null : this.triggeredBy,
        mode: this.modeFilter == '' ? null : this.modeFilter
      }
    } else if (this.selectedTime == '') {
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

  private getHistory(request: VehicleGateHistoryRequest) {
    this.vehicleGateService.getPlatesHistory(this.vehicleGateId, request).subscribe({
      next: response =>  this.platesInfo = response.sort((a, b) => {
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

  public liveStream(command: VehicleGateActionsHistoryResponse) {
    let period:PredefinedHistoryPeriod = this.stringToEnum(this.oldTime)
    if(period == 'REAL_TIME'
        && (command.triggeredBy == this.oldTriggeredBy || this.triggeredBy == '')
        && (command.mode == this.modeFilter || this.modeFilter == '')) this.platesInfo.unshift(command)
  }

  ngOnChanges(changes: SimpleChanges): void {
    let newTableItem = changes['tableItem'].currentValue;
    this.liveStream(newTableItem)
  }

  public getTableString(item: string, action: string | null = null) {
    if (item == 'ALWAYS_OPEN') {
      if (action == 'USER_CHANGE') return 'Switched to Open'
      else return 'Open'
    }
    else if (item == 'PRIVATE_MODE') {
      if (action == 'USER_CHANGE') return 'Switched to Private Mode'
      else return 'Private Mode'
    }
    else if (item == 'PUBLIC_MODE') {
      if (action == 'USER_CHANGE') return 'Switched to Public Mode'
      else return 'Public Mode'
    }
    else if (item == 'IN') return 'Go Inside'
    else if (item == 'OUT') return 'Go Outside'
    else if (item == 'DENIED') return 'Entry Denied'
    else if (item == 'SYSTEM') return 'System'
    else if (item == 'CLOSE') return 'Close'
    else if (item == "USER_CHANGE") return 'Changed Mode'
    else return item
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
    else if (selected == 'ALWAYS_OPEN') this.modeFilterShow = "Open"
    else if (selected == 'PRIVATE_MODE') this.modeFilterShow = "Private Mode"
    else this.modeFilterShow = "Public Mode"
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
