import {Component, Input, QueryList, ViewChildren} from '@angular/core';
import {GraphComponent} from "../../graph/graph.component";
import {GraphDataSeries} from "../../../model/graph-data-series.model";
import {StartEndDateRange} from "../../spu-devices/helpers/start-end-date-input/start-end-date-input.component";
import {PredefinedHistoryPeriod} from "../../../model/ambient-sensor/constants/ambient-sensor-predefined-history-period.enum";
import {HttpErrorResponse} from "@angular/common/http";
import {OnlineOfflineService} from "../../../service/online-offline.service";
import {OnlineOfflineRequest} from "../../../model/online-offline/request/online-offline-request.model";

@Component({
  selector: 'app-online-offline-graph',
  templateUrl: './online-offline-graph.component.html',
  styleUrls: ['./online-offline-graph.component.css']
})
export class OnlineOfflineGraphComponent {

  constructor(private onlineOfflineService: OnlineOfflineService) {}

  @Input() deviceId: number

  @ViewChildren(GraphComponent) graphComponents!: QueryList<GraphComponent>;
  public data:GraphDataSeries[] = [];
  selectedTime = ''
  isDateRange: boolean = false

  yAxisFormatter = (val: any) => {
    if (val == 1) return "Online"
    else if (val == 0) return "Offline"
    else return ""
  };

  onTimeSelect(event: string) {
    this.selectedTime = event
    this.isDateRange = this.selectedTime == "CUSTOM_DATE";
    this.clearGraph()
    this.displayData()
  }

  public onCustomDate(range: StartEndDateRange) {
    this.clearGraph()
    let request = {
      period:this.stringToEnum(this.selectedTime),
      startDateTime: range.start,
      endDateTime: range.end,
    }
    this.getHistory(request)
  }

  public displayData() {
    this.clearGraph()
    let request: OnlineOfflineRequest

    if (this.selectedTime == 'CUSTOM_DATE') return
    else {
      request = {
        period:this.stringToEnum(this.selectedTime),
        startDateTime: null,
        endDateTime: null,
      }
    }
    this.getHistory(request)
  }

  private getHistory(request: OnlineOfflineRequest) {
    this.onlineOfflineService.getGraphData(this.deviceId, request).subscribe({
      next: response => {
        response.forEach( el => {
          let timestamp = new Date(el.timestamp);
          this.data.push({date: timestamp, value: el.failed ? 0 : 1})
          this.data.sort((a, b) => a.date.getTime() - b.date.getTime())
          const graph = this.graphComponents.get(0);
          graph?.setData(this.data)
          graph?.initChartData()
        })
      },
      error: (error: HttpErrorResponse) => { console.log(error) }
    })
  }

  private clearGraph() {
    const graph = this.graphComponents.get(0);
    graph?.clear()
    this.data = []
  }

  private stringToEnum(value: string): PredefinedHistoryPeriod {
    return value as PredefinedHistoryPeriod;
  }

}
