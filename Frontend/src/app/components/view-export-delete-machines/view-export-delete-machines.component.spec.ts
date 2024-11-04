import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewExportDeleteMachinesComponent } from './view-export-delete-machines.component';

describe('ViewExportDeleteMachinesComponent', () => {
  let component: ViewExportDeleteMachinesComponent;
  let fixture: ComponentFixture<ViewExportDeleteMachinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ViewExportDeleteMachinesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewExportDeleteMachinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
