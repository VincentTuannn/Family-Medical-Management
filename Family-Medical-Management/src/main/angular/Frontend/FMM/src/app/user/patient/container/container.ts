import { Component, OnInit } from '@angular/core';
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
    private router: Router  // Import Router để navigate
  ) {}

  ngOnInit(): void {
    this.loadMyPatients();  // Load chỉ bệnh nhân của user hiện tại
  }

  loadMyPatients() {
  const userId = this.authService.getUserId();  

  if (userId === null) {
    console.error('User ID không tồn tại, vui lòng đăng nhập lại.');
    this.router.navigate(['/login']);  // Redirect nếu chưa login
    return;
  }

  // Bây giờ userId là number, gọi service an toàn
  this.patientService.getPatientsByUserId(userId).subscribe({
    next: (data) => this.patients = data,
    error: (err) => console.error('Lỗi load bệnh nhân của bạn:', err)
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
          // Gọi API tạo
          this.patientService.createPatient(result).subscribe({
            next: (newPatient) => {
              this.patients.push(newPatient);  // Thêm vào list
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
