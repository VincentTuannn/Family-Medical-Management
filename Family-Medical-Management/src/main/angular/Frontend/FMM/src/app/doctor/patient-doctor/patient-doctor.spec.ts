import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PatientDoctor } from './patient-doctor';

describe('PatientDoctor', () => {
  let component: PatientDoctor;
  let fixture: ComponentFixture<PatientDoctor>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatientDoctor]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PatientDoctor);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
