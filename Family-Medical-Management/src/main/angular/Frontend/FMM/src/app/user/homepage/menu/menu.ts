import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-menu',
  imports: [MatIconModule, MatButtonModule, RouterLink, CommonModule],  
  templateUrl: './menu.html',
  styleUrls: ['./menu.scss']
})
export class Menu {
  @Input() isOpen: boolean = false;  // Input từ parent (Header) để kiểm soát mở/đóng

  menuItems = [
    { path: '/dashboard', icon: 'dashboard', label: 'Dashboard' },
    { path: '/patients', icon: 'people', label: 'Patients' },
    { path: '/doctor', icon: 'medical_services', label: 'Doctor' },
    { path: '/transfers', icon: 'share', label: 'Transfers' },
    { path: '/appointments', icon: 'event', label: 'Appointments' }
  ];

  constructor() {}

  onItemClick() {
    // Đóng menu khi click item (gọi event từ parent nếu cần)
    console.log('Menu item clicked');
  }

  logout() {
    // Gọi AuthService.logout() nếu có
    console.log('Logout clicked');
  }
}