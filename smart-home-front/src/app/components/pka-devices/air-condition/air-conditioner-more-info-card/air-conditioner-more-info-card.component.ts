import { Component, ElementRef, ViewChild } from '@angular/core';

@Component({
  selector: 'app-air-conditioner-more-info-card',
  templateUrl: './air-conditioner-more-info-card.component.html',
  styleUrls: ['./air-conditioner-more-info-card.component.css'],
})
export class AirConditionerMoreInfoCardComponent {
  public minTemperature: number;
  public maxTemperature: number;
}
