import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../environments/environment";
import { PropertyRequestDto, PropertyStatusRequestDto } from "../model/request/property-request.model";
import { PropertyResponseDto } from "../model/response/property-response.model";
import {PropertyRefResponse} from "../model/response/property-ref-response.model";

@Injectable({providedIn: 'root'})
export class PropertyService {
    constructor(private httpClient: HttpClient) {}

    public sendRequest(property: PropertyRequestDto): Observable<PropertyResponseDto> {
        return this.httpClient.post<PropertyResponseDto>(`${environment.beBaseUrl}/properties/send-request`, property)
    }
    public getAllByEmail(email: string): Observable<PropertyResponseDto[]> {
        return this.httpClient.get<PropertyResponseDto[]>(`${environment.beBaseUrl}/properties/${email}`)
    }
    public getAllRequests(): Observable<PropertyResponseDto[]> {
        return this.httpClient.get<PropertyResponseDto[]>(`${environment.beBaseUrl}/properties/requests`)
    }
    public changeStatus(property: PropertyStatusRequestDto) {
        console.log(property)
        return this.httpClient.post(`${environment.beBaseUrl}/properties/status`, property)
    }

    public getAll(): Observable<Array<PropertyRefResponse>> {
      return this.httpClient.get<Array<PropertyRefResponse>>(`${environment.beBaseUrl}/properties`);
    }
}
