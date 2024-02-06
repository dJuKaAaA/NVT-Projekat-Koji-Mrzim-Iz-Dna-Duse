import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {
  ElectricVehicleChargerRequest
} from "../model/electric_vehicle_charger/request/electric-vehicle-charger-request.model";
import {Observable} from "rxjs";
import {
  ElectricVehicleChargerResponse
} from "../model/electric_vehicle_charger/response/electric-vehicle-charger-response.model";
import {environment} from "../environments/environment";
import {ChargingVehicleRequest} from "../model/electric_vehicle_charger/request/charging-vehicle-request.model";
import {DatePeriodRequest} from "../model/request/date-period-request.model";
import {FluxResultDto} from "../model/response/flux-result-dto.model";
import {FluxResultWithTags} from "../model/response/flux-result-with-tags.model";

const ENDPOINT_PREFIX: string = "electric-vehicle-charger";

@Injectable({
  providedIn: 'root'
})
export class ElectricVehicleChargerService {
  constructor(
    private httpClient: HttpClient
  ) { }

  public create(request: ElectricVehicleChargerRequest): Observable<ElectricVehicleChargerResponse> {
    return this.httpClient.post<ElectricVehicleChargerResponse>(`${environment.beBaseUrl}/${ENDPOINT_PREFIX}`, request);
  }

  public getById(id: number): Observable<ElectricVehicleChargerResponse> {
    return this.httpClient.get<ElectricVehicleChargerResponse>(`${environment.beBaseUrl}/${ENDPOINT_PREFIX}/${id}`);
  }

  public setChargeLimit(id: number, chargeLimit: number): Observable<ElectricVehicleChargerResponse> {
    return this.httpClient.put<ElectricVehicleChargerResponse>(`${environment.beBaseUrl}/${ENDPOINT_PREFIX}/${id}/set-charge-limit/${chargeLimit}`, {});
  }

  public addPowerToVehicle(id: number, chargingVehicleId: number, power: number): Observable<ElectricVehicleChargerResponse> {
    return this.httpClient.put<ElectricVehicleChargerResponse>(`${environment.beBaseUrl}/${ENDPOINT_PREFIX}/${id}/add-power-to-vehicle/${chargingVehicleId}/${power}`, {});
  }

  public startCharging(id: number, request: ChargingVehicleRequest): Observable<ElectricVehicleChargerResponse> {
    return this.httpClient.put<ElectricVehicleChargerResponse>(`${environment.beBaseUrl}/${ENDPOINT_PREFIX}/${id}/start-charging`, request);
  }

  public stopCharging(id: number, chargingVehicleId: number): Observable<ElectricVehicleChargerResponse> {
    return this.httpClient.put<ElectricVehicleChargerResponse>(`${environment.beBaseUrl}/${ENDPOINT_PREFIX}/${id}/stop-charging/${chargingVehicleId}`, {});
  }

  public getAllActions(id: number, datePeriod: DatePeriodRequest): Observable<Array<FluxResultWithTags>> {
    return this.httpClient.post<Array<FluxResultWithTags>>(`${environment.beBaseUrl}/${ENDPOINT_PREFIX}/${id}/get-all-actions`, datePeriod);
  }

  public getActionsByUser(id: number, userId: number, datePeriod: DatePeriodRequest): Observable<Array<FluxResultWithTags>> {
    return this.httpClient.post<Array<FluxResultWithTags>>(`${environment.beBaseUrl}/${ENDPOINT_PREFIX}/${id}/get-actions/${userId}`, datePeriod);
  }

}
