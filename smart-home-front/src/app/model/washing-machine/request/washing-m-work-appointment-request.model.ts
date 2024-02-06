import { WashingMachineCommand } from "../constants/washing-m-command-enum";

export interface WashingMachineAppointmentRequest {
    bookedByEmail:string;
    startTime:string;
    command:WashingMachineCommand;
}