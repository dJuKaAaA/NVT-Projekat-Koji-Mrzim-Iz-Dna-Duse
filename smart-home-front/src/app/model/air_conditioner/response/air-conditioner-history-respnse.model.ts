export interface AirConditionerHistoryResponse {
  executor: string;
  action: string;
  timestamp: string;
  temperature: number | null;
}