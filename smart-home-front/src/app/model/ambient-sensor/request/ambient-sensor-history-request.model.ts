import { PredefinedHistoryPeriod } from "../constants/ambient-sensor-predefined-history-period.enum";

export interface AmbientSensorHistoryRequest {
    period: PredefinedHistoryPeriod;
    startDateTime: String | null; 
    endDateTime: String | null; 
}