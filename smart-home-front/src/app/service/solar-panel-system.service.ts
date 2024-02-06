import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {LampRequest} from "../model/lamp/request/lamp-request.model";
import {Observable} from "rxjs";
import {LampResponse} from "../model/lamp/response/lamp-response.model";
import {environment} from "../environments/environment";
import {SolarPanelSystemResponse} from "../model/response/solar-panel-system-response.model";
import {SolarPanelSystemRequest} from "../model/solar-panel-system-request.model";
import {FluxResultDto} from "../model/response/flux-result-dto.model";
import {DatePeriodRequest} from "../model/request/date-period-request.model";

@Injectable({
  providedIn: 'root'
})
export class SolarPanelSystemService {

  constructor(
    private httpClient: HttpClient
  ) { }


  public create(request: SolarPanelSystemRequest): Observable<SolarPanelSystemResponse> {
    return this.httpClient.post<SolarPanelSystemResponse>(`${environment.beBaseUrl}/solar-panel-system`, request);
  }

  public setActive(id: number): Observable<SolarPanelSystemResponse> {
    return this.httpClient.put<SolarPanelSystemResponse>(`${environment.beBaseUrl}/solar-panel-system/${id}/set-active`, {});
  }

  public setInactive(id: number): Observable<SolarPanelSystemResponse> {
    return this.httpClient.put<SolarPanelSystemResponse>(`${environment.beBaseUrl}/solar-panel-system/${id}/set-inactive`, {});
  }

  public getActions(id: number, datePeriod: DatePeriodRequest): Observable<Array<FluxResultDto>> {
    return this.httpClient.post<Array<FluxResultDto>>(`${environment.beBaseUrl}/solar-panel-system/${id}/actions`, datePeriod);
  }

}
