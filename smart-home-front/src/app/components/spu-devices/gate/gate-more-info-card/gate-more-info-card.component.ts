import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-gate-more-info-card',
  templateUrl: './gate-more-info-card.component.html',
  styleUrls: ['./gate-more-info-card.component.css']
})
export class GateMoreInfoCardComponent {

  @Output() onPlatesChanged = new EventEmitter<string[]>

  @Input() carPlates: string[] = []
  plateInput: string
  haveError: boolean = false

  onChipClick(plate: string) {
    var index = this.carPlates.indexOf(plate)
    this.carPlates.splice(index, 1);
    this.onPlatesChanged.emit(this.carPlates)
  }
  onInputChange() {
    if (this.carPlates.indexOf(this.plateInput) > 0) this.haveError = true
    else this.haveError = false
  }
  onEnterClick() {
    if (this.haveError) return
    this.carPlates.push(this.plateInput)
    this.plateInput = ""
    this.onPlatesChanged.emit(this.carPlates)
  }
}
