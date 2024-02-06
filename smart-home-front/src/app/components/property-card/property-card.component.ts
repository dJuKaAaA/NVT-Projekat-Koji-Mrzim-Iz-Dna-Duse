import { HttpErrorResponse } from '@angular/common/http';
import { AfterViewInit, ChangeDetectorRef, Component, EventEmitter, Input, Output } from '@angular/core';
import { PropertyStatusRequestDto } from 'src/app/model/request/property-request.model';
import { PropertyResponseDto } from 'src/app/model/response/property-response.model';
import { AuthService } from 'src/app/service/auth.service';
import { ImageService } from 'src/app/service/image.service';
import { PropertyService } from 'src/app/service/property.service';
import {environment} from "../../environments/environment";
import {DomSanitizer, SafeUrl} from "@angular/platform-browser";
import { Router } from '@angular/router';

@Component({
  selector: 'app-property-card',
  templateUrl: './property-card.component.html',
  styleUrls: ['./property-card.component.css'],
})
export class PropertyCardComponent implements AfterViewInit {
  constructor(
    private imageService: ImageService,
    private authService: AuthService,
    private propertyService: PropertyService,
    private cdr: ChangeDetectorRef,
    private sanitizer: DomSanitizer,
    private router: Router
  ) {
    if(this.authService.getRole() == 'ADMIN' || this.authService.getRole() == "SUPER_ADMIN") this.isAdmin = true
  }
  @Input() property: PropertyResponseDto | null = null
  @Input() index: number | null = null
  @Output() forRemove = new EventEmitter<PropertyResponseDto | null>();
  isAdmin = false

  public imageBlob: SafeUrl = {} as SafeUrl;
  public imgPresent = false;

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
    this.dialog_id = "property-card-" + this.index;
    this.cdr.detectChanges()
    this.dialog = document.getElementById(this.dialog_id) as HTMLDialogElement
    console.log(this.property?.image.name);
    this.imageService.getImage(String(this.property?.image.name), environment.nginxPropertyDirBaseUrl).subscribe({
      next: (response: Blob) => {
        const objectURL = URL.createObjectURL(response);
        this.imageBlob = this.sanitizer.bypassSecurityTrustUrl(objectURL);
        this.imgPresent = true;
      },
      error: (err: HttpErrorResponse) => console.error(err),
    });
  }

  dialog: HTMLDialogElement | null = null
  dialog_id: string = ''
  reason: string = '';
  showDialog() { console.log(this.dialog_id); this.dialog?.showModal()}
  denyDialogButton() {
    this.dialog?.close()
    let property: PropertyStatusRequestDto = {id: this.property?.id ?? -1, isApproved: false, denialReason: this.reason }
    this.propertyService.changeStatus(property).subscribe({
      next: () => this.forRemove.emit(this.property),
      error: (error: HttpErrorResponse) => console.log(error.message)
    })
  }
  cancelDialogButton() {
    this.reason = '';
    this.dialog?.close()
  }

  approveRequest() {
    let property: PropertyStatusRequestDto = {id: this.property?.id ?? -1, isApproved: true, denialReason: '' }
    this.propertyService.changeStatus(property).subscribe({
      next: () => this.forRemove.emit(this.property),
      error: (error: HttpErrorResponse) => console.log(error.message)
    })
  }

  public goToDevices() {
    console.log(this.isAdmin);
    console.log(this.property?.status)

    if (!this.isAdmin && this.property?.status.toString() === 'APPROVED') {
      this.router.navigate([`devices/${this.property.id}`]);
    }
  }
}
