import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../environments/environment";
import { VehicleGateRequest } from "../model/vehicle_gate/request/vehicle-gate-request.model";
import { VehicleGateResponse } from "../model/vehicle_gate/response/vehicle-gate-response.model";
import {VehicleGateHistoryRequest} from "../model/vehicle_gate/request/vehicle-gate-history-request.model";
import {VehicleGateActionsHistoryResponse} from "../model/vehicle_gate/response/vehicle-gate-actions-history-response.model";
import {VehicleGateInOutResponse} from "../model/vehicle_gate/response/vehicle-gate-in-out-response.model";

@Injectable({
    providedIn: 'root'
  })
  export class VehicleGateService {

    constructor(private httpClient: HttpClient) { }

    public create(request: VehicleGateRequest): Observable<VehicleGateResponse> {
        return this.httpClient.post<VehicleGateResponse>(`${environment.beBaseUrl}/vehicle-gate`, request);
    }

    public setPublicMode(id: number, triggeredBy: string): Observable<VehicleGateResponse> {
      return this.httpClient.put<VehicleGateResponse>(`${environment.beBaseUrl}/vehicle-gate/${id}/set-public/${triggeredBy}`, {});
    }

    public setPrivateMode(id: number, triggeredBy: string): Observable<VehicleGateResponse> {
      return this.httpClient.put<VehicleGateResponse>(`${environment.beBaseUrl}/vehicle-gate/${id}/set-private/${triggeredBy}`, {});
    }

    public close(id: number, triggeredBy: string): Observable<VehicleGateResponse> {
      return this.httpClient.put<VehicleGateResponse>(`${environment.beBaseUrl}/vehicle-gate/${id}/close/${triggeredBy}`, {});
    }

    public open(id: number, triggeredBy: string): Observable<VehicleGateResponse> {
      return this.httpClient.put<VehicleGateResponse>(`${environment.beBaseUrl}/vehicle-gate/${id}/open/${triggeredBy}`, {});
    }

    public getById(id: number): Observable<VehicleGateResponse> {
      return this.httpClient.get<VehicleGateResponse>(`${environment.beBaseUrl}/vehicle-gate/${id}`)
    }

    public setAllowedVehiclePlates(id: number, plates: string[]): Observable<VehicleGateResponse> {
      return this.httpClient.put<VehicleGateResponse>(`${environment.beBaseUrl}/vehicle-gate/${id}/set-plate`, plates)
    }

    public getPlatesHistory(id: number, request: VehicleGateHistoryRequest): Observable<VehicleGateActionsHistoryResponse[]> {
      return this.httpClient.post<VehicleGateActionsHistoryResponse[]>(`${environment.beBaseUrl}/vehicle-gate/${id}/history-actions`, request);
    }

    public getInOutHistory(id: number, request: VehicleGateHistoryRequest): Observable<VehicleGateInOutResponse[]> {
      return this.httpClient.post<VehicleGateInOutResponse[]>(`${environment.beBaseUrl}/vehicle-gate/${id}/history-in-out`, request);
    }
}
