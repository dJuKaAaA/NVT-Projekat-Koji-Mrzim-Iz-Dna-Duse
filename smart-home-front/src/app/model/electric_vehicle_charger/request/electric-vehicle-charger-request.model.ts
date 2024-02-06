import {SmartDeviceRequest} from "../../request/smart-device-request.model";

export interface ElectricVehicleChargerRequest extends SmartDeviceRequest {
  chargePower: number,
  chargerCount: number
}
