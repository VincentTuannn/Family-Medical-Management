import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardAdmin } from './dashboard-admin';

describe('DashboardAdmin', () => {
  let component: DashboardAdmin;
  let fixture: ComponentFixture<DashboardAdmin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardAdmin]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DashboardAdmin);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
