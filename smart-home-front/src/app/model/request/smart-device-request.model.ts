import {ImgRequest} from "./img-request.model";

export interface SmartDeviceRequest {
  name: string,
  propertyId: number,
  usesBatteries: boolean,
  image: ImgRequest,
  powerConsumption: number
}
