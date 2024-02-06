import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {FluxResultDto} from "../model/response/flux-result-dto.model";
import {environment} from "../environments/environment";
import {DatePeriodRequest} from "../model/request/date-period-request.model";


const ENDPOINT_PREFIX: string = "power-consumption";

@Injectable({
  providedIn: 'root'
})
export class PowerConsumptionService {

  constructor(
    private httpClient: HttpClient
  ) { }

  public getLastSixHoursForProperty(propertyId: number): Observable<Array<FluxResultDto>> {
    return this.httpClient.get<Array<FluxResultDto>>(`${environment.beBaseUrl}/${ENDPOINT_PREFIX}/for-property/${propertyId}/last-6-hours`);
  }

  public getLastTwelveHoursForProperty(propertyId: number): Observable<Array<FluxResultDto>> {
    return this.httpClient.get<Array<FluxResultDto>>(`${environment.beBaseUrl}/${ENDPOINT_PREFIX}/for-property/${propertyId}/last-12-hours`);
  }

  public getLastTwentyFourHoursForProperty(propertyId: number): Observable<Array<FluxResultDto>> {
    return this.httpClient.get<Array<FluxResultDto>>(`${environment.beBaseUrl}/${ENDPOINT_PREFIX}/for-property/${propertyId}/last-24-hours`);
  }

  public getLastWeekForProperty(propertyId: number): Observable<Array<FluxResultDto>> {
    return this.httpClient.get<Array<FluxResultDto>>(`${environment.beBaseUrl}/${ENDPOINT_PREFIX}/for-property/${propertyId}/last-week`);
  }

  public getLastMonthForProperty(propertyId: number): Observable<Array<FluxResultDto>> {
    return this.httpClient.get<Array<FluxResultDto>>(`${environment.beBaseUrl}/${ENDPOINT_PREFIX}/for-property/${propertyId}/last-month`);
  }

  public getBetweenDatesForProperty(propertyId: number, datePeriod: DatePeriodRequest): Observable<Array<FluxResultDto>> {
    return this.httpClient.post<Array<FluxResultDto>>(`${environment.beBaseUrl}/${ENDPOINT_PREFIX}/for-property/${propertyId}/date-period`, datePeriod);
  }

  public getLastSixHoursForCity(cityId: number): Observable<Array<FluxResultDto>> {
    return this.httpClient.get<Array<FluxResultDto>>(`${environment.beBaseUrl}/${ENDPOINT_PREFIX}/for-city/${cityId}/last-6-hours`);
  }

  public getLastTwelveHoursForCity(cityId: number): Observable<Array<FluxResultDto>> {
    return this.httpClient.get<Array<FluxResultDto>>(`${environment.beBaseUrl}/${ENDPOINT_PREFIX}/for-city/${cityId}/last-12-hours`);
  }

  public getLastTwentyFourHoursForCity(cityId: number): Observable<Array<FluxResultDto>> {
    return this.httpClient.get<Array<FluxResultDto>>(`${environment.beBaseUrl}/${ENDPOINT_PREFIX}/for-city/${cityId}/last-24-hours`);
  }

  public getLastWeekForCity(cityId: number): Observable<Array<FluxResultDto>> {
    return this.httpClient.get<Array<FluxResultDto>>(`${environment.beBaseUrl}/${ENDPOINT_PREFIX}/for-city/${cityId}/last-week`);
  }

  public getLastMonthForCity(cityId: number): Observable<Array<FluxResultDto>> {
    return this.httpClient.get<Array<FluxResultDto>>(`${environment.beBaseUrl}/${ENDPOINT_PREFIX}/for-city/${cityId}/last-month`);
  }

  public getBetweenDatesForCity(cityId: number, datePeriod: DatePeriodRequest): Observable<Array<FluxResultDto>> {
    return this.httpClient.post<Array<FluxResultDto>>(`${environment.beBaseUrl}/${ENDPOINT_PREFIX}/for-city/${cityId}/date-period`, datePeriod);
  }
}
