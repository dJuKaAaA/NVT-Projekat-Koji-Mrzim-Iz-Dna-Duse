import {
  Component,
  ElementRef,
  EventEmitter,
  Output,
  ViewChild,
} from '@angular/core';
import { ImageService } from '../../service/image.service';
import { AuthService } from '../../service/auth.service';
import { ImgRequest } from '../../model/request/img-request.model';
import { UserRequest } from '../../model/request/user-request.model';
import { UserResponse } from '../../model/response/user-response.model';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-create-account',
  templateUrl: './create-account.component.html',
  styleUrls: ['./create-account.component.css'],
})
export class CreateAccountComponent {
  public name: string = '';
  public email: string = '';
  public password: string = '';
  public uploadedImage: File = {} as File;
  public imageUrl: string = '';
  public imageUploaded: boolean = false;

  @ViewChild('fileInput') fileInput!: ElementRef;

  constructor(
    private imageService: ImageService,
    private authService: AuthService
  ) {}

  openFileInput() {
    this.fileInput.nativeElement.click(); // Trigger a click event on the file input
  }
  public async onImageUpload(event: any) {
    this.uploadedImage = event.target.files[0];
    try {
      this.imageUrl = await this.imageService.convertImageForDisplayOnUpload(
        this.uploadedImage
      );
      this.imageUploaded = true;
    } catch (error: any) {
      alert(error.message);
    }
  }

  public async createAccount() {
    try {
      let img: ImgRequest = await this.imageService.convertImageForSending(
        this.uploadedImage
      );
      let createAccountRequest: UserRequest = {
        name: this.name,
        email: this.email,
        password: this.password,
        profileImage: img,
      };
      console.log(createAccountRequest);

      localStorage.removeItem('jwt');
      this.authService.creteUserAccount(createAccountRequest).subscribe({
        next(response: UserResponse) {
          alert(`Activation email send on ${response.email}`);
        },
        error: (error: any) => {
          if (error instanceof HttpErrorResponse) {
            alert(
              `Status Code: ${error.status}\nMessage: ${error.error?.message}`
            );
          } else {
            alert('Error check console!');
            console.log(error);
          }
        },
      });
    } catch (error) {
      alert('Error, check console!');
      console.log(error);
    }
  }
}
