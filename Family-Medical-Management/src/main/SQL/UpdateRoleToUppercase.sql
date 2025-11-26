-- Script để cập nhật role từ chữ thường sang chữ hoa trong database
-- Chạy script này để đảm bảo dữ liệu role trong DB khớp với enum Java

USE medical_family_db;

-- Tắt safe update mode tạm thời để cho phép update
SET SQL_SAFE_UPDATES = 0;

-- Cập nhật tất cả role từ chữ thường sang chữ hoa
UPDATE `user` 
SET role = UPPER(role)
WHERE role IN ('user', 'doctor', 'admin');

-- Bật lại safe update mode để đảm bảo an toàn
SET SQL_SAFE_UPDATES = 1;

-- Kiểm tra kết quả
SELECT user_id, username, role FROM `user`;

-- Nếu muốn giữ chữ thường trong DB (và dùng AttributeConverter để chuyển đổi)
-- thì không cần chạy script trên, AttributeConverter sẽ tự động xử lý

