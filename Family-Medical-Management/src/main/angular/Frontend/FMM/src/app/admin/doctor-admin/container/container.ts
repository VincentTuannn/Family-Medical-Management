import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { DoctorService } from '../../../features/service/doctor-service/doctor.service';
import { DoctorDTO } from '../../../features/model/doctor.model';
import { DoctorDialogComponent } from '../dialog/doctor-dialog.component';

@Component({
  selector: 'app-doctor-admin-container',
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatCardModule, MatDialogModule],
  templateUrl: './container.html',
  styleUrl: './container.scss',
})
export class DoctorAdminContainer implements OnInit {
  displayedColumns: string[] = ['doctorId', 'fullName', 'specialty', 'clinicName', 'phone', 'email', 'licenseNumber', 'isActive', 'actions'];
  doctors: DoctorDTO[] = [];
  loading = false;

  constructor(
    private doctorService: DoctorService,
    public dialog: MatDialog,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadDoctors();
  }

  loadDoctors() {
    this.loading = true;
    this.doctorService.getAllDoctors().subscribe({
      next: (data) => {
        console.log('📋 Doctors data received:', data);
        // Debug: Kiểm tra fullName
        data.forEach((doctor, index) => {
          console.log(`Doctor ${index + 1}:`, {
            id: doctor.doctorId,
            fullName: doctor.fullName,
            specialty: doctor.specialty
          });
        });
        this.doctors = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Lỗi load doctors:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  addDoctor() {
    const dialogRef = this.dialog.open(DoctorDialogComponent, {
      width: '500px',
      data: { action: 'create' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.doctorService.createDoctor(result).subscribe({
          next: () => {
            this.loadDoctors();
          },
          error: (err) => {
            console.error('Lỗi tạo doctor:', err);
            alert('Lỗi tạo bác sĩ!');
          }
        });
      }
    });
  }

  editDoctor(id: number) {
    const doctor = this.doctors.find(d => d.doctorId === id);
    if (doctor) {
      const dialogRef = this.dialog.open(DoctorDialogComponent, {
        width: '500px',
        data: { action: 'edit', doctor }
      });

      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.doctorService.updateDoctor(id, result).subscribe({
            next: () => {
              this.loadDoctors();
            },
            error: (err) => {
              console.error('Lỗi cập nhật doctor:', err);
              alert('Lỗi cập nhật bác sĩ!');
            }
          });
        }
      });
    }
  }

  deleteDoctor(id: number) {
    if (confirm('Bạn có chắc chắn muốn xóa bác sĩ này?')) {
      this.doctorService.deleteDoctor(id).subscribe({
        next: () => {
          this.loadDoctors();
          alert('Xóa thành công!');
        },
        error: (err) => {
          console.error('Lỗi xóa doctor:', err);
          alert('Lỗi xóa bác sĩ!');
        }
      });
    }
  }
}

