import {SmartDeviceRequest} from "./smart-device-request.model";

export interface HomeBatteryRequest extends SmartDeviceRequest {
  capacity: number
}
