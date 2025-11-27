import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';

interface AdminMenuItem {
  title: string;
  icon: string;
  route: string;
  color: string;
  description?: string;
}

@Component({
  selector: 'app-dashboard-admin',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    RouterModule
  ],
  templateUrl: './dashboard-admin.html',
  styleUrls: ['./dashboard-admin.scss']
})
export class DashboardAdminComponent {
  menuItems: AdminMenuItem[] = [
    {
      title: 'Quản Lý Bệnh Nhân',
      icon: 'people',
      route: '/admin/patients',
      color: 'primary',
      description: 'Thêm, sửa, xóa, xem chi tiết bệnh nhân'
    },
    {
      title: 'Quản Lý Bác Sĩ',
      icon: 'medical_services',
      route: '/admin/doctors',
      color: 'accent',
      description: 'Quản lý thông tin bác sĩ, chuyên khoa'
    },
    {
      title: 'Quản Lý Người Dùng',
      icon: 'group',
      route: '/admin/users',
      color: 'warn',
      description: 'Quản lý tài khoản user, admin, role'
    },
    
    {
      title: 'Lịch Hẹn',
      icon: 'event',
      route: '/admin/appointments',
      color: 'primary',
      description: 'Xem và duyệt lịch hẹn'
    },
    {
      title: 'Chuyển Hồ Sơ',
      icon: 'share',
      route: '/admin/transfers',
      color: 'accent',
      description: 'Quản lý yêu cầu chuyển hồ sơ'
    },
    {
      title: 'Hồ Sơ Y Tế',
      icon: 'folder_shared',
      route: '/admin/medical-records',
      color: 'warn',
      description: 'Xem tất cả hồ sơ y tế'
    },
    {
      title: 'Thống Kê & Báo Cáo',
      icon: 'bar_chart',
      route: '/admin/reports',
      color: 'primary',
      description: 'Báo cáo doanh thu, hoạt động'
    },
    {
      title: 'Cài Đặt Hệ Thống',
      icon: 'settings',
      route: '/admin/settings',
      color: 'warn',
      description: 'Cấu hình hệ thống, backup'
    }
  ];
}