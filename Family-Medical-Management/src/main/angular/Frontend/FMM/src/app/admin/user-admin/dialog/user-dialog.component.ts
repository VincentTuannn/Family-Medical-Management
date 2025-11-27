import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogContent, MatDialogActions, MatDialogClose } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { FormsModule } from '@angular/forms';
import { UserDTO } from '../../../features/model/user.model';

@Component({
  selector: 'app-user-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    FormsModule,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose
  ],
  template: `
    <h2 mat-dialog-title>{{ data.action === 'create' ? 'Thêm Người Dùng' : 'Sửa Người Dùng' }}</h2>
    <mat-dialog-content>
      <form #userForm="ngForm">
        <mat-form-field appearance="fill">
          <mat-label>Tên Đăng Nhập</mat-label>
          <input matInput [(ngModel)]="user.username" name="username" required>
        </mat-form-field>
        <mat-form-field appearance="fill">
          <mat-label>Email</mat-label>
          <input matInput type="email" [(ngModel)]="user.email" name="email" required>
        </mat-form-field>
        <mat-form-field appearance="fill" *ngIf="data.action === 'create'">
          <mat-label>Mật Khẩu</mat-label>
          <input matInput type="password" [(ngModel)]="user.password" name="password" [required]="data.action === 'create'">
        </mat-form-field>
        <mat-form-field appearance="fill">
          <mat-label>Điện Thoại</mat-label>
          <input matInput [(ngModel)]="user.phone" name="phone">
        </mat-form-field>
        <mat-form-field appearance="fill">
          <mat-label>Địa Chỉ</mat-label>
          <input matInput [(ngModel)]="user.address" name="address">
        </mat-form-field>
        <mat-form-field appearance="fill">
          <mat-label>Vai Trò</mat-label>
          <mat-select [(ngModel)]="user.role" name="role" required>
            <mat-option value="USER">USER</mat-option>
            <mat-option value="DOCTOR">DOCTOR</mat-option>
            <mat-option value="ADMIN">ADMIN</mat-option>
          </mat-select>
        </mat-form-field>
        <mat-checkbox [(ngModel)]="user.isActive" name="isActive">Hoạt động</mat-checkbox>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="dialogRef.close()">Hủy</button>
      <button mat-raised-button color="primary" [mat-dialog-close]="user" [disabled]="!userForm.valid">Lưu</button>
    </mat-dialog-actions>
  `
})
export class UserDialogComponent {
  user: UserDTO = {
    userId: 0,
    username: '',
    email: '',
    phone: '',
    address: '',
    createdAt: new Date(),
    role: 'USER',
    isActive: true
  };

  constructor(
    public dialogRef: MatDialogRef<UserDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { action: 'create' | 'edit'; user?: UserDTO }
  ) {
    if (this.data.user) {
      this.user = { ...this.data.user };
      delete this.user.password; // Không hiển thị password khi edit
    }
  }
}

