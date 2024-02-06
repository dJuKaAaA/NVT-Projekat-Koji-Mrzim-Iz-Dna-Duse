import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WashingMachineMoreInfoCardComponent } from './washing-machine-more-info-card.component';

describe('WashingMachineMoreInfoCardComponent', () => {
  let component: WashingMachineMoreInfoCardComponent;
  let fixture: ComponentFixture<WashingMachineMoreInfoCardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [WashingMachineMoreInfoCardComponent]
    });
    fixture = TestBed.createComponent(WashingMachineMoreInfoCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
