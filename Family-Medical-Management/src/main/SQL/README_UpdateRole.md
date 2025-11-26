# Hướng dẫn cập nhật Role trong Database

## Vấn đề
Database hiện tại có thể chứa role dạng chữ thường (`user`, `doctor`, `admin`), nhưng code Java sử dụng enum chữ hoa (`USER`, `DOCTOR`, `ADMIN`).

## Giải pháp
Đã tạo `RoleAttributeConverter` để tự động xử lý chuyển đổi. Tuy nhiên, để nhất quán, nên cập nhật dữ liệu trong DB.

## Cách thực hiện

### Cách 1: Chạy script SQL trực tiếp
1. Mở MySQL Workbench hoặc command line MySQL
2. Kết nối đến database `medical_family_db`
3. Chạy script trong file `UpdateRoleToUppercase.sql`:

```sql
USE medical_family_db;

UPDATE `user` 
SET role = UPPER(role)
WHERE role IN ('user', 'doctor', 'admin');
```

### Cách 2: Sử dụng MySQL Command Line
```bash
mysql -u root -p medical_family_db < src/main/SQL/UpdateRoleToUppercase.sql
```

### Cách 3: Không cần update (AttributeConverter tự xử lý)
Nếu không muốn update DB, AttributeConverter sẽ tự động chuyển đổi khi đọc dữ liệu. Tuy nhiên, dữ liệu mới sẽ được lưu dạng chữ hoa.

## Kiểm tra kết quả
Sau khi chạy script, kiểm tra bằng:
```sql
SELECT user_id, username, role FROM `user`;
```

Tất cả role phải là chữ hoa: `USER`, `DOCTOR`, `ADMIN`

