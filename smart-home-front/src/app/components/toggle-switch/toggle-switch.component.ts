import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-toggle-switch',
  templateUrl: './toggle-switch.component.html',
  styleUrls: ['./toggle-switch.component.css']
})
export class ToggleSwitchComponent {
  @Input() public isChecked!: boolean;
  @Output() isCheckedChange = new EventEmitter<boolean>();

  onToggle(newCheckedValue: boolean) {
    console.log('Toggle event:', newCheckedValue);
    this.isChecked = newCheckedValue;
    this.isCheckedChange.emit(this.isChecked);
  }
}
