# Phân Tích Vấn Đề API và Các Cách Sửa

## 🔍 Phân Tích Vấn Đề

### Vấn Đề Hiện Tại
- ✅ **AuthService** (login/register) hoạt động tốt
- ❌ **PatientService, DoctorService, DashboardService** không lấy được dữ liệu

### Nguyên Nhân

#### 1. **Vấn Đề với PatientService** (File: `src/app/features/service/patient-service/patient.service.ts`)

**Code hiện tại:**
```typescript
private httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

getAllPatients(): Observable<PatientDTO[]> {
  return this.http.get<PatientDTO[]>(this.apiUrl, this.httpOptions);
}
```

**Vấn đề:**
- Khi truyền `httpOptions` vào request, Angular có thể **ghi đè** hoặc **không merge** đúng với headers từ interceptor
- Interceptor thêm `Authorization: Bearer <token>` nhưng `httpOptions` chỉ có `Content-Type`, có thể làm mất header Authorization

#### 2. **Backend Security Config** (File: `Backend/FMM/Configure/SecurityConfig.java`)

Tất cả các endpoint sau **YÊU CẦU AUTHENTICATION**:
- `/api/patient/**` - yêu cầu role USER, DOCTOR, hoặc ADMIN
- `/api/doctor/**` - yêu cầu role DOCTOR hoặc ADMIN  
- `/api/appointment/**` - yêu cầu role USER, DOCTOR, hoặc ADMIN
- `/api/transfer/**` - yêu cầu role USER, DOCTOR, hoặc ADMIN

Chỉ `/api/auth/**` là public (không cần token).

#### 3. **Interceptor** (File: `src/app/features/interceptor/auth.interceptor.ts`)

Interceptor đã được đăng ký đúng trong `app.config.ts`, nhưng có thể bị ghi đè bởi `httpOptions`.

---

## 🛠️ CÁC CÁCH SỬA (Chọn 1 trong 3)

### **CÁCH 1: Loại Bỏ httpOptions (Đơn Giản Nhất - KHUYẾN NGHỊ)** ⭐

**Ưu điểm:**
- Đơn giản, dễ bảo trì
- Để interceptor tự động xử lý headers
- Giống với cách AuthService đang làm (đã hoạt động tốt)

**Nhược điểm:**
- Không kiểm soát trực tiếp Content-Type (nhưng Angular tự động set)

**File cần sửa:**
- `src/app/features/service/patient-service/patient.service.ts`

**Cách sửa:**
```typescript
// XÓA dòng này:
private httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

// SỬA tất cả các method, BỎ tham số this.httpOptions:
getAllPatients(): Observable<PatientDTO[]> {
  return this.http.get<PatientDTO[]>(this.apiUrl);  // Bỏ this.httpOptions
}

getPatientById(id: number): Observable<PatientDTO> {
  return this.http.get<PatientDTO>(`${this.apiUrl}/${id}`);  // Bỏ this.httpOptions
}

createPatient(patient: PatientDTO): Observable<PatientDTO> {
  return this.http.post<PatientDTO>(this.apiUrl, patient);  // Bỏ this.httpOptions
}

updatePatient(id: number, patient: PatientDTO): Observable<PatientDTO> {
  return this.http.put<PatientDTO>(`${this.apiUrl}/${id}`, patient);  // Bỏ this.httpOptions
}

deletePatient(id: number): Observable<void> {
  return this.http.delete<void>(`${this.apiUrl}/${id}`);  // Bỏ this.httpOptions
}

getPatientsByUserId(userId: number): Observable<PatientDTO[]> {
  return this.http.get<PatientDTO[]>(`${this.apiUrl}/user/${userId}`);  // Bỏ this.httpOptions
}
```

---

### **CÁCH 2: Tạo httpOptions Động Trong Mỗi Method**

**Ưu điểm:**
- Kiểm soát được headers
- Merge đúng với token từ interceptor

**Nhược điểm:**
- Code dài hơn, lặp lại nhiều
- Cần inject AuthService vào mỗi service

**File cần sửa:**
- `src/app/features/service/patient-service/patient.service.ts`
- `src/app/features/service/doctor-service/doctor.service.ts`
- `src/app/features/service/dashboard-service/dashboard.service.ts`

**Cách sửa cho PatientService:**
```typescript
import { AuthService } from '../auth-service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private apiUrl = `${environment.apiUrl}/patient`;

  constructor(
    private http: HttpClient,
    private authService: AuthService  // Thêm AuthService
  ) {}

  // Tạo method helper để tạo headers
  private getHttpOptions() {
    const token = this.authService.getToken();
    return {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
      })
    };
  }

  getAllPatients(): Observable<PatientDTO[]> {
    return this.http.get<PatientDTO[]>(this.apiUrl, this.getHttpOptions());
  }

  // ... các method khác cũng dùng this.getHttpOptions()
}
```

---

### **CÁCH 3: Sửa Interceptor Để Merge Headers Đúng Cách**

**Ưu điểm:**
- Giữ nguyên code service
- Interceptor xử lý thông minh hơn

**Nhược điểm:**
- Phức tạp hơn
- Cần hiểu rõ cách Angular merge headers

**File cần sửa:**
- `src/app/features/interceptor/auth.interceptor.ts`

**Cách sửa:**
```typescript
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  if (token) {
    // Merge headers thay vì set mới
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
        // Không set Content-Type ở đây, để giữ nguyên từ request
      }
    });
    console.log('Interceptor: Đã thêm token vào request:', req.url);
    return next(cloned);
  }

  return next(req);
};
```

**Lưu ý:** Cách này có thể vẫn không hoạt động nếu `httpOptions` được tạo với `new HttpHeaders()` tĩnh.

---

## 📋 TÓM TẮT VÀ KHUYẾN NGHỊ

### **KHUYẾN NGHỊ: Chọn CÁCH 1** ⭐

**Lý do:**
1. ✅ Đơn giản nhất, ít code nhất
2. ✅ Đã được chứng minh hoạt động (AuthService dùng cách này)
3. ✅ Angular tự động set Content-Type cho JSON
4. ✅ Interceptor sẽ tự động thêm Authorization header
5. ✅ Dễ bảo trì, ít lỗi

### **Các File Cần Sửa (Nếu chọn Cách 1):**

1. **`src/app/features/service/patient-service/patient.service.ts`**
   - Xóa `httpOptions`
   - Bỏ tham số `this.httpOptions` trong tất cả các method

2. **Kiểm tra các service khác:**
   - `doctor-service/doctor.service.ts` - đã đúng (không dùng httpOptions)
   - `dashboard-service/dashboard.service.ts` - đã đúng (không dùng httpOptions)

### **Sau Khi Sửa, Kiểm Tra:**

1. Mở Browser DevTools (F12) → Tab Network
2. Thực hiện một request (ví dụ: load danh sách patients)
3. Kiểm tra Request Headers phải có:
   ```
   Authorization: Bearer <your-token>
   Content-Type: application/json
   ```
4. Kiểm tra Response phải trả về dữ liệu (status 200) thay vì 401/403

---

## 🔧 Các Vấn Đề Khác Có Thể Gặp

### Nếu vẫn không hoạt động sau khi sửa:

1. **Kiểm tra token có tồn tại:**
   ```typescript
   console.log('Token:', localStorage.getItem('token'));
   ```

2. **Kiểm tra interceptor có chạy:**
   - Xem console có log "Interceptor: Đã thêm token vào request" không

3. **Kiểm tra CORS:**
   - Backend đã config CORS cho `http://localhost:4200`
   - Nếu frontend chạy port khác, cần sửa `SecurityConfig.java`

4. **Kiểm tra Backend đang chạy:**
   - Backend phải chạy trên `http://localhost:8081` (theo `environment.ts`)

5. **Kiểm tra Role của User:**
   - User phải có role USER, DOCTOR, hoặc ADMIN
   - Xem trong database hoặc token JWT

