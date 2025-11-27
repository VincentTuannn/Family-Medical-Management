import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../service/auth-service/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  
  // Lấy token từ BehaviorSubject (đảm bảo luôn có giá trị mới nhất)
  // Nếu BehaviorSubject chưa có giá trị, thử lấy từ localStorage trực tiếp
  let token = authService.getToken();
  
  // Fallback: Nếu token từ service là null, thử lấy từ localStorage
  // Điều này xử lý race condition khi component load nhanh hơn service
  if (!token) {
    try {
      token = localStorage.getItem('token');
    } catch (e) {
      // localStorage có thể không available trong một số trường hợp
      console.warn('⚠️ Không thể truy cập localStorage');
    }
  }

  // Nếu có token, thêm Authorization header
  // Angular sẽ tự động merge với headers hiện có từ service
  // Điều này cho phép service thêm custom headers (cho Spring AI, Kafka, etc.) mà không bị ghi đè
  if (token) {
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    
    // Debug log (chỉ log cho các request cần auth, trừ /auth/**)
    if (!req.url.includes('/auth/')) {
      console.log('✅ Interceptor: Đã thêm token vào request:', req.url);
    }
    
    return next(cloned);
  } else {
    // Chỉ log warning cho các request cần auth (trừ /auth/**)
    if (!req.url.includes('/auth/')) {
      console.warn('⚠️ Interceptor: Không có token cho request:', req.url);
    }
  }

  return next(req);
};

