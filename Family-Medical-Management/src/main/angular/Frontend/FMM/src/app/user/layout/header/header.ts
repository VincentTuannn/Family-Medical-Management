import { Component } from '@angular/core';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Menu } from '../menu/menu';
import { AuthService } from '../../../features/service/auth-service/auth.service';

@Component({
  selector: 'app-header',
  imports: [MatSidenavModule, MatIconModule, MatButtonModule, RouterModule, CommonModule, Menu],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header {
  isOpen = false;  // Trạng thái mở/đóng sidebar

  constructor(public authService: AuthService) {}

  toggleMenu() {
    this.isOpen = !this.isOpen;  // Toggle khi click hamburger
  }

  closeMenu() {
    this.isOpen = false;  // Đóng khi chọn item
  }

  logout() {
    this.authService.logout();
    this.closeMenu();
  }
}
