import { Component } from '@angular/core';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Menu } from '../menu/menu';

@Component({
  selector: 'app-header',
  imports: [MatSidenavModule, MatIconModule, MatButtonModule, RouterModule, CommonModule, Menu],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header {
  isMenuOpen = false;  // Trạng thái mở/đóng sidebar

  constructor() {}

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;  // Toggle khi click hamburger
  }

  closeMenu() {
    this.isMenuOpen = false;  // Đóng khi chọn item
  }

  logout() {
    // Gọi AuthService.logout() nếu có
    this.closeMenu();
  }
}
