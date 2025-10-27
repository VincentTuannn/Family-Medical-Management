import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { PatientDialogComponent } from '../dialog/patient-dialog.component';
import { PatientService } from '../../../features/service/patient-service/patient.service'
import { PatientDTO } from '../../../model/patient.model';

@Component({
  selector: 'app-container',
  imports: [CommonModule, MatTableModule, MatButtonModule],
  templateUrl: './container.html',
  styleUrl: './container.scss',
})
export class PatientContainer implements OnInit {
  displayedColumns: string[] = ['id', 'fullName', 'dateOfBirth', 'gender', 'actions'];  // Cột table
  patients: PatientDTO[] = [];  // Danh sách bệnh nhân

  constructor(
    private patientService: PatientService, 
    public dialog: MatDialog // Inject MatDialog (fix lỗi 'dialog' not exist)
  ) {}

  ngOnInit(): void {
    this.loadPatients();  // Load dữ liệu khi init
  }

  loadPatients() {
    this.patientService.getAllPatients().subscribe({
      next: (data) => this.patients = data,
      error: (err) => console.error('Lỗi load patients:', err)
    });
  }

  addPatient() {
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

  editPatient(id: number) {
    const patient = this.patients.find(p => p.patientId === id);
    if (patient) {
      // Mở dialog/form sửa
      const dialogRef = this.dialog.open(PatientDialogComponent, {
        width: '400px',
        data: { action: 'edit', patient }  // Truyền data cho dialog
      });

      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          // Gọi API sửa
          this.patientService.updatePatient(id, result).subscribe({
            next: (updatedPatient) => {
              const index = this.patients.findIndex(p => p.patientId === id);
              this.patients[index] = updatedPatient;  // Cập nhật list
              console.log('Cập nhật bệnh nhân thành công:', updatedPatient);
            },
            error: (err) => console.error('Lỗi cập nhật patient:', err)
          });
        }
      });
    }
  }

  deletePatient(id: number) {
    if (confirm('Xóa bệnh nhân này?')) {
      this.patientService.deletePatient(id).subscribe({
        next: () => {
          this.patients = this.patients.filter(p => p.patientId !== id);
          alert('Xóa thành công!');
        },
        error: (err) => console.error('Lỗi xóa patient:', err)
      });
    }
  }
}
