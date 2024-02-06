import {SmartDeviceResponse} from "../../response/smart-device-response.model";
import {SprinklerSystemScheduleResponse} from "./sprinkler-system-schedule-response.model";

export interface SprinklerSystemResponse extends SmartDeviceResponse {
  systemOn: boolean
  schedule: SprinklerSystemScheduleResponse[]
}
