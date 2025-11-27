import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatChipsModule } from '@angular/material/chips';
import { RouterModule } from '@angular/router';
import { AppointmentService } from '../../../features/service/appointment-service/appointment.service';
import { DoctorService } from '../../../features/service/doctor-service/doctor.service';
import { PatientService } from '../../../features/service/patient-service/patient.service';
import { AuthService } from '../../../features/service/auth-service/auth.service';
import { AppointmentDTO } from '../../../features/model/appointment.model';
import { DoctorDTO } from '../../../features/model/doctor.model';
import { PatientDTO } from '../../../features/model/patient.model';
import { AppointmentDialogComponent } from '../dialog/appointment-dialog.component';
import { filter, take, takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';

interface AppointmentDisplay {
  appointment: AppointmentDTO;
  doctorName?: string;
  patientName?: string;
}

@Component({
  selector: 'app-appointment-container',
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatDialogModule,
    MatChipsModule,
    RouterModule
  ],
  templateUrl: './container.html',
  styleUrl: './container.scss',
})
export class AppointmentContainer implements OnInit, OnDestroy {
  displayedColumns: string[] = ['appointmentDate', 'doctor', 'patient', 'status', 'notes', 'actions'];
  appointments: AppointmentDisplay[] = [];
  doctors: DoctorDTO[] = [];
  patients: PatientDTO[] = [];
  loading = false;
  private destroy$ = new Subject<void>();

  constructor(
    private appointmentService: AppointmentService,
    private doctorService: DoctorService,
    private patientService: PatientService,
    private authService: AuthService,
    public dialog: MatDialog,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.waitForTokenAndLoad();
  }

  private waitForTokenAndLoad() {
    const token = this.authService.getToken();
    if (token) {
      setTimeout(() => {
        this.loadData();
      }, 50);
      return;
    }

    this.authService.isLoggedIn$
      .pipe(
        filter((value): value is string => !!value),
        take(1),
        takeUntil(this.destroy$)
      )
      .subscribe(() => this.loadData());
  }

  loadData() {
    this.loading = true;
    // Load appointments, doctors, và patients song song
    this.appointmentService.getMyAppointments().subscribe({
      next: (appointments) => {
        this.loadDoctorsAndPatients(appointments);
      },
      error: (err) => {
        console.error('Lỗi load appointments:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadDoctorsAndPatients(appointments: AppointmentDTO[]) {
    // Load doctors và patients để map tên
    this.doctorService.getAllDoctors().subscribe({
      next: (doctors) => {
        this.doctors = doctors;
        this.patientService.getMyPatients().subscribe({
          next: (patients) => {
            this.patients = patients;
            this.mapAppointments(appointments);
          },
          error: (err) => {
            console.error('Lỗi load patients:', err);
            this.mapAppointments(appointments);
          }
        });
      },
      error: (err) => {
        console.error('Lỗi load doctors:', err);
        this.mapAppointments(appointments);
      }
    });
  }

  mapAppointments(appointments: AppointmentDTO[]) {
    this.appointments = appointments.map(apt => ({
      appointment: apt,
      doctorName: this.doctors.find(d => d.doctorId === apt.doctorId)?.fullName || `Bác sĩ ID: ${apt.doctorId}`,
      patientName: this.patients.find(p => p.patientId === apt.patientId)?.fullName || `Bệnh nhân ID: ${apt.patientId}`
    }));
    this.loading = false;
    this.cdr.detectChanges();
  }

  openCreateDialog() {
    const dialogRef = this.dialog.open(AppointmentDialogComponent, {
      width: '500px',
      data: { action: 'create' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.appointmentService.createAppointment(result).subscribe({
          next: () => {
            this.loadData();
            alert('Đặt lịch hẹn thành công!');
          },
          error: (err) => {
            console.error('Lỗi đặt lịch hẹn:', err);
            alert('Lỗi đặt lịch hẹn! Vui lòng thử lại.');
          }
        });
      }
    });
  }

  openEditDialog(appointment: AppointmentDTO) {
    const dialogRef = this.dialog.open(AppointmentDialogComponent, {
      width: '500px',
      data: { action: 'edit', appointment }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && appointment.appointmentId) {
        this.appointmentService.updateAppointment(appointment.appointmentId, result).subscribe({
          next: () => {
            this.loadData();
            alert('Cập nhật lịch hẹn thành công!');
          },
          error: (err) => {
            console.error('Lỗi cập nhật lịch hẹn:', err);
            alert('Lỗi cập nhật lịch hẹn!');
          }
        });
      }
    });
  }

  cancelAppointment(id: number) {
    if (confirm('Bạn có chắc chắn muốn hủy lịch hẹn này?')) {
      this.appointmentService.getAppointmentById(id).subscribe({
        next: (apt) => {
          apt.status = 'CANCELLED';
          this.appointmentService.updateAppointment(id, apt).subscribe({
            next: () => {
              this.loadData();
              alert('Hủy lịch hẹn thành công!');
            },
            error: (err) => {
              console.error('Lỗi hủy lịch hẹn:', err);
              alert('Lỗi hủy lịch hẹn!');
            }
          });
        },
        error: (err) => {
          console.error('Lỗi load appointment:', err);
        }
      });
    }
  }

  getStatusLabel(status: string): string {
    const statusMap: { [key: string]: string } = {
      'SCHEDULED': 'Đã Đặt',
      'COMPLETED': 'Hoàn Thành',
      'CANCELLED': 'Đã Hủy'
    };
    return statusMap[status] || status;
  }

  getStatusColor(status: string): string {
    const colorMap: { [key: string]: string } = {
      'SCHEDULED': 'primary',
      'COMPLETED': 'accent',
      'CANCELLED': 'warn'
    };
    return colorMap[status] || '';
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

