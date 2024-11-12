import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditMachineTypesDialogComponent } from './edit-machine-types-dialog.component';

describe('EditMachineTypesDialogComponent', () => {
  let component: EditMachineTypesDialogComponent;
  let fixture: ComponentFixture<EditMachineTypesDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditMachineTypesDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditMachineTypesDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
