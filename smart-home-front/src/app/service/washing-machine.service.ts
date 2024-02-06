import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { WashingMachineCancelAppointmentRequest } from '../model/washing-machine/request/washing-m-cancel-appintment-request.model';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { WashingMachineAppointmentResponse } from '../model/washing-machine/response/washing-m-appointment-response.model';
import { WashingMachineAppointmentRequest } from '../model/washing-machine/request/washing-m-work-appointment-request.model';
import { WashingMachineSetWorkModeRequest } from '../model/washing-machine/request/washing-m-set-work-mode-request.model';
import { WashingMachineRequest } from '../model/washing-machine/request/washing-m-request.model';
import { WashingMachineResponse } from '../model/washing-machine/response/washing-m-response.model';
import { WashingMachineHistoryResponse } from '../model/washing-machine/response/washing-m-history-response.model';

@Injectable({
  providedIn: 'root'
})
export class WashingMachineService {

  private WASHING_MACHINE = "washing-machine";

  constructor(private http: HttpClient,) {}

     getById(id: number): Observable<WashingMachineResponse> {
    return this.http.get<WashingMachineResponse>(`${environment.beBaseUrl}/${this.WASHING_MACHINE}/${id}`);
  }

  getHistory(deviceId: number, pageable: {pageNumber:number, pageSize:number}): Observable<WashingMachineHistoryResponse[]> {
    return this.http.get<WashingMachineHistoryResponse[]>(
      `${environment.beBaseUrl}/${this.WASHING_MACHINE}/${deviceId}/history`, 
      { params: { page: pageable.pageNumber.toString(), size: pageable.pageSize.toString() } });
  }

  create(request: WashingMachineRequest): Observable<WashingMachineResponse> {
    return this.http.post<WashingMachineResponse>(`${environment.beBaseUrl}/${this.WASHING_MACHINE}`, request);
  }

  setCurrentWorkMode(deviceId: number, dto: WashingMachineSetWorkModeRequest): Observable<void> {
    return this.http.put<void>(`${environment.beBaseUrl}/${this.WASHING_MACHINE}/${deviceId}/set-current-work-mode`, dto);
  }

  schedule(deviceId: number, dto: WashingMachineAppointmentRequest): Observable<WashingMachineAppointmentResponse> {
    return this.http.put<WashingMachineAppointmentResponse>(
      `${environment.beBaseUrl}/${this.WASHING_MACHINE}/${deviceId}/schedule`, dto);
  }

  cancelAppointment(deviceId: number, appointmentId: number, requestDto: WashingMachineCancelAppointmentRequest): Observable<void> {
    return this.http.put<void>(`${environment.beBaseUrl}/${this.WASHING_MACHINE}/${deviceId}/cancel-appointment/${appointmentId}`, requestDto);
  }
}
