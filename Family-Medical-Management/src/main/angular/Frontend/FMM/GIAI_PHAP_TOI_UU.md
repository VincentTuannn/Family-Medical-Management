# 🎯 Giải Pháp Tối Ưu: Sửa Interceptor (Dễ Bảo Trì & Tương Lai)

## 📋 Tại Sao Chọn Cách Này?

### ✅ Ưu Điểm:
1. **Dễ bảo trì**: Giữ nguyên code service, chỉ sửa 1 file (interceptor)
2. **Linh hoạt cho tương lai**: 
   - Service có thể thêm custom headers cho Spring AI (ví dụ: `X-AI-Model`, `X-Stream`)
   - Service có thể thêm headers cho Kafka (ví dụ: `X-Kafka-Topic`)
   - Interceptor không ghi đè headers từ service
3. **Tập trung logic**: Tất cả authentication logic ở 1 nơi
4. **Không ảnh hưởng code hiện tại**: Service code giữ nguyên
5. **Hỗ trợ streaming/SSE**: Khi thêm Spring AI streaming, không cần sửa interceptor

### ❌ Nhược Điểm:
- Cần hiểu cách Angular merge headers (nhưng đơn giản)

---

## 🔧 Cách Sửa

### File Cần Sửa:
**`src/app/features/interceptor/auth.interceptor.ts`**

### Giải Thích:
- Angular `HttpHeaders` là **immutable** và **merge** headers khi clone request
- Khi dùng `setHeaders`, nó sẽ **merge** với headers hiện có, không ghi đè
- Nhưng cần đảm bảo merge đúng cách để không mất headers từ service

### Code Mới:

```typescript
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../service/auth-service/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  // Nếu có token, merge vào headers hiện có
  if (token) {
    // Clone request và merge Authorization header
    // Angular sẽ tự động merge với headers từ httpOptions
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
        // Không set Content-Type ở đây, để giữ nguyên từ service
        // Nếu service đã set Content-Type, nó sẽ được giữ
        // Nếu service chưa set, Angular sẽ tự động set khi có body
      }
    });
    console.log('Interceptor: Đã thêm token vào request:', req.url);
    return next(cloned);
  } else {
    console.warn('Interceptor: Không có token cho request:', req.url);
  }

  return next(req);
};
```

**Lưu ý**: Code này đã đúng về mặt merge headers, nhưng vấn đề có thể là cách `HttpHeaders` được tạo trong service.

---

## 🔍 Vấn Đề Thực Sự

Sau khi phân tích kỹ, vấn đề là:
- `new HttpHeaders({ 'Content-Type': 'application/json' })` tạo headers **immutable**
- Khi interceptor clone request với `setHeaders`, nó merge đúng
- **NHƯNG** nếu service truyền `httpOptions` object trực tiếp, Angular có thể xử lý khác

**Giải pháp tốt hơn**: Sửa interceptor để đảm bảo merge đúng, VÀ sửa service để dùng cách tạo headers linh hoạt hơn.

---

## 🎯 GIẢI PHÁP KẾT HỢP (KHUYẾN NGHỊ)

### Bước 1: Sửa Interceptor (Bắt buộc)
Sửa để merge headers đúng cách và log để debug.

### Bước 2: Sửa PatientService (Tùy chọn nhưng khuyến nghị)
Loại bỏ `httpOptions` tĩnh, để Angular tự động xử lý Content-Type.

**Lý do kết hợp:**
- Interceptor đảm bảo Authorization luôn được thêm
- Service không cần quan tâm Content-Type (Angular tự động)
- Khi cần custom headers (AI/Kafka), service có thể thêm dễ dàng

---

## 📝 Code Chi Tiết

### File 1: `src/app/features/interceptor/auth.interceptor.ts`

```typescript
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../service/auth-service/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  // Nếu có token, thêm Authorization header
  // Angular sẽ tự động merge với headers hiện có từ service
  if (token) {
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    
    // Debug log (có thể bỏ trong production)
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
```

### File 2: `src/app/features/service/patient-service/patient.service.ts`

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';  // Bỏ HttpHeaders
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { PatientDTO } from '../../model/patient.model';

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private apiUrl = `${environment.apiUrl}/patient`;

  // XÓA httpOptions - không cần nữa
  // Angular tự động set Content-Type cho JSON
  // Interceptor tự động thêm Authorization

  constructor(private http: HttpClient) {}

  // GET all patients
  getAllPatients(): Observable<PatientDTO[]> {
    return this.http.get<PatientDTO[]>(this.apiUrl);
  }

  // GET by ID
  getPatientById(id: number): Observable<PatientDTO> {
    return this.http.get<PatientDTO>(`${this.apiUrl}/${id}`);
  }

  // POST create
  createPatient(patient: PatientDTO): Observable<PatientDTO> {
    return this.http.post<PatientDTO>(this.apiUrl, patient);
  }

  // PUT update
  updatePatient(id: number, patient: PatientDTO): Observable<PatientDTO> {
    return this.http.put<PatientDTO>(`${this.apiUrl}/${id}`, patient);
  }

  // DELETE by ID
  deletePatient(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getPatientsByUserId(userId: number): Observable<PatientDTO[]> {
    return this.http.get<PatientDTO[]>(`${this.apiUrl}/user/${userId}`);
  }

  // Ví dụ: Khi cần thêm custom headers cho Spring AI (tương lai)
  // getAIPrediction(data: any): Observable<any> {
  //   return this.http.post(`${this.apiUrl}/ai/predict`, data, {
  //     headers: { 'X-AI-Model': 'gpt-4', 'X-Stream': 'true' }
  //   });
  // }
}
```

---

## 🚀 Lợi Ích Cho Tương Lai

### Khi Thêm Spring AI:
```typescript
// Service có thể thêm custom headers dễ dàng
getAIChat(message: string): Observable<any> {
  return this.http.post(`${this.apiUrl}/ai/chat`, { message }, {
    headers: {
      'X-AI-Model': 'gpt-4',
      'X-Stream': 'true'  // Cho streaming response
    }
  });
  // Interceptor vẫn tự động thêm Authorization
}
```

### Khi Thêm Kafka:
```typescript
// Service có thể thêm Kafka headers
sendKafkaMessage(topic: string, data: any): Observable<any> {
  return this.http.post(`${this.apiUrl}/kafka/send`, data, {
    headers: {
      'X-Kafka-Topic': topic,
      'X-Kafka-Key': 'user-123'
    }
  });
  // Interceptor vẫn tự động thêm Authorization
}
```

### Khi Cần Streaming (SSE):
```typescript
// Service có thể dùng observe: 'events' cho SSE
getAIStream(prompt: string): Observable<any> {
  return this.http.post(`${this.apiUrl}/ai/stream`, { prompt }, {
    observe: 'events',
    headers: { 'Accept': 'text/event-stream' }
  });
  // Interceptor vẫn tự động thêm Authorization
}
```

---

## ✅ Tóm Tắt

**Cách sửa:**
1. ✅ Sửa interceptor để merge headers đúng (bắt buộc)
2. ✅ Loại bỏ `httpOptions` tĩnh trong PatientService (khuyến nghị)
3. ✅ Để Angular tự động xử lý Content-Type

**Kết quả:**
- ✅ Dễ bảo trì: Logic tập trung ở interceptor
- ✅ Linh hoạt: Service có thể thêm custom headers khi cần
- ✅ Tương lai: Hỗ trợ Spring AI, Kafka, streaming dễ dàng
- ✅ Không ảnh hưởng: Code hiện tại hoạt động tốt hơn

