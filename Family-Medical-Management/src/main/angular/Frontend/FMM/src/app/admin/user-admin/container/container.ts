import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { UserService } from '../../../features/service/user-service/user.service';
import { UserDTO } from '../../../features/model/user.model';
import { UserDialogComponent } from '../dialog/user-dialog.component';

@Component({
  selector: 'app-user-admin-container',
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatCardModule, MatDialogModule],
  templateUrl: './container.html',
  styleUrl: './container.scss',
})
export class UserAdminContainer implements OnInit {
  displayedColumns: string[] = ['userId', 'username', 'email', 'phone', 'address', 'role', 'isActive', 'actions'];
  users: UserDTO[] = [];
  loading = false;

  constructor(
    private userService: UserService,
    public dialog: MatDialog,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers() {
    this.loading = true;
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Lỗi load users:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  addUser() {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      width: '500px',
      data: { action: 'create' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.userService.createUser(result).subscribe({
          next: () => {
            this.loadUsers();
          },
          error: (err) => {
            console.error('Lỗi tạo user:', err);
            alert('Lỗi tạo người dùng!');
          }
        });
      }
    });
  }

  editUser(id: number) {
    const user = this.users.find(u => u.userId === id);
    if (user) {
      const dialogRef = this.dialog.open(UserDialogComponent, {
        width: '500px',
        data: { action: 'edit', user }
      });

      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.userService.updateUser(id, result).subscribe({
            next: () => {
              this.loadUsers();
            },
            error: (err) => {
              console.error('Lỗi cập nhật user:', err);
              alert('Lỗi cập nhật người dùng!');
            }
          });
        }
      });
    }
  }

  deleteUser(id: number) {
    if (confirm('Bạn có chắc chắn muốn xóa người dùng này?')) {
      this.userService.deleteUser(id).subscribe({
        next: () => {
          this.loadUsers();
          alert('Xóa thành công!');
        },
        error: (err) => {
          console.error('Lỗi xóa user:', err);
          alert('Lỗi xóa người dùng!');
        }
      });
    }
  }
}

