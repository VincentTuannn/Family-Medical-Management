# 🧪 Hướng Dẫn Test AI bằng Postman

## 📋 Mục Lục
1. [Chuẩn Bị](#chuẩn-bị)
2. [Bước 1: Đăng Nhập để Lấy JWT Token](#bước-1-đăng-nhập-để-lấy-jwt-token)
3. [Bước 2: Test Chat AI (Không RAG)](#bước-2-test-chat-ai-không-rag)
4. [Bước 3: Test Chat AI với RAG](#bước-3-test-chat-ai-với-rag)
5. [Bước 4: Upload Tài Liệu](#bước-4-upload-tài-liệu)
6. [Bước 5: Lấy Danh Sách Tài Liệu](#bước-5-lấy-danh-sách-tài-liệu)
7. [Bước 6: Xóa Tài Liệu](#bước-6-xóa-tài-liệu)

---

## 🔧 Chuẩn Bị

### Yêu Cầu:
- ✅ Backend đang chạy tại `http://localhost:8081`
- ✅ Ollama đang chạy tại `http://localhost:11434` (nếu dùng Ollama)
- ✅ Database MySQL đã được cấu hình
- ✅ Đã có tài khoản user trong database

### Base URL:
```
http://localhost:8081
```

---

## 🔐 Bước 1: Đăng Nhập để Lấy JWT Token

### Request:
- **Method:** `POST`
- **URL:** `http://localhost:8081/api/auth/login`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "username": "your_username",
    "password": "your_password"
  }
  ```

### Response Thành Công:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login thành công"
}
```

### 📝 Lưu ý:
- **Copy token** từ response để dùng cho các request sau
- Token có thời hạn (mặc định 24 giờ)

---

## 💬 Bước 2: Test Chat AI (Không RAG)

### Request:
- **Method:** `POST`
- **URL:** `http://localhost:8081/api/ai/chat`
- **Headers:**
  ```
  Content-Type: application/json
  Authorization: Bearer YOUR_JWT_TOKEN_HERE
  ```
- **Body (raw JSON):**
  ```json
  {
    "message": "Xin chào, bạn là ai?",
    "useRAG": false
  }
  ```

### Response Thành Công:
```json
{
  "response": "Xin chào! Tôi là trợ lý AI...",
  "conversationId": null,
  "sources": [],
  "usedRAG": false
}
```

### 📝 Giải Thích:
- `useRAG: false` → Chat trực tiếp với AI, không tìm kiếm trong tài liệu
- Phù hợp cho câu hỏi chung, không cần thông tin từ tài liệu

---

## 📚 Bước 3: Test Chat AI với RAG

### Request:
- **Method:** `POST`
- **URL:** `http://localhost:8081/api/ai/chat`
- **Headers:**
  ```
  Content-Type: application/json
  Authorization: Bearer YOUR_JWT_TOKEN_HERE
  ```
- **Body (raw JSON):**
  ```json
  {
    "message": "Tóm tắt thông tin về bệnh tiểu đường trong tài liệu của tôi",
    "useRAG": true
  }
  ```

### Response Thành Công:
```json
{
  "response": "Dựa trên tài liệu của bạn, bệnh tiểu đường...",
  "conversationId": null,
  "sources": [
    "medical_report_2024.pdf",
    "diabetes_guide.docx"
  ],
  "usedRAG": true
}
```

### 📝 Giải Thích:
- `useRAG: true` → AI sẽ tìm kiếm trong các tài liệu đã upload
- Trả về danh sách `sources` (tên file) được sử dụng
- Phù hợp cho câu hỏi về nội dung trong tài liệu

---

## 📄 Bước 4: Upload Tài Liệu

### Request:
- **Method:** `POST`
- **URL:** `http://localhost:8081/api/ai/documents/upload`
- **Headers:**
  ```
  Authorization: Bearer YOUR_JWT_TOKEN_HERE
  ```
- **Body (form-data):**
  - Key: `file`
  - Type: `File`
  - Value: Chọn file (PDF, Word, hoặc text)

### Response Thành Công:
```json
{
  "documentId": 1,
  "fileName": "medical_report.pdf",
  "fileType": "application/pdf",
  "content": "Nội dung đầu tiên của tài liệu...",
  "chunkIndex": 0,
  "metadata": {
    "fileName": "medical_report.pdf",
    "fileType": "application/pdf",
    "fileSize": 102400
  },
  "createdAt": "2024-01-15T10:30:00",
  "userId": 1
}
```

### 📝 Lưu ý:
- Hỗ trợ file: PDF, Word (.doc, .docx), Text (.txt)
- File sẽ được chia nhỏ (chunking) và tạo embedding
- Cần upload tài liệu trước khi dùng RAG

---

## 📋 Bước 5: Lấy Danh Sách Tài Liệu

### Request:
- **Method:** `GET`
- **URL:** `http://localhost:8081/api/ai/documents`
- **Headers:**
  ```
  Authorization: Bearer YOUR_JWT_TOKEN_HERE
  ```

### Response Thành Công:
```json
[
  {
    "documentId": 1,
    "fileName": "medical_report.pdf",
    "fileType": "application/pdf",
    "content": "Nội dung tài liệu...",
    "chunkIndex": 0,
    "metadata": {...},
    "createdAt": "2024-01-15T10:30:00",
    "userId": 1
  },
  {
    "documentId": 2,
    "fileName": "diabetes_guide.docx",
    "fileType": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    "content": "Nội dung tài liệu...",
    "chunkIndex": 0,
    "metadata": {...},
    "createdAt": "2024-01-15T11:00:00",
    "userId": 1
  }
]
```

### 📝 Giải Thích:
- Trả về tất cả tài liệu của user hiện tại
- Mỗi tài liệu có thể có nhiều chunks (chunkIndex khác nhau)

---

## 🗑️ Bước 6: Xóa Tài Liệu

### Request:
- **Method:** `DELETE`
- **URL:** `http://localhost:8081/api/ai/documents/{id}`
- **Example:** `http://localhost:8081/api/ai/documents/1`
- **Headers:**
  ```
  Authorization: Bearer YOUR_JWT_TOKEN_HERE
  ```

### Response Thành Công:
- **Status Code:** `204 No Content`
- **Body:** (rỗng)

### 📝 Lưu ý:
- Chỉ có thể xóa tài liệu của chính mình
- Xóa tài liệu sẽ xóa tất cả chunks liên quan

---

## 🎯 Collection Postman Mẫu

### Import Collection vào Postman:

1. Tạo Collection mới tên "Family Medical Management - AI"
2. Tạo các request như sau:

#### 1. Login
```
POST http://localhost:8081/api/auth/login
Body: {
  "username": "testuser",
  "password": "password123"
}
```

#### 2. Chat AI (No RAG)
```
POST http://localhost:8081/api/ai/chat
Headers: Authorization: Bearer {{token}}
Body: {
  "message": "Xin chào",
  "useRAG": false
}
```

#### 3. Chat AI (With RAG)
```
POST http://localhost:8081/api/ai/chat
Headers: Authorization: Bearer {{token}}
Body: {
  "message": "Tóm tắt tài liệu của tôi",
  "useRAG": true
}
```

#### 4. Upload Document
```
POST http://localhost:8081/api/ai/documents/upload
Headers: Authorization: Bearer {{token}}
Body: form-data
  file: [Select File]
```

#### 5. Get Documents
```
GET http://localhost:8081/api/ai/documents
Headers: Authorization: Bearer {{token}}
```

#### 6. Delete Document
```
DELETE http://localhost:8081/api/ai/documents/1
Headers: Authorization: Bearer {{token}}
```

### 🔄 Sử dụng Variables trong Postman:

1. Tạo Environment variable `token`
2. Sau khi login, dùng script sau để tự động lưu token:
   ```javascript
   // Trong Tests tab của Login request
   if (pm.response.code === 200) {
       var jsonData = pm.response.json();
       pm.environment.set("token", jsonData.token);
   }
   ```

---

## ⚠️ Xử Lý Lỗi Thường Gặp

### 1. Lỗi 401 Unauthorized
- **Nguyên nhân:** Token hết hạn hoặc không hợp lệ
- **Giải pháp:** Đăng nhập lại để lấy token mới

### 2. Lỗi 403 Forbidden
- **Nguyên nhân:** User không có quyền truy cập
- **Giải pháp:** Kiểm tra role của user (cần USER, DOCTOR, hoặc ADMIN)

### 3. Lỗi 500 Internal Server Error
- **Nguyên nhân:** 
  - Ollama không chạy (nếu dùng Ollama)
  - Database connection error
  - AI service error
- **Giải pháp:** 
  - Kiểm tra Ollama: `curl http://localhost:11434/api/tags`
  - Kiểm tra database connection
  - Xem backend logs

### 4. RAG không tìm thấy tài liệu
- **Nguyên nhân:** Chưa upload tài liệu hoặc tài liệu không liên quan
- **Giải pháp:** Upload tài liệu trước, sau đó mới dùng RAG

---

## 📊 Test Cases Mẫu

### Test Case 1: Chat đơn giản
```json
{
  "message": "Bạn có thể giúp gì cho tôi?",
  "useRAG": false
}
```

### Test Case 2: Chat với RAG (có tài liệu)
```json
{
  "message": "Tóm tắt các triệu chứng trong báo cáo y tế của tôi",
  "useRAG": true
}
```

### Test Case 3: Chat với RAG (không có tài liệu)
```json
{
  "message": "Thông tin về bệnh tiểu đường",
  "useRAG": true
}
```
→ Sẽ trả về: "Không tìm thấy thông tin liên quan trong tài liệu" và trả lời dựa trên kiến thức chung

---

## 🚀 Tips & Best Practices

1. **Luôn kiểm tra token trước khi test**
2. **Upload tài liệu trước khi test RAG**
3. **Sử dụng Environment variables trong Postman để quản lý token**
4. **Kiểm tra Ollama đang chạy trước khi test AI features**
5. **Xem backend logs để debug khi có lỗi**

---

## 📞 Hỗ Trợ

Nếu gặp vấn đề, kiểm tra:
- Backend logs trong console
- Database connection
- Ollama service status
- JWT token validity

---

**Chúc bạn test thành công! 🎉**

