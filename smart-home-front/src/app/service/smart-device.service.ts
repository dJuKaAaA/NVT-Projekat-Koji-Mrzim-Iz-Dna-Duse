import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {SmartDeviceResponse} from "../model/response/smart-device-response.model";
import {environment} from "../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class SmartDeviceService {

  constructor(
    private httpClient: HttpClient
  ) { }

  public getByPropertyId(propertyId: number): Observable<Array<SmartDeviceResponse>> {
    return this.httpClient.get<Array<SmartDeviceResponse>>(`${environment.beBaseUrl}/smart-device/for-property/${propertyId}`);
  }
}
