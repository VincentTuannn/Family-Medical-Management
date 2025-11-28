import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MedicalRecordService } from '../../../features/service/medical-record-service/medical-record.service';
import { MedicalRecordDTO } from '../../../features/model/medical-record.model';

@Component({
  selector: 'app-medical-record-admin-container',
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatCardModule, MatDialogModule],
  templateUrl: './container.html',
  styleUrl: './container.scss',
})
export class MedicalRecordAdminContainer implements OnInit {
  displayedColumns: string[] = ['recordId', 'patientId', 'diagnosis', 'treatment', 'medications', 'recordDate', 'doctorName', 'actions'];
  medicalRecords: MedicalRecordDTO[] = [];
  loading = false;

  constructor(
    private medicalRecordService: MedicalRecordService,
    public dialog: MatDialog,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadMedicalRecords();
  }

  loadMedicalRecords() {
    this.loading = true;
    this.medicalRecordService.getAllMedicalRecords().subscribe({
      next: (data) => {
        this.medicalRecords = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Lỗi load medical records:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  deleteMedicalRecord(id: number) {
    if (confirm('Bạn có chắc chắn muốn xóa hồ sơ y tế này?')) {
      this.medicalRecordService.deleteMedicalRecord(id).subscribe({
        next: () => {
          this.loadMedicalRecords();
          alert('Xóa thành công!');
        },
        error: (err) => {
          console.error('Lỗi xóa medical record:', err);
          alert('Lỗi xóa hồ sơ y tế!');
        }
      });
    }
  }
}

