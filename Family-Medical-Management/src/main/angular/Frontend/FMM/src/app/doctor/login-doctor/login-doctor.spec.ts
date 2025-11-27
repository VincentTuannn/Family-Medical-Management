import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginDoctor } from './login-doctor';

describe('LoginDoctor', () => {
  let component: LoginDoctor;
  let fixture: ComponentFixture<LoginDoctor>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginDoctor]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginDoctor);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
