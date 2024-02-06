import { Component, EventEmitter, Output } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { PermissionFilter } from '../filterDto';

@Component({
  selector: 'app-filter-permissions',
  templateUrl: './filter-permissions.component.html',
  styleUrls: ['./filter-permissions.component.css']
})
export class FilterPermissionsComponent {
  public filterForm: FormGroup;
  @Output() closeEvent = new EventEmitter();
  @Output() applyFilterEvent = new EventEmitter();

  email:string;
  propertyName:string;
  deviceName:string;

  isFilteredByEmail:boolean = false;
  isFilteredByProperty:boolean = false;
  isFilteredByDevice:boolean = false;

  constructor() {}

  public applyFilters() {
      let filterObj:PermissionFilter = {
        email: this.email,
        propertyName:this.propertyName,
        deviceName:this.deviceName,
        isFilterByEmail:this.isFilteredByEmail,
        isFilterByPropertyName:this.isFilteredByProperty,
        isFilterByDeviceName:this.isFilteredByDevice
      } 
      this.applyFilterEvent.emit(filterObj)
  }

  public closeFilterForm() {
    this.closeEvent.emit();
  }

}
