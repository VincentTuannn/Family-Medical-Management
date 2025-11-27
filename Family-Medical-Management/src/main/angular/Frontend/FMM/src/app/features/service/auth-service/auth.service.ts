import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { jwtDecode } from 'jwt-decode';

interface JwtPayload {  
  sub: string;  // Username
  userId: number;  // ID user từ token
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private tokenSubject = new BehaviorSubject<string | null>(localStorage.getItem('token'));
  public isLoggedIn$ = this.tokenSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(credentials: { username: string; password: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials);
  }

  register(userData: any): Observable<any> {
  return this.http.post(`${this.apiUrl}/register`, userData);
}

  setToken(token: string) {
    localStorage.setItem('token', token);
    this.tokenSubject.next(token);
  }

  logout() {
    localStorage.removeItem('token');
    this.tokenSubject.next(null);
  }

  isLoggedIn(): boolean {
    return !!this.tokenSubject.value;  // Check token exist
  }

  getToken(): string | null {
    // Lấy từ BehaviorSubject trước
    let token = this.tokenSubject.value;
    
    // Fallback: Nếu BehaviorSubject chưa có giá trị, thử lấy từ localStorage
    // Điều này xử lý race condition khi service chưa khởi tạo xong
    if (!token) {
      try {
        token = localStorage.getItem('token');
        // Nếu tìm thấy trong localStorage, cập nhật BehaviorSubject
        if (token) {
          this.tokenSubject.next(token);
        }
      } catch (e) {
        // localStorage có thể không available trong một số trường hợp
        console.warn('⚠️ Không thể truy cập localStorage');
      }
    }
    
    return token;
  }

  getUserId(): number | null {
    const token = this.getToken();
    if (token) {
      try {
        const payload: JwtPayload = jwtDecode(token);
        return payload.userId;  // Giả định token có claim 'userId' từ backend
      } catch (error) {
        console.error('Lỗi decode token:', error);
        return null;
      }
    }
    return null;
  }
}