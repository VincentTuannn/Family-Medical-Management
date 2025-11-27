-- Script để kiểm tra và sửa lỗi 403
-- Chạy script này để đảm bảo user có role và is_active đúng

USE medical_family_db;

-- 1. Kiểm tra tất cả users
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
FROM `user`;

-- 2. Sửa role thành uppercase nếu cần
UPDATE `user` 
SET role = UPPER(role)
WHERE role IS NOT NULL 
  AND role NOT IN ('USER', 'DOCTOR', 'ADMIN');

-- 3. Set tất cả users active (nếu cần)
-- UNCOMMENT dòng dưới nếu muốn set tất cả users active
-- UPDATE `user` SET is_active = TRUE;

-- 4. Sửa user cụ thể (thay 'your_username' bằng username của bạn)
-- UPDATE `user` 
-- SET role = 'USER', is_active = TRUE 
-- WHERE username = 'your_username';

-- 5. Kiểm tra lại sau khi sửa
SELECT 
    user_id, 
    username, 
    role, 
    is_active 
FROM `user` 
WHERE username = 'your_username';  -- Thay bằng username của bạn

-- 6. Tạo user test mới (nếu cần)
-- Password hash cho "password123" (BCrypt)
-- INSERT INTO `user` (username, email, password, phone, role, is_active) 
-- VALUES (
--     'testuser', 
--     'test@example.com', 
--     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
--     '0123456789',
--     'USER', 
--     TRUE
-- );


