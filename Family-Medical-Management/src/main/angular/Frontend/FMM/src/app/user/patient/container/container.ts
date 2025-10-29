import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { RouterModule } from '@angular/router';
import { MatDialogModule } from '@angular/material/dialog';  
import { MatDialog } from '@angular/material/dialog';
import { PatientDialogComponent } from '../dialog/patient-dialog.component';
import { PatientService } from '../../../features/service/patient-service/patient.service'
import { PatientDTO } from '../../../features/model/patient.model';

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
    public dialog: MatDialog  // Inject MatDialog
  ) {}

  ngOnInit(): void {
    this.loadMyPatients();  // Load chỉ bệnh nhân của user hiện tại
  }

  loadMyPatients() {
    // Giả định userId từ auth (e.g., localStorage hoặc AuthService)
    const userId = 1;  // Thay bằng userId thực tế từ login
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
    // Navigate đến chi tiết bệnh nhân
    // Ví dụ: Mở dialog hoặc route đến /patient/{id}
    console.log('Xem chi tiết bệnh nhân ID:', id);
  }

  requestTransfer(id: number) {
    // Navigate đến trang transfer cho bệnh nhân này
    console.log('Yêu cầu chuyển hồ sơ bệnh nhân ID:', id);
    // Ví dụ: this.router.navigate(['/transfers', id]);
  }
}
