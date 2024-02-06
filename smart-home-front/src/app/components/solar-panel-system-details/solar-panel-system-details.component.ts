import {Component, QueryList, ViewChildren} from '@angular/core';
import {GraphComponent} from "../graph/graph.component";
import {GraphDataSeries} from "../../model/graph-data-series.model";
import {ActivatedRoute} from "@angular/router";
import {FluxResultDto} from "../../model/response/flux-result-dto.model";
import {HttpErrorResponse} from "@angular/common/http";
import {SolarPanelSystemService} from "../../service/solar-panel-system.service";

@Component({
  selector: 'app-solar-panel-system-details',
  templateUrl: './solar-panel-system-details.component.html',
  styleUrls: ['./solar-panel-system-details.component.css']
})
export class SolarPanelSystemDetailsComponent {

  @ViewChildren(GraphComponent) graphComponents!: QueryList<GraphComponent>;
  public selectedPeriod: string = "";
  public deviceId: number;

  public startDateTime: string;
  public endDateTime: string;

  public actionsDataSeries: GraphDataSeries[] = [];

  constructor(
    private route: ActivatedRoute,
    private solarPanelSystemService: SolarPanelSystemService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = params['deviceId'];
      this.deviceId = Number(id);
    });
  }

  ngOnDestroy(): void {
  }

  onPeriodChange(event: any): void {
    this.selectedPeriod = event.target.value;
    this.actionsDataSeries = []

    const actionsGraph = this.graphComponents.get(0);
    if (actionsGraph && !actionsGraph.dataSeries) {
      actionsGraph.dataSeries = this.actionsDataSeries;
    }

    actionsGraph?.initChartData()
  }

  public displayData() {
    this.actionsDataSeries = []

    const actionsGraph = this.graphComponents.get(0);
    if (actionsGraph && !actionsGraph.dataSeries) {
      actionsGraph.dataSeries = this.actionsDataSeries;
    }

    const onResponse = (response: Array<FluxResultDto>) => {
      console.log(response)
      response.forEach(el => {
        let timestamp = new Date(el.timestamp * 1000);
        this.actionsDataSeries.push({date: timestamp, value: el.value})
      })

      this.actionsDataSeries.sort((a, b) => a.date.getTime() - b.date.getTime())

      const actionsGraph = this.graphComponents.get(0);
      if (actionsGraph && !actionsGraph.dataSeries) {
        actionsGraph.dataSeries = this.actionsDataSeries;
      }
      actionsGraph?.initChartData()
    }

    const startDate = new Date(this.startDateTime);
    const endDate = new Date(this.endDateTime);
    if (endDate.getTime() <= startDate.getTime()) {
      console.log("Start date must be before end date!");
      return;
    }
    if (endDate.getTime() - startDate.getTime() > 1000 * 60 * 60 * 24 * 30) {
      console.log("Start date and end date max difference is one month!");
      return;
    }
    this.solarPanelSystemService.getActions(this.deviceId, { startDate: startDate, endDate: endDate }).subscribe({
      next: onResponse,
      error: (error) => {
        if (error instanceof HttpErrorResponse) {
          console.log(error);
        }
      }
    })

  }
}
