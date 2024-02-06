import {SprinklingSystemScheduleRequest} from "./sprinkling-system-schedule-request.model";

export interface SetScheduleRequest {
  id: number
  schedule: SprinklingSystemScheduleRequest[]
}
