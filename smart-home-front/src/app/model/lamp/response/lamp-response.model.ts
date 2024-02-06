import {SmartDeviceResponse} from "../../response/smart-device-response.model";

export interface LampResponse extends SmartDeviceResponse{
  lightLevel: number;
  autoModeOn: boolean;
  bulbOn: boolean;
}

