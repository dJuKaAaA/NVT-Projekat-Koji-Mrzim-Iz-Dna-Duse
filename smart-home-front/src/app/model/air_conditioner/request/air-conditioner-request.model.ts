import { ImgRequest } from "../../request/img-request.model"
import { SmartDeviceRequest } from "../../request/smart-device-request.model"

export interface AirConditionerRequest extends SmartDeviceRequest {
  maxTemperature: number,
  minTemperature:number
}