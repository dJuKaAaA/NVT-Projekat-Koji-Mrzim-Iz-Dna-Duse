import {ImgResponse} from "./img-response.model";

export interface SmartDeviceResponse {
  id: number,
  name: string,
  groupType: string,
  deviceActive: boolean,
  image: ImgResponse,
  deviceType: string
}
