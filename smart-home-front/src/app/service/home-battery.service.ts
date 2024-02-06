import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {LampRequest} from "../model/lamp/request/lamp-request.model";
import {Observable} from "rxjs";
import {LampResponse} from "../model/lamp/response/lamp-response.model";
import {environment} from "../environments/environment";
import {HomeBatteryRequest} from "../model/request/home-battery-request.model";
import {HomeBatteryResponse} from "../model/response/home-battery-response.model";
import {FluxResultDto} from "../model/response/flux-result-dto.model";
import {DatePeriodRequest} from "../model/request/date-period-request.model";

@Injectable({
  providedIn: 'root'
})
export class HomeBatteryService {

  constructor(
    private httpClient: HttpClient
  ) { }

  public create(request: HomeBatteryRequest): Observable<HomeBatteryResponse> {
    return this.httpClient.post<HomeBatteryResponse>(`${environment.beBaseUrl}/home-battery`, request);
  }

  public getLastSixHours(propertyId: number, powerConsumptionType: string): Observable<Array<FluxResultDto>> {
    return this.httpClient.get<Array<FluxResultDto>>(`${environment.beBaseUrl}/home-battery/for-property/${propertyId}/last-6-hours/${powerConsumptionType}`);
  }

  public getLastTwelveHours(propertyId: number, powerConsumptionType: string): Observable<Array<FluxResultDto>> {
    return this.httpClient.get<Array<any>>(`${environment.beBaseUrl}/home-battery/for-property/${propertyId}/last-12-hours/${powerConsumptionType}`);
  }

  public getLastTwentyFourHours(propertyId: number, powerConsumptionType: string): Observable<Array<FluxResultDto>> {
    return this.httpClient.get<Array<FluxResultDto>>(`${environment.beBaseUrl}/home-battery/for-property/${propertyId}/last-24-hours/${powerConsumptionType}`);
  }

  public getLastWeek(propertyId: number, powerConsumptionType: string): Observable<Array<FluxResultDto>> {
    return this.httpClient.get<Array<FluxResultDto>>(`${environment.beBaseUrl}/home-battery/for-property/${propertyId}/last-week/${powerConsumptionType}`);
  }

  public getLastMonth(propertyId: number, powerConsumptionType: string): Observable<Array<FluxResultDto>> {
    return this.httpClient.get<Array<FluxResultDto>>(`${environment.beBaseUrl}/home-battery/for-property/${propertyId}/last-month/${powerConsumptionType}`);
  }

  public getLastHour(propertyId: number, powerConsumptionType: string): Observable<Array<FluxResultDto>> {
    return this.httpClient.get<Array<FluxResultDto>>(`${environment.beBaseUrl}/home-battery/for-property/${propertyId}/last-hour/${powerConsumptionType}`);
  }

  public getBetweenDates(propertyId: number, period: DatePeriodRequest, powerConsumptionType: string): Observable<Array<FluxResultDto>> {
    return this.httpClient.post<Array<FluxResultDto>>(`${environment.beBaseUrl}/home-battery/for-property/${propertyId}/date-period/${powerConsumptionType}`, period);
  }

}
