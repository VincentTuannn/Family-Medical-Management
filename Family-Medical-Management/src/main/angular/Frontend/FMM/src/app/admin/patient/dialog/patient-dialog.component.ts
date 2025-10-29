import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';
import { MatDialogContent, MatDialogActions, MatDialogClose } from '@angular/material/dialog';  
import { PatientDTO } from '../../../features/model/patient.model';  

@Component({
  selector: 'app-patient-dialog',
  standalone: true,
  imports: [
    MatFormFieldModule, 
    MatInputModule, 
    MatSelectModule, 
    FormsModule,
    MatDialogContent,  // Import cho <mat-dialog-content>
    MatDialogActions,  // Import cho <mat-dialog-actions>
    MatDialogClose  // Import cho [mat-dialog-close]
  ],
  template: `
    <h2 mat-dialog-title>{{ data.action === 'create' ? 'Thêm Bệnh Nhân' : 'Sửa Bệnh Nhân' }}</h2>
    <mat-dialog-content>
      <form #patientForm="ngForm">
        <mat-form-field appearance="fill">
          <mat-label>Họ Tên</mat-label>
          <input matInput [(ngModel)]="patient.fullName" name="fullName" required>
        </mat-form-field>
        <mat-form-field appearance="fill">
          <mat-label>Ngày Sinh</mat-label>
          <input matInput type="date" [(ngModel)]="patient.dateOfBirth" name="dateOfBirth" required>
        </mat-form-field>
        <mat-form-field appearance="fill">
          <mat-label>Giới Tính</mat-label>
          <mat-select [(ngModel)]="patient.gender" name="gender" required>
            <mat-option value="male">Nam</mat-option>
            <mat-option value="female">Nữ</mat-option>
            <mat-option value="other">Khác</mat-option>
          </mat-select>
        </mat-form-field>
        <!-- Thêm fields khác nếu cần (bloodType, emergencyContact) -->
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="dialogRef.close()">Hủy</button>
      <button mat-raised-button color="primary" [mat-dialog-close]="patient" [disabled]="!patientForm.valid">Lưu</button>
    </mat-dialog-actions>
  `,
  styles: []  // Inline styles nếu cần
})
export class PatientDialogComponent {
  patient: PatientDTO = {
    patientId: 0,
    userId: 0,
    fullName: '',
    dateOfBirth: new Date(),
    gender: 'male',
    bloodType: '',
    emergencyContact: '',
    createdAt: new Date()  // Thêm default để fix missing property (hoặc làm optional trong model)
  };

  constructor(
    public dialogRef: MatDialogRef<PatientDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { action: 'create' | 'edit'; patient?: PatientDTO }
  ) {
    if (this.data.patient) {
      this.patient = { ...this.data.patient, createdAt: new Date() };  // Copy và set createdAt nếu cần
    }
  }
}