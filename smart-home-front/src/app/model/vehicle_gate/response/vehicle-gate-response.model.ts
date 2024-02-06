import {SmartDeviceResponse} from "../../response/smart-device-response.model";

export interface VehicleGateResponse extends SmartDeviceResponse{
    open: boolean
    alwaysOpen: boolean
    privateMode: boolean
    lastLicencePlateIn: string
    lastInDate: Date
    lastLicencePlateOut: string
    lastOutDate: Date
    allowedLicencePlates: string[]
    lastInCommand: string
}
