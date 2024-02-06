import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../environments/environment";
import {AmbientSensorResponse} from "../model/response/ambient-sensor-response.model";
import {AuthService} from "./auth.service";
import { SmartDeviceRequest } from '../model/request/smart-device-request.model';
import { SmartDeviceResponse } from '../model/response/smart-device-response.model';
import { AmbientSensorHistoryRequest } from '../model/ambient-sensor/request/ambient-sensor-history-request.model';
import { AmbientSensorHistoryResponse } from '../model/ambient-sensor/response/ambient-sensor-history-reponse.model';


@Injectable({
  providedIn: 'root'
})
export class AmbientSensorService {

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {
  }

  public create(request: SmartDeviceRequest): Observable<SmartDeviceResponse> {
    return this.http.post<SmartDeviceResponse>(`${environment.beBaseUrl}/ambient-sensor`, request);
  }

  public setActive(id: number): Observable<AmbientSensorResponse> {
    return this.http.put<AmbientSensorResponse>(`${environment.beBaseUrl}/ambient-sensor/${id}/set-active`, {});
  }

  public setInactive(id: number): Observable<AmbientSensorResponse> {
    return this.http.put<AmbientSensorResponse>(`${environment.beBaseUrl}/ambient-sensor/${id}/set-inactive`, {});
  }

   public getById(id: number): Observable<SmartDeviceResponse> {
    return this.http.get<SmartDeviceResponse>(`${environment.beBaseUrl}/ambient-sensor/${id}`);
  }

  public getHistory(id: number, request: AmbientSensorHistoryRequest): Observable<AmbientSensorHistoryResponse[]> {
    return this.http.post<AmbientSensorHistoryResponse[]>(`${environment.beBaseUrl}/ambient-sensor/${id}/history`, request);
  }
}
