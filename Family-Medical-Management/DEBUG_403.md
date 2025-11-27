# 🔍 Hướng Dẫn Debug Lỗi 403

## 📊 Tình Trạng Hiện Tại

Từ console log frontend:
- ✅ Interceptor đã thêm token vào request (có dấu checkmark xanh)
- ❌ Backend trả về **403 Forbidden** cho các endpoint:
  - `/api/patient/user/3`
  - `/api/patient/my`
  - `/api/appointment/my`
  - `/api/transfer/my`
  - `/api/doctor`

## 🔍 Bước 1: Kiểm Tra Backend Logs

**Quan trọng nhất:** Xem backend console/terminal nơi bạn chạy Spring Boot.

Bạn sẽ thấy một trong các log sau:

### ✅ Nếu thành công:
```
📋 Loading user: your_username, Role: USER, Active: true
✓ Authentication set cho user: your_username, Authorities: [ROLE_USER], Request: /api/patient/user/3, Method: GET
```

### ❌ Nếu có vấn đề:

**1. User không active:**
```
📋 Loading user: your_username, Role: USER, Active: false
✗ User your_username không active (bị khóa)
```

**2. User không có authorities:**
```
📋 Loading user: your_username, Role: USER, Active: true
✗ User your_username không có authorities
```

**3. Token không hợp lệ:**
```
✗ Request đến /api/patient/user/3 có token nhưng không hợp lệ
```

**4. Không có token:**
```
✗ Request đến /api/patient/user/3 không có Authorization header
```

**5. User không tìm thấy:**
```
✗ User không tìm thấy: User not found: your_username
  Request: /api/patient/user/3
```

---

## 🛠️ Bước 2: Kiểm Tra Database

Chạy SQL sau để kiểm tra user của bạn:

```sql
SELECT user_id, username, email, role, is_active 
FROM user 
WHERE username = 'your_username';
```

### ✅ Kết quả mong đợi:
- `role` = **'USER'**, **'DOCTOR'**, hoặc **'ADMIN'** (chữ HOA, không có prefix)
- `is_active` = **true** (hoặc 1)

### ❌ Nếu có vấn đề:

**1. Role là lowercase hoặc null:**
```sql
-- Sửa role thành uppercase
UPDATE user 
SET role = 'USER' 
WHERE username = 'your_username' AND (role IS NULL OR LOWER(role) = 'user');
```

**2. User không active:**
```sql
-- Set user active
UPDATE user 
SET is_active = true 
WHERE username = 'your_username';
```

**3. Kiểm tra tất cả users:**
```sql
SELECT user_id, username, role, is_active FROM user;
```

---

## 🔍 Bước 3: Kiểm Tra Token

### Cách 1: Decode JWT Token

1. Mở Browser DevTools (F12) → Application tab → Local Storage
2. Copy giá trị của key `token`
3. Vào https://jwt.io
4. Paste token vào phần "Encoded"
5. Kiểm tra payload có:
   - `sub`: username của bạn
   - `userId`: số ID
   - `role`: "ROLE_USER", "ROLE_DOCTOR", hoặc "ROLE_ADMIN"

### Cách 2: Kiểm Tra Token Trong Console

Mở Browser Console và chạy:
```javascript
const token = localStorage.getItem('token');
console.log('Token:', token);
if (token) {
  const payload = JSON.parse(atob(token.split('.')[1]));
  console.log('Token payload:', payload);
  console.log('Username:', payload.sub);
  console.log('User ID:', payload.userId);
  console.log('Role:', payload.role);
}
```

---

## 🛠️ Bước 4: Test Với Postman/Thunder Client

1. **Lấy token:**
   - POST `http://localhost:8081/api/auth/login`
   - Body: `{ "username": "your_username", "password": "your_password" }`
   - Copy token từ response

2. **Test API với token:**
   - GET `http://localhost:8081/api/patient/user/3`
   - Headers: `Authorization: Bearer <your_token>`
   - Xem response và status code

---

## 🔧 Các Cách Sửa Thường Gặp

### Sửa 1: Đảm Bảo User Có Role Đúng

```sql
-- Kiểm tra và sửa tất cả users
UPDATE user 
SET role = UPPER(role) 
WHERE role IS NOT NULL;

-- Hoặc set cụ thể
UPDATE user 
SET role = 'USER' 
WHERE username = 'your_username';
```

### Sửa 2: Đảm Bảo User Active

```sql
UPDATE user 
SET is_active = true 
WHERE username = 'your_username';
```

### Sửa 3: Tạo User Test Mới

```sql
-- Tạo user test với role USER
INSERT INTO user (username, email, password, role, is_active, created_at) 
VALUES (
  'testuser', 
  'test@example.com', 
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- password: "password"
  'USER', 
  true,
  NOW()
);
```

**Lưu ý:** Password hash trên là cho mật khẩu "password". Để tạo password hash mới, bạn cần dùng BCrypt.

---

## 📋 Checklist Debug

- [ ] Backend đang chạy trên port 8081
- [ ] Frontend đang chạy trên port 4200
- [ ] Đã xem backend logs (console/terminal)
- [ ] Token được lưu trong localStorage sau khi login
- [ ] User trong database có role đúng (USER/DOCTOR/ADMIN - chữ HOA)
- [ ] User trong database có is_active = true
- [ ] Backend logs hiển thị "✓ Authentication set cho user"
- [ ] Backend logs hiển thị authorities đúng (ROLE_USER, ROLE_DOCTOR, hoặc ROLE_ADMIN)
- [ ] Đã test với Postman/Thunder Client

---

## 🚨 Nếu Vẫn Lỗi 403 Sau Khi Kiểm Tra

1. **Kiểm tra backend logs** - xem có log gì không
2. **Kiểm tra SecurityConfig** - đảm bảo endpoint được config đúng
3. **Kiểm tra CORS** - đảm bảo CORS config đúng
4. **Restart backend** - sau khi sửa code
5. **Clear browser cache** - và login lại

---

## 💡 Lưu Ý Quan Trọng

- Spring Security tự động thêm prefix "ROLE_" khi:
  - Dùng `.roles()` trong UserDetails → tạo authorities với prefix
  - Dùng `.hasRole()` hoặc `.hasAnyRole()` → tự động thêm prefix khi check

- Vậy nếu database có `role = "USER"`:
  - UserDetails sẽ có authority = `"ROLE_USER"`
  - SecurityConfig check `hasRole("USER")` sẽ match với `"ROLE_USER"` ✅

- Điều này **ĐÚNG** và không cần sửa gì!

---

## 📞 Bước Tiếp Theo

Sau khi kiểm tra backend logs, hãy cho tôi biết:
1. Backend logs hiển thị gì?
2. User trong database có role và is_active như thế nào?
3. Token payload có gì?

Tôi sẽ giúp bạn sửa cụ thể dựa trên thông tin đó!

