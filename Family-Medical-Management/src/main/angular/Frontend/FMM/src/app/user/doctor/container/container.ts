import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { DoctorService } from '../../../features/service/doctor-service/doctor.service';  
import { DoctorDTO } from '../../../features/model/doctor.model';  
import { DoctorDialogComponent } from '../dialog/doctor-dialog.component';
import { AuthService } from '../../../features/service/auth-service/auth.service';
import { filter, take, takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-container',
  imports: [CommonModule, MatTableModule, MatCardModule, MatButtonModule, MatIconModule],
  templateUrl: './container.html',
  styleUrl: './container.scss',
})
export class DoctorContainer implements OnInit, OnDestroy {
  displayedColumns: string[] = ['fullName', 'specialty', 'clinicName', 'phone', 'email', 'actions'];  // Cột table
  doctors: DoctorDTO[] = [];  // Danh sách bác sĩ
  selectedDoctor: DoctorDTO | null = null;  // Bác sĩ được chọn để xem chi tiết
  private destroy$ = new Subject<void>();

  constructor(
    private doctorService: DoctorService,
    public dialog: MatDialog,
    private cdr: ChangeDetectorRef,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.waitForTokenAndLoad();
  }

  private waitForTokenAndLoad() {
    const token = this.authService.getToken();
    if (token) {
      // Token đã sẵn sàng -> load doctors (delay nhẹ để đảm bảo interceptor đã attach token)
      setTimeout(() => this.loadDoctors(), 50);
      return;
    }

    // Token chưa có -> chờ observable emit
    this.authService.isLoggedIn$
      .pipe(
        filter((value): value is string => !!value),
        take(1),
        takeUntil(this.destroy$)
      )
      .subscribe(() => this.loadDoctors());
  }

  loadDoctors() {
    this.doctorService.getAllDoctors().subscribe({
      next: (data) => {
        this.doctors = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Lỗi load bác sĩ:', err);
        if (err.status === 403) {
          console.log('🔄 Lỗi 403, thử lại sau 200ms...');
          setTimeout(() => this.loadDoctors(), 200);
        } else {
          this.cdr.detectChanges();
        }
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  viewDoctorDetail(doctor: DoctorDTO) {
    this.selectedDoctor = doctor;  // Hiển thị chi tiết trong card
  }

  hireDoctor(doctorId: number) {
    const doctor = this.doctors.find(d => d.doctorId === doctorId);
    if (doctor) {
      const dialogRef = this.dialog.open(DoctorDialogComponent, {
        width: '400px',
        data: { doctor }  // Truyền thông tin bác sĩ cho dialog
      });

      dialogRef.afterClosed().subscribe((result: any) => {
        if (result) {
          // Gọi API "thuê bác sĩ"
          console.log('Yêu cầu thuê bác sĩ:', doctor);
          this.doctorService.hireDoctor(doctorId, result);
        }
      });
    }
  }
}
