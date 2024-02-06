import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {LampRequest} from "../model/lamp/request/lamp-request.model";
import {Observable} from "rxjs";
import {LampResponse} from "../model/lamp/response/lamp-response.model";
import {environment} from "../environments/environment";
import { LampHistoryRequest } from '../model/lamp/request/lamp-history-request.model';
import { LampValuesHistoryResponseDto } from '../model/lamp/response/lamp-values-history-response.model';
import {LampCommandHistoryResponse} from "../model/lamp/response/lamp-command-history-response.model";
import {LampActionsHistoryRequest} from "../model/lamp/request/lamp-actions-history-request.model";

@Injectable({
  providedIn: 'root'
})
export class LampService {

  constructor(private httpClient: HttpClient) { }

  public create(request: LampRequest): Observable<LampResponse> {
    return this.httpClient.post<LampResponse>(`${environment.beBaseUrl}/lamp`, request);
  }

  public setBulbOn(id: number, triggeredBy: string): Observable<LampResponse> {
    return this.httpClient.put<LampResponse>(`${environment.beBaseUrl}/lamp/${id}/bulb-on/${triggeredBy}`, {});
  }

  public setBulbOff(id: number, triggeredBy: string): Observable<LampResponse> {
    return this.httpClient.put<LampResponse>(`${environment.beBaseUrl}/lamp/${id}/bulb-off/${triggeredBy}`, {});
  }

  public setAutoOn(id: number, triggeredBy: string): Observable<LampResponse> {
    return this.httpClient.put<LampResponse>(`${environment.beBaseUrl}/lamp/${id}/auto-on/${triggeredBy}`, {});
  }

  public setAutoOff(id: number, triggeredBy: string): Observable<LampResponse> {
    return this.httpClient.put<LampResponse>(`${environment.beBaseUrl}/lamp/${id}/auto-off/${triggeredBy}`, {});
  }

  public getById(id: number): Observable<LampResponse> {
    return this.httpClient.get<LampResponse>(`${environment.beBaseUrl}/lamp/${id}`)
  }

  public getIlluminationHistory(id: number, request: LampHistoryRequest): Observable<LampValuesHistoryResponseDto[]> {
    return this.httpClient.post<LampValuesHistoryResponseDto[]>(`${environment.beBaseUrl}/lamp/${id}/history-light-level`, request);
  }

  public getBulbOnHistory(id: number, request: LampHistoryRequest): Observable<LampValuesHistoryResponseDto[]> {
    return this.httpClient.post<LampValuesHistoryResponseDto[]>(`${environment.beBaseUrl}/lamp/${id}/history-bulb-on`, request);
  }

  public getActionsHistory(id: number, request: LampActionsHistoryRequest): Observable<LampCommandHistoryResponse[]> {
    return this.httpClient.post<LampCommandHistoryResponse[]>(`${environment.beBaseUrl}/lamp/${id}/history-command`, request);
  }

}
