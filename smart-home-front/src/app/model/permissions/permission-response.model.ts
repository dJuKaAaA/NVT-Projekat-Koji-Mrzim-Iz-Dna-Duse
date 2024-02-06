import { PropertyRefResponse } from "../response/property-ref-response.model";
import { SmartDeviceRefResponse } from "../response/smart-device-ref-response.model";
import { UserRefResponse } from "../response/user-ref-response,model"

export interface PermissionResponse {
    id:number;
    permissionGiver: UserRefResponse;
    permissionReceiver: UserRefResponse;
    property: PropertyRefResponse;
    device: SmartDeviceRefResponse;
}