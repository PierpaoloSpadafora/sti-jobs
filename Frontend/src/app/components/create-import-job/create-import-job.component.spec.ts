import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Create_import_jobComponent } from './create_import_job.component';

describe('JsonHandlerComponent', () => {
  let component: Create_import_jobComponent;
  let fixture: ComponentFixture<Create_import_jobComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Create_import_jobComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Create_import_jobComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
