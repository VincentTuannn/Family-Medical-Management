# Hướng Dẫn Set Java 17 cho Maven

## Vấn Đề

Maven đang dùng **Java 21** để chạy (runtime), mặc dù bạn đã cấu hình compile với Java 17. Điều này gây ra lỗi Lombok:
```
ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN
```

## Giải Pháp: Set JAVA_HOME về Java 17

### Bước 1: Cài Đặt Java 17

1. **Tải Java 17**:
   - Truy cập: https://adoptium.net/temurin/releases/?version=17
   - Chọn: **Windows x64** → **JDK** → **.msi** installer
   - Tải và cài đặt

2. **Ghi nhớ đường dẫn cài đặt** (thường là):
   ```
   C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot
   ```

### Bước 2: Set JAVA_HOME (Tạm Thời - Cho Session Hiện Tại)

Mở PowerShell và chạy:

```powershell
# Thay đổi đường dẫn theo nơi bạn cài Java 17
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.13+11-hotspot"

# Thêm vào PATH
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Kiểm tra
java -version
```

Kết quả mong đợi:
```
openjdk version "17.0.13" 2024-10-15
OpenJDK Runtime Environment Temurin-17.0.13+11 (build 17.0.13+11)
OpenJDK 64-Bit Server VM Temurin-17.0.17+11 (build 17.0.13+11, mixed mode, sharing)
```

### Bước 3: Set JAVA_HOME (Vĩnh Viễn)

#### Cách 1: Qua GUI (Dễ Nhất)

1. Mở **System Properties**:
   - Nhấn `Win + R`
   - Gõ `sysdm.cpl` và nhấn Enter
   - Hoặc: Settings → System → About → Advanced system settings

2. Chọn tab **Advanced** → Click **Environment Variables**

3. Trong **User variables** (hoặc **System variables**):
   - Click **New**
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Eclipse Adoptium\jdk-17.0.13+11-hotspot`
   - Click **OK**

4. Tìm biến **Path** → Click **Edit**:
   - Click **New**
   - Thêm: `%JAVA_HOME%\bin`
   - Click **OK** để lưu

5. **Quan trọng**: Đóng và mở lại PowerShell/Terminal để áp dụng

#### Cách 2: Qua PowerShell (Admin)

```powershell
# Chạy PowerShell với quyền Administrator

# Set JAVA_HOME
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-17.0.13+11-hotspot", "User")

# Thêm vào PATH
$currentPath = [Environment]::GetEnvironmentVariable("Path", "User")
if ($currentPath -notlike "*%JAVA_HOME%\bin*") {
    [Environment]::SetEnvironmentVariable("Path", "$currentPath;%JAVA_HOME%\bin", "User")
}
```

### Bước 4: Kiểm Tra

Mở PowerShell mới và chạy:

```powershell
# Kiểm tra JAVA_HOME
$env:JAVA_HOME

# Kiểm tra Java version
java -version

# Kiểm tra Maven dùng Java nào
cd Family-Medical-Management
.\mvnw.cmd -version
```

Kết quả `mvnw.cmd -version` phải hiển thị:
```
Apache Maven 3.9.11
Maven home: ...
Java version: 17.0.13, vendor: Eclipse Adoptium
```

### Bước 5: Chạy Compile

```powershell
cd Family-Medical-Management
.\mvnw.cmd clean compile
```

## Nếu Vẫn Gặp Lỗi

### Kiểm Tra Java Version Maven Đang Dùng

```powershell
cd Family-Medical-Management
.\mvnw.cmd -version
```

Nếu vẫn hiển thị Java 21:
1. Đảm bảo đã đóng và mở lại PowerShell
2. Kiểm tra JAVA_HOME:
   ```powershell
   $env:JAVA_HOME
   ```
3. Nếu JAVA_HOME vẫn trỏ đến Java 21, sửa lại

### Xóa Cache Maven

```powershell
Remove-Item -Recurse -Force $env:USERPROFILE\.m2\repository\org\projectlombok
.\mvnw.cmd clean compile
```

## Tóm Tắt

1. ✅ Cài Java 17
2. ✅ Set JAVA_HOME về Java 17
3. ✅ Thêm `%JAVA_HOME%\bin` vào PATH
4. ✅ Đóng và mở lại PowerShell
5. ✅ Chạy `.\mvnw.cmd clean compile`

Sau khi set đúng JAVA_HOME, mọi thứ sẽ hoạt động ổn định với Java 17 + Lombok 1.18.30.


