# Hướng dẫn xử lý lỗi kết nối Backend

## Lỗi: ERR_CONNECTION_REFUSED

Lỗi này xảy ra khi frontend không thể kết nối đến backend. Các bước kiểm tra:

### 1. Kiểm tra Backend có đang chạy không

**Cách 1: Kiểm tra trong IDE (IntelliJ/Eclipse)**
- Xem console/log của ứng dụng Spring Boot
- Tìm dòng: `Started FMMApplication in X.XXX seconds`
- Nếu không thấy, backend chưa khởi động thành công

**Cách 2: Kiểm tra bằng trình duyệt/Postman**
- Mở trình duyệt hoặc Postman
- Gửi GET request đến: `http://localhost:8082/api/public/health`
- Nếu nhận được response `{"status":"UP","userCount":X}` → Backend đang chạy
- Nếu lỗi connection → Backend chưa chạy hoặc đang crash

**Cách 3: Kiểm tra port có bị chiếm không**
```bash
# Windows PowerShell
netstat -ano | findstr :8082

# Nếu có process đang dùng port 8082, ghi nhớ PID và kill nó:
taskkill /PID <PID> /F
```

### 2. Kiểm tra Database Connection

Backend cần kết nối đến MySQL. Kiểm tra:

1. **MySQL có đang chạy không?**
   - Mở MySQL Workbench hoặc command line
   - Thử kết nối với thông tin trong `application.properties`

2. **Database và bảng đã được tạo chưa?**
   - Database: `medical_family_db`
   - Bảng: `user`, `patient`, `doctor`, etc.
   - Chạy script `src/main/SQL/CreateTable` nếu chưa có

3. **Thông tin kết nối trong `application.properties`:**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/medical_family_db
   spring.datasource.username=root
   spring.datasource.password=hallelujah1098
   ```

### 3. Kiểm tra Log Backend

Xem log trong console để tìm lỗi:

**Lỗi thường gặp:**
- `Communications link failure` → MySQL chưa chạy hoặc sai thông tin kết nối
- `Table 'medical_family_db.user' doesn't exist` → Chưa chạy script tạo bảng
- `Access denied for user` → Sai username/password MySQL
- `Port 8082 already in use` → Port bị chiếm, đổi port hoặc kill process

### 4. Khởi động lại Backend

1. **Dừng ứng dụng** (nếu đang chạy)
2. **Clean và rebuild project:**
   ```bash
   # Maven
   mvn clean install
   ```
3. **Chạy lại ứng dụng:**
   - Trong IDE: Run `FMMApplication.java`
   - Hoặc command line: `mvn spring-boot:run`

### 5. Test kết nối

Sau khi backend khởi động thành công:

1. **Test Health Endpoint:**
   ```
   GET http://localhost:8082/api/public/health
   ```

2. **Test Login Endpoint (Postman):**
   ```
   POST http://localhost:8082/api/auth/login
   Content-Type: application/json
   
   {
     "username": "phamthienphu",
     "password": "123456"
   }
   ```

### 6. Kiểm tra CORS

Nếu backend chạy nhưng vẫn lỗi CORS:
- Kiểm tra `SecurityConfig.java` có cấu hình CORS cho `http://localhost:4200` chưa
- Đảm bảo frontend chạy trên port 4200 (hoặc cập nhật CORS config)

## Lỗi liên quan đến Role

Nếu gặp lỗi `No enum constant Backend.FMM.Entity.User.Role.user`:

1. **Chạy script SQL để update role:**
   ```sql
   USE medical_family_db;
   SET SQL_SAFE_UPDATES = 0;
   UPDATE `user` SET role = UPPER(role) WHERE role IN ('user', 'doctor', 'admin');
   SET SQL_SAFE_UPDATES = 1;
   ```

2. **Hoặc để `DatabaseRoleUpdater` tự động xử lý** khi khởi động (đã được cấu hình)

## Liên hệ

Nếu vẫn gặp vấn đề, kiểm tra:
- Log đầy đủ từ backend console
- Log từ browser console (F12)
- Cấu hình firewall/antivirus có chặn port 8082 không

