import { AirConditionerCommand } from "../constants/air-conditioner-command-enum";

export interface AirConditionerAppointmentRequest {
    bookedByEmail:string;
    startTime:string;
    endTime:string;
    wantedTemperature:number;
    command:AirConditionerCommand;
}