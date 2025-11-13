import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-container',
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, RouterModule],
  templateUrl: './container.html',
  styleUrl: './container.scss',
})
export class DashboardContainer {
  private apiUrl = `${environment.apiUrl}/dashboard/stats`;

  constructor() {}

  navigateTo(route: string) {
    console.log('Navigate to:', route);
  }
}
