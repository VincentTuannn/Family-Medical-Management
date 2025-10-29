import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [MatIconModule, MatButtonModule, RouterLink, CommonModule],  
  templateUrl: './menu.html',
  styleUrls: ['./menu.scss']
})
export class Menu {
  @Input() isOpen: boolean = false;  // Input từ parent (Header) để kiểm soát mở/đóng
  @Output() isOpenChange = new EventEmitter<boolean>();

  menuItems = [
    { path: '', redirectTo: '/dashboard', pathMatch: 'full', icon: 'dashboard', label: 'Trang Chủ' },
    // { path: '/dashboard' },
    { path: '/patients', icon: 'people', label: 'Bệnh Nhân' },
    { path: '/doctor', icon: 'medical_services', label: 'Bác Sĩ' },
    { path: '/transfers', icon: 'share', label: 'Chia Sẻ' },
    { path: '/appointments', icon: 'event', label: 'Lịch Hẹn' }
  ];

  constructor(private router: Router) {
    // Listener navigation để tự động close menu khi navigate
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.closeMenu();  // Đóng menu sau mỗi navigation
      }
    });
  }

  onItemClick(itemPath:string) {
    this.router.navigate([itemPath]);  // Navigate thực tế
    this.closeMenu();
    console.log('Menu item clicked'); // Đóng menu sau click
  }

  closeMenu() {
    this.isOpen = false;
    this.isOpenChange.emit(false);  // Notify parent
  }

  isActive(itemPath: string): boolean {
    return this.router.isActive(itemPath, true);  // exact: true để match chính xác
  }

  logout() {
    // Gọi AuthService.logout() nếu có
    console.log('Logout clicked');
  }
}