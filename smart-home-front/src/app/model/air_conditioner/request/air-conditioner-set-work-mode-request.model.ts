import { AirConditionerCurrentWorkMode } from '../constants/air-conditioner-current-work-mode-enum';

export interface AirConditionerSetWorkModeRequest {
    setByUserEmail:string;
    workMode:AirConditionerCurrentWorkMode;
    wantedTemperature:number;
}