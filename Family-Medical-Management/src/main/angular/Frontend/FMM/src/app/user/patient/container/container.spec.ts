import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PatientContainer } from './container';

describe('Container', () => {
  let component: PatientContainer;
  let fixture: ComponentFixture<PatientContainer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatientContainer]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PatientContainer);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
