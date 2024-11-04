import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewExportDeleteJobsComponent } from './view-export-delete-jobs.component';

describe('ViewExportDeleteJobsComponent', () => {
  let component: ViewExportDeleteJobsComponent;
  let fixture: ComponentFixture<ViewExportDeleteJobsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ViewExportDeleteJobsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewExportDeleteJobsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
