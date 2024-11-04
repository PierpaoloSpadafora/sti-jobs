import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowMachineComponent } from './show-machine.component';

describe('ShowMachineComponent', () => {
  let component: ShowMachineComponent;
  let fixture: ComponentFixture<ShowMachineComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ShowMachineComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ShowMachineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
