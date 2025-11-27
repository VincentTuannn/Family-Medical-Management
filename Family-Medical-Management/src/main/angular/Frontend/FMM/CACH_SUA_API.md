# 🔧 CÁC CÁCH SỬA LỖI API

## 📍 File Cần Sửa Chính

**`src/app/features/service/patient-service/patient.service.ts`**

---

## 🎯 CÁCH 1: Loại Bỏ httpOptions (KHUYẾN NGHỊ) ⭐

### Giải Thích:
- `httpOptions` với `HttpHeaders` tĩnh có thể **ghi đè** headers từ interceptor
- Interceptor thêm `Authorization: Bearer <token>` nhưng bị mất khi dùng `httpOptions`
- AuthService hoạt động tốt vì **KHÔNG dùng httpOptions**

### Cách Sửa:

**XÓA dòng 13-15:**
```typescript
// XÓA 3 dòng này:
private httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
```

**SỬA tất cả 6 methods (dòng 20, 25, 30, 35, 40, 44):**
- Bỏ tham số `, this.httpOptions` ở cuối mỗi dòng

**Ví dụ:**
```typescript
// TRƯỚC:
getAllPatients(): Observable<PatientDTO[]> {
  return this.http.get<PatientDTO[]>(this.apiUrl, this.httpOptions);
}

// SAU:
getAllPatients(): Observable<PatientDTO[]> {
  return this.http.get<PatientDTO[]>(this.apiUrl);
}
```

**XÓA import không cần thiết (dòng 2):**
```typescript
// TRƯỚC:
import { HttpClient, HttpHeaders } from '@angular/common/http';

// SAU:
import { HttpClient } from '@angular/common/http';
```

---

## 🎯 CÁCH 2: Tạo httpOptions Động Với Token

### Giải Thích:
- Tạo headers động trong mỗi method, merge token vào
- Kiểm soát được headers nhưng code dài hơn

### Cách Sửa:

**Thêm import (sau dòng 5):**
```typescript
import { AuthService } from '../auth-service/auth.service';
```

**Sửa constructor (dòng 17):**
```typescript
// TRƯỚC:
constructor(private http: HttpClient) {}

// SAU:
constructor(
  private http: HttpClient,
  private authService: AuthService
) {}
```

**XÓA httpOptions cũ (dòng 13-15) và THÊM method mới:**
```typescript
// XÓA:
private httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

// THÊM:
private getHttpOptions() {
  const token = this.authService.getToken();
  return {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : ''
    })
  };
}
```

**SỬA tất cả methods:**
```typescript
// Thay this.httpOptions thành this.getHttpOptions()
getAllPatients(): Observable<PatientDTO[]> {
  return this.http.get<PatientDTO[]>(this.apiUrl, this.getHttpOptions());
}
```

---

## 🎯 CÁCH 3: Sửa Interceptor (Phức Tạp - Không Khuyến Nghị)

### File: `src/app/features/interceptor/auth.interceptor.ts`

Sửa để merge headers đúng cách, nhưng vẫn có thể không hoạt động với `httpOptions` tĩnh.

---

## ✅ SAU KHI SỬA - KIỂM TRA

1. **Mở Browser DevTools (F12) → Network tab**
2. **Thực hiện request** (ví dụ: load danh sách patients)
3. **Kiểm tra Request Headers:**
   - ✅ Phải có: `Authorization: Bearer <token>`
   - ✅ Phải có: `Content-Type: application/json`
4. **Kiểm tra Response:**
   - ✅ Status 200 (thành công)
   - ❌ Status 401/403 (vẫn lỗi, cần kiểm tra token/role)

---

## 📝 TÓM TẮT

**KHUYẾN NGHỊ: Chọn CÁCH 1**

**Lý do:**
- ✅ Đơn giản nhất
- ✅ Đã được chứng minh (AuthService dùng cách này)
- ✅ Angular tự động set Content-Type
- ✅ Interceptor tự động thêm Authorization

**Các bước:**
1. Xóa `httpOptions` (dòng 13-15)
2. Bỏ `, this.httpOptions` trong 6 methods
3. Xóa `HttpHeaders` khỏi import
4. Test lại


