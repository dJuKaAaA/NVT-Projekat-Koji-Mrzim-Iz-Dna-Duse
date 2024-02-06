import {SmartDeviceRequest} from "../../request/smart-device-request.model";
import {ScheduleWorkRequest} from "../../request/schedule-work-request.model";
import {SprinklingSystemScheduleRequest} from "./sprinkling-system-schedule-request.model";

export interface SprinklerSystemRequest extends SmartDeviceRequest {
  schedule: SprinklingSystemScheduleRequest[]
}
