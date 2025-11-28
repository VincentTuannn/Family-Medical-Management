import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { AppointmentService } from '../../../features/service/appointment-service/appointment.service';
import { AppointmentDTO } from '../../../features/model/appointment.model';
import { AuthService } from '../../../features/service/auth-service/auth.service';
import { DoctorService } from '../../../features/service/doctor-service/doctor.service';
import { DoctorDTO } from '../../../features/model/doctor.model';

@Component({
  selector: 'app-appointment-requests-container',
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatCardModule, MatChipsModule],
  templateUrl: './container.html',
  styleUrl: './container.scss',
})
export class AppointmentRequestsContainer implements OnInit {
  displayedColumns: string[] = ['appointmentId', 'patientId', 'appointmentDate', 'status', 'notes', 'actions'];
  allAppointments: AppointmentDTO[] = [];
  myAppointments: AppointmentDTO[] = [];
  loading = false;
  currentDoctorId: number | null = null;

  constructor(
    private appointmentService: AppointmentService,
    private authService: AuthService,
    private doctorService: DoctorService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadDoctorAndAppointments();
  }

  loadDoctorAndAppointments() {
    this.loading = true;
    const userId = this.authService.getUserId();
    
    if (!userId) {
      this.loading = false;
      return;
    }

    // Tìm doctor theo userId
    this.doctorService.getAllDoctors().subscribe({
      next: (doctors) => {
        // Giả định doctor có userId tương ứng
        // Nếu không có, có thể cần API khác để lấy doctor theo userId
        const doctor = doctors.find(d => (d as any).userId === userId);
        if (doctor) {
          this.currentDoctorId = doctor.doctorId;
          this.loadAppointments();
        } else {
          this.loading = false;
          this.cdr.detectChanges();
        }
      },
      error: (err) => {
        console.error('Lỗi load doctor:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadAppointments() {
    this.appointmentService.getAllAppointments().subscribe({
      next: (data) => {
        this.allAppointments = data;
        // Filter appointments của doctor hiện tại
        if (this.currentDoctorId) {
          this.myAppointments = data.filter(apt => apt.doctorId === this.currentDoctorId);
        }
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Lỗi load appointments:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  updateAppointmentStatus(appointmentId: number, status: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED') {
    const appointment = this.myAppointments.find(a => a.appointmentId === appointmentId);
    if (appointment) {
      const updatedAppointment = { ...appointment, status };
      this.appointmentService.updateAppointment(appointmentId, updatedAppointment).subscribe({
        next: () => {
          this.loadAppointments();
          alert('Cập nhật trạng thái thành công!');
        },
        error: (err) => {
          console.error('Lỗi cập nhật appointment:', err);
          alert('Lỗi cập nhật trạng thái!');
        }
      });
    }
  }
}

