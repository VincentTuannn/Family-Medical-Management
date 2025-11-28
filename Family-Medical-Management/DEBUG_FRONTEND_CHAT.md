# Hướng dẫn Debug Frontend Chatbox

## Vấn đề
Backend chạy đúng (Postman test OK) nhưng frontend không hoạt động như mong đợi.

## Các bước Debug

### 1. Mở Browser DevTools
- Nhấn `F12` hoặc `Ctrl+Shift+I`
- Chọn tab **Console** để xem logs
- Chọn tab **Network** để xem HTTP requests

### 2. Kiểm tra Console Logs

#### ✅ Logs bình thường:
```
📤 Sending chat request: {url: "...", request: {...}}
✅ Chat response received: {response: "...", sources: [...], usedRAG: true}
✅ Documents loaded: [...]
✅ Interceptor: Đã thêm token vào request: ...
```

#### ❌ Logs lỗi:
```
❌ Chat error: ...
⚠️ Interceptor: Không có token cho request: ...
```

### 3. Kiểm tra Network Tab

#### Request Headers:
- ✅ `Authorization: Bearer <token>` - Có token
- ✅ `Content-Type: application/json` - Đúng format
- ✅ URL: `http://localhost:8081/api/ai/chat` - Đúng endpoint

#### Response:
- ✅ Status: `200 OK`
- ✅ Body: `{response: "...", sources: [...], usedRAG: true}`

#### ❌ Lỗi thường gặp:
- **401 Unauthorized**: Token hết hạn hoặc không hợp lệ
- **403 Forbidden**: Không có quyền
- **0 (Failed)**: CORS error hoặc server không chạy
- **404 Not Found**: URL sai

### 4. Kiểm tra CORS

Nếu thấy lỗi CORS trong console:
```
Access to XMLHttpRequest at 'http://localhost:8081/api/ai/chat' 
from origin 'http://localhost:4200' has been blocked by CORS policy
```

**Giải pháp:**
- Kiểm tra backend có `@CrossOrigin(origins = "*")` không
- Hoặc cấu hình CORS đúng trong backend

### 5. Kiểm tra Authentication

#### Kiểm tra token:
```javascript
// Trong Browser Console
localStorage.getItem('token')
```

Nếu `null`:
- User chưa đăng nhập
- Token đã hết hạn
- Cần đăng nhập lại

#### Kiểm tra AuthService:
```javascript
// Trong Browser Console
// Kiểm tra xem AuthService có token không
```

### 6. Kiểm tra Environment

File: `src/environments/environment.ts`
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8081/api'  // ✅ Phải đúng với backend
};
```

### 7. Kiểm tra Response Format

Backend trả về:
```json
{
  "response": "string",
  "conversationId": "string | null",
  "sources": ["file1.pdf", "file2.pdf"],
  "usedRAG": true
}
```

Frontend model phải khớp:
```typescript
export interface ChatResponse {
  response: string;
  conversationId?: string;
  sources?: string[];
  usedRAG?: boolean;
}
```

### 8. Các lỗi thường gặp và cách sửa

#### Lỗi 1: "Cannot read property 'response' of undefined"
**Nguyên nhân:** Response không đúng format
**Giải pháp:** Kiểm tra backend trả về đúng format

#### Lỗi 2: "Network Error" hoặc "Failed to fetch"
**Nguyên nhân:** 
- Backend không chạy
- CORS error
- URL sai
**Giải pháp:** 
- Kiểm tra backend đang chạy ở `http://localhost:8081`
- Kiểm tra CORS config
- Kiểm tra `environment.apiUrl`

#### Lỗi 3: "401 Unauthorized"
**Nguyên nhân:** Token hết hạn hoặc không hợp lệ
**Giải pháp:** Đăng nhập lại để lấy token mới

#### Lỗi 4: Sources không hiển thị
**Nguyên nhân:** 
- `response.sources` là `undefined` hoặc `null`
- Template không render đúng
**Giải pháp:** 
- Kiểm tra backend trả về `sources` array
- Kiểm tra template: `*ngIf="message.usedRAG && message.sources && message.sources.length > 0"`

### 9. Test thủ công trong Console

```javascript
// Test API call trực tiếp
fetch('http://localhost:8081/api/ai/chat', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + localStorage.getItem('token')
  },
  body: JSON.stringify({
    message: 'Test message',
    useRAG: true
  })
})
.then(res => res.json())
.then(data => console.log('Response:', data))
.catch(err => console.error('Error:', err));
```

### 10. So sánh với Postman

Nếu Postman chạy được nhưng frontend không:
1. So sánh **Headers** giữa Postman và Network tab
2. So sánh **Request Body** format
3. So sánh **Response** format
4. Kiểm tra **Authorization** header có giống nhau không

## Checklist Debug

- [ ] Backend đang chạy ở `http://localhost:8081`
- [ ] Frontend đang chạy ở `http://localhost:4200`
- [ ] Token có trong `localStorage`
- [ ] Request có `Authorization` header
- [ ] Response status là `200 OK`
- [ ] Response body có đúng format
- [ ] Console không có errors
- [ ] Network tab không có failed requests
- [ ] CORS không bị block

## Liên hệ

Nếu vẫn không giải quyết được, cung cấp:
1. Console logs (copy/paste)
2. Network tab screenshot
3. Error message đầy đủ
4. Request/Response details từ Network tab


