import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JsonHandlerComponent } from './json-handler.component';

describe('JsonHandlerComponent', () => {
  let component: JsonHandlerComponent;
  let fixture: ComponentFixture<JsonHandlerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [JsonHandlerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JsonHandlerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
