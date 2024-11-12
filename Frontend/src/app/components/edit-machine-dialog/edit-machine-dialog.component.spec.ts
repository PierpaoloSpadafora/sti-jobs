import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditMachineDialogComponent } from './edit-machine-dialog.component';

describe('EditMachineDialogComponent', () => {
  let component: EditMachineDialogComponent;
  let fixture: ComponentFixture<EditMachineDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditMachineDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditMachineDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
