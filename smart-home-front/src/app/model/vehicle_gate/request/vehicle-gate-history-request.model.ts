import {PredefinedHistoryPeriod} from "../../ambient-sensor/constants/ambient-sensor-predefined-history-period.enum";

export interface VehicleGateHistoryRequest {
  period: PredefinedHistoryPeriod
  startDateTime: String | null
  endDateTime: String | null
  triggeredBy: String | null
  mode: String | null
}
