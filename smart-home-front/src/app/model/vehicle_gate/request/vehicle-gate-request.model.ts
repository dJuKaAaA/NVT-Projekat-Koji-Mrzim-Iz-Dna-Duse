import {SmartDeviceRequest} from "../../request/smart-device-request.model";

export interface VehicleGateRequest extends SmartDeviceRequest{
    allowedLicencePlates: string[]
}
