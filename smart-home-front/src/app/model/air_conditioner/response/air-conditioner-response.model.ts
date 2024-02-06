import { ImgResponse } from "../../response/img-response.model";

export interface AirConditionerResponse {
    id:number;
    name:string;
    groupType:string;
    deviceActive:boolean;
    usesBatteries:boolean;
    powerConsumption:boolean;
    deviceType:string;
    image:ImgResponse;
    maxTemperature:string;
    minTemperature:string;
    currentWorkTemperature:Number;
    workMode:string;
    workPlan:Array<any>;

    // TODO
}
