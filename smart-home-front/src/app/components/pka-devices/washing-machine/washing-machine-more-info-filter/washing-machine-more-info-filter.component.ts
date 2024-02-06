import { Component, EventEmitter, Output } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-washing-machine-more-info-filter',
  templateUrl: './washing-machine-more-info-filter.component.html',
  styleUrls: ['./washing-machine-more-info-filter.component.css']
})
export class WashingMachineMoreInfoFilterComponent {

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
