import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { PropertyResponseDto } from 'src/app/model/response/property-response.model';
import { AuthService } from 'src/app/service/auth.service';
import { PermissionService } from 'src/app/service/permission.service';
import { PropertyService } from 'src/app/service/property.service';

@Component({
  selector: 'app-properties-page',
  templateUrl: './properties-page.component.html',
  styleUrls: ['./properties-page.component.css'],
})
export class PropertiesPageComponent {
  isAdmin = false
  isEmptyList = false
  properties : PropertyResponseDto[] = []
  permissions: PropertyResponseDto[] = []

  constructor(
    private router: Router,
    private propertyService: PropertyService,
    private authService: AuthService,
    private permissionService: PermissionService) {

    if(authService.getRole() == 'ADMIN' || authService.getRole() == "SUPER_ADMIN") this.isAdmin = true
    if(authService.getRole() == 'USER') {
      authService.getEmail()
      permissionService.getAllObtainedProperties(authService.getEmail()).subscribe(response => {
        this.permissions = response;
      })

      propertyService.getAllByEmail(authService.getEmail()).subscribe({
        next: (properties: PropertyResponseDto[]) => {
          this.properties = properties
          if (properties.length == 0) this.isEmptyList = true
        },
        error: (error: HttpErrorResponse) => console.log(error.message)
      })
    } else {
      propertyService.getAllRequests().subscribe({
        next: (properties: PropertyResponseDto[]) =>
        {
          this.properties = properties
          if (properties.length == 0) this.isEmptyList = true
        } ,
        error: (error: HttpErrorResponse) => console.log(error.message)
      })
    }
  }


  removeElementEvent(forRemove: PropertyResponseDto | null) {
    if (forRemove) {
      const indexToRemove = this.properties.findIndex(prop => prop.id === forRemove.id)
      if (indexToRemove !== -1) this.properties.splice(indexToRemove, 1)
    }
  }

  addNewButtonClicked() {this.router.navigate(['add-property']);}

}
