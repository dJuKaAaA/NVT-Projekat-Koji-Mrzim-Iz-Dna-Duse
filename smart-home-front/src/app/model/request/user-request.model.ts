import { ImgRequest } from './img-request.model';

export interface UserRequest {
  name: string;
  email: string;
  password: string;
  profileImage: ImgRequest;
}
