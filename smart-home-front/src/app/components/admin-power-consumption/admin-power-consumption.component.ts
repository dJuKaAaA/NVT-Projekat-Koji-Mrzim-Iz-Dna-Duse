import {Component, OnInit, QueryList, ViewChildren} from '@angular/core';
import {GraphComponent} from "../graph/graph.component";
import {GraphDataSeries} from "../../model/graph-data-series.model";
import {ActivatedRoute} from "@angular/router";
import {HomeBatteryWebSocketService} from "../../service/socket/home-battery-web-socket.service";
import {HomeBatteryService} from "../../service/home-battery.service";
import {
  PredefinedHistoryPeriod
} from "../../model/ambient-sensor/constants/ambient-sensor-predefined-history-period.enum";
import {FluxResultDto} from "../../model/response/flux-result-dto.model";
import {HttpErrorResponse} from "@angular/common/http";
import {HomeBatteryMqtt} from "../../model/response/home-battery-mqtt.model";
import {PowerConsumptionService} from "../../service/power-consumption.service";
import {PropertyService} from "../../service/property.service";
import {CityService} from "../../service/city.service";
import {CityRequestDto} from "../../model/request/city-country-request.model";
import {PropertyRefResponse} from "../../model/response/property-ref-response.model";

@Component({
  selector: 'app-admin-power-consumption',
  templateUrl: './admin-power-consumption.component.html',
  styleUrls: ['./admin-power-consumption.component.css']
})
export class AdminPowerConsumptionComponent implements OnInit {

  @ViewChildren(GraphComponent) graphComponents!: QueryList<GraphComponent>;
  public selectedPeriod: string = "";

  public startDateTime: string;
  public endDateTime: string;

  public powerConsumptionDataSeries: GraphDataSeries[] = [];

  allCities: Array<CityRequestDto> = [
    { id: 1, name: "City1", countryName: "Country", countryId: 1},
    { id: 2, name: "City2", countryName: "Country", countryId: 1},
    { id: 3, name: "City3", countryName: "Country", countryId: 1},
    { id: 4, name: "City4", countryName: "Country", countryId: 1},
    { id: 5, name: "City5", countryName: "Country", countryId: 1},
  ];
  allProperties: Array<PropertyRefResponse> = [];

  dialog: HTMLDialogElement | null = null;

  constructor(
    private route: ActivatedRoute,
    private powerConsumptionService: PowerConsumptionService,
    private propertyService: PropertyService,
    private cityService: CityService,
  ) { }

  ngOnInit() {
    this.dialog = document.getElementById("dialog") as HTMLDialogElement;
    this.cityService.getAll().subscribe((response: Array<CityRequestDto>) => {
      this.allCities = response;
      if (this.allCities.length > 0) {
        this.city = this.allCities[0]
      }
    });
    this.propertyService.getAll().subscribe((response: Array<PropertyRefResponse>) => {
      this.allProperties = response;
      if (this.allProperties.length > 0) {
        this.property = this.allProperties[0]
      }
    });
  }

  category: string = "CITY";
  onCategoryChange(event: any) {
    this.category = event.target.value;
    this.powerConsumptionDataSeries = []
  }

  property: PropertyRefResponse = {} as PropertyRefResponse;
  onPropertyChange(event: any) {
    // this.property = event.target.value;
    console.log(this.property)
  }

  city: CityRequestDto = {} as CityRequestDto;
  onCityChange(event: any) {
    // this.city = event.target.value;
    console.log(this.city)
  }

  onPeriodChange(event: any): void {
    this.selectedPeriod = event.target.value;
  }

  public displayData() {
    let period: PredefinedHistoryPeriod = this.stringToEnum(this.selectedPeriod);

    this.powerConsumptionDataSeries = []

    const powerConsumptionGraph = this.graphComponents.get(0);
    if (powerConsumptionGraph && !powerConsumptionGraph.dataSeries) {
      powerConsumptionGraph.dataSeries = this.powerConsumptionDataSeries;
    }

    const onResponse = (response: Array<FluxResultDto>) => {
      console.log(response);
      if (response.length <= 0) {
        this.dialog?.showModal();
      }
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

    switch (this.category) {
      case "CITY":
        this.fetchConsumptionForCity(this.city.id, period, onResponse);
        break;
      case "PROPERTY":
        this.fetchConsumptionForProperty(this.property.id, period, onResponse);
        break;
    }

  }

  fetchConsumptionForProperty(propertyId: number, period: string, onResponse: (response: Array<FluxResultDto>) => void) {
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
      this.powerConsumptionService.getBetweenDatesForProperty(propertyId, {
        startDate: startDate,
        endDate: endDate,
      }).subscribe({
        next: onResponse,
        error: (error) => {
          if (error instanceof HttpErrorResponse) {
            console.log(error);
          }
        }
      });
    } else {
      switch (period) {
        case "H6":
          this.powerConsumptionService.getLastSixHoursForProperty(propertyId).subscribe({
            next: onResponse,
            error: (error) => {
              if (error instanceof HttpErrorResponse) {
                console.log(error);
              }
            }
          })
          break;
        case "H12":
          this.powerConsumptionService.getLastTwelveHoursForProperty(propertyId).subscribe({
            next: onResponse,
            error: (error) => {
              if (error instanceof HttpErrorResponse) {
                console.log(error);
              }
            }
          })
          break;
        case "H24":
          this.powerConsumptionService.getLastTwentyFourHoursForProperty(propertyId).subscribe({
            next: onResponse,
            error: (error) => {
              if (error instanceof HttpErrorResponse) {
                console.log(error);
              }
            }
          })
          break;
        case "W1":
          this.powerConsumptionService.getLastWeekForProperty(propertyId).subscribe({
            next: onResponse,
            error: (error) => {
              if (error instanceof HttpErrorResponse) {
                console.log(error);
              }
            }
          })
          break;
        case "M1":
          this.powerConsumptionService.getLastMonthForProperty(propertyId).subscribe({
            next: onResponse,
            error: (error) => {
              if (error instanceof HttpErrorResponse) {
                console.log(error);
              }
            }
          })
          break;
      }
    }
  }

  fetchConsumptionForCity(cityId: number, period: string, onResponse: (response: Array<FluxResultDto>) => void) {
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
      this.powerConsumptionService.getBetweenDatesForCity(cityId, {
        startDate: startDate,
        endDate: endDate,
      }).subscribe({
        next: onResponse,
        error: (error) => {
          if (error instanceof HttpErrorResponse) {
            console.log(error);
          }
        }
      });
    } else {
      switch (period) {
        case "H6":
          this.powerConsumptionService.getLastSixHoursForCity(cityId).subscribe({
            next: onResponse,
            error: (error) => {
              if (error instanceof HttpErrorResponse) {
                console.log(error);
              }
            }
          })
          break;
        case "H12":
          this.powerConsumptionService.getLastTwelveHoursForCity(cityId).subscribe({
            next: onResponse,
            error: (error) => {
              if (error instanceof HttpErrorResponse) {
                console.log(error);
              }
            }
          })
          break;
        case "H24":
          this.powerConsumptionService.getLastTwentyFourHoursForCity(cityId).subscribe({
            next: onResponse,
            error: (error) => {
              if (error instanceof HttpErrorResponse) {
                console.log(error);
              }
            }
          })
          break;
        case "W1":
          this.powerConsumptionService.getLastWeekForCity(cityId).subscribe({
            next: onResponse,
            error: (error) => {
              if (error instanceof HttpErrorResponse) {
                console.log(error);
              }
            }
          })
          break;
        case "M1":
          this.powerConsumptionService.getLastMonthForCity(cityId).subscribe({
            next: onResponse,
            error: (error) => {
              if (error instanceof HttpErrorResponse) {
                console.log(error);
              }
            }
          })
          break;
      }
    }
  }

  private stringToEnum(value: string): PredefinedHistoryPeriod {
    return value as PredefinedHistoryPeriod;
  }

  closeDialog() {
    this.dialog?.close()
  }
}
