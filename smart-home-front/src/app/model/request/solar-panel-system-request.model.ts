import {SolarPanelRequest} from "./solar-panel-request.model";
import {ImgRequest} from "./img-request.model";
import {SmartDeviceRequest} from "./smart-device-request.model";

export interface SolarPanelSystemRequest extends SmartDeviceRequest {
  solarPanels: Array<SolarPanelRequest>,
}
