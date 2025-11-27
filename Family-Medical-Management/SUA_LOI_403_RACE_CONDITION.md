# 🔧 Sửa Lỗi 403 Race Condition

## 🔍 Vấn Đề

Khi chuyển từ trang **patient** sang **dashboard**:
- **Lần đầu click**: Bị lỗi 403
- **Lần thứ 2 click**: Hoạt động bình thường

## 🎯 Nguyên Nhân

Đây là **race condition** - vấn đề về timing:

1. **Khi navigate**: Angular destroy component cũ và tạo component mới
2. **Component mới load**: `ngOnInit()` chạy ngay lập tức
3. **API được gọi**: Trước khi interceptor hoặc token sẵn sàng
4. **Lần thứ 2**: Token đã được cache/ready nên hoạt động

### Các Nguyên Nhân Cụ Thể:

1. **AuthService BehaviorSubject chưa có giá trị**: 
   - `tokenSubject` được khởi tạo với `localStorage.getItem('token')`
   - Nhưng có thể chưa được emit kịp khi component load

2. **Interceptor inject AuthService mỗi lần request**:
   - Có thể có delay khi inject service
   - Token có thể chưa được đọc kịp

3. **Component lifecycle timing**:
   - `ngOnInit()` chạy ngay khi component được tạo
   - Request được gửi trước khi Angular hoàn tất initialization

## ✅ Đã Sửa

### 1. Cải Thiện Interceptor (`auth.interceptor.ts`)

**Thêm fallback để lấy token từ localStorage:**
```typescript
// Nếu token từ service là null, thử lấy từ localStorage
if (!token) {
  try {
    token = localStorage.getItem('token');
  } catch (e) {
    console.warn('⚠️ Không thể truy cập localStorage');
  }
}
```

**Lợi ích:**
- Đảm bảo token luôn được lấy được, kể cả khi BehaviorSubject chưa ready
- Xử lý race condition tốt hơn

### 2. Cải Thiện Dashboard Component (`dashboard/container/container.ts`)

**Thêm kiểm tra token và retry logic:**
```typescript
ngOnInit(): void {
  // Đảm bảo token đã sẵn sàng trước khi gọi API
  setTimeout(() => {
    this.loadStats();
  }, 0);
}

loadStats() {
  // Kiểm tra token trước khi gọi API
  const token = this.authService.getToken();
  if (!token) {
    // Đợi và thử lại
    setTimeout(() => {
      if (this.authService.getToken()) {
        this.loadStats();
      }
    }, 100);
    return;
  }
  
  // Nếu lỗi 403, thử lại sau 200ms
  if (err.status === 403) {
    setTimeout(() => {
      this.loadStats();
    }, 200);
  }
}
```

**Lợi ích:**
- Đảm bảo token sẵn sàng trước khi gọi API
- Tự động retry nếu gặp lỗi 403 (do race condition)

## 🔄 Các Cách Sửa Khác (Nếu Vẫn Lỗi)

### Cách 1: Sử dụng Router Events

Thay vì gọi API trong `ngOnInit()`, đợi route activation:

```typescript
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

constructor(
  private router: Router,
  private dashboardService: DashboardService
) {
  // Chỉ load khi route đã được activate hoàn toàn
  this.router.events
    .pipe(filter(event => event instanceof NavigationEnd))
    .subscribe(() => {
      this.loadStats();
    });
}
```

### Cách 2: Sử dụng Route Resolver

Tạo resolver để đảm bảo data được load trước khi component hiển thị:

```typescript
// dashboard.resolver.ts
@Injectable({ providedIn: 'root' })
export class DashboardResolver implements Resolve<DashboardStats> {
  constructor(private dashboardService: DashboardService) {}
  
  resolve(): Observable<DashboardStats> {
    return this.dashboardService.getDashboardStats();
  }
}
```

### Cách 3: Sử dụng Observable từ AuthService

Thay vì kiểm tra token trực tiếp, subscribe vào `isLoggedIn$`:

```typescript
ngOnInit(): void {
  this.authService.isLoggedIn$.pipe(
    filter(isLoggedIn => isLoggedIn),
    take(1),
    switchMap(() => this.dashboardService.getDashboardStats())
  ).subscribe({
    next: (data) => { /* ... */ },
    error: (err) => { /* ... */ }
  });
}
```

### Cách 4: Thêm Delay Nhỏ

Đảm bảo Angular đã hoàn tất initialization:

```typescript
ngOnInit(): void {
  // Đợi Angular hoàn tất initialization
  setTimeout(() => {
    this.loadStats();
  }, 50); // 50ms thường đủ
}
```

## 📋 Checklist

- [x] Đã cải thiện interceptor với fallback localStorage
- [x] Đã thêm kiểm tra token trong dashboard component
- [x] Đã thêm retry logic cho lỗi 403
- [ ] Đã test lại sau khi sửa
- [ ] Vẫn còn lỗi? → Thử các cách sửa khác ở trên

## 🚨 Nếu Vẫn Lỗi

Nếu vẫn gặp lỗi 403 ở lần đầu, hãy thử:

1. **Tăng delay trong `ngOnInit()`**: Từ 0ms lên 50-100ms
2. **Sử dụng Router Events**: Đợi route activation hoàn toàn
3. **Kiểm tra backend logs**: Xem có log gì khi request đầu tiên đến không
4. **Thêm logging chi tiết**: Để xem timing của các events

## 💡 Lưu Ý

- Race condition thường xảy ra khi:
  - Component load quá nhanh
  - Service chưa được inject đầy đủ
  - Token chưa được đọc từ localStorage
  
- Giải pháp tốt nhất là đảm bảo token luôn sẵn sàng trước khi gọi API


