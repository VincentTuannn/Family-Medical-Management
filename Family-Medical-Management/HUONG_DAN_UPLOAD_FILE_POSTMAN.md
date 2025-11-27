# 📄 Hướng Dẫn Upload File trong Postman

## ❌ Lỗi Thường Gặp

```
Required part 'file' is not present
```

Lỗi này xảy ra khi request không có phần 'file' trong multipart form-data.

---

## ✅ Cách Upload File Đúng trong Postman

### Bước 1: Tạo Request

1. **Method:** `POST`
2. **URL:** `http://localhost:8081/api/ai/documents/upload`
3. **Headers:**
   ```
   Authorization: Bearer <your_token>
   ```
   ⚠️ **KHÔNG** thêm `Content-Type: multipart/form-data` - Postman sẽ tự động thêm!

### Bước 2: Cấu Hình Body

1. Vào tab **Body**
2. Chọn **form-data** (KHÔNG phải raw, x-www-form-urlencoded, binary, hay GraphQL)
3. Thêm field mới:
   - **Key:** `file` (phải đúng tên này, không phải `files`, `document`, hay tên khác)
   - **Type:** Chọn **File** (dropdown bên phải key)
   - **Value:** Click **Select Files** và chọn file của bạn

### Bước 3: Gửi Request

Click **Send** và kiểm tra response.

---

## 📸 Hình Ảnh Minh Họa

### ✅ ĐÚNG - Cấu hình form-data:

```
Body Tab:
┌─────────────────────────────────────┐
│ ○ none  ○ form-data  ○ x-www...    │
│                                     │
│ Key          Value        Type      │
│ ┌─────┐  ┌──────────┐  ┌──────┐   │
│ │file │  │Select...  │  │File ▼│   │
│ └─────┘  └──────────┘  └──────┘   │
└─────────────────────────────────────┘
```

### ❌ SAI - Các cách cấu hình sai:

#### 1. Dùng raw JSON:
```
❌ Body -> raw -> JSON
{
  "file": "..."
}
```

#### 2. Key sai tên:
```
❌ Key: "files" (phải là "file")
❌ Key: "document" (phải là "file")
❌ Key: "upload" (phải là "file")
```

#### 3. Type sai:
```
❌ Key: "file", Type: "Text" (phải là "File")
```

#### 4. Thêm Content-Type header thủ công:
```
❌ Headers:
   Content-Type: multipart/form-data
   
✅ Postman tự động thêm header này!
```

---

## 🔍 Kiểm Tra Request

### Headers Tự Động (Postman tự thêm):
```
Authorization: Bearer <token>
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary...
```

### Body (form-data):
```
------WebKitFormBoundary...
Content-Disposition: form-data; name="file"; filename="document.pdf"
Content-Type: application/pdf

[File content]
------WebKitFormBoundary...
```

---

## 🧪 Test Cases

### Test Case 1: Upload PDF
- **File:** `DTD-ban-dich.pdf`
- **Key:** `file`
- **Type:** `File`
- **Expected:** 200 OK với DocumentDTO

### Test Case 2: Upload Word Document
- **File:** `document.docx`
- **Key:** `file`
- **Type:** `File`
- **Expected:** 200 OK với DocumentDTO

### Test Case 3: Upload Text File
- **File:** `notes.txt`
- **Key:** `file`
- **Type:** `File`
- **Expected:** 200 OK với DocumentDTO

### Test Case 4: File Quá Lớn (>50MB)
- **File:** `large_file.pdf` (60MB)
- **Expected:** 413 Payload Too Large với error message

### Test Case 5: Không Có File
- **Key:** `file` nhưng không chọn file
- **Expected:** 400 Bad Request với error "File không được tìm thấy"

---

## ⚠️ Lưu Ý Quan Trọng

1. **Key phải là "file"** - Không phải "files", "document", hay tên khác
2. **Type phải là "File"** - Không phải "Text"
3. **Body phải là form-data** - Không phải raw, x-www-form-urlencoded
4. **KHÔNG thêm Content-Type header** - Postman tự động thêm
5. **File size tối đa: 50MB** - Nếu lớn hơn sẽ bị từ chối

---

## 🔧 Troubleshooting

### Lỗi: "Required part 'file' is not present"

**Nguyên nhân:**
- Key không đúng tên (phải là "file")
- Type không đúng (phải là "File")
- Body không phải form-data

**Giải pháp:**
1. Kiểm tra Key = "file" (chính xác)
2. Kiểm tra Type = "File" (dropdown)
3. Kiểm tra Body = "form-data" (tab)

---

### Lỗi: "Maximum upload size exceeded"

**Nguyên nhân:**
- File lớn hơn 50MB

**Giải pháp:**
- Nén file hoặc chia nhỏ
- Hoặc tăng limit trong `application.properties`:
  ```properties
  spring.servlet.multipart.max-file-size=100MB
  spring.servlet.multipart.max-request-size=100MB
  ```

---

### Lỗi: "File không được tìm thấy"

**Nguyên nhân:**
- Chưa chọn file trong Postman
- Key không đúng

**Giải pháp:**
1. Click "Select Files" và chọn file
2. Đảm bảo Key = "file"

---

## 📋 Checklist Trước Khi Gửi Request

- [ ] Method: POST
- [ ] URL: `http://localhost:8081/api/ai/documents/upload`
- [ ] Header: `Authorization: Bearer <token>` (đã login)
- [ ] Body: form-data (không phải raw)
- [ ] Key: `file` (chính xác)
- [ ] Type: `File` (dropdown)
- [ ] Value: Đã chọn file
- [ ] File size < 50MB
- [ ] KHÔNG có Content-Type header thủ công

---

## ✅ Response Thành Công

```json
{
  "documentId": 1,
  "fileName": "DTD-ban-dich.pdf",
  "fileType": "application/pdf",
  "content": "Nội dung đầu tiên của tài liệu...",
  "chunkIndex": 0,
  "metadata": {
    "fileName": "DTD-ban-dich.pdf",
    "fileType": "application/pdf",
    "fileSize": 1024000
  },
  "createdAt": "2024-11-27T21:58:31",
  "userId": 3
}
```

---

## ❌ Response Lỗi

### File không tìm thấy:
```json
{
  "error": "File không được tìm thấy trong request",
  "message": "Vui lòng đảm bảo gửi file với key 'file' trong form-data",
  "hint": "Trong Postman, chọn Body -> form-data -> Key: 'file' (type: File)"
}
```

### File quá lớn:
```json
{
  "error": "File quá lớn. Kích thước tối đa là 50MB",
  "fileSize": "60000000",
  "maxSize": "52428800"
}
```

---

**Chúc bạn upload file thành công! 🎉**

