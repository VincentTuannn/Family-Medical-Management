import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { DoctorService } from '../../../features/service/doctor-service/doctor.service';  
import { DoctorDTO } from '../../../features/model/doctor.model';  
import { DoctorDialogComponent } from '../dialog/doctor-dialog.component';

@Component({
  selector: 'app-container',
  imports: [CommonModule, MatTableModule, MatCardModule, MatButtonModule, MatIconModule],
  templateUrl: './container.html',
  styleUrl: './container.scss',
})
export class DoctorContainer implements OnInit {
  displayedColumns: string[] = ['fullName', 'specialty', 'clinicName', 'phone', 'email', 'actions'];  // Cột table
  doctors: DoctorDTO[] = [];  // Danh sách bác sĩ
  selectedDoctor: DoctorDTO | null = null;  // Bác sĩ được chọn để xem chi tiết

  constructor(
    private doctorService: DoctorService,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadDoctors();
  }

  loadDoctors() {
    this.doctorService.getAllDoctors().subscribe({
      next: (data) => this.doctors = data,
      error: (err) => console.error('Lỗi load bác sĩ:', err)
    });
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
