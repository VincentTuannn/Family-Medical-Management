# Sửa Lỗi: Module JDK 0

## Lỗi

```
java: Cannot start javac process for Family-Medical-Management: 
it is configured to use JDK 0
```

**Nguyên nhân:** Module chưa được associate với JDK cụ thể.

## Giải Pháp Chi Tiết

### Bước 1: Kiểm Tra Project SDK

1. **File → Project Structure** (Ctrl+Alt+Shift+S)
2. **Project Settings → Project**
   - **SDK:** Phải có **Java 25** (openjdk-25 25.0.1)
   - Nếu không có, click **New...** → **JDK** và chọn thư mục Java 25
3. Click **Apply**

### Bước 2: Cấu Hình Module SDK (QUAN TRỌNG!)

1. Vẫn trong **Project Structure**
2. **Project Settings → Modules**
3. Chọn module **`Family-Medical-Management`**
4. Tab **Dependencies** (hoặc tab đầu tiên)
5. Tìm field **"Module SDK"** hoặc **"SDK"**
6. **QUAN TRỌNG:** 
   - Click dropdown
   - Chọn **`25`** hoặc **`openjdk-25 25.0.1`** (SDK cụ thể)
   - **KHÔNG** chọn "Project SDK" hoặc "No SDK" hoặc để trống
7. Click **Apply** và **OK**

### Bước 3: Kiểm Tra Sources Tab

1. Vẫn trong **Project Structure → Modules**
2. Chọn module `Family-Medical-Management`
3. Tab **Sources**
   - **Language level:** `23` hoặc `Project default`
4. Click **Apply** và **OK**

### Bước 4: Nếu Vẫn Không Thấy Module SDK Option

#### Option A: Reimport Module

1. **File → Project Structure → Modules**
2. Chọn module `Family-Medical-Management`
3. Click **-** (Remove)
4. Click **+** → **Import Module**
5. Chọn `pom.xml`
6. Chọn **Import Maven project**
7. Trong quá trình import, đảm bảo chọn **Java 25** cho SDK

#### Option B: Xóa và Tạo Lại .idea

1. Đóng IntelliJ
2. Backup `.idea` folder:
   ```powershell
   Copy-Item -Path ".idea" -Destination ".idea.backup" -Recurse
   ```
3. Xóa `.idea` folder:
   ```powershell
   Remove-Item -Path ".idea" -Recurse -Force
   ```
4. Mở lại project trong IntelliJ
5. IntelliJ sẽ tự động import Maven project với Java 25

### Bước 5: Invalidate Caches

1. **File → Invalidate Caches...**
2. Chọn tất cả options
3. Click **Invalidate and Restart**

### Bước 6: Reimport Maven Project

1. **Maven tool window** (View → Tool Windows → Maven)
2. Click **Reload All Maven Projects** (icon refresh)

### Bước 7: Rebuild Project

1. **Build → Rebuild Project**
2. Đợi build hoàn tất

## Kiểm Tra Sau Khi Fix

### Trong Project Structure:

1. **File → Project Structure → Modules**
2. Module `Family-Medical-Management` phải có:
   - **Module SDK:** `25` hoặc `openjdk-25 25.0.1` (SDK cụ thể)
   - **KHÔNG** phải "Project SDK" hoặc "No SDK" hoặc để trống

### Trong IntelliJ Terminal:

```powershell
java -version
```

Kết quả: `java version "25.0.1"`

## Nếu Vẫn Không Được

### Tạo Project Mới:

1. **File → Close Project**
2. **File → Open**
3. Chọn folder `Family-Medical-Management`
4. Chọn **Open as Project**
5. IntelliJ sẽ tự động import Maven project
6. Trong quá trình import, chọn **Java 25** cho SDK

## Lưu Ý Quan Trọng

- **JDK 0** = Module chưa có SDK được assign
- Module **PHẢI** có SDK cụ thể, không thể để "Project SDK"
- Sau khi fix, rebuild project để áp dụng thay đổi


