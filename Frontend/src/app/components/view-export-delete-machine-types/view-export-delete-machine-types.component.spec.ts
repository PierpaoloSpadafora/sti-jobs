import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewExportDeleteMachineTypesComponent } from './view-export-delete-machine-types.component';

describe('ViewExportDeleteMachineTypesComponent', () => {
  let component: ViewExportDeleteMachineTypesComponent;
  let fixture: ComponentFixture<ViewExportDeleteMachineTypesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ViewExportDeleteMachineTypesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewExportDeleteMachineTypesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
