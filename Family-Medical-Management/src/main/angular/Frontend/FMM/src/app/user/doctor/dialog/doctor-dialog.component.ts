import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';  
import { DoctorDTO } from '../../../features/model/doctor.model';  

@Component({
  selector: 'app-doctor-dialog',
  standalone: true,
  imports: [
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatDialogModule  
  ],
  template: `
    <h2 mat-dialog-title>Yêu Cầu Thuê Bác Sĩ: {{data.doctor.fullName}}</h2>
    <mat-dialog-content>
      <form #hireForm="ngForm">
        <mat-form-field>
          <mat-label>Lý Do Thuê</mat-label>
          <textarea matInput [(ngModel)]="request.reason" name="reason" rows="3" required></textarea>
        </mat-form-field>
        <mat-form-field>
          <mat-label>Ngày Bắt Đầu Dự Kiến</mat-label>
          <input matInput type="date" [(ngModel)]="request.startDate" name="startDate" required>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions>
      <button mat-button (click)="dialogRef.close()">Hủy</button>
      <button mat-raised-button color="primary" [mat-dialog-close]="request" [disabled]="!hireForm.valid">Gửi Yêu Cầu</button>
    </mat-dialog-actions>
  `,
  styles: [`
    form { display: flex; flex-direction: column; gap: 1rem; }
  `]
})
export class DoctorDialogComponent {
  request = { reason: '', startDate: '' };

  constructor(
    public dialogRef: MatDialogRef<DoctorDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { doctor: DoctorDTO }
  ) {}
}