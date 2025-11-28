# Sửa Lỗi: Language Level Không Đúng

## Vấn Đề

Trong **Project Structure → Project**:
- ✅ **SDK:** `openjdk-25 25.0.1` (ĐÚNG)
- ❌ **Language level:** `22 - Unnamed variables and patterns` (SAI!)

**Phải là:** `25` (hoặc `25 - Patterns`)

## Giải Pháp

### Bước 1: Sửa Language Level

1. **File → Project Structure** (Ctrl+Alt+Shift+S)
2. **Project Settings → Project**
3. **Language level:** 
   - Click dropdown
   - Chọn **`25`** hoặc **`25 - Patterns`** (KHÔNG phải 22!)
4. Click **Apply** và **OK**

### Bước 2: Kiểm Tra Module Language Level

1. Vẫn trong **Project Structure**
2. **Project Settings → Modules**
3. Chọn module `Family-Medical-Management`
4. Tab **Sources**
   - **Language level:** Phải là **`25`** (hoặc `Project default`)
5. Click **Apply** và **OK**

### Bước 3: Kiểm Tra Facets

1. **Project Settings → Facets**
2. Nếu có facet nào (ví dụ: Java, Spring), đảm bảo:
   - **Language level:** `25`
3. Click **Apply** và **OK**

### Bước 4: Invalidate Caches

1. **File → Invalidate Caches...**
2. Chọn tất cả options
3. Click **Invalidate and Restart**

### Bước 5: Rebuild Project

1. **Build → Rebuild Project**
2. Đợi build hoàn tất

## Nếu Vẫn Không Có Language Level 25

### Kiểm Tra IntelliJ Version

Language level 25 chỉ có trong:
- **IntelliJ IDEA 2024.3** hoặc mới hơn
- **IntelliJ IDEA 2024.2** có thể không có

### Nếu IntelliJ Cũ:

1. **Help → Check for Updates**
2. Update lên version mới nhất
3. Hoặc dùng **Language level: 21** (Java 21 LTS)

### Hoặc Dùng Java 21 Thay Vì 25:

1. **Project Structure → Project → SDK:** Chọn Java 21
2. **Language level:** Chọn `21`
3. **pom.xml:** Đổi `<java.version>25</java.version>` thành `<java.version>21</java.version>`

## Kiểm Tra Sau Khi Fix

1. **File → Project Structure → Project**
2. Đảm bảo:
   - **SDK:** `openjdk-25 25.0.1` (hoặc Java 21)
   - **Language level:** `25` (hoặc `21`) - KHÔNG phải 22!

## Lưu Ý

- **Language level 22** = Java 22 (không đúng với Java 25)
- **Language level 25** = Java 25 (đúng)
- Language level phải khớp với SDK version


