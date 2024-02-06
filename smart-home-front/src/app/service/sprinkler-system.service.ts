import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../environments/environment";
import {SprinklerSystemRequest} from "../model/sprinkler-system/request/sprinkler-system-request.model";
import {SprinklerSystemResponse} from "../model/sprinkler-system/response/sprinkler-system-response.model";
import {LampResponse} from "../model/lamp/response/lamp-response.model";
import {
  SprinklingSystemScheduleRequest
} from "../model/sprinkler-system/request/sprinkling-system-schedule-request.model";
import {SetScheduleRequest} from "../model/sprinkler-system/request/set-schedule-request.model";
import {SetSystemOnOffRequest} from "../model/sprinkler-system/request/set-system-on-off-request.model";
import {VehicleGateHistoryRequest} from "../model/vehicle_gate/request/vehicle-gate-history-request.model";
import {
  VehicleGateActionsHistoryResponse
} from "../model/vehicle_gate/response/vehicle-gate-actions-history-response.model";
import {
  SprinklerSystemHistoryOfActionsRequest
} from "../model/sprinkler-system/request/sprinkler-system-history-of-actions-request.model";
import {
  SprinklerSystemHistoryResponse
} from "../model/sprinkler-system/response/sprinkler-system-history-response.model";

@Injectable({
    providedIn: 'root'
})
export class SprinklerSystemService {

  constructor(private httpClient: HttpClient) { }

  public create(request: SprinklerSystemRequest): Observable<SprinklerSystemResponse> {
    return this.httpClient.post<SprinklerSystemResponse>(`${environment.beBaseUrl}/sprinkler-system`, request);
  }

  public getById(id: number): Observable<SprinklerSystemResponse> {
    return this.httpClient.get<SprinklerSystemResponse>(`${environment.beBaseUrl}/sprinkler-system/${id}`)
  }

  public setSchedules(sprinklerSystem: SetScheduleRequest): Observable<SprinklerSystemResponse> {
    return this.httpClient.put<SprinklerSystemResponse>(`${environment.beBaseUrl}/sprinkler-system/set-schedule`, sprinklerSystem)
  }

  public setSystemOnOff(id: number, request: SetSystemOnOffRequest): Observable<SprinklerSystemResponse> {
    return this.httpClient.put<SprinklerSystemResponse>(`${environment.beBaseUrl}/sprinkler-system/${id}/set-on-off`, request)
  }

  public getHistoryOfActions(id: number, request: SprinklerSystemHistoryOfActionsRequest): Observable<SprinklerSystemHistoryResponse[]> {
    return this.httpClient.post<SprinklerSystemHistoryResponse[]>(`${environment.beBaseUrl}/sprinkler-system/${id}/history-actions`, request);
  }
}
