# 🔧 Hướng Dẫn Sửa Lỗi 403 - Step by Step

## 🎯 Vấn Đề
Frontend đã gửi token (interceptor hoạt động), nhưng backend vẫn trả về **403 Forbidden**.

---

## 📋 BƯỚC 1: Kiểm Tra Backend Logs (QUAN TRỌNG NHẤT)

**Mở terminal/console nơi bạn chạy Spring Boot backend.**

Sau khi frontend gửi request, bạn sẽ thấy một trong các log sau:

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
  Request: /api/patient/user/3, Method: GET
```

**2. User không có authorities:**
```
📋 Loading user: your_username, Role: USER, Active: true
✗ User your_username không có authorities
  Request: /api/patient/user/3, Method: GET
```

**3. Token không hợp lệ:**
```
✗ Request đến /api/patient/user/3 có token nhưng không hợp lệ, Method: GET
```

**4. Không có token:**
```
✗ Request đến /api/patient/user/3 không có Authorization header, Method: GET
```

**5. User không tìm thấy:**
```
✗ User không tìm thấy: User not found: your_username
  Request: /api/patient/user/3, Method: GET
```

---

## 📋 BƯỚC 2: Test Endpoint Debug

Mở Browser Console và chạy:

```javascript
// Test endpoint debug
fetch('http://localhost:8081/api/public/test-auth', {
  headers: {
    'Authorization': 'Bearer ' + localStorage.getItem('token')
  }
})
.then(r => r.json())
.then(data => {
  console.log('Auth Info:', data);
  if (data.authenticated) {
    console.log('✅ User:', data.username);
    console.log('✅ Authorities:', data.authorities);
  } else {
    console.log('❌ Chưa được authenticate');
  }
});
```

**Kết quả mong đợi:**
```json
{
  "authenticated": true,
  "username": "your_username",
  "authorities": ["ROLE_USER"]
}
```

**Nếu trả về `authenticated: false`** → Token không được validate đúng.

---

## 📋 BƯỚC 3: Kiểm Tra Database

### 3.1. Mở MySQL Workbench hoặc MySQL Command Line

### 3.2. Chạy SQL để kiểm tra user:

```sql
USE medical_family_db;

-- Kiểm tra user của bạn
SELECT 
    user_id, 
    username, 
    email, 
    role, 
    is_active,
    CASE 
        WHEN role NOT IN ('USER', 'DOCTOR', 'ADMIN') THEN '❌ Role sai'
        WHEN is_active = FALSE THEN '❌ Không active'
        ELSE '✅ OK'
    END AS status
FROM `user`
WHERE username = 'your_username';  -- Thay bằng username của bạn
```

### 3.3. Kiểm tra kết quả:

**✅ Kết quả đúng:**
- `role` = **'USER'**, **'DOCTOR'**, hoặc **'ADMIN'** (chữ HOA)
- `is_active` = **1** hoặc **true**

**❌ Nếu có vấn đề:**

**Vấn đề 1: Role sai format**
```sql
-- Sửa role thành uppercase
UPDATE `user` 
SET role = UPPER(role)
WHERE username = 'your_username';
```

**Vấn đề 2: User không active**
```sql
-- Set user active
UPDATE `user` 
SET is_active = TRUE 
WHERE username = 'your_username';
```

**Vấn đề 3: Sửa cả 2**
```sql
-- Sửa cả role và is_active
UPDATE `user` 
SET role = 'USER', is_active = TRUE 
WHERE username = 'your_username';
```

---

## 📋 BƯỚC 4: Kiểm Tra Token

### 4.1. Decode JWT Token

1. Mở Browser DevTools (F12) → **Application** tab → **Local Storage**
2. Copy giá trị của key `token`
3. Vào https://jwt.io
4. Paste token vào phần "Encoded"
5. Kiểm tra **payload** có:
   - `sub`: username của bạn
   - `userId`: số ID
   - `role`: "ROLE_USER", "ROLE_DOCTOR", hoặc "ROLE_ADMIN"

### 4.2. Kiểm Tra Token Trong Console

Mở Browser Console và chạy:

```javascript
const token = localStorage.getItem('token');
if (token) {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    console.log('Token payload:', payload);
    console.log('Username:', payload.sub);
    console.log('User ID:', payload.userId);
    console.log('Role:', payload.role);
    
    // Kiểm tra token có hợp lệ không
    const now = Math.floor(Date.now() / 1000);
    if (payload.exp && payload.exp < now) {
      console.error('❌ Token đã hết hạn!');
    } else {
      console.log('✅ Token còn hiệu lực');
    }
  } catch (e) {
    console.error('❌ Lỗi decode token:', e);
  }
} else {
  console.error('❌ Không có token trong localStorage');
}
```

---

## 📋 BƯỚC 5: Sửa Database (Nếu Cần)

### Cách 1: Sửa User Cụ Thể

```sql
USE medical_family_db;

-- Thay 'your_username' bằng username của bạn
UPDATE `user` 
SET 
    role = 'USER',           -- Hoặc 'DOCTOR', 'ADMIN'
    is_active = TRUE 
WHERE username = 'your_username';

-- Kiểm tra lại
SELECT user_id, username, role, is_active 
FROM `user` 
WHERE username = 'your_username';
```

### Cách 2: Sửa Tất Cả Users

```sql
USE medical_family_db;

-- Sửa tất cả roles thành uppercase
UPDATE `user` 
SET role = UPPER(role)
WHERE role IS NOT NULL;

-- Set tất cả users active
UPDATE `user` 
SET is_active = TRUE;
```

### Cách 3: Chạy Script SQL

Chạy file: `src/main/SQL/FIX_USER_403.sql`

---

## 📋 BƯỚC 6: Restart và Test Lại

1. **Restart backend** (nếu đã sửa database)
2. **Clear browser cache** và **login lại**
3. **Test lại** các API endpoints

---

## 🔍 Checklist Debug

- [ ] Đã xem backend logs (console/terminal)
- [ ] Đã test endpoint `/api/public/test-auth` với token
- [ ] Đã kiểm tra database (role và is_active)
- [ ] Đã decode JWT token và kiểm tra payload
- [ ] Đã sửa database nếu cần
- [ ] Đã restart backend
- [ ] Đã clear browser cache và login lại
- [ ] Đã test lại các API endpoints

---

## 🚨 Các Trường Hợp Đặc Biệt

### Trường Hợp 1: Backend Logs Không Hiển Thị Gì

**Nguyên nhân:** Filter không chạy hoặc token không được gửi đúng.

**Kiểm tra:**
1. Xem Network tab trong Browser DevTools
2. Kiểm tra Request Headers có `Authorization: Bearer <token>` không
3. Kiểm tra backend có đang chạy không

### Trường Hợp 2: Backend Logs Hiển Thị "Authentication set" Nhưng Vẫn 403

**Nguyên nhân:** Authorities không match với SecurityConfig.

**Kiểm tra:**
1. Xem authorities trong backend logs
2. So sánh với SecurityConfig requirements
3. Đảm bảo authorities có format `ROLE_USER`, `ROLE_DOCTOR`, hoặc `ROLE_ADMIN`

### Trường Hợp 3: Token Hết Hạn

**Nguyên nhân:** Token đã hết hạn.

**Giải pháp:**
1. Logout và login lại để lấy token mới
2. Hoặc tăng thời gian hết hạn trong `application.properties`

---

## 💡 Lưu Ý Quan Trọng

1. **Spring Security tự động thêm prefix "ROLE_"** khi:
   - Dùng `.roles()` trong UserDetails → tạo authorities với prefix
   - Dùng `.hasRole()` hoặc `.hasAnyRole()` → tự động thêm prefix khi check

2. **Vậy nếu database có `role = "USER"`:**
   - UserDetails sẽ có authority = `"ROLE_USER"` ✅
   - SecurityConfig check `hasRole("USER")` sẽ match với `"ROLE_USER"` ✅

3. **Điều này ĐÚNG và không cần sửa gì!**

---

## 📞 Bước Tiếp Theo

Sau khi thực hiện các bước trên, hãy cho tôi biết:

1. **Backend logs hiển thị gì?** (quan trọng nhất)
2. **Kết quả test endpoint `/api/public/test-auth`?**
3. **User trong database có role và is_active như thế nào?**
4. **Token payload có gì?**

Tôi sẽ giúp bạn sửa cụ thể dựa trên thông tin đó!

