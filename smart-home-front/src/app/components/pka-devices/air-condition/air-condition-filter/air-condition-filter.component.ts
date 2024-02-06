import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-air-condition-filter',
  templateUrl: './air-condition-filter.component.html',
  styleUrls: ['./air-condition-filter.component.css']
})
export class AirConditionFilterComponent {

  public filterForm: FormGroup;
  @Output() closeEvent = new EventEmitter();
  @Output() applyFilterEvent = new EventEmitter();

  constructor(private fb: FormBuilder) {
    this.filterForm = this.fb.group({
      userEmail: ['', Validators.email],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required]
    });
  }

  public applyFilters() {
    const form = this.filterForm.value;
    this.applyFilterEvent.emit({
      userEmail: form.userEmail,
      startDate: form.startDate,
      endDate: form.endDate
    });
  }

  public closeFilterForm() {
    this.closeEvent.emit();
  }
}
