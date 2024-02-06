import {Component, Input, OnChanges, QueryList, SimpleChanges, ViewChildren} from '@angular/core';
import {GraphDataSeries} from "../../../../model/graph-data-series.model";
import {StartEndDateRange} from "../../helpers/start-end-date-input/start-end-date-input.component";
import {
  PredefinedHistoryPeriod
} from "../../../../model/ambient-sensor/constants/ambient-sensor-predefined-history-period.enum";
import {VehicleGateHistoryRequest} from "../../../../model/vehicle_gate/request/vehicle-gate-history-request.model";
import {LampHistoryRequest} from "../../../../model/lamp/request/lamp-history-request.model";
import {HttpErrorResponse} from "@angular/common/http";
import {VehicleGateInOutResponse} from "../../../../model/vehicle_gate/response/vehicle-gate-in-out-response.model";
import {VehicleGateService} from "../../../../service/vehicle-gate.service";
import {GraphComponent} from "../../../graph/graph.component";
import {timestamp} from "rxjs";

@Component({
  selector: 'app-gate-in-out-history-table',
  templateUrl: './gate-in-out-history-table.component.html',
  styleUrls: ['./gate-in-out-history-table.component.css']
})
export class GateInOutHistoryTableComponent implements OnChanges{

  @Input() gateId: number
  @Input('graphItem') graphItem: {data: GraphDataSeries, triggeredBy: string}
  @ViewChildren(GraphComponent) graphComponents!: QueryList<GraphComponent>;
  public data:GraphDataSeries[] = [];
  selectedTime = ''
  isDateRange: boolean = false
  range: StartEndDateRange
  haveError = false
  errorMessage: string = "'Time' and 'Vehicle Plate' fields are required."

  constructor(private vehicleGateService: VehicleGateService) {}

  yAxisFormatter = (val: any) => {
    if (val == 1) return "IN"
    else if (val == 0) return "OUT"
    else return ""
  };

  onTimeSelect(event: string) {
    this.selectedTime = event
    this.isDateRange = this.selectedTime == "CUSTOM_DATE";
  }

  public onCustomDate(range: StartEndDateRange) {
    this.range = range
    this.displayData()
  }

  public onDateChange(range: StartEndDateRange) {
    this.range = range
  }

  public displayData() {
    this.clearGraph()

    if (this.selectedTime == '' || this.triggeredBy == '') {
      console.log("ERROR")
      this.haveError = true
      return
    } else this.haveError = false

    this.oldTime = this.selectedTime
    this.oldTriggeredBy = this.triggeredBy

    let request: VehicleGateHistoryRequest
    if (this.selectedTime == 'REAL_TIME') {
      request = {
        period:this.stringToEnum('H1'),
        startDateTime: null,
        endDateTime: null,
        mode: null,
        triggeredBy: this.triggeredBy == "" ? null : this.triggeredBy
      }
    } else if (this.selectedTime == 'CUSTOM_DATE')  {
      request = {
        period:this.stringToEnum(this.selectedTime),
        startDateTime: this.range.start,
        endDateTime: this.range.end,
        triggeredBy: this.triggeredBy == "" ? null : this.triggeredBy,
        mode: null
      }
    } else {
      request = {
        period:this.stringToEnum(this.selectedTime),
        startDateTime: null,
        endDateTime: null,
        mode: null,
        triggeredBy: this.triggeredBy == "" ? null : this.triggeredBy
      }
    }
    this.getHistory(request)
  }

  private getHistory(request: VehicleGateHistoryRequest) {
    this.vehicleGateService.getInOutHistory(this.gateId, request).subscribe({
      next: response => {
        let lastVehicleIn
        response.forEach( el => {
          let timestamp = new Date(el.timestamp);
          this.data.push({ date: new Date(timestamp), value: el.vehicleIn ? 1 : 0 });
          timestamp.setMilliseconds(timestamp.getMilliseconds() - 300);
          this.data.push({ date: new Date(timestamp), value: el.vehicleIn ? 0 : 1 });
          lastVehicleIn = el.vehicleIn
        })
        this.data.sort((a, b) => a.date.getTime() - b.date.getTime())
        // TODO: Maybe do this:
        // if (this.selectedTime != 'REAL_TIME') this.data.push({ date: new Date(), value: this.data[-1] ? 0 : 1 });
        const graph = this.graphComponents.get(0);
        graph?.setData(this.data)
        graph?.initChartData()
      },
      error: (error: HttpErrorResponse) => { console.log(error) }
    })
  }

  public liveStream(info: {data: GraphDataSeries, triggeredBy: string}) {
    let period:PredefinedHistoryPeriod = this.stringToEnum(this.oldTime)
    if(period == 'REAL_TIME' && (info.triggeredBy == this.oldTriggeredBy || this.triggeredBy == '')) {
      const graph = this.graphComponents.get(0)
      let timestampBefore = info.data.date
      timestampBefore.setMilliseconds(timestampBefore.getMilliseconds() - 300);
      graph?.addAddData({ date: new Date(timestampBefore), value: info.data.value == 1 ? 0 : 1 })
      graph?.addAddData(info.data)
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    let newGraphItem = changes['graphItem'].currentValue;
    this.liveStream(newGraphItem)
  }

  private clearGraph() {
    const graph = this.graphComponents.get(0);
    graph?.clear()
    this.data = []
  }

  private stringToEnum(value: string): PredefinedHistoryPeriod {
    return value as PredefinedHistoryPeriod;
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
