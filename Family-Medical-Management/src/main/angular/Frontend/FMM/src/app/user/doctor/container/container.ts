import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { DoctorService } from '../../../features/service/doctor-service/doctor.service';  
import { DoctorDTO } from '../../../features/model/doctor.model';  
import { DoctorDialogComponent } from '../dialog/doctor-dialog.component';
import { AuthService } from '../../../features/service/auth-service/auth.service';
import { filter, take, takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-container',
  imports: [
    CommonModule, 
    MatTableModule, 
    MatCardModule, 
    MatButtonModule, 
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    FormsModule
  ],
  templateUrl: './container.html',
  styleUrl: './container.scss',
})
export class DoctorContainer implements OnInit, OnDestroy {
  displayedColumns: string[] = ['fullName', 'specialty', 'clinicName', 'phone', 'email', 'actions'];  // Cột table
  doctors: DoctorDTO[] = [];  // Danh sách bác sĩ gốc
  filteredDoctors: DoctorDTO[] = [];  // Danh sách bác sĩ sau khi filter
  selectedDoctor: DoctorDTO | null = null;  // Bác sĩ được chọn để xem chi tiết
  
  // Search và filter
  searchText: string = '';
  selectedClinic: string = '';
  selectedSpecialty: string = '';
  uniqueClinics: string[] = [];
  uniqueSpecialties: string[] = [];
  
  private destroy$ = new Subject<void>();

  constructor(
    private doctorService: DoctorService,
    public dialog: MatDialog,
    private cdr: ChangeDetectorRef,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.waitForTokenAndLoad();
  }

  private waitForTokenAndLoad() {
    const token = this.authService.getToken();
    if (token) {
      // Token đã sẵn sàng -> load doctors (delay nhẹ để đảm bảo interceptor đã attach token)
      setTimeout(() => this.loadDoctors(), 50);
      return;
    }

    // Token chưa có -> chờ observable emit
    this.authService.isLoggedIn$
      .pipe(
        filter((value): value is string => !!value),
        take(1),
        takeUntil(this.destroy$)
      )
      .subscribe(() => this.loadDoctors());
  }

  loadDoctors() {
    this.doctorService.getAllDoctors().subscribe({
      next: (data) => {
        this.doctors = data;
        // Lấy danh sách unique clinics và specialties
        this.uniqueClinics = [...new Set(data.map(d => d.clinicName).filter(c => c))].sort();
        this.uniqueSpecialties = [...new Set(data.map(d => d.specialty).filter(s => s))].sort();
        // Áp dụng filter ban đầu
        this.applyFilters();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Lỗi load bác sĩ:', err);
        if (err.status === 403) {
          console.log('🔄 Lỗi 403, thử lại sau 200ms...');
          setTimeout(() => this.loadDoctors(), 200);
        } else {
          this.cdr.detectChanges();
        }
      }
    });
  }

  applyFilters() {
    this.filteredDoctors = this.doctors.filter(doctor => {
      // Filter theo search text (tìm trong tên, email, phone, specialty, clinic)
      const matchesSearch = !this.searchText || 
        doctor.fullName.toLowerCase().includes(this.searchText.toLowerCase()) ||
        doctor.email.toLowerCase().includes(this.searchText.toLowerCase()) ||
        doctor.phone.toLowerCase().includes(this.searchText.toLowerCase()) ||
        doctor.specialty.toLowerCase().includes(this.searchText.toLowerCase()) ||
        doctor.clinicName.toLowerCase().includes(this.searchText.toLowerCase());

      // Filter theo clinic
      const matchesClinic = !this.selectedClinic || doctor.clinicName === this.selectedClinic;

      // Filter theo specialty
      const matchesSpecialty = !this.selectedSpecialty || doctor.specialty === this.selectedSpecialty;

      return matchesSearch && matchesClinic && matchesSpecialty;
    });
  }

  onSearchChange() {
    this.applyFilters();
  }

  onClinicChange() {
    this.applyFilters();
  }

  onSpecialtyChange() {
    this.applyFilters();
  }

  clearFilters() {
    this.searchText = '';
    this.selectedClinic = '';
    this.selectedSpecialty = '';
    this.applyFilters();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
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
        if (result && result.success) {
          // Thanh toán thành công
          console.log('Thanh toán thành công:', result.payment);
          alert('Thanh toán thành công! Bác sĩ đã được thuê.');
          // Có thể reload danh sách appointments hoặc cập nhật UI
        } else if (result && !result.success) {
          alert('Thanh toán thất bại! Vui lòng thử lại.');
        }
      });
    }
  }
}
