import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-time-range-dropdown-select',
  templateUrl: './time-range-dropdown-select.component.html',
  styleUrls: ['./time-range-dropdown-select.component.css']
})
export class TimeRangeDropdownSelectComponent {
  @Input() haveRealtime: boolean = true
  @Output() onSelect = new EventEmitter<string>
  // dropdown
  openedDropdown = false
  selectedTimeId = ""
  selectedTimeForDisplay = ""

  onDropdownInputClick() {this.openedDropdown = !this.openedDropdown}
  onSelectItem(time: string, displayText: string) {
    this.selectedTimeId = time
    this.selectedTimeForDisplay = displayText
    this.openedDropdown = false
    this.onSelect.emit(this.selectedTimeId)
  }
}
