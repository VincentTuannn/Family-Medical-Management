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
    MatDialogContent,  
    MatDialogActions,  
    MatDialogClose  
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
          </mat-select>
        </mat-form-field>
        <mat-form-field appearance="fill">
          <mat-label>Nhóm Máu</mat-label>
          <input matInput [(ngModel)]="patient.bloodType" name="bloodType" required>
        </mat-form-field>
        <mat-form-field appearance="fill">
          <mat-label>Số Điện Thoại Khẩn Cấp</mat-label>
          <input matInput [(ngModel)]="patient.emergencyContact" name="emergencyContact" required>
        </mat-form-field>
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
    createdAt: new Date()  
  };

  constructor(
    public dialogRef: MatDialogRef<PatientDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { action: 'create' | 'edit'; patient?: PatientDTO }
  ) {
    if (this.data.patient) {
      // Convert dateOfBirth từ string hoặc Date object thành Date
      const dateOfBirth = this.data.patient.dateOfBirth 
        ? (typeof this.data.patient.dateOfBirth === 'string' 
            ? new Date(this.data.patient.dateOfBirth) 
            : this.data.patient.dateOfBirth)
        : new Date();
      
      this.patient = { 
        ...this.data.patient, 
        dateOfBirth: dateOfBirth,
        createdAt: this.data.patient.createdAt ? new Date(this.data.patient.createdAt as any) : new Date()
      };
      
      // Format dateOfBirth thành yyyy-MM-dd cho input type="date"
      if (this.patient.dateOfBirth instanceof Date) {
        const year = this.patient.dateOfBirth.getFullYear();
        const month = String(this.patient.dateOfBirth.getMonth() + 1).padStart(2, '0');
        const day = String(this.patient.dateOfBirth.getDate()).padStart(2, '0');
        // Tạo một object mới với dateOfBirth là string
        this.patient = { ...this.patient, dateOfBirth: `${year}-${month}-${day}` as any };
      }
    } else {
      // Format default date thành yyyy-MM-dd
      const today = new Date();
      const year = today.getFullYear();
      const month = String(today.getMonth() + 1).padStart(2, '0');
      const day = String(today.getDate()).padStart(2, '0');
      this.patient.dateOfBirth = `${year}-${month}-${day}` as any;
    }
  }
  
  // Convert dateOfBirth từ string (yyyy-MM-dd) về Date object khi submit
  getFormattedPatient(): PatientDTO {
    const formatted = { ...this.patient };
    if (typeof formatted.dateOfBirth === 'string') {
      formatted.dateOfBirth = new Date(formatted.dateOfBirth);
    }
    return formatted;
  }
}