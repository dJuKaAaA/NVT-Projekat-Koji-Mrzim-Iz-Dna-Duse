import { Injectable } from '@angular/core';
import { HttpBackend, HttpClient, HttpHeaders } from '@angular/common/http';
import { ImgRequest } from '../model/request/img-request.model';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ImageService {
  private http: HttpClient;

  constructor(handler: HttpBackend) {
    this.http = new HttpClient(handler);
  }

  public getImage(image: string, path: string): Observable<Blob> {
    let headers = new HttpHeaders();
    headers = headers.append('Authorization', environment.nginxKey);

    return this.http.get(`${path}/${image}`, {
      headers: headers,
      responseType: 'blob', // binary response
    });
  }

  public async convertImageForSending(file: File): Promise<ImgRequest> {
    if (!this.isImageUploaded(file)) {
      throw new Error('You must upload image!');
    }
    const imgBase64 = await this.convertFileToBase64StringAsync(file);
    const img: ImgRequest = {
      name: file.name,
      format: file.type.split('/')[1],
      base64FormatString: imgBase64,
    };
    return img;
  }

  // TODO delete
  public convertImageForDisplayOnResponse(img: ImgRequest): string {
    return `data:image/${img.format};base64,` + img.base64FormatString;
  }

  public async convertImageForDisplayOnUpload(file: File): Promise<string> {
    if (!this.isUploadedImageValid(file)) {
      throw new Error('Image is not valid!');
    }
    return new Promise<string>((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        const imageUrl = e.target.result;
        resolve(imageUrl);
      };
      reader.readAsDataURL(file);
    });
  }

  private convertFileToBase64StringAsync(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => {
        if (typeof reader.result === 'string') {
          let image = reader.result.split(',')[1];
          resolve(image);
        } else {
          reject('Not possible to convert to base64 string!');
        }
      };
      reader.readAsDataURL(file);
    });
  }

  private isImageUploaded(image: File): boolean {
    return image.name ? true : false;
  }
  private isUploadedImageValid(image: File): boolean {
    if (
      image &&
      image.type.match(/image\/*/) &&
      image.name.match(/\.(jpg|jpeg|png)$/)
    ) {
      return true;
    } else {
      return false;
    }
  }
}
