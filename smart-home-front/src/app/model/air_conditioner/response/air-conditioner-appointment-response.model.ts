export interface AirConditionerAppointmentResponse {
    id:number;
    executor: string;
    startTime: string;
    endTime: string;
    command: string;
    wantedTemperature: number;
}