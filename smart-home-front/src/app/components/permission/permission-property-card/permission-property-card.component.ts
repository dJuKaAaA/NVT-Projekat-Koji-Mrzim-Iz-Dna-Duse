import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component, Input } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { environment } from 'src/app/environments/environment';
import { PropertyResponseDto } from 'src/app/model/response/property-response.model';
import { AuthService } from 'src/app/service/auth.service';
import { ImageService } from 'src/app/service/image.service';
import { PropertyService } from 'src/app/service/property.service';

@Component({
  selector: 'app-permission-property-card',
  templateUrl: './permission-property-card.component.html',
  styleUrls: ['./permission-property-card.component.css']
})
export class PermissionPropertyCardComponent {

  @Input() property: PropertyResponseDto | null = null
  @Input() index: number | null = null

  public imageBlob: SafeUrl = {} as SafeUrl;
  public imgPresent = false;

  constructor( private imageService: ImageService,
    private sanitizer: DomSanitizer,
    private router: Router ) {}

  getType() {
    console.log(this.property)
    if (this.property?.type.toString() === 'HOUSE') return 'House';
    else return 'Apartment';
  }
  getStatus() {
    if (this.property?.status.toString() === 'APPROVED') return 'Approved'
    else if (this.property?.status.toString() == 'DENIED') return 'Denied'
    else return 'Pending';
  }

  ngAfterViewInit() {
    this.imageService.getImage(String(this.property?.image.name), environment.nginxPropertyDirBaseUrl).subscribe({
      next: (response: Blob) => {
        const objectURL = URL.createObjectURL(response);
        this.imageBlob = this.sanitizer.bypassSecurityTrustUrl(objectURL);
        this.imgPresent = true;
      },
      error: (err: HttpErrorResponse) => console.error(err),
    });
  }

  public goToDevices() {
    this.router.navigate([`permission-devices/${this.property?.id}`]);
  }
}
