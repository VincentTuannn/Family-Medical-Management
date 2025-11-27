import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { PatientDialogComponent } from '../dialog/patient-dialog.component';
import { PatientService } from '../../../features/service/patient-service/patient.service'
import { PatientDTO } from '../../../features/model/patient.model';

@Component({
  selector: 'app-patient-admin-container',
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatCardModule, MatDialogModule],
  templateUrl: './container.html',
  styleUrl: './container.scss',
})
export class PatientAdminContainer implements OnInit {
  displayedColumns: string[] = ['patientId', 'userId', 'fullName', 'dateOfBirth', 'gender', 'bloodType', 'emergencyContact', 'actions'];
  patients: PatientDTO[] = [];
  loading = false;

  constructor(
    private patientService: PatientService, 
    public dialog: MatDialog,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients() {
    this.loading = true;
    this.patientService.getAllPatients().subscribe({
      next: (data) => {
        this.patients = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Lỗi load patients:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  addPatient() {
    const dialogRef = this.dialog.open(PatientDialogComponent, {
      width: '500px',
      data: { action: 'create' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.patientService.createPatient(result).subscribe({
          next: () => {
            this.loadPatients(); // Reload danh sách
          },
          error: (err) => {
            console.error('Lỗi tạo patient:', err);
            alert('Lỗi tạo bệnh nhân!');
          }
        });
      }
    });
  }

  editPatient(id: number) {
    const patient = this.patients.find(p => p.patientId === id);
    if (patient) {
      const dialogRef = this.dialog.open(PatientDialogComponent, {
        width: '500px',
        data: { action: 'edit', patient }
      });

      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.patientService.updatePatient(id, result).subscribe({
            next: () => {
              this.loadPatients(); // Reload danh sách
            },
            error: (err) => {
              console.error('Lỗi cập nhật patient:', err);
              alert('Lỗi cập nhật bệnh nhân!');
            }
          });
        }
      });
    }
  }

  deletePatient(id: number) {
    if (confirm('Bạn có chắc chắn muốn xóa bệnh nhân này?')) {
      this.patientService.deletePatient(id).subscribe({
        next: () => {
          this.loadPatients(); // Reload danh sách
          alert('Xóa thành công!');
        },
        error: (err) => {
          console.error('Lỗi xóa patient:', err);
          alert('Lỗi xóa bệnh nhân!');
        }
      });
    }
  }
}
