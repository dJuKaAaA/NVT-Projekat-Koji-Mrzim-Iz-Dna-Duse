import { Component } from '@angular/core';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-home-battery-info-page',
  templateUrl: './home-battery-info-page.component.html',
  styleUrls: ['./home-battery-info-page.component.css']
})
export class HomeBatteryInfoPageComponent {
  public deviceId: number
  constructor(
    private activatedRoute: ActivatedRoute) {
    this.deviceId = Number(this.activatedRoute.snapshot.paramMap.get("deviceId"));
  }
}
