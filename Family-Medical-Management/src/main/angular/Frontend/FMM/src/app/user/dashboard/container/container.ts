import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { environment } from '../../../../environments/environment';
import { DashboardService, DashboardStats } from '../../../features/service/dashboard-service/dashboard.service';

interface StatItem {
  title: string;
  count: number;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-container',
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, RouterModule],
  templateUrl: './container.html',
  styleUrl: './container.scss',
})
export class DashboardContainer {
  stats: StatItem[] = [];
  loading = true;

  constructor(private dashboardService: DashboardService) {}

  

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats() {
    this.loading = true;
    this.dashboardService.getDashboardStats().subscribe({
      next: (data: DashboardStats) => {
        this.stats = [
          { title: 'Bệnh Nhân', count: data.patients, icon: 'people', route: '/patients' },
          { title: 'Lịch Hẹn', count: data.appointments, icon: 'event', route: '/appointments' },
          { title: 'Chuyển Hồ Sơ', count: data.transfers, icon: 'share', route: '/transfers' },
          { title: 'Bác Sĩ', count: data.doctors, icon: 'medical_services', route: '/doctor' }
        ];
        this.loading = false;
      },
      error: (err) => {
        console.error('Lỗi load dashboard:', err);
        this.loading = false;
        // Fallback dữ liệu giả nếu có lỗi
        this.stats = Array(6).fill(null).map((_, i) => ({
          title: ['Bệnh Nhân', 'Lịch Hẹn', 'Chuyển Hồ Sơ', 'Bác Sĩ', 'Hồ Sơ Y Tế', 'Người Dùng'][i],
          count: 0,
          icon: ['people', 'event', 'share', 'medical_services', 'folder', 'group'][i],
          route: ['/patients', '/appointments', '/transfers', '/doctor', '/medical-records', '/users'][i]
        }));
      }
    });
  }
}
