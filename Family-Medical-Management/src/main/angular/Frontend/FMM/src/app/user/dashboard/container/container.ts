import { Component, OnInit, OnDestroy } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { DashboardService, DashboardStats } from '../../../features/service/dashboard-service/dashboard.service';
import { AuthService } from '../../../features/service/auth-service/auth.service';
import { filter, take, switchMap, takeUntil, delay } from 'rxjs/operators';
import { Subject, of } from 'rxjs';

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
export class DashboardContainer implements OnInit, OnDestroy {
  stats: StatItem[] = [];
  loading = true;
  private destroy$ = new Subject<void>();

  constructor(
    private dashboardService: DashboardService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Đảm bảo user đã login (token sẵn sàng) trước khi gọi API
    // Sử dụng Observable để đảm bảo token đã được load
    this.authService.isLoggedIn$.pipe(
      filter(isLoggedIn => isLoggedIn), // Chỉ tiếp tục khi đã login
      take(1), // Chỉ lấy giá trị đầu tiên
      takeUntil(this.destroy$), // Cleanup khi component destroy
      delay(50), // Đợi 50ms để đảm bảo interceptor đã sẵn sàng
      switchMap(() => this.dashboardService.getDashboardStats())
    ).subscribe({
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
        console.error('❌ Lỗi load dashboard:', err);
        // Nếu lỗi 403, thử lại sau 200ms (có thể do race condition)
        if (err.status === 403) {
          console.log('🔄 Lỗi 403, thử lại sau 200ms...');
          setTimeout(() => {
            this.loadStatsWithRetry();
          }, 200);
        } else {
          this.loading = false;
          // Fallback dữ liệu giả nếu có lỗi
          this.stats = Array(6).fill(null).map((_, i) => ({
            title: ['Bệnh Nhân', 'Lịch Hẹn', 'Chuyển Hồ Sơ', 'Bác Sĩ', 'Hồ Sơ Y Tế', 'Người Dùng'][i],
            count: 0,
            icon: ['people', 'event', 'share', 'medical_services', 'folder', 'group'][i],
            route: ['/patients', '/appointments', '/transfers', '/doctor', '/medical-records', '/users'][i]
          }));
        }
      }
    });
  }

  // Method để retry khi gặp lỗi 403
  private loadStatsWithRetry() {
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
        console.error('❌ Lỗi load dashboard (retry):', err);
        this.loading = false;
        this.stats = Array(6).fill(null).map((_, i) => ({
          title: ['Bệnh Nhân', 'Lịch Hẹn', 'Chuyển Hồ Sơ', 'Bác Sĩ', 'Hồ Sơ Y Tế', 'Người Dùng'][i],
          count: 0,
          icon: ['people', 'event', 'share', 'medical_services', 'folder', 'group'][i],
          route: ['/patients', '/appointments', '/transfers', '/doctor', '/medical-records', '/users'][i]
        }));
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
