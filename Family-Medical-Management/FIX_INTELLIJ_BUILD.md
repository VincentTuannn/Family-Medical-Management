# Hướng Dẫn Sửa Lỗi Build IntelliJ IDEA

## Lỗi

```
class file has wrong version 69.0, should be 67.0
```

**Nguyên nhân:**
- IntelliJ đang dùng **Java 11** (version 67.0) để compile
- Nhưng class files trong `target/` đã được compile bằng **Java 25** (version 69.0)
- Version mismatch → Build fail

## Giải Pháp

### Bước 1: Xóa Target Folder

**Trong IntelliJ:**
1. Right-click thư mục `target`
2. **Delete** (hoặc **Exclude from Project**)
3. Confirm delete

**Hoặc dùng Maven:**
```powershell
.\mvnw.cmd clean
```

### Bước 2: Cấu Hình Java Version trong IntelliJ

#### 2.1. Project SDK

1. **File → Project Structure** (Ctrl+Alt+Shift+S)
2. **Project Settings → Project**
   - **SDK:** Chọn **Java 25** (hoặc Java 21 nếu không có 25)
   - **Language level:** Chọn **25** (hoặc 21)
3. Click **Apply** và **OK**

#### 2.2. Module SDK

1. Vẫn trong **Project Structure**
2. **Project Settings → Modules**
3. Chọn module `Family-Medical-Management`
4. Tab **Dependencies**
   - **Module SDK:** Chọn **Java 25** (hoặc Java 21)
5. Click **Apply** và **OK**

#### 2.3. Maven Runner

1. **File → Settings** (Ctrl+Alt+S)
2. **Build, Execution, Deployment → Build Tools → Maven → Runner**
3. **JRE:** Chọn **Java 25** (hoặc Java 21)
4. Click **Apply** và **OK**

### Bước 3: Invalidate Caches

1. **File → Invalidate Caches...**
2. Chọn tất cả options:
   - ✅ Clear file system cache and Local History
   - ✅ Clear downloaded shared indexes
   - ✅ Clear VCS Log caches and indexes
3. Click **Invalidate and Restart**
4. Đợi IntelliJ restart

### Bước 4: Reimport Maven Project

1. **Maven tool window** (View → Tool Windows → Maven)
2. Click **Reload All Maven Projects** (icon refresh)
3. Hoặc: Right-click `pom.xml` → **Maven → Reload Project**

### Bước 5: Rebuild Project

1. **Build → Rebuild Project**
2. Đợi IntelliJ rebuild toàn bộ project

## Kiểm Tra Java Version

### Trong IntelliJ Terminal:

```powershell
java -version
```

Kết quả mong đợi:
```
java version "25.0.1"  # hoặc 21.x.x
```

### Nếu Vẫn Dùng Java 11:

1. **File → Settings → Build, Execution, Deployment → Build Tools → Maven → Runner**
2. **JRE:** Chọn **Java 25** từ dropdown
3. Nếu không thấy Java 25:
   - **File → Project Structure → SDKs**
   - Click **+** → **Add JDK**
   - Chọn thư mục Java 25 (ví dụ: `C:\Program Files\Java\jdk-25`)

## Nếu Vẫn Không Được

### Option 1: Tạo Project Mới

1. **File → Close Project**
2. **File → Open**
3. Chọn folder `Family-Medical-Management`
4. Chọn **Open as Project**
5. IntelliJ sẽ tự động import Maven project với Java version từ `pom.xml`

### Option 2: Xóa .idea Folder

```powershell
# Backup trước
Copy-Item -Path ".idea" -Destination ".idea.backup" -Recurse

# Xóa .idea
Remove-Item -Path ".idea" -Recurse -Force

# Mở lại project trong IntelliJ
```

## Kiểm Tra pom.xml

Đảm bảo `pom.xml` có:

```xml
<properties>
    <java.version>25</java.version>
</properties>
```

## Sau Khi Fix

1. **Build → Rebuild Project**
2. Kiểm tra **Build Output** - không còn lỗi
3. Chạy tests: **Run → Run All Tests**

## Lưu Ý

- **Version 69.0** = Java 25
- **Version 67.0** = Java 11
- IntelliJ phải dùng cùng Java version với Maven
- Luôn xóa `target/` folder sau khi đổi Java version


