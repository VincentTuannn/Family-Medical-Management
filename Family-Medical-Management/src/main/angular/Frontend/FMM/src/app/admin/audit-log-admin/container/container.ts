import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { AuditLogService } from '../../../features/service/audit-log-service/audit-log.service';
import { AuditLogDTO } from '../../../features/model/audit-log.model';

@Component({
  selector: 'app-audit-log-admin-container',
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatCardModule],
  templateUrl: './container.html',
  styleUrl: './container.scss',
})
export class AuditLogAdminContainer implements OnInit {
  displayedColumns: string[] = ['logId', 'userId', 'action', 'details', 'ipAddress', 'createdAt'];
  auditLogs: AuditLogDTO[] = [];
  loading = false;

  constructor(
    private auditLogService: AuditLogService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadAuditLogs();
  }

  loadAuditLogs() {
    this.loading = true;
    this.auditLogService.getAllAuditLogs().subscribe({
      next: (data) => {
        this.auditLogs = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Lỗi load audit logs:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}

