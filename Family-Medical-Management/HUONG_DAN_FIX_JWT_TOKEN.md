# 🔧 Hướng Dẫn Sửa Lỗi JWT Signature

## ❌ Lỗi
```
JWT signature does not match locally computed signature
```

## 🔍 Nguyên Nhân

1. **Token được tạo với secret key khác** - Token cũ từ lần chạy trước
2. **Token đã hết hạn** - Token có thời hạn 24 giờ
3. **Format token sai trong Postman** - Có dấu `{{}}` hoặc thiếu "Bearer "

## ✅ Giải Pháp

### Bước 1: Đăng Nhập Lại để Lấy Token Mới

**Request:**
```
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "username": "patipu",
  "password": "your_password"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Login thành công"
}
```

**Copy token mới này!**

---

### Bước 2: Cấu Hình Đúng trong Postman

#### ❌ SAI - Có dấu `{{}}`:
```
Authorization: Bearer {{eyJhbGciOiJIUzUxMiJ9...}}
```

#### ✅ ĐÚNG - Không có dấu `{{}}`:
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwYXRpcHUiLCJ1c2VySWQiOjMsInJvbGUiOiJST0xFX1VTRVIiLCJpYXQiOjE3NjQyNTM4OTMsImV4cCI6MTc2NDM0MDI5M30.Af25zKI6iIuoiRjKeY_m6EWK12SeZjovhUjqXLGMVRxjYnEnvDcudZfKN2PH0x5_HNcO6ED7dc0V82iyISKOEw
```

#### Cách Thêm Header trong Postman:

1. Mở request trong Postman
2. Vào tab **Headers**
3. Thêm header mới:
   - **Key:** `Authorization`
   - **Value:** `Bearer <paste_token_here>` (không có dấu `{{}}`)
4. **Lưu ý:** Phải có khoảng trắng giữa "Bearer" và token

---

### Bước 3: Sử Dụng Environment Variable (Khuyến Nghị)

#### Tạo Environment Variable:

1. Click **Environments** ở góc trái Postman
2. Tạo Environment mới: "FMM Local"
3. Thêm variable:
   - **Variable:** `token`
   - **Initial Value:** (để trống)
   - **Current Value:** (để trống)

#### Tự Động Lưu Token Sau Khi Login:

1. Mở request **Login**
2. Vào tab **Tests**
3. Thêm script:
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("token", jsonData.token);
    console.log("Token đã được lưu:", jsonData.token);
}
```

#### Sử Dụng Token trong Request:

1. Chọn Environment "FMM Local"
2. Trong Header:
   - **Key:** `Authorization`
   - **Value:** `Bearer {{token}}`

**Lưu ý:** Lần này dùng `{{token}}` là ĐÚNG vì đây là environment variable!

---

## 🧪 Test Token

### Kiểm Tra Token Có Hợp Lệ:

**Request:**
```
GET http://localhost:8081/api/public/test-auth
Authorization: Bearer <your_token>
```

**Response Thành Công:**
```json
{
  "message": "Token hợp lệ",
  "username": "patipu",
  "userId": 3
}
```

---

## 🔄 Quy Trình Test Đúng

1. **Login** → Lấy token mới
2. **Copy token** từ response
3. **Paste vào Header** của request tiếp theo:
   ```
   Authorization: Bearer <paste_token_here>
   ```
4. **Gửi request** → Nếu lỗi, login lại và lấy token mới

---

## ⚠️ Lưu Ý Quan Trọng

1. **Token có thời hạn 24 giờ** - Nếu hết hạn, phải login lại
2. **Mỗi lần restart backend** - Secret key không đổi, nhưng nên login lại để chắc chắn
3. **Không dùng token cũ** - Luôn lấy token mới sau khi login
4. **Format phải đúng** - `Bearer <token>` (có khoảng trắng)

---

## 🐛 Debug

Nếu vẫn lỗi, kiểm tra:

1. **Backend logs** - Xem có log gì về token không
2. **Token có đầy đủ 3 phần không:**
   - Header.Claims.Signature
   - Ví dụ: `eyJ...` `.` `eyJ...` `.` `Af25...`
3. **Secret key trong application.properties:**
   ```
   app.jwt.secret=TXlTdXBlclNlY3JldEtleTEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MA==
   ```

---

## ✅ Checklist

- [ ] Đã login và lấy token mới
- [ ] Token được paste đúng format: `Bearer <token>`
- [ ] Không có dấu `{{}}` xung quanh token (trừ khi dùng environment variable)
- [ ] Có khoảng trắng giữa "Bearer" và token
- [ ] Token chưa hết hạn (24 giờ)
- [ ] Backend đang chạy với secret key đúng

---

**Sau khi làm theo các bước trên, token sẽ hoạt động! 🎉**

