import { ImgResponse } from '../response/img-response.model';

export interface UserResponse {
  id: number;
  name: string;
  email: string;
  profileImage: ImgResponse;
}
