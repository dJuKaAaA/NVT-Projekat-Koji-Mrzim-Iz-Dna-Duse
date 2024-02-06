import {ImgResponse} from "./img-response.model";
import {SmartDeviceResponse} from "./smart-device-response.model";
import {SolarPanelResponse} from "./solar-panel-response.model";

export interface SolarPanelSystemResponse extends SmartDeviceResponse {
  solarPanels: Array<SolarPanelResponse>
}
