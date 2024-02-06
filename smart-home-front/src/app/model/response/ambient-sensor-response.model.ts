import {ImgResponse} from "./img-response.model";

export interface AmbientSensorResponse {
  id: number,
  name: string,
  groupType: string,
  deviceActive: boolean,
  image: ImgResponse,
  deviceType: string
  roomTemperature: number,
  roomHumidity: number
}
