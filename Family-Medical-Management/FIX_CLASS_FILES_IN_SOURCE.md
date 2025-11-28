# Sửa Lỗi: Class Files Trong Source Code

## Vấn Đề

Lỗi: `class file has wrong version 69.0, should be 65.0`

**Nguyên nhân:**
1. ❌ Có `.class` files trong thư mục `src/main/java/` (không nên có!)
2. ❌ IntelliJ đang dùng **Java 21** (65.0) thay vì **Java 25** (69.0)
3. ❌ Class files đã compile bằng Java 25 nhưng IntelliJ cố compile lại bằng Java 21

## Giải Pháp

### Bước 1: Xóa Tất Cả .class Files Trong Source

**Trong IntelliJ:**
1. **View → Tool Windows → Terminal**
2. Chạy lệnh:
```powershell
Get-ChildItem -Path "src" -Filter "*.class" -Recurse | Remove-Item -Force
```

**Hoặc thủ công:**
1. Right-click thư mục `src/main/java/Backend/FMM/DTO/`
2. **Find in Files** (Ctrl+Shift+F)
3. Tìm: `*.class`
4. Xóa tất cả `.class` files tìm thấy

### Bước 2: Đảm Bảo IntelliJ Dùng Java 25

#### 2.1. Project SDK

1. **File → Project Structure** (Ctrl+Alt+Shift+S)
2. **Project Settings → Project**
   - **SDK:** Chọn **Java 25** (KHÔNG phải Java 21!)
   - **Language level:** Chọn **25**
3. Click **Apply** và **OK**

#### 2.2. Module SDK

1. Vẫn trong **Project Structure**
2. **Project Settings → Modules**
3. Chọn module `Family-Medical-Management`
4. Tab **Dependencies**
   - **Module SDK:** Chọn **Java 25**
5. Click **Apply** và **OK**

#### 2.3. Maven Runner

1. **File → Settings** (Ctrl+Alt+S)
2. **Build, Execution, Deployment → Build Tools → Maven → Runner**
3. **JRE:** Chọn **Java 25** (KHÔNG phải Java 21!)
4. Click **Apply** và **OK**

### Bước 3: Xóa Target Folder

```powershell
.\mvnw.cmd clean
```

Hoặc trong IntelliJ:
1. Right-click `target` folder
2. **Delete**

### Bước 4: Invalidate Caches

1. **File → Invalidate Caches...**
2. Chọn tất cả:
   - ✅ Clear file system cache and Local History
   - ✅ Clear downloaded shared indexes
   - ✅ Clear VCS Log caches and indexes
3. Click **Invalidate and Restart**

### Bước 5: Reimport Maven Project

1. **Maven tool window** (View → Tool Windows → Maven)
2. Click **Reload All Maven Projects** (icon refresh)

### Bước 6: Rebuild Project

1. **Build → Rebuild Project**
2. Đợi build hoàn tất

## Kiểm Tra

### Trong IntelliJ Terminal:

```powershell
java -version
```

Kết quả mong đợi:
```
java version "25.0.1"  # KHÔNG phải 21.x.x!
```

### Kiểm Tra Không Còn .class Files Trong Source:

```powershell
Get-ChildItem -Path "src" -Filter "*.class" -Recurse
```

Kết quả: **Không có file nào** (empty)

## Tại Sao Có .class Files Trong Source?

- Có thể do:
  - Compile thủ công trong source folder
  - Copy/paste nhầm từ `target/classes/`
  - IDE bug khi compile

**Lưu ý:** `.class` files chỉ nên ở trong `target/classes/`, KHÔNG BAO GIỜ ở trong `src/`!

## Nếu Vẫn Không Được

### Option 1: Tạo Project Mới

1. **File → Close Project**
2. **File → Open**
3. Chọn folder `Family-Medical-Management`
4. Chọn **Open as Project**
5. IntelliJ sẽ tự động import với Java 25

### Option 2: Xóa .idea Folder

```powershell
# Backup
Copy-Item -Path ".idea" -Destination ".idea.backup" -Recurse

# Xóa
Remove-Item -Path ".idea" -Recurse -Force

# Mở lại project
```

## Sau Khi Fix

1. **Build → Rebuild Project** ✅
2. **Build Output** không còn lỗi ✅
3. Chạy tests: **Run → Run All Tests** ✅


