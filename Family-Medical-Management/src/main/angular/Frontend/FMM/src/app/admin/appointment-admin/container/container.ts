import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { AppointmentService } from '../../../features/service/appointment-service/appointment.service';
import { AppointmentDTO } from '../../../features/model/appointment.model';

@Component({
  selector: 'app-appointment-admin-container',
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatCardModule, MatDialogModule],
  templateUrl: './container.html',
  styleUrl: './container.scss',
})
export class AppointmentAdminContainer implements OnInit {
  displayedColumns: string[] = ['appointmentId', 'patientId', 'doctorId', 'appointmentDate', 'status', 'notes', 'actions'];
  appointments: AppointmentDTO[] = [];
  loading = false;

  constructor(
    private appointmentService: AppointmentService,
    public dialog: MatDialog,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadAppointments();
  }

  loadAppointments() {
    this.loading = true;
    this.appointmentService.getAllAppointments().subscribe({
      next: (data) => {
        this.appointments = data;
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

  deleteAppointment(id: number) {
    if (confirm('Bạn có chắc chắn muốn xóa lịch hẹn này?')) {
      this.appointmentService.deleteAppointment(id).subscribe({
        next: () => {
          this.loadAppointments();
          alert('Xóa thành công!');
        },
        error: (err) => {
          console.error('Lỗi xóa appointment:', err);
          alert('Lỗi xóa lịch hẹn!');
        }
      });
    }
  }
}

