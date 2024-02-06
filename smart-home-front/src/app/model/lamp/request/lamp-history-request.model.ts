import { PredefinedHistoryPeriod } from "../../ambient-sensor/constants/ambient-sensor-predefined-history-period.enum";

export interface LampHistoryRequest {
    period: PredefinedHistoryPeriod
    startDateTime: String | null
    endDateTime: String | null
}
