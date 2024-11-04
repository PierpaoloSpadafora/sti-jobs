import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowMachineTypeComponent } from './show-machine-type.component';

describe('ShowMachineTypeComponent', () => {
  let component: ShowMachineTypeComponent;
  let fixture: ComponentFixture<ShowMachineTypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ShowMachineTypeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ShowMachineTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
