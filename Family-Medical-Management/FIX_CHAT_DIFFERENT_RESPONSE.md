# Sửa lỗi: Frontend và Postman trả về kết quả khác nhau

## Vấn đề
Khi hỏi cùng một câu hỏi:
- **Postman**: Trả về kết quả đúng (dựa trên documents của user)
- **Frontend**: Trả về kết quả khác (có thể dựa trên TẤT CẢ documents)

## Nguyên nhân

### Vấn đề chính: `userId` là `null`

Khi frontend gửi request:
1. Nếu **token không được gửi** hoặc **token không hợp lệ** → `userId = null`
2. Khi `userId = null`, `similaritySearch()` sẽ tìm trong **TẤT CẢ documents** của tất cả users
3. Khi `userId` có giá trị, `similaritySearch()` chỉ tìm trong **documents của user đó**

### Code logic:
```java
// VectorStoreService.java
if (userId != null) {
    allDocuments = documentRepository.findByUserId(userId);  // ✅ Chỉ documents của user
} else {
    allDocuments = documentRepository.findAll();  // ❌ TẤT CẢ documents
}
```

## Cách kiểm tra

### 1. Kiểm tra Backend Logs

Sau khi thêm logging, backend sẽ in ra:

#### ✅ Trường hợp đúng (có userId):
```
🔍 Chat request - useRAG: true, userId: 3
🔍 Message: Trong tài liệu của tôi bệnh dại là gì?
🔍 Auth header found, token length: 200, userId: 3
🔍 Similarity search - userId: 3, found 15 documents
✅ Chat response - usedRAG: true, sources: [Bệnh dại.pdf]
```

#### ❌ Trường hợp sai (userId = null):
```
🔍 Chat request - useRAG: true, userId: null
🔍 Message: Trong tài liệu của tôi bệnh dại là gì?
⚠️ No Authorization header found or invalid format
⚠️ WARNING: userId is null, RAG will search in ALL documents
⚠️ Similarity search - userId is NULL, searching in ALL 150 documents
✅ Chat response - usedRAG: true, sources: [Bệnh dại.pdf, OtherFile.pdf, ...]
```

### 2. Kiểm tra Frontend

#### Mở Browser DevTools (F12) → Console tab

#### Kiểm tra token:
```javascript
// Trong Console
localStorage.getItem('token')
```

Nếu trả về `null` → **Token không có** → Cần đăng nhập lại

#### Kiểm tra Network tab:
1. Gửi một message từ frontend
2. Xem request trong Network tab
3. Kiểm tra **Headers**:
   - ✅ `Authorization: Bearer <token>` - Có token
   - ❌ Không có `Authorization` header - **Vấn đề ở đây!**

### 3. So sánh với Postman

#### Postman Headers:
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

#### Frontend Headers (trong Network tab):
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

Nếu frontend **KHÔNG có** header này → **Vấn đề!**

## Giải pháp

### Giải pháp 1: Kiểm tra Auth Interceptor

File: `src/app/features/interceptor/auth.interceptor.ts`

Đảm bảo interceptor thêm token vào request:
```typescript
if (token) {
  const cloned = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });
  return next(cloned);
}
```

### Giải pháp 2: Kiểm tra Token trong localStorage

1. Mở Browser Console (F12)
2. Chạy:
```javascript
localStorage.getItem('token')
```

Nếu `null`:
- User chưa đăng nhập
- Token đã hết hạn
- Cần đăng nhập lại

### Giải pháp 3: Kiểm tra AuthService

Đảm bảo `AuthService` lưu token đúng cách:
```typescript
// Sau khi login thành công
localStorage.setItem('token', token);
```

### Giải pháp 4: Kiểm tra Request Headers

Trong Browser DevTools → Network tab:
1. Tìm request `POST /api/ai/chat`
2. Click vào request
3. Xem tab **Headers**
4. Kiểm tra **Request Headers**:
   - Phải có: `Authorization: Bearer <token>`

## Debug Steps

### Bước 1: Kiểm tra Backend Logs
1. Chạy backend
2. Gửi message từ frontend
3. Xem console output của backend
4. Kiểm tra:
   - `userId` có giá trị không?
   - Có warning "userId is null" không?

### Bước 2: Kiểm tra Frontend Token
1. Mở Browser DevTools (F12)
2. Console tab → Chạy: `localStorage.getItem('token')`
3. Nếu `null` → Đăng nhập lại

### Bước 3: Kiểm tra Network Request
1. Network tab → Gửi message
2. Tìm request `POST /api/ai/chat`
3. Xem Headers → Kiểm tra `Authorization` header

### Bước 4: So sánh với Postman
1. Test cùng một câu hỏi trong Postman
2. So sánh:
   - Request headers
   - Response
   - Backend logs

## Kết quả mong đợi

Sau khi sửa, backend logs sẽ hiển thị:
```
🔍 Chat request - useRAG: true, userId: 3
🔍 Auth header found, token length: 200, userId: 3
🔍 Similarity search - userId: 3, found 15 documents
✅ Chat response - usedRAG: true, sources: [Bệnh dại.pdf]
```

Và frontend sẽ nhận được kết quả **GIỐNG** với Postman.

## Lưu ý

- **Token phải hợp lệ** và **chưa hết hạn**
- **Authorization header** phải được gửi trong mọi request
- **userId** phải có giá trị để RAG chỉ tìm trong documents của user đó
- Nếu `userId = null`, RAG sẽ tìm trong **TẤT CẢ documents** → Kết quả khác nhau!


