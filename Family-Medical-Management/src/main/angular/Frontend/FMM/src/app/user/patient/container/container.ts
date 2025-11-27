import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { RouterModule, Router } from '@angular/router';
import { MatDialogModule } from '@angular/material/dialog';  
import { MatDialog } from '@angular/material/dialog';
import { PatientDialogComponent } from '../dialog/patient-dialog.component';
import { PatientService } from '../../../features/service/patient-service/patient.service'
import { PatientDTO } from '../../../features/model/patient.model';
import { AuthService } from '../../../features/service/auth-service/auth.service';
import { filter, take } from 'rxjs/operators';

@Component({
  selector: 'app-container',
  imports: [CommonModule, MatTableModule, MatButtonModule, MatCardModule, RouterModule, MatDialogModule, PatientDialogComponent],
  templateUrl: './container.html',
  styleUrl: './container.scss',
})
export class PatientContainer implements OnInit {
  displayedColumns: string[] = ['fullName', 'dateOfBirth', 'gender', 'bloodType', 'emergencyContact', 'actions'];  // Không có ID cho user view
  patients: PatientDTO[] = [];  // Danh sách bệnh nhân của user

  constructor(
    private patientService: PatientService,
    public dialog: MatDialog,  // Inject MatDialog
    private authService: AuthService,  // Import AuthService để lấy userId
    private router: Router,  // Import Router để navigate
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.waitForTokenAndLoad();  // Load chỉ bệnh nhân của user hiện tại
  }

  private waitForTokenAndLoad() {
    const token = this.authService.getToken();
    if (token) {
      // Token đã sẵn sàng -> load patients (delay nhẹ để đảm bảo interceptor đã attach token)
      setTimeout(() => this.loadMyPatients(), 50);
      return;
    }

    // Token chưa có -> chờ observable emit
    this.authService.isLoggedIn$
      .pipe(filter((value): value is string => !!value), take(1))
      .subscribe(() => this.loadMyPatients());
  }

  loadMyPatients() {
    this.patientService.getMyPatients().subscribe({
      next: (data) => {
        this.patients = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Lỗi load bệnh nhân của bạn:', err);
        if (err.status === 403) {
          console.log('🔄 Lỗi 403, thử lại sau 200ms...');
          setTimeout(() => this.loadMyPatients(), 200);
        } else {
          this.cdr.detectChanges();
        }
      }
    });
  }

  createPatient() {
      // Mở dialog/form thêm mới (giả định có PatientDialogComponent)
      const dialogRef = this.dialog.open(PatientDialogComponent, {
        width: '400px',
        data: { action: 'create' }  // Truyền data cho dialog
      });
  
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          // Lấy userId từ authService và thêm vào patient data
          const userId = this.authService.getUserId();
          if (userId === null) {
            console.error('User ID không tồn tại');
            this.router.navigate(['/login']);
            return;
          }
          
          // Format date và thêm userId
          const patientData = {
            ...result,
            userId: userId,
            dateOfBirth: typeof result.dateOfBirth === 'string' 
              ? new Date(result.dateOfBirth) 
              : result.dateOfBirth
          };
          
          // Gọi API tạo
          this.patientService.createPatient(patientData).subscribe({
            next: (newPatient) => {
              this.loadMyPatients();  // Reload danh sách
              console.log('Tạo bệnh nhân thành công:', newPatient);
            },
            error: (err) => console.error('Lỗi tạo patient:', err)
          });
        }
      });
    }

  viewPatientDetail(id: number) {
    this.router.navigate(['/patient', id]);  // Navigate đến trang chi tiết bệnh nhân
    console.log('Xem chi tiết bệnh nhân ID:', id);
  }

  requestTransfer(id: number) {
    this.router.navigate(['/transfers', id]);  // Navigate đến trang chuyển hồ sơ cho bệnh nhân
    console.log('Yêu cầu chuyển hồ sơ bệnh nhân ID:', id);
  }
}
