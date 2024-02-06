import {SmartDeviceResponse} from "../../response/smart-device-response.model";
import {ChargingVehicleResponse} from "./charging-vehicle-response.model";

export interface ElectricVehicleChargerResponse extends SmartDeviceResponse {
  vehiclesCharging: Array<ChargingVehicleResponse>,
  chargePower: number,
  chargerCount: number,
  chargersOccupied: number,
  chargeLimit: number
}
