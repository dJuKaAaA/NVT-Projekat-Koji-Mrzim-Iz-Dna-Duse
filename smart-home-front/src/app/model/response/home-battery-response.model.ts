import {SmartDeviceResponse} from "./smart-device-response.model";

export interface HomeBatteryResponse extends SmartDeviceResponse{
  capacity: number
}
