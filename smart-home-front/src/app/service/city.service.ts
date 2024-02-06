import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../environments/environment";
import { CityRequestDto } from "../model/request/city-country-request.model";

@Injectable({providedIn: 'root'})
export class CityService {
    constructor(private httpClient: HttpClient) {}
  
    public getAll(): Observable<CityRequestDto[]> {
        return this.httpClient.get<CityRequestDto[]>(`${environment.beBaseUrl}/cities`);
    }
}
