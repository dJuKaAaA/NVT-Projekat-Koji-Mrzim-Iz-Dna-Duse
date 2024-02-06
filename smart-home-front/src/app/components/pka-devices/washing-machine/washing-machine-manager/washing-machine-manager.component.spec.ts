import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WashingMachineManagerComponent } from './washing-machine-manager.component';

describe('WashingMachineManagerComponent', () => {
  let component: WashingMachineManagerComponent;
  let fixture: ComponentFixture<WashingMachineManagerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [WashingMachineManagerComponent]
    });
    fixture = TestBed.createComponent(WashingMachineManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
