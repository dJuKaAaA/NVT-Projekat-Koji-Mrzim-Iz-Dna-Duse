import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ObtainedPermissionDevicesPageComponent } from './obtained-permission-devices-page.component';

describe('ObtainedPermissionDevicesPageComponent', () => {
  let component: ObtainedPermissionDevicesPageComponent;
  let fixture: ComponentFixture<ObtainedPermissionDevicesPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ObtainedPermissionDevicesPageComponent]
    });
    fixture = TestBed.createComponent(ObtainedPermissionDevicesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
