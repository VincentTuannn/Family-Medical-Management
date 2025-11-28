import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MedicalRecordService } from '../../../features/service/medical-record-service/medical-record.service';
import { MedicalRecordDTO } from '../../../features/model/medical-record.model';
import { AppointmentService } from '../../../features/service/appointment-service/appointment.service';
import { AppointmentDTO } from '../../../features/model/appointment.model';
import { AuthService } from '../../../features/service/auth-service/auth.service';
import { DoctorService } from '../../../features/service/doctor-service/doctor.service';

@Component({
  selector: 'app-patient-records-container',
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatCardModule, MatDialogModule],
  templateUrl: './container.html',
  styleUrl: './container.scss',
})
export class PatientRecordsContainer implements OnInit {
  displayedColumns: string[] = ['recordId', 'patientId', 'diagnosis', 'treatment', 'medications', 'recordDate', 'doctorName'];
  medicalRecords: MedicalRecordDTO[] = [];
  myPatientIds: number[] = [];
  loading = false;
  currentDoctorId: number | null = null;

  constructor(
    private medicalRecordService: MedicalRecordService,
    private appointmentService: AppointmentService,
    private authService: AuthService,
    private doctorService: DoctorService,
    public dialog: MatDialog,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadDoctorAndRecords();
  }

  loadDoctorAndRecords() {
    this.loading = true;
    const userId = this.authService.getUserId();
    
    if (!userId) {
      this.loading = false;
      return;
    }

    // Tìm doctor theo userId
    this.doctorService.getAllDoctors().subscribe({
      next: (doctors) => {
        const doctor = doctors.find(d => (d as any).userId === userId);
        if (doctor) {
          this.currentDoctorId = doctor.doctorId;
          this.loadMyPatients();
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

  loadMyPatients() {
    // Lấy danh sách appointments của doctor để biết patientIds
    this.appointmentService.getAllAppointments().subscribe({
      next: (appointments) => {
        if (this.currentDoctorId) {
          const myAppointments = appointments.filter(apt => apt.doctorId === this.currentDoctorId);
          this.myPatientIds = [...new Set(myAppointments.map(apt => apt.patientId))];
          this.loadMedicalRecords();
        }
      },
      error: (err) => {
        console.error('Lỗi load appointments:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadMedicalRecords() {
    this.medicalRecordService.getAllMedicalRecords().subscribe({
      next: (data) => {
        // Chỉ hiển thị records của các bệnh nhân đã có appointment với doctor này
        this.medicalRecords = data.filter(record => 
          this.myPatientIds.includes(record.patientId)
        );
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Lỗi load medical records:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}

