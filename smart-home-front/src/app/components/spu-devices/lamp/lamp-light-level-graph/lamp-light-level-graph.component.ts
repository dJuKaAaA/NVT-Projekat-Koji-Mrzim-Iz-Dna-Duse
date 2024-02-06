import {Component, Input, OnChanges, QueryList, SimpleChanges, ViewChildren} from '@angular/core';
import {GraphComponent} from "../../../graph/graph.component";
import {GraphDataSeries} from "../../../../model/graph-data-series.model";
import {LampService} from "../../../../service/lamp.service";
import {LampHistoryRequest} from "../../../../model/lamp/request/lamp-history-request.model";
import {HttpErrorResponse} from "@angular/common/http";
import {PredefinedHistoryPeriod} from "../../../../model/ambient-sensor/constants/ambient-sensor-predefined-history-period.enum";
import {formatDate} from "@angular/common";
import {
  StartEndDateRange
} from "../../helpers/start-end-date-input/start-end-date-input.component";

@Component({
  selector: 'app-lamp-light-level-graph',
  templateUrl: './lamp-light-level-graph.component.html',
  styleUrls: ['./lamp-light-level-graph.component.css']
})
export class LampLightLevelGraphComponent implements OnChanges{
  constructor(private lampService: LampService) {}

  @Input() lampId: number
  @Input('graphItem') graphItem: GraphDataSeries
  @ViewChildren(GraphComponent) graphComponents!: QueryList<GraphComponent>;
  public data:GraphDataSeries[] = [];
  selectedTime = ''
  isDateRange: boolean = false


  private clearGraph() {
    const graph = this.graphComponents.get(0);
    graph?.clear()
    this.data = []
  }
  onTimeSelect(event: string) {
    this.selectedTime = event
    if (this.selectedTime == "CUSTOM_DATE") this.isDateRange = true
    else this.isDateRange = false
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
    let request: LampHistoryRequest

    if (this.selectedTime == 'REAL_TIME') {
      request = {
        period:this.stringToEnum('H1'),
        startDateTime: '',
        endDateTime: '',
      };
    } else if (this.selectedTime == 'CUSTOM_DATE') return
    else {
      request = {
        period:this.stringToEnum(this.selectedTime),
        startDateTime: null,
        endDateTime: null,
      }
    }
    this.getHistory(request)
  }

  private getHistory(request: LampHistoryRequest) {
    this.lampService.getIlluminationHistory(this.lampId, request).subscribe({
      next: response => {
        response.forEach( el => {
          let timestamp = new Date(el.timestamp);
          this.data.push({date:timestamp, value:el.value})
          this.data.sort((a, b) => a.date.getTime() - b.date.getTime())
          const graph = this.graphComponents.get(0);
          graph?.setData(this.data)
          graph?.initChartData()
        })
      },
      error: (error: HttpErrorResponse) => { console.log(error) }
    })
  }

  private stringToEnum(value: string): PredefinedHistoryPeriod {
    return value as PredefinedHistoryPeriod;
  }

  public liveStream(lightLevelData: GraphDataSeries) {
    let period:PredefinedHistoryPeriod = this.stringToEnum(this.selectedTime)
    if(period == 'REAL_TIME') {
      const lightLevelGraph = this.graphComponents.get(0)
      lightLevelGraph?.addAddData(lightLevelData)
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    let newGraphItem = changes['graphItem'].currentValue;
    this.liveStream(newGraphItem)
  }

}
