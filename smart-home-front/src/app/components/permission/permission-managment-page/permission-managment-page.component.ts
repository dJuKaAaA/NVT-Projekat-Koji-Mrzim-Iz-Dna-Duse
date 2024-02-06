import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PermissionResponse } from 'src/app/model/permissions/permission-response.model';
import { PropertyResponseDto } from 'src/app/model/response/property-response.model';
import { SmartDeviceResponse } from 'src/app/model/response/smart-device-response.model';
import { AuthService } from 'src/app/service/auth.service';
import { PermissionService } from 'src/app/service/permission.service';
import { PropertyService } from 'src/app/service/property.service';
import { SmartDeviceService } from 'src/app/service/smart-device.service';
import { PermissionFilter } from '../filterDto';

@Component({
  selector: 'app-permission-managment-page',
  templateUrl: './permission-managment-page.component.html',
  styleUrls: ['./permission-managment-page.component.css']
})
export class PermissionManagmentPageComponent implements OnInit {



  permissions:Array<PermissionResponse> = []
  originalFetchData:Array<PermissionResponse> = []

  properties:Array<PropertyResponseDto> = []

  addEmailForm: FormGroup;
  addProperty:PropertyResponseDto | null = null;
  addDevice:SmartDeviceResponse | null | string = null;
  addDevices:Array<SmartDeviceResponse> = []

  removeEmailForm:FormGroup;
  removeProperty:PropertyResponseDto | null = null;
  removeDevice:SmartDeviceResponse | null | string = null;
  removeDevices:Array<SmartDeviceResponse> = []
  isAddDeviceSelectDisabled:boolean = true;
  isRemoveDeviceSelectDisabled:boolean = true;

  isAddButtonEnabled = false;
  isRemoveButtonEnable = false;

  isFilterOpen:boolean = false;

  constructor(
    private authService:AuthService, 
    private propertyService:PropertyService,
    private permissionService:PermissionService,
    private smartDeviceService:SmartDeviceService,
    private formBuilder:FormBuilder) {
      this.addEmailForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]]
    });

      this.removeEmailForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit(): void {
    this.propertyService.getAllByEmail(this.authService.getEmail())
    .subscribe(response => {
      this.properties = response; 
    }, (err) => alert(err.error.message))
    this.permissionService.getAllGivenPermissions(
      this.authService.getEmail()).subscribe(response => {
        this.permissions = response;
        this.originalFetchData = response;
    }, (err) => alert(err.error.message))
  }

  onAddEmailChange() {
    if (!this.isAddDeviceSelectDisabled && this.addEmailForm.get('email')?.valid) {
      this.isAddButtonEnabled = true
    } else {
      this.isAddButtonEnabled = false;
    }
  }
  
  onAddPropertyChange() {
    if (this.addProperty != null) {
      this.smartDeviceService.getByPropertyId(this.addProperty.id).subscribe(response => {
        this.isAddDeviceSelectDisabled = false
        this.addDevices = response;
      }, (err) => alert(err.error.message))
    }
  }

  onAddDeviceChange() {
    if(this.addEmailForm.get('email')?.valid) {
      this.isAddButtonEnabled = true;
    }
  }
  
  onAdd():void {
    if (this.addDevice !== null && this.addProperty !== null) {
      if (typeof this.addDevice === 'string') {
        this.permissionService.addPropertyPermissions(this.addEmailForm.get('email')?.value, this.addProperty?.id)
        .subscribe(response => {
          alert("You have successfully added permissions!");
          this.reloadPage();
        }, (err) => alert(err.error.message))
      } else {
        this.permissionService.addDevicePermission(this.addEmailForm.get('email')?.value, this.addDevice.id)
        .subscribe(response => {
          alert("You have successfully added permission!")
          this.reloadPage();
        }, (err) => alert(err.error.message))
      } 
    } 
  }

  onRemoveEmailChange() {
    if (!this.isRemoveDeviceSelectDisabled && this.removeEmailForm.get('email')?.valid) {
      this.isRemoveButtonEnable = true
    } else {
      this.isRemoveButtonEnable = false;
    }
  }
  
  onRemovePropertyChange() {
    if (this.removeProperty != null) {
      this.smartDeviceService.getByPropertyId(this.removeProperty.id).subscribe(response => {
        this.isRemoveDeviceSelectDisabled = false;
        this.removeDevices = response;
      }, (err) => alert(err.error.message))
    }
  }
  onRemoveDeviceChange() {
    if(this.removeEmailForm.get('email')?.valid) {
      this.isRemoveButtonEnable = true;
    }
  }

  onRemove():void {
    if (this.removeDevice !== null && this.removeProperty !== null) {
      
      if (typeof this.removeDevice === 'string') {
        this.permissionService.removeAllPropertyPermissions(
          this.removeEmailForm.get('email')?.value, 
          this.removeProperty?.id)
        .subscribe(response => {
          alert("You have successfully removed permissions!")
          this.originalFetchData = this.originalFetchData
          .filter(el => el.property.id !==  this.removeProperty?.id)
         this.permissions = this.originalFetchData;
        }, (err) => alert(err.error.message))
      } else {
          let deviceId = this.removeDevice.id;
        this.permissionService.removeAllPropertyPermissions(
          this.removeEmailForm.get('email')?.value, 
          this.removeDevice.id)
        .subscribe(response => {
          alert("You have successfully removed permission!")
          this.originalFetchData = this.originalFetchData
          .filter(el => el.device.id !==  deviceId)
         this.permissions = this.originalFetchData;
        }, (err) => alert(err.error.message))
      } 
    } 
  }

  removePermission(permissionId: number) {
    this.permissionService.removeById(permissionId).subscribe(response => {
      alert("You have successfully removed permission!")
      this.originalFetchData = this.originalFetchData.filter(el => el.id !== permissionId)
      this.permissions = this.originalFetchData;
    }, (err) => err.error.message);
  }

  applyFilter($event: PermissionFilter) {
    console.log($event)
    if ($event.isFilterByEmail) {
      this.permissions = this.originalFetchData
      .filter(el => el.permissionReceiver.email === $event.email)
    }
    else if ($event.isFilterByPropertyName) {
      this.permissions = this.originalFetchData
      .filter(el => el.property.name === $event.propertyName)
    }
    else if ($event.isFilterByDeviceName) {
      this.permissions = this.originalFetchData
      .filter(el => el.device.name === $event.deviceName)
    }
    this.closeFilterForm();
  }

   public openFilterForm():void {
    this.isFilterOpen = true;
  }

  public closeFilterForm():void {
    this.isFilterOpen = false;
  }

  public reloadPage():void {
    window.location.reload()
  }

  reset() {
    this.permissions = this.originalFetchData;
  }

}
