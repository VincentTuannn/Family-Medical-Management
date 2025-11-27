import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogContent, MatDialogActions, MatDialogClose } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { AppointmentDTO } from '../../../features/model/appointment.model';
import { DoctorDTO } from '../../../features/model/doctor.model';
import { PatientDTO } from '../../../features/model/patient.model';
import { DoctorService } from '../../../features/service/doctor-service/doctor.service';
import { PatientService } from '../../../features/service/patient-service/patient.service';

@Component({
  selector: 'app-appointment-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose
  ],
  template: `
    <h2 mat-dialog-title>{{ data.action === 'create' ? 'Đặt Lịch Hẹn Mới' : 'Sửa Lịch Hẹn' }}</h2>
    <mat-dialog-content>
      <form [formGroup]="appointmentForm" #appointmentFormRef="ngForm">
        <mat-form-field appearance="fill">
          <mat-label>Chọn Bác Sĩ</mat-label>
          <mat-select formControlName="doctorId" required>
            <mat-option *ngFor="let doctor of doctors" [value]="doctor.doctorId">
              {{ getDoctorDisplayName(doctor) }}
            </mat-option>
          </mat-select>
          <mat-error *ngIf="appointmentForm.get('doctorId')?.hasError('required')">
            Vui lòng chọn bác sĩ
          </mat-error>
        </mat-form-field>

        <mat-form-field appearance="fill">
          <mat-label>Chọn Bệnh Nhân</mat-label>
          <mat-select formControlName="patientId" required>
            <mat-option *ngFor="let patient of patients" [value]="patient.patientId">
              {{ patient.fullName }} ({{ patient.dateOfBirth | date:'dd/MM/yyyy' }})
            </mat-option>
          </mat-select>
          <mat-error *ngIf="appointmentForm.get('patientId')?.hasError('required')">
            Vui lòng chọn bệnh nhân
          </mat-error>
        </mat-form-field>

        <mat-form-field appearance="fill">
          <mat-label>Ngày Giờ Hẹn</mat-label>
          <input matInput [matDatepicker]="picker" formControlName="appointmentDate" required>
          <mat-datepicker-toggle matIconSuffix [for]="picker"></mat-datepicker-toggle>
          <mat-datepicker #picker></mat-datepicker>
          <mat-error *ngIf="appointmentForm.get('appointmentDate')?.hasError('required')">
            Vui lòng chọn ngày giờ hẹn
          </mat-error>
        </mat-form-field>

        <mat-form-field appearance="fill">
          <mat-label>Ghi Chú</mat-label>
          <textarea matInput formControlName="notes" rows="4" placeholder="Nhập ghi chú (nếu có)"></textarea>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="dialogRef.close()">Hủy</button>
      <button mat-raised-button color="primary" [mat-dialog-close]="getFormValue()" [disabled]="!appointmentForm.valid">
        {{ data.action === 'create' ? 'Đặt Lịch' : 'Cập Nhật' }}
      </button>
    </mat-dialog-actions>
  `
})
export class AppointmentDialogComponent implements OnInit {
  appointmentForm: FormGroup;
  doctors: DoctorDTO[] = [];
  patients: PatientDTO[] = [];

  constructor(
    public dialogRef: MatDialogRef<AppointmentDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { action: 'create' | 'edit'; appointment?: AppointmentDTO },
    private fb: FormBuilder,
    private doctorService: DoctorService,
    private patientService: PatientService
  ) {
    this.appointmentForm = this.fb.group({
      doctorId: ['', Validators.required],
      patientId: ['', Validators.required],
      appointmentDate: ['', Validators.required],
      notes: ['']
    });
  }

  ngOnInit(): void {
    this.loadDoctors();
    this.loadPatients();
    
    if (this.data.appointment) {
      // Pre-fill form for edit
      this.appointmentForm.patchValue({
        doctorId: this.data.appointment.doctorId,
        patientId: this.data.appointment.patientId,
        appointmentDate: new Date(this.data.appointment.appointmentDate),
        notes: this.data.appointment.notes || ''
      });
    }
  }

  loadDoctors() {
    this.doctorService.getAllDoctors().subscribe({
      next: (data) => {
        this.doctors = data.filter(d => d.isActive);
      },
      error: (err) => console.error('Lỗi load doctors:', err)
    });
  }

  loadPatients() {
    this.patientService.getMyPatients().subscribe({
      next: (data) => {
        this.patients = data;
      },
      error: (err) => console.error('Lỗi load patients:', err)
    });
  }

  getDoctorDisplayName(doctor: DoctorDTO): string {
    return `${doctor.fullName} - ${doctor.specialty}`;
  }

  getFormValue(): AppointmentDTO {
    const formValue = this.appointmentForm.value;
    return {
      appointmentId: this.data.appointment?.appointmentId || 0,
      patientId: formValue.patientId,
      doctorId: formValue.doctorId,
      appointmentDate: new Date(formValue.appointmentDate),
      status: this.data.appointment?.status || 'SCHEDULED',
      notes: formValue.notes || ''
    };
  }
}

