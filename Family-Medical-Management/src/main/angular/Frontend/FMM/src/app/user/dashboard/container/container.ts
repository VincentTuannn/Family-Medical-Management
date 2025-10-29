import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-container',
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, RouterModule],
  templateUrl: './container.html',
  styleUrl: './container.scss',
})
export class DashboardContainer {
  // Dữ liệu mẫu (thay bằng API call nếu cần)
  stats = [
    { title: 'Bệnh Nhân', count: 5, icon: 'people', route: '/patients' },
    { title: 'Lịch Hẹn', count: 2, icon: 'event', route: '/appointments' },
    { title: 'Chuyển Hồ Sơ', count: 1, icon: 'share', route: '/transfers' },
    { title: 'Bác Sĩ', count: 3, icon: 'medical_services', route: '/doctors' }
  ];

  constructor() {}

  navigateTo(route: string) {
    console.log('Navigate to:', route);
  }
}
