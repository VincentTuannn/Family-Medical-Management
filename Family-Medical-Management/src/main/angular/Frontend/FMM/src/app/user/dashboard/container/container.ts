import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { DashboardService, DashboardStats } from '../../../features/service/dashboard-service/dashboard.service';
import { AuthService } from '../../../features/service/auth-service/auth.service';
import { filter, take, takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';

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
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Lần đầu vào dashboard: nếu token đã sẵn sàng thì load ngay, ngược lại chờ tới khi có token
    this.waitForTokenAndLoad();

    // Nếu người dùng nhấn lại vào logo/menu để quay lại dashboard, đảm bảo stats được reload
    this.router.events
      .pipe(
        filter((event): event is NavigationEnd => event instanceof NavigationEnd),
        takeUntil(this.destroy$)
      )
      .subscribe(event => {
        if (event.urlAfterRedirects === '/dashboard') {
          this.waitForTokenAndLoad(true);
        }
      });
  }

  private waitForTokenAndLoad(forceReload = false) {
    const token = this.authService.getToken();
    if (token) {
      // token đã sẵn sàng -> load stats (forceReload cho phép gọi ngay)
      if (forceReload) {
        this.loadStats();
      } else {
        // delay nhẹ để đảm bảo interceptor đã attach token
        setTimeout(() => this.loadStats(), 50);
      }
      return;
    }

    // token chưa có -> chờ observable emit
    this.authService.isLoggedIn$
      .pipe(
        filter((value): value is string => !!value),
        take(1),
        takeUntil(this.destroy$)
      )
      .subscribe(() => this.loadStats());
  }

  private loadStats() {
    this.loading = true;
    this.dashboardService.getDashboardStats().subscribe({
      next: (data: DashboardStats) => this.handleStatsSuccess(data),
      error: (err) => {
        console.error('❌ Lỗi load dashboard:', err);
        if (err.status === 403) {
          console.log('🔄 Lỗi 403, thử lại sau 200ms...');
          setTimeout(() => this.loadStatsWithRetry(), 200);
        } else {
          this.handleStatsFailure();
        }
      }
    });
  }

  // Method để retry khi gặp lỗi 403
  private loadStatsWithRetry() {
    this.loading = true;
    this.dashboardService.getDashboardStats().subscribe({
      next: (data: DashboardStats) => this.handleStatsSuccess(data),
      error: (err) => {
        console.error('❌ Lỗi load dashboard (retry):', err);
        this.handleStatsFailure();
      }
    });
  }

  private handleStatsSuccess(data: DashboardStats) {
    this.stats = [
      { title: 'Bệnh Nhân', count: data.patients, icon: 'people', route: '/patients' },
      { title: 'Lịch Hẹn', count: data.appointments, icon: 'event', route: '/appointments' },
      { title: 'Chuyển Hồ Sơ', count: data.transfers, icon: 'share', route: '/transfers' },
      { title: 'Bác Sĩ', count: data.doctors, icon: 'medical_services', route: '/doctor' }
    ];
    this.loading = false;
    this.cdr.detectChanges();
  }

  private handleStatsFailure() {
    this.loading = false;
    // Fallback dữ liệu giả nếu có lỗi
    this.stats = Array(6).fill(null).map((_, i) => ({
      title: ['Bệnh Nhân', 'Lịch Hẹn', 'Chuyển Hồ Sơ', 'Bác Sĩ', 'Hồ Sơ Y Tế', 'Người Dùng'][i],
      count: 0,
      icon: ['people', 'event', 'share', 'medical_services', 'folder', 'group'][i],
      route: ['/patients', '/appointments', '/transfers', '/doctor', '/medical-records', '/users'][i]
    }));
    this.cdr.detectChanges();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
