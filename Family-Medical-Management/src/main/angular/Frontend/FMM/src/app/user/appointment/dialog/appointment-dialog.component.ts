import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogContent, MatDialogActions, MatDialogClose } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
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
    MatIconModule,
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

        <div style="display: flex; gap: 16px;">
          <mat-form-field appearance="fill" style="flex: 1;">
            <mat-label>Ngày Hẹn</mat-label>
            <input matInput type="date" formControlName="appointmentDate" [min]="getMinDate()" required>
            <mat-icon matSuffix>calendar_today</mat-icon>
            <mat-error *ngIf="appointmentForm.get('appointmentDate')?.hasError('required')">
              Vui lòng chọn ngày hẹn
            </mat-error>
          </mat-form-field>

          <div style="display: flex; gap: 8px; flex: 1; align-items: center;">
            <mat-form-field appearance="fill" style="flex: 1;">
              <mat-label>Giờ (24h)</mat-label>
              <mat-select formControlName="appointmentHour" required>
                <mat-option *ngFor="let hour of hours" [value]="hour">
                  {{ formatHour(hour) }}
                </mat-option>
              </mat-select>
              <mat-icon matSuffix>access_time</mat-icon>
              <mat-error *ngIf="appointmentForm.get('appointmentHour')?.hasError('required')">
                Vui lòng chọn giờ
              </mat-error>
            </mat-form-field>

            <span style="font-size: 18px; font-weight: bold; margin-top: 8px;">:</span>

            <mat-form-field appearance="fill" style="flex: 1;">
              <mat-label>Phút</mat-label>
              <mat-select formControlName="appointmentMinute" required>
                <mat-option *ngFor="let minute of minutes" [value]="minute">
                  {{ formatMinute(minute) }}
                </mat-option>
              </mat-select>
              <mat-error *ngIf="appointmentForm.get('appointmentMinute')?.hasError('required')">
                Vui lòng chọn phút
              </mat-error>
            </mat-form-field>
          </div>
        </div>
        
        <div *ngIf="appointmentForm.get('appointmentDate')?.hasError('pastDate') || appointmentForm.get('appointmentTime')?.hasError('pastDate')" style="color: #f44336; font-size: 12px; margin-top: -16px; margin-bottom: 16px; margin-left: 16px;">
          Không thể đặt lịch trong quá khứ
        </div>
        <div *ngIf="appointmentForm.get('appointmentDate')?.hasError('minHoursAhead') || appointmentForm.get('appointmentTime')?.hasError('minHoursAhead')" style="color: #f44336; font-size: 12px; margin-top: -16px; margin-bottom: 16px; margin-left: 16px;">
          Phải đặt lịch sau ít nhất 2 giờ tính từ giờ hiện tại
        </div>

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
  originalAppointmentDate: Date | null = null; // Lưu date gốc khi edit
  
  // Tạo mảng giờ (0-23) và phút (0-59)
  hours: number[] = Array.from({ length: 24 }, (_, i) => i);
  minutes: number[] = Array.from({ length: 60 }, (_, i) => i);

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
      appointmentHour: ['', Validators.required],
      appointmentMinute: ['', Validators.required],
      notes: ['']
    }, { validators: this.validateAppointmentDateTime.bind(this) });
  }

  ngOnInit(): void {
    this.loadDoctors();
    this.loadPatients();
    
    if (this.data.appointment) {
      // Pre-fill form for edit
      const appointmentDate = new Date(this.data.appointment.appointmentDate);
      this.originalAppointmentDate = new Date(appointmentDate); // Lưu date gốc
      
      // Format date: YYYY-MM-DD
      const year = appointmentDate.getFullYear();
      const month = String(appointmentDate.getMonth() + 1).padStart(2, '0');
      const day = String(appointmentDate.getDate()).padStart(2, '0');
      const dateStr = `${year}-${month}-${day}`;
      
      // Lấy giờ và phút
      const hours = appointmentDate.getHours();
      const minutes = appointmentDate.getMinutes();
      
      this.appointmentForm.patchValue({
        doctorId: this.data.appointment.doctorId,
        patientId: this.data.appointment.patientId,
        appointmentDate: dateStr,
        appointmentHour: hours,
        appointmentMinute: minutes,
        notes: this.data.appointment.notes || ''
      });
    }
    
    // Validate khi date, hour hoặc minute thay đổi
    this.appointmentForm.get('appointmentDate')?.valueChanges.subscribe(() => {
      this.validateDateTime();
    });
    
    this.appointmentForm.get('appointmentHour')?.valueChanges.subscribe(() => {
      this.validateDateTime();
    });
    
    this.appointmentForm.get('appointmentMinute')?.valueChanges.subscribe(() => {
      this.validateDateTime();
    });
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

  /**
   * Validator để kiểm tra ngày giờ hẹn (combine date, hour và minute):
   * - Không được trong quá khứ (trừ khi edit và giữ nguyên date cũ)
   * - Phải cách hiện tại ít nhất 2 giờ (chỉ khi create hoặc thay đổi date khi edit)
   */
  validateAppointmentDateTime(formGroup: AbstractControl): ValidationErrors | null {
    const dateControl = formGroup.get('appointmentDate');
    const hourControl = formGroup.get('appointmentHour');
    const minuteControl = formGroup.get('appointmentMinute');
    
    if (!dateControl?.value || !hourControl || !minuteControl || 
        hourControl.value === null || hourControl.value === '' || 
        minuteControl.value === null || minuteControl.value === '') {
      return null; // required validator sẽ xử lý
    }

    // Combine date, hour và minute thành một Date object
    const dateStr = dateControl.value;
    const hour = hourControl.value;
    const minute = minuteControl.value;
    const selectedDate = new Date(`${dateStr}T${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`);

    const now = new Date();
    const twoHoursLater = new Date(now.getTime() + 2 * 60 * 60 * 1000); // +2 giờ

    // Khi edit: nếu date mới giống date cũ, không validate (cho phép giữ nguyên)
    if (this.data.action === 'edit' && this.originalAppointmentDate) {
      if (selectedDate.getTime() === this.originalAppointmentDate.getTime()) {
        return null; // Giữ nguyên date cũ, không validate
      }
    }

    // Kiểm tra không được trong quá khứ
    if (selectedDate <= now) {
      dateControl.setErrors({ pastDate: true });
      hourControl.setErrors({ pastDate: true });
      minuteControl.setErrors({ pastDate: true });
      return { pastDate: true };
    }

    // Kiểm tra phải cách hiện tại ít nhất 2 giờ
    if (selectedDate < twoHoursLater) {
      dateControl.setErrors({ minHoursAhead: true });
      hourControl.setErrors({ minHoursAhead: true });
      minuteControl.setErrors({ minHoursAhead: true });
      return { minHoursAhead: true };
    }

    // Clear errors nếu valid
    dateControl.setErrors(null);
    hourControl.setErrors(null);
    minuteControl.setErrors(null);
    return null; // Valid
  }

  /**
   * Validate date, hour và minute khi có thay đổi
   */
  validateDateTime(): void {
    const dateControl = this.appointmentForm.get('appointmentDate');
    const hourControl = this.appointmentForm.get('appointmentHour');
    const minuteControl = this.appointmentForm.get('appointmentMinute');
    
    if (!dateControl || !hourControl || !minuteControl || 
        !dateControl.value || 
        hourControl.value === null || hourControl.value === '' || 
        minuteControl.value === null || minuteControl.value === '') {
      return;
    }

    // Combine date, hour và minute thành một Date object
    const dateStr = dateControl.value;
    const hour = hourControl.value;
    const minute = minuteControl.value;
    const selectedDate = new Date(`${dateStr}T${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`);

    const now = new Date();
    const twoHoursLater = new Date(now.getTime() + 2 * 60 * 60 * 1000);

    // Khi edit: nếu date mới giống date cũ, không validate
    if (this.data.action === 'edit' && this.originalAppointmentDate) {
      if (selectedDate.getTime() === this.originalAppointmentDate.getTime()) {
        dateControl.setErrors(null);
        hourControl.setErrors(null);
        minuteControl.setErrors(null);
        return;
      }
    }

    // Kiểm tra không được trong quá khứ
    if (selectedDate <= now) {
      dateControl.setErrors({ pastDate: true });
      hourControl.setErrors({ pastDate: true });
      minuteControl.setErrors({ pastDate: true });
      return;
    }

    // Kiểm tra phải cách hiện tại ít nhất 2 giờ
    if (selectedDate < twoHoursLater) {
      dateControl.setErrors({ minHoursAhead: true });
      hourControl.setErrors({ minHoursAhead: true });
      minuteControl.setErrors({ minHoursAhead: true });
      return;
    }

    // Clear errors nếu valid
    dateControl.setErrors(null);
    hourControl.setErrors(null);
    minuteControl.setErrors(null);
  }

  /**
   * Lấy giá trị min cho input date (ngày hôm nay)
   */
  getMinDate(): string {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  /**
   * Format giờ để hiển thị (ví dụ: 18h)
   */
  formatHour(hour: number): string {
    return `${String(hour).padStart(2, '0')}h`;
  }

  /**
   * Format phút để hiển thị (ví dụ: 30)
   */
  formatMinute(minute: number): string {
    return String(minute).padStart(2, '0');
  }

  getFormValue(): any {
    const formValue = this.appointmentForm.value;
    // Combine date, hour và minute thành một Date object
    const dateStr = formValue.appointmentDate;
    const hour = String(formValue.appointmentHour).padStart(2, '0');
    const minute = String(formValue.appointmentMinute).padStart(2, '0');
    
    // Format thành ISO string với local timezone để tránh bị convert sai
    // Format: YYYY-MM-DDTHH:mm:ss (không có timezone info = local time)
    const dateTimeString = `${dateStr}T${hour}:${minute}:00`;
    
    // Tạo Date object để validate
    const appointmentDateTime = new Date(dateTimeString);
    
    // Kiểm tra nếu Date không hợp lệ, log để debug
    if (isNaN(appointmentDateTime.getTime())) {
      console.error('Invalid date:', dateTimeString);
      throw new Error('Ngày giờ không hợp lệ');
    }
    
    // Trả về object với appointmentDate là Date object
    // Angular sẽ tự động serialize Date thành ISO string khi gửi HTTP request
    // Nhưng để đảm bảo đúng format, ta sẽ format thủ công
    return {
      appointmentId: this.data.appointment?.appointmentId || 0,
      patientId: formValue.patientId,
      doctorId: formValue.doctorId,
      appointmentDate: appointmentDateTime, // Date object sẽ được serialize đúng
      status: this.data.appointment?.status || 'SCHEDULED',
      notes: formValue.notes || ''
    };
  }
}

