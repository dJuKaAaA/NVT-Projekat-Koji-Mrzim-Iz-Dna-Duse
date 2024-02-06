import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {environment} from "../environments/environment";
import {HttpClient} from "@angular/common/http";
import {OnlineOfflineRequest} from "../model/online-offline/request/online-offline-request.model";
import {OnlineOfflineResponse} from "../model/online-offline/response/online-offline-response.model";

@Injectable({
  providedIn: 'root'
})
export class OnlineOfflineService {

  constructor(private httpClient: HttpClient) { }

  public getGraphData(id: number, request: OnlineOfflineRequest): Observable<OnlineOfflineResponse[]> {
    return this.httpClient.post<OnlineOfflineResponse[]>(`${environment.beBaseUrl}/offline-online/${id}/graph-data`, request);
  }
}
