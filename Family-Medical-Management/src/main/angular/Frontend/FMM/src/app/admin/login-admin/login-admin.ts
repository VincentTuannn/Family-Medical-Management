import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../features/service/auth-service/auth.service';
import { jwtDecode } from 'jwt-decode';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

interface JwtPayload {
  role?: string;
  roles?: string;
  userId?: number;
}

@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatCardModule, MatSnackBarModule],
  templateUrl: './login-admin.html',
  styleUrl: './login-admin.scss',
})
export class LoginAdminComponent {
  loginForm: FormGroup;
  errorMessage = '';

  constructor(private fb: FormBuilder, private router: Router, private authService: AuthService, private snackBar: MatSnackBar) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) return;

    const { username, password } = this.loginForm.value;

    this.authService.login({ username, password }).subscribe({
      next: (response: any) => {
        const token = response.token;
        this.authService.setToken(token);

        // Decode token để lấy role
        try {
          const payload: JwtPayload = jwtDecode(token);
          const role = payload.role || payload.roles;

          if (role === 'ADMIN' || role?.includes('ADMIN')) {
            // Chỉ ADMIN được vào dashboard
            this.router.navigate(['/dashboard-admin']);
          } else {
            // Không phải ADMIN → logout + báo lỗi
            this.authService.logout();
            this.errorMessage = 'Chỉ tài khoản ADMIN mới được phép đăng nhập vào trang này!';
            alert(this.errorMessage);
          }
        } catch (error) {
          console.error('Lỗi decode token:', error);
          this.authService.logout();
          this.errorMessage = 'Token không hợp lệ!';
        }
      },
      error: (err) => {
        console.error('Lỗi đăng nhập:', err);
        this.errorMessage = err.error?.error || 'Sai tên đăng nhập hoặc mật khẩu!';
      }
    });
  }

  private showError(message: string) {
    this.snackBar.open(`❌ ${message}`, 'Đóng', {
      duration: 5000,
      panelClass: ['error-toast'],
      horizontalPosition: 'center',
      verticalPosition: 'top'
    });
  }

  private showSuccess(message: string) {
    this.snackBar.open(`✅ ${message}`, 'Đóng', {
      duration: 3000,
      panelClass: ['success-toast'],
      horizontalPosition: 'center',
      verticalPosition: 'top'
    });
  }

  navigateToRegister() {
    this.router.navigate(['/register']);  // Navigate đến route register
  }
}
