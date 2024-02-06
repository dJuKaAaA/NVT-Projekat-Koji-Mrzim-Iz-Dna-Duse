import {Component, OnDestroy, OnInit, QueryList, ViewChildren} from '@angular/core';
import {GraphComponent} from "../graph/graph.component";
import {GraphDataSeries} from "../../model/graph-data-series.model";
import {ActivatedRoute} from "@angular/router";
import {
  PredefinedHistoryPeriod
} from "../../model/ambient-sensor/constants/ambient-sensor-predefined-history-period.enum";
import {HomeBatteryService} from "../../service/home-battery.service";
import {FluxResultDto} from "../../model/response/flux-result-dto.model";
import {HttpErrorResponse} from "@angular/common/http";
import {HomeBatteryWebSocketService} from "../../service/socket/home-battery-web-socket.service";
import {HomeBatteryMqtt} from "../../model/response/home-battery-mqtt.model";

@Component({
  selector: 'app-home-battery-details',
  templateUrl: './power-consumption.component.html',
  styleUrls: ['./power-consumption.component.css']
})
export class PowerConsumptionComponent implements OnInit, OnDestroy {

  @ViewChildren(GraphComponent) graphComponents!: QueryList<GraphComponent>;
  public selectedPeriod: string = "";
  public propertyId: number;

  public startDateTime: string;
  public endDateTime: string;

  public powerConsumptionDataSeries: GraphDataSeries[] = [];

  dialog: HTMLDialogElement | null = null;
  dialogMessage: string = "";

  constructor(
    private route: ActivatedRoute,
    private homeBatteryWebSocketService: HomeBatteryWebSocketService,
    private homeBatteryService: HomeBatteryService) {
  }

  ngOnInit(): void {
    this.dialog = document.getElementById("dialog") as HTMLDialogElement;
    this.route.params.subscribe(params => {
      const id = params['propertyId'];
      this.propertyId = Number(id);
    });
    this.homeBatteryWebSocketService.closeConnection(this.propertyId);
  }

  ngOnDestroy(): void {
    this.homeBatteryWebSocketService.closeConnection(this.propertyId);
  }

  onPeriodChange(event: any): void {
    this.selectedPeriod = event.target.value;
    this.powerConsumptionDataSeries = []

    const powerConsumptionGraph = this.graphComponents.get(0);
    if (powerConsumptionGraph && !powerConsumptionGraph.dataSeries) {
      powerConsumptionGraph.dataSeries = this.powerConsumptionDataSeries;
    }

    powerConsumptionGraph?.initChartData()
  }

  powerConsumptionType: string = "";

  onPowerConsumptionTypeChange(event: any) {
    this.powerConsumptionType = event.target.value;
  }

  public displayData() {

    if (this.powerConsumptionType == "") {
      console.log("Please select a power consumption type");
      return;
    }
    let period: PredefinedHistoryPeriod = this.stringToEnum(this.selectedPeriod);

    this.powerConsumptionDataSeries = []

    const powerConsumptionGraph = this.graphComponents.get(0);
    if (powerConsumptionGraph && !powerConsumptionGraph.dataSeries) {
      powerConsumptionGraph.dataSeries = this.powerConsumptionDataSeries;
    }

    const onResponse = (response: Array<FluxResultDto>) => {
      console.log(response)
      response.forEach(el => {
        let timestamp = new Date(el.timestamp * 1000);
        this.powerConsumptionDataSeries.push({date: timestamp, value: el.value})
      })

      this.powerConsumptionDataSeries.sort((a, b) => a.date.getTime() - b.date.getTime())

      const powerConsumptionGraph = this.graphComponents.get(0);
      if (powerConsumptionGraph && !powerConsumptionGraph.dataSeries) {
        powerConsumptionGraph.dataSeries = this.powerConsumptionDataSeries;
      }
      powerConsumptionGraph?.initChartData()
    }

    if (period == 'CUSTOM_DATE') {
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
      this.homeBatteryService.getBetweenDates(this.propertyId, {
        startDate: startDate,
        endDate: endDate,
      }, this.powerConsumptionType).subscribe({
        next: onResponse,
        error: (error) => {
          if (error instanceof HttpErrorResponse) {
            this.dialogMessage = error.error.message;
            this.dialog?.showModal();
            console.log(error);
          }
        }
      })
    } else {
      switch (period) {
        case "REAL_TIME":
          this.homeBatteryService.getLastHour(this.propertyId, this.powerConsumptionType).subscribe({
            next: (response: Array<FluxResultDto>) => {
              onResponse(response);
              this.homeBatteryWebSocketService.connect(this.propertyId, (message: string) => {
                console.log(message)
                const response: HomeBatteryMqtt = JSON.parse(message);
                if (response.powerConsumptionType == this.powerConsumptionType) {
                  this.addPoint({date: new Date(response.timestamp * 1000), value: response.powerConsumed});
                }
              });
            },
            error: (error) => {
              if (error instanceof HttpErrorResponse) {
                this.dialogMessage = error.error.message;
                this.dialog?.showModal();
                console.log(error);
              }
            }
          })
          break;
        case "H6":
          this.homeBatteryService.getLastSixHours(this.propertyId, this.powerConsumptionType).subscribe({
            next: onResponse,
            error: (error) => {
              if (error instanceof HttpErrorResponse) {
                this.dialogMessage = error.error.message;
                this.dialog?.showModal();
                console.log(error);
              }
            }
          })
          this.homeBatteryWebSocketService.closeConnection(this.propertyId);
          break;
        case "H12":
          this.homeBatteryService.getLastTwelveHours(this.propertyId, this.powerConsumptionType).subscribe({
            next: onResponse,
            error: (error) => {
              if (error instanceof HttpErrorResponse) {
                this.dialogMessage = error.error.message;
                this.dialog?.showModal();
                console.log(error);
              }
            }
          })
          this.homeBatteryWebSocketService.closeConnection(this.propertyId);
          break;
        case "H24":
          this.homeBatteryService.getLastTwentyFourHours(this.propertyId, this.powerConsumptionType).subscribe({
            next: onResponse,
            error: (error) => {
              if (error instanceof HttpErrorResponse) {
                this.dialogMessage = error.error.message;
                this.dialog?.showModal();
                console.log(error);
              }
            }
          })
          this.homeBatteryWebSocketService.closeConnection(this.propertyId);
          break;
        case "W1":
          this.homeBatteryService.getLastWeek(this.propertyId, this.powerConsumptionType).subscribe({
            next: onResponse,
            error: (error) => {
              if (error instanceof HttpErrorResponse) {
                this.dialogMessage = error.error.message;
                this.dialog?.showModal();
                console.log(error);
              }
            }
          })
          this.homeBatteryWebSocketService.closeConnection(this.propertyId);
          break;
        case "M1":
          this.homeBatteryService.getLastMonth(this.propertyId, this.powerConsumptionType).subscribe({
            next: onResponse,
            error: (error) => {
              if (error instanceof HttpErrorResponse) {
                this.dialogMessage = error.error.message;
                this.dialog?.showModal();
                console.log(error);
              }
            }
          })
          this.homeBatteryWebSocketService.closeConnection(this.propertyId);
          break;
      }
    }
  }

  public addPoint(point: GraphDataSeries) {
    const graphComponent = this.graphComponents.get(0);
    console.log(graphComponent);
    if (graphComponent && !graphComponent.dataSeries) {
      graphComponent.dataSeries = this.powerConsumptionDataSeries;
    }

    let obj = {date: point.date, value: point.value};
    graphComponent?.addAddData(obj)
  }

  private stringToEnum(value: string): PredefinedHistoryPeriod {
    return value as PredefinedHistoryPeriod;
  }

  closeDialog() {
    this.dialogMessage = "";
    this.dialog?.close()
  }

}
