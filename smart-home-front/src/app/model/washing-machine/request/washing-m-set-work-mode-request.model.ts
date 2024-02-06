import { WashingMachineCurrentWorkMode } from "../constants/washing-m-current-work-mode-enum";

export interface WashingMachineSetWorkModeRequest {
    setByUserEmail:string;
    workMode:WashingMachineCurrentWorkMode;
}