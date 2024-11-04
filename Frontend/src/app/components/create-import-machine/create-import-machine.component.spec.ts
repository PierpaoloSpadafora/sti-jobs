import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateImportMachineComponent } from './create-import-machine.component';

describe('CreateImportMachineComponent', () => {
  let component: CreateImportMachineComponent;
  let fixture: ComponentFixture<CreateImportMachineComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateImportMachineComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateImportMachineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
