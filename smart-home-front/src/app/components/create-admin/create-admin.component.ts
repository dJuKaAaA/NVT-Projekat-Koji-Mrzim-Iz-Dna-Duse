import { HttpErrorResponse } from '@angular/common/http';
import { Component, ElementRef, ViewChild } from '@angular/core';
import { ImgRequest } from 'src/app/model/request/img-request.model';
import { UserRequest } from 'src/app/model/request/user-request.model';
import { UserResponse } from 'src/app/model/response/user-response.model';
import { AuthService } from 'src/app/service/auth.service';
import { ImageService } from 'src/app/service/image.service';

@Component({
  selector: 'app-create-admin',
  templateUrl: './create-admin.component.html',
  styleUrls: ['./create-admin.component.css'],
})
export class CreateAdminComponent {
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

      this.authService.creteAdminAccount(createAccountRequest).subscribe({
        next(response: UserResponse) {
          alert(`Admin with email ${response.email} is created!`);
        },
        error: (error: HttpErrorResponse) => {
          alert(
            `Status Code: ${error.status}\nMessage: ${error.error?.message}`
          );
        },
      });
    } catch (error) {
      console.log(error);
      alert('Error, check console!');
    }
  }
}
