# 🔧 Hướng Dẫn Sửa Lỗi 403 Forbidden

## 🔍 Phân Tích Vấn Đề

Từ console log, bạn thấy:
- ✅ Interceptor đã thêm token vào request (có dấu checkmark xanh)
- ❌ Server vẫn trả về **403 Forbidden**

**Nguyên nhân có thể:**
1. User không có role đúng trong database
2. User không active (isActive = false)
3. Authorities không match với SecurityConfig requirements
4. Exception trong JwtAuthenticationFilter không được handle đúng

---

## ✅ Đã Sửa

### File 1: `JwtAuthenticationFilter.java`
- ✅ Thêm kiểm tra user active
- ✅ Thêm kiểm tra authorities không rỗng
- ✅ Cải thiện logging chi tiết
- ✅ Xử lý exception tốt hơn

### File 2: `CustomUserDetailsService.java`
- ✅ Thêm logging để debug
- ✅ Đảm bảo user disabled nếu không active
- ✅ Đảm bảo authorities được tạo đúng với prefix ROLE_

---

## 🔍 Các Bước Kiểm Tra

### Bước 1: Kiểm Tra Backend Logs

Sau khi sửa, restart backend và xem logs. Bạn sẽ thấy:

**Nếu thành công:**
```
📋 Loading user: your_username, Role: USER, Active: true
✓ Authentication set cho user: your_username, Authorities: [ROLE_USER], Request: /api/patient, Method: GET
```

**Nếu có vấn đề:**
```
✗ User your_username không active (bị khóa)
```
hoặc
```
✗ User your_username không có authorities
```

### Bước 2: Kiểm Tra Database

Kiểm tra user trong database có:
- ✅ `role` = 'USER', 'DOCTOR', hoặc 'ADMIN' (chữ HOA)
- ✅ `is_active` = true (hoặc 1)

**SQL để kiểm tra:**
```sql
SELECT user_id, username, role, is_active 
FROM user 
WHERE username = 'your_username';
```

**Nếu role là lowercase hoặc null:**
```sql
-- Sửa role thành uppercase
UPDATE user 
SET role = 'USER' 
WHERE username = 'your_username' AND (role IS NULL OR role != 'USER');
```

### Bước 3: Kiểm Tra Token

Decode JWT token để xem có đúng claims không:

1. Copy token từ localStorage
2. Vào https://jwt.io
3. Paste token và kiểm tra payload có:
   - `sub`: username
   - `userId`: số
   - `role`: "ROLE_USER" hoặc "ROLE_DOCTOR" hoặc "ROLE_ADMIN"

---

## 🛠️ Các Cách Sửa Khác (Nếu Vẫn Lỗi)

### Cách 1: Kiểm Tra Role Trong Database

**Vấn đề:** Role trong database có thể là lowercase hoặc format khác.

**Sửa:**
```sql
-- Kiểm tra tất cả users
SELECT user_id, username, role, is_active FROM user;

-- Sửa role thành uppercase nếu cần
UPDATE user SET role = UPPER(role) WHERE role IS NOT NULL;
```

### Cách 2: Đảm Bảo User Active

**Vấn đề:** User có thể bị set is_active = false.

**Sửa:**
```sql
-- Kiểm tra user có active không
SELECT username, is_active FROM user WHERE username = 'your_username';

-- Set active nếu cần
UPDATE user SET is_active = true WHERE username = 'your_username';
```

### Cách 3: Kiểm Tra SecurityConfig

**Vấn đề:** SecurityConfig có thể yêu cầu role khác.

**Kiểm tra file:** `SecurityConfig.java`

Đảm bảo:
```java
.requestMatchers("/api/patient/**").hasAnyRole("USER", "DOCTOR", "ADMIN")
```

Spring Security tự động thêm prefix "ROLE_" khi check, vậy nó sẽ check:
- `ROLE_USER`
- `ROLE_DOCTOR`
- `ROLE_ADMIN`

### Cách 4: Test Với User Khác

Tạo user mới với role đúng:

```sql
-- Tạo user test với role USER
INSERT INTO user (username, email, password, role, is_active) 
VALUES ('testuser', 'test@example.com', '$2a$10$...', 'USER', true);
```

---

## 📋 Checklist Debug

- [ ] Backend đang chạy trên port 8081
- [ ] Frontend đang chạy trên port 4200
- [ ] Token được lưu trong localStorage sau khi login
- [ ] User trong database có role đúng (USER/DOCTOR/ADMIN - chữ HOA)
- [ ] User trong database có is_active = true
- [ ] Backend logs hiển thị "✓ Authentication set cho user"
- [ ] Backend logs hiển thị authorities đúng (ROLE_USER, ROLE_DOCTOR, hoặc ROLE_ADMIN)

---

## 🚨 Nếu Vẫn Lỗi 403

1. **Kiểm tra backend logs** - xem có log "✓ Authentication set" không
2. **Kiểm tra database** - xem user có role và is_active đúng không
3. **Test với Postman** - gửi request với token để xem backend trả về gì
4. **Kiểm tra CORS** - đảm bảo CORS config đúng trong SecurityConfig

---

## 📝 Lưu Ý

- Spring Security tự động thêm prefix "ROLE_" khi:
  - Dùng `.roles()` trong UserDetails (tạo authorities)
  - Dùng `.hasRole()` hoặc `.hasAnyRole()` trong SecurityConfig (check authorities)

- Vậy nếu database có role = "USER", thì:
  - UserDetails sẽ có authority = "ROLE_USER"
  - SecurityConfig check "USER" sẽ match với "ROLE_USER"

- Điều này đúng và không cần sửa gì!

