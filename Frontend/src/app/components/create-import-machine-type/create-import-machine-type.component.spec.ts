import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateImportMachineTypeComponent } from './create-import-machine-type.component';

describe('CreateImportMachineTypeComponent', () => {
  let component: CreateImportMachineTypeComponent;
  let fixture: ComponentFixture<CreateImportMachineTypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateImportMachineTypeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateImportMachineTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
