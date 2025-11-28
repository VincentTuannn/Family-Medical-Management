import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatStepperModule } from '@angular/material/stepper';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { firstValueFrom } from 'rxjs';
import { DoctorDTO } from '../../../features/model/doctor.model';
import { PaymentService } from '../../../features/service/payment-service/payment.service';
import { PaymentRequest } from '../../../features/model/payment.model';

@Component({
  selector: 'app-doctor-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatStepperModule,
    MatCardModule,
    MatIconModule,
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule
  ],
  templateUrl: './doctor-dialog.component.html',
  styleUrls: ['./doctor-dialog.component.scss']
})
export class DoctorDialogComponent {
  step = 0;
  isProcessing = false;
  
  hireForm: FormGroup;
  paymentForm: FormGroup;
  
  // Giá thuê bác sĩ (có thể lấy từ doctor hoặc cố định)
  doctorFee = 500000; // 500,000 VND mặc định
  
  paymentMethods = [
    { value: 'CASH', label: 'Tiền Mặt' },
    { value: 'BANK_TRANSFER', label: 'Chuyển Khoản Ngân Hàng' },
    { value: 'CREDIT_CARD', label: 'Thẻ Tín Dụng' },
    { value: 'E_WALLET', label: 'Ví Điện Tử' }
  ];

  constructor(
    public dialogRef: MatDialogRef<DoctorDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { doctor: DoctorDTO },
    private fb: FormBuilder,
    private paymentService: PaymentService
  ) {
    // Form thông tin thuê
    this.hireForm = this.fb.group({
      reason: ['', Validators.required],
      appointmentDate: ['', Validators.required],
      notes: ['']
    });
    
    // Form thanh toán
    this.paymentForm = this.fb.group({
      paymentMethod: ['', Validators.required],
      transactionId: ['']
    });
  }

  nextStep() {
    if (this.step === 0 && this.hireForm.valid) {
      this.step = 1;
    }
  }

  previousStep() {
    if (this.step === 1) {
      this.step = 0;
    }
  }

  async processPayment() {
    if (this.paymentForm.invalid) {
      return;
    }

    this.isProcessing = true;
    
    try {
      const hireData = this.hireForm.value;
      const paymentData = this.paymentForm.value;
      
      // Tạo payment request
      const paymentRequest: PaymentRequest = {
        doctorId: this.data.doctor.doctorId,
        amount: this.doctorFee,
        paymentMethod: paymentData.paymentMethod,
        appointmentDate: new Date(hireData.appointmentDate),
        notes: hireData.notes,
        reason: hireData.reason,
        transactionId: paymentData.paymentMethod === 'BANK_TRANSFER' ? paymentData.transactionId : undefined
      };
      
      // Gọi API thanh toán
      const payment = await firstValueFrom(this.paymentService.createPayment(paymentRequest));
      
      if (payment) {
        // Thanh toán thành công
        this.dialogRef.close({
          success: true,
          payment: payment,
          hireData: hireData
        });
      }
    } catch (error) {
      console.error('Lỗi thanh toán:', error);
      alert('Thanh toán thất bại! Vui lòng thử lại.');
    } finally {
      this.isProcessing = false;
    }
  }

  cancel() {
    this.dialogRef.close();
  }
}