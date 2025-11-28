# Sửa Lỗi: JDK 0 Configuration

## Lỗi

```
java: Cannot start javac process for Family-Medical-Management: 
it is configured to use JDK 0, but IDE supports compilation using JDK 7 and newer only.
```

**Nguyên nhân:** IntelliJ đang cấu hình module với JDK 0 (không hợp lệ).

## Giải Pháp

### Bước 1: Cấu Hình Project SDK

1. **File → Project Structure** (Ctrl+Alt+Shift+S)
2. **Project Settings → Project**
   - **SDK:** Chọn **Java 25** (hoặc Java 21 nếu không có 25)
     - Nếu không thấy Java 25, click **New...** → **JDK**
     - Chọn thư mục Java 25 (ví dụ: `C:\Program Files\Java\jdk-25`)
   - **Language level:** Chọn **25** (hoặc 21)
3. Click **Apply** và **OK**

### Bước 2: Cấu Hình Module SDK

1. Vẫn trong **Project Structure**
2. **Project Settings → Modules**
3. Chọn module `Family-Medical-Management`
4. Tab **Dependencies**
   - **Module SDK:** Chọn **Java 25** (hoặc Java 21)
     - **QUAN TRỌNG:** Phải chọn SDK cụ thể, KHÔNG để "Project SDK" hoặc "No SDK"
5. Click **Apply** và **OK**

### Bước 3: Kiểm Tra Facets

1. Vẫn trong **Project Structure**
2. **Project Settings → Facets**
3. Nếu có facet nào, đảm bảo:
   - **Language level:** 25 (hoặc 21)
   - **Target platform:** Java 25 (hoặc 21)

### Bước 4: Cấu Hình Maven Runner

1. **File → Settings** (Ctrl+Alt+S)
2. **Build, Execution, Deployment → Build Tools → Maven → Runner**
3. **JRE:** Chọn **Java 25** (hoặc Java 21)
   - Nếu không thấy, click **...** và thêm JDK
4. Click **Apply** và **OK**

### Bước 5: Invalidate Caches

1. **File → Invalidate Caches...**
2. Chọn tất cả:
   - ✅ Clear file system cache and Local History
   - ✅ Clear downloaded shared indexes
   - ✅ Clear VCS Log caches and indexes
3. Click **Invalidate and Restart**

### Bước 6: Reimport Maven Project

1. **Maven tool window** (View → Tool Windows → Maven)
2. Click **Reload All Maven Projects** (icon refresh)
3. Hoặc: Right-click `pom.xml` → **Maven → Reload Project**

### Bước 7: Rebuild Project

1. **Build → Rebuild Project**
2. Đợi build hoàn tất

## Nếu Không Thấy Java 25 trong IntelliJ

### Thêm JDK Manually:

1. **File → Project Structure → SDKs**
2. Click **+** → **Add JDK...**
3. Chọn thư mục Java 25:
   - Windows: `C:\Program Files\Java\jdk-25`
   - Hoặc: `C:\Program Files\Eclipse Adoptium\jdk-25`
   - Hoặc: `C:\Users\Admin\.jdks\jdk-25`
4. Click **OK**

### Tìm Java 25 trên Windows:

```powershell
# Tìm tất cả Java installations
Get-ChildItem -Path "C:\Program Files\Java" -ErrorAction SilentlyContinue
Get-ChildItem -Path "C:\Program Files\Eclipse Adoptium" -ErrorAction SilentlyContinue
Get-ChildItem -Path "$env:USERPROFILE\.jdks" -ErrorAction SilentlyContinue
```

## Kiểm Tra Sau Khi Fix

### Trong IntelliJ Terminal:

```powershell
java -version
```

Kết quả mong đợi:
```
java version "25.0.1"  # hoặc 21.x.x
```

### Kiểm Tra Project Structure:

1. **File → Project Structure → Modules**
2. Module `Family-Medical-Management` phải có:
   - **Module SDK:** Java 25 (hoặc Java 21) - KHÔNG phải "Project SDK" hoặc "No SDK"

## Nếu Vẫn Không Được

### Option 1: Tạo Project Mới

1. **File → Close Project**
2. **File → Open**
3. Chọn folder `Family-Medical-Management`
4. Chọn **Open as Project**
5. IntelliJ sẽ tự động import Maven project

### Option 2: Xóa .idea Folder

```powershell
# Backup
Copy-Item -Path ".idea" -Destination ".idea.backup" -Recurse

# Xóa
Remove-Item -Path ".idea" -Recurse -Force

# Mở lại project trong IntelliJ
```

### Option 3: Sửa .idea/misc.xml Thủ Công

1. Đóng IntelliJ
2. Mở file `.idea/misc.xml`
3. Tìm dòng:
   ```xml
   <component name="ProjectRootManager" version="2" languageLevel="..." project-jdk-name="..." project-jdk-type="...">
   ```
4. Đảm bảo:
   - `project-jdk-name` có giá trị hợp lệ (ví dụ: "25" hoặc "21")
   - `project-jdk-type` = "JavaSDK"
5. Lưu file và mở lại IntelliJ

## Lưu Ý

- **JDK 0** = Không có JDK được cấu hình
- Module phải có SDK cụ thể, không thể để "Project SDK" hoặc "No SDK"
- Sau khi fix, rebuild project để áp dụng thay đổi


