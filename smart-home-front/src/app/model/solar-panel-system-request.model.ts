import {ImgRequest} from "./request/img-request.model";
import {SolarPanelRequest} from "./request/solar-panel-request.model";
import {SmartDeviceRequest} from "./request/smart-device-request.model";

export interface SolarPanelSystemRequest extends SmartDeviceRequest{
  solarPanels: Array<SolarPanelRequest>
}
