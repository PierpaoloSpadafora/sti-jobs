import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JobProjectHandlerComponent } from './job-project-handler.component';

describe('JsonHandlerComponent', () => {
  let component: JobProjectHandlerComponent;
  let fixture: ComponentFixture<JobProjectHandlerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [JobProjectHandlerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JobProjectHandlerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
