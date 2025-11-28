import { Component, OnInit, ChangeDetectorRef, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TransferService } from '../../../features/service/transfer-service/transfer.service';
import { TransferDTO } from '../../../features/model/transfer.model';
import { MedicalRecordService } from '../../../features/service/medical-record-service/medical-record.service';
import { MedicalRecordDTO } from '../../../features/model/medical-record.model';
import { DoctorService } from '../../../features/service/doctor-service/doctor.service';
import { DoctorDTO } from '../../../features/model/doctor.model';
import { AuthService } from '../../../features/service/auth-service/auth.service';
import { AppointmentService } from '../../../features/service/appointment-service/appointment.service';

@Component({
  selector: 'app-transfer-record-container',
  imports: [
    CommonModule, 
    MatTableModule, 
    MatButtonModule, 
    MatIconModule, 
    MatCardModule, 
    MatDialogModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './container.html',
  styleUrl: './container.scss',
})
export class TransferRecordContainer implements OnInit {
  displayedColumns: string[] = ['transferId', 'patientId', 'doctorId', 'accessType', 'status', 'transferredAt', 'actions'];
  transfers: TransferDTO[] = [];
  myPatientIds: number[] = [];
  availableDoctors: DoctorDTO[] = [];
  availableRecords: MedicalRecordDTO[] = [];
  loading = false;
  currentDoctorId: number | null = null;
  transferForm: FormGroup;

  constructor(
    private transferService: TransferService,
    private medicalRecordService: MedicalRecordService,
    private doctorService: DoctorService,
    private appointmentService: AppointmentService,
    private authService: AuthService,
    public dialog: MatDialog,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {
    this.transferForm = this.fb.group({
      patientId: ['', Validators.required],
      doctorId: ['', Validators.required],
      recordIds: [[]],
      accessType: ['VIEW', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadDoctorAndData();
  }

  loadDoctorAndData() {
    this.loading = true;
    const userId = this.authService.getUserId();
    
    if (!userId) {
      this.loading = false;
      return;
    }

    this.doctorService.getAllDoctors().subscribe({
      next: (doctors) => {
        const doctor = doctors.find(d => (d as any).userId === userId);
        if (doctor) {
          this.currentDoctorId = doctor.doctorId;
          this.availableDoctors = doctors.filter(d => d.doctorId !== doctor.doctorId);
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
    this.appointmentService.getAllAppointments().subscribe({
      next: (appointments) => {
        if (this.currentDoctorId) {
          const myAppointments = appointments.filter(apt => apt.doctorId === this.currentDoctorId);
          this.myPatientIds = [...new Set(myAppointments.map(apt => apt.patientId))];
          this.loadTransfers();
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
        this.availableRecords = data.filter(record => 
          this.myPatientIds.includes(record.patientId)
        );
      },
      error: (err) => {
        console.error('Lỗi load medical records:', err);
      }
    });
  }

  loadTransfers() {
    this.transferService.getAllTransfers().subscribe({
      next: (data) => {
        // Chỉ hiển thị transfers liên quan đến patients của doctor này
        this.transfers = data.filter(transfer => 
          this.myPatientIds.includes(transfer.patientId)
        );
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Lỗi load transfers:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  openTransferDialog() {
    const dialogRef = this.dialog.open(TransferDialogComponent, {
      width: '500px',
      data: {
        patientIds: this.myPatientIds,
        doctors: this.availableDoctors,
        records: this.availableRecords
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const transfer: TransferDTO = {
          transferId: 0,
          userId: this.authService.getUserId() || 0,
          patientId: result.patientId,
          doctorId: result.doctorId,
          recordIds: result.recordIds || [],
          accessType: result.accessType,
          status: 'PENDING',
          transferredAt: new Date()
        };

        this.transferService.createTransfer(transfer).subscribe({
          next: () => {
            this.loadTransfers();
            alert('Tạo yêu cầu chuyển hồ sơ thành công!');
          },
          error: (err) => {
            console.error('Lỗi tạo transfer:', err);
            alert('Lỗi tạo yêu cầu chuyển hồ sơ!');
          }
        });
      }
    });
  }

  deleteTransfer(id: number) {
    if (confirm('Bạn có chắc chắn muốn xóa yêu cầu chuyển hồ sơ này?')) {
      this.transferService.deleteTransfer(id).subscribe({
        next: () => {
          this.loadTransfers();
          alert('Xóa thành công!');
        },
        error: (err) => {
          console.error('Lỗi xóa transfer:', err);
          alert('Lỗi xóa yêu cầu chuyển hồ sơ!');
        }
      });
    }
  }
}

// Transfer Dialog Component
@Component({
  selector: 'app-transfer-dialog',
  template: `
    <h2 mat-dialog-title>Chuyển Hồ Sơ Bệnh Nhân</h2>
    <mat-dialog-content>
      <form [formGroup]="form">
        <mat-form-field>
          <mat-label>Bệnh Nhân</mat-label>
          <mat-select formControlName="patientId">
            <mat-option *ngFor="let patientId of data.patientIds" [value]="patientId">
              Bệnh Nhân ID: {{patientId}}
            </mat-option>
          </mat-select>
        </mat-form-field>

        <mat-form-field>
          <mat-label>Bác Sĩ Nhận</mat-label>
          <mat-select formControlName="doctorId">
            <mat-option *ngFor="let doctor of data.doctors" [value]="doctor.doctorId">
              {{doctor.fullName}} - {{doctor.specialty}}
            </mat-option>
          </mat-select>
        </mat-form-field>

        <mat-form-field>
          <mat-label>Quyền Truy Cập</mat-label>
          <mat-select formControlName="accessType">
            <mat-option value="VIEW">Xem</mat-option>
            <mat-option value="EDIT">Chỉnh Sửa</mat-option>
          </mat-select>
        </mat-form-field>

        <mat-form-field>
          <mat-label>Hồ Sơ Y Tế (tùy chọn)</mat-label>
          <mat-select formControlName="recordIds" multiple>
            <mat-option *ngFor="let record of data.records" [value]="record.recordId">
              Record #{{record.recordId}} - {{record.diagnosis}}
            </mat-option>
          </mat-select>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions>
      <button mat-button (click)="dialogRef.close()">Hủy</button>
      <button mat-raised-button color="primary" [mat-dialog-close]="form.value" [disabled]="!form.valid">Gửi Yêu Cầu</button>
    </mat-dialog-actions>
  `,
  imports: [CommonModule, MatFormFieldModule, MatSelectModule, MatInputModule, ReactiveFormsModule, MatButtonModule, MatDialogModule]
})
class TransferDialogComponent {
  form: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<TransferDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: FormBuilder
  ) {
    this.form = this.fb.group({
      patientId: ['', Validators.required],
      doctorId: ['', Validators.required],
      recordIds: [[]],
      accessType: ['VIEW', Validators.required]
    });
  }
}

