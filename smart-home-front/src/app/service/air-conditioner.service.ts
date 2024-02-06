import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AirConditionerResponse } from '../model/air_conditioner/response/air-conditioner-response.model';
import { environment } from '../environments/environment';
import { AirConditionerHistoryResponse } from '../model/air_conditioner/response/air-conditioner-history-respnse.model';
import { AirConditionerRequest } from '../model/air_conditioner/request/air-conditioner-request.model';
import { AirConditionerSetWorkModeRequest } from '../model/air_conditioner/request/air-conditioner-set-work-mode-request.model';
import { AirConditionerAppointmentRequest } from '../model/air_conditioner/request/air-conditioner-appointment-request.model';
import { AirConditionerCancelAppointmentRequest } from '../model/air_conditioner/request/cancel-air-conditioner-appointment-request.model';
import { AirConditionerAppointmentResponse } from '../model/air_conditioner/response/air-conditioner-appointment-response.model';

@Injectable({
  providedIn: 'root'
})
export class AirConditionerService {

  constructor(private http: HttpClient,) { }

   getById(id: number): Observable<AirConditionerResponse> {
    return this.http.get<AirConditionerResponse>(`${environment.beBaseUrl}/air-conditioner/${id}`);
  }

  getHistory(deviceId: number, pageable: {pageNumber:number, pageSize:number}): Observable<AirConditionerHistoryResponse[]> {
    return this.http.get<AirConditionerHistoryResponse[]>(
      `${environment.beBaseUrl}/air-conditioner/${deviceId}/history`, 
      { params: { page: pageable.pageNumber.toString(), size: pageable.pageSize.toString() } });
  }

  create(request: AirConditionerRequest): Observable<AirConditionerResponse> {
    return this.http.post<AirConditionerResponse>(`${environment.beBaseUrl}/air-conditioner`, request);
  }

  setCurrentWorkMode(deviceId: number, dto: AirConditionerSetWorkModeRequest): Observable<void> {
    return this.http.put<void>(`${environment.beBaseUrl}/air-conditioner/${deviceId}/set-current-work-mode`, dto);
  }

  schedule(deviceId: number, dto: AirConditionerAppointmentRequest): Observable<AirConditionerAppointmentResponse> {
    return this.http.put<AirConditionerAppointmentResponse>(
      `${environment.beBaseUrl}/air-conditioner/${deviceId}/schedule`, dto);
  }

  cancelAppointment(deviceId: number, appointmentId: number, requestDto: AirConditionerCancelAppointmentRequest): Observable<void> {
    return this.http.put<void>(`${environment.beBaseUrl}/air-conditioner/${deviceId}/cancel-appointment/${appointmentId}`, requestDto);
  }
}
