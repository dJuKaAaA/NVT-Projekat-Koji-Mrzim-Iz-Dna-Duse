import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PermissionPropertyCardComponent } from './permission-property-card.component';

describe('PermissionPropertyCardComponent', () => {
  let component: PermissionPropertyCardComponent;
  let fixture: ComponentFixture<PermissionPropertyCardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PermissionPropertyCardComponent]
    });
    fixture = TestBed.createComponent(PermissionPropertyCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
