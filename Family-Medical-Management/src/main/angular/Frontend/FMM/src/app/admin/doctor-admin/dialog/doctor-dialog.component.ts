import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogContent, MatDialogActions, MatDialogClose } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { FormsModule } from '@angular/forms';
import { DoctorDTO } from '../../../features/model/doctor.model';

@Component({
  selector: 'app-doctor-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    FormsModule,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose
  ],
  template: `
    <h2 mat-dialog-title>{{ data.action === 'create' ? 'Thêm Bác Sĩ' : 'Sửa Bác Sĩ' }}</h2>
    <mat-dialog-content>
      <form #doctorForm="ngForm">
        <mat-form-field appearance="fill">
          <mat-label>Họ Tên</mat-label>
          <input matInput [(ngModel)]="doctor.fullName" name="fullName" required>
        </mat-form-field>
        <mat-form-field appearance="fill">
          <mat-label>Chuyên Khoa</mat-label>
          <input matInput [(ngModel)]="doctor.specialty" name="specialty" required>
        </mat-form-field>
        <mat-form-field appearance="fill">
          <mat-label>Phòng Khám</mat-label>
          <input matInput [(ngModel)]="doctor.clinicName" name="clinicName">
        </mat-form-field>
        <mat-form-field appearance="fill">
          <mat-label>Email</mat-label>
          <input matInput type="email" [(ngModel)]="doctor.email" name="email" required>
        </mat-form-field>
        <mat-form-field appearance="fill">
          <mat-label>Điện Thoại</mat-label>
          <input matInput [(ngModel)]="doctor.phone" name="phone" required>
        </mat-form-field>
        <mat-form-field appearance="fill">
          <mat-label>Số Giấy Phép</mat-label>
          <input matInput [(ngModel)]="doctor.licenseNumber" name="licenseNumber">
        </mat-form-field>
        <mat-checkbox [(ngModel)]="doctor.isActive" name="isActive">Hoạt động</mat-checkbox>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="dialogRef.close()">Hủy</button>
      <button mat-raised-button color="primary" [mat-dialog-close]="doctor" [disabled]="!doctorForm.valid">Lưu</button>
    </mat-dialog-actions>
  `
})
export class DoctorDialogComponent {
  doctor: DoctorDTO = {
    doctorId: 0,
    fullName: '',
    specialty: '',
    clinicName: '',
    email: '',
    phone: '',
    licenseNumber: '',
    isActive: true
  };

  constructor(
    public dialogRef: MatDialogRef<DoctorDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { action: 'create' | 'edit'; doctor?: DoctorDTO }
  ) {
    if (this.data.doctor) {
      this.doctor = { ...this.data.doctor };
    }
  }
}

