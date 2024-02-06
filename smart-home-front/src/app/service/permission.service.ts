import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PropertyResponseDto } from '../model/response/property-response.model';
import { SmartDeviceResponse } from '../model/response/smart-device-response.model';
import { PermissionResponse } from '../model/permissions/permission-response.model';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  private PERMISSIONS:string = "permissions";

  constructor(private http: HttpClient) { }

  getAllObtainedProperties(email: string): Observable<PropertyResponseDto[]> {
    return this.http.get<PropertyResponseDto[]>(`${environment.beBaseUrl}/${this.PERMISSIONS}/properties/${email}`);
  }

  getAllObtainedDevicesByProperty(email: string, propertyId: number): Observable<SmartDeviceResponse[]> {
    return this.http.get<SmartDeviceResponse[]>(`${environment.beBaseUrl}/${this.PERMISSIONS}/devices/${email}/${propertyId}`);
  }

  getAllGivenPermissions(giverEmail: string): Observable<PermissionResponse[]> {
    return this.http.get<PermissionResponse[]>(`${environment.beBaseUrl}/${this.PERMISSIONS}/given/${giverEmail}`);
  }

  addPropertyPermissions(receiverEmail: string, propertyId: number): Observable<PermissionResponse[]> {
    return this.http.post<PermissionResponse[]>(`${environment.beBaseUrl}/${this.PERMISSIONS}/properties/${receiverEmail}/${propertyId}`, null);
  }

  addDevicePermission(receiverEmail: string, deviceId: number): Observable<PermissionResponse> {
    return this.http.post<PermissionResponse>(`${environment.beBaseUrl}/${this.PERMISSIONS}/devices/${receiverEmail}/${deviceId}`, null);
  }

  removeAllPropertyPermissions(userEmailToRemovePermissions: string, propertyId: number): Observable<void> {
    return this.http.delete<void>(`${environment.beBaseUrl}/${this.PERMISSIONS}/properties/${userEmailToRemovePermissions}/${propertyId}`);
  }

  removeDevicePermissions(userEmailToRemovePermissions: string, deviceId: number): Observable<void> {
    return this.http.delete<void>(`${environment.beBaseUrl}/${this.PERMISSIONS}/devices/${userEmailToRemovePermissions}/${deviceId}`);
  }

  removeById(id:number): Observable<void> {
    return this.http.delete<void>(`${environment.beBaseUrl}/${this.PERMISSIONS}/${id}`);
  }
}
