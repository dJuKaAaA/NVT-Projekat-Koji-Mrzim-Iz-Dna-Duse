import { SmartDeviceResponse } from "../../response/smart-device-response.model";
import { WashingMachineAppointmentResponse } from "./washing-m-appointment-response.model";

export interface WashingMachineResponse extends SmartDeviceResponse {
    workMode:string;
    workPlan:Array<WashingMachineAppointmentResponse>;
}