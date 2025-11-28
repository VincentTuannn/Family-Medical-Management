# Hướng Dẫn Chạy Tests với Maven Wrapper

## Vấn Đề

Khi chạy lệnh `mvn` hoặc `.\mvnw.cmd`, bạn gặp lỗi vì:
- Maven chưa được cài đặt toàn cục
- Hoặc không biết cách sử dụng Maven Wrapper

## Giải Pháp: Sử Dụng Script Tự Động

Đã tạo 2 script để bạn chạy tests dễ dàng:

### 1. PowerShell Script (Khuyến Nghị)

```powershell
# Di chuyển vào thư mục project
cd Family-Medical-Management

# Chạy script
.\run-tests.ps1
```

### 2. Batch Script (CMD)

```cmd
REM Di chuyển vào thư mục project
cd Family-Medical-Management

REM Chạy script
run-tests.bat
```

## Các Lệnh Thủ Công

Nếu muốn chạy từng lệnh riêng:

### Bước 1: Di chuyển vào thư mục project

```powershell
cd Family-Medical-Management
```

### Bước 2: Chạy các lệnh Maven

```powershell
# Chạy tests
.\mvnw.cmd clean test

# Tạo coverage report
.\mvnw.cmd jacoco:report

# Chạy tests và tạo report cùng lúc
.\mvnw.cmd clean test jacoco:report

# Kiểm tra coverage có đạt 70%
.\mvnw.cmd jacoco:check

# Build project
.\mvnw.cmd clean package

# Chạy ứng dụng Spring Boot
.\mvnw.cmd spring-boot:run
```

## Các Lệnh Thường Dùng

### Chạy Tests

```powershell
.\mvnw.cmd test
```

### Chạy Tests và Tạo Coverage Report

```powershell
.\mvnw.cmd clean test jacoco:report
```

### Xem Coverage Report

Sau khi chạy `jacoco:report`, mở file:
```
target\site\jacoco\index.html
```

Trong PowerShell:
```powershell
Start-Process target\site\jacoco\index.html
```

Trong CMD:
```cmd
start target\site\jacoco\index.html
```

### Kiểm Tra Coverage Có Đạt 70%

```powershell
.\mvnw.cmd clean test jacoco:check
```

Lệnh này sẽ:
- Chạy tests
- Tạo coverage report
- Kiểm tra xem coverage có đạt 70% không
- Nếu không đạt, build sẽ fail

## Troubleshooting

### Lỗi: "mvnw.cmd is not recognized"

**Nguyên nhân**: Bạn không ở đúng thư mục

**Giải pháp**:
```powershell
# Kiểm tra thư mục hiện tại
pwd

# Di chuyển vào thư mục project
cd Family-Medical-Management

# Kiểm tra xem có mvnw.cmd không
Test-Path mvnw.cmd
```

### Lỗi: "JAVA_HOME is not set"

**Nguyên nhân**: Java chưa được cài đặt hoặc JAVA_HOME chưa được set

**Giải pháp**:
1. Cài đặt JDK 21
2. Thêm JAVA_HOME vào Environment Variables:
   ```
   JAVA_HOME = C:\Program Files\Java\jdk-21
   ```
3. Thêm vào PATH:
   ```
   %JAVA_HOME%\bin
   ```

### Lỗi: "Maven wrapper jar not found"

**Nguyên nhân**: Maven Wrapper chưa được tải về

**Giải pháp**: Lần đầu chạy `mvnw.cmd`, nó sẽ tự động tải Maven về. Đợi vài phút.

### Lỗi: "Execution policy"

**Nguyên nhân**: PowerShell chặn script

**Giải pháp**:
```powershell
# Chạy PowerShell với quyền Administrator
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

## Tóm Tắt

### Cách Nhanh Nhất:

```powershell
cd Family-Medical-Management
.\run-tests.ps1
```

### Hoặc Chạy Thủ Công:

```powershell
cd Family-Medical-Management
.\mvnw.cmd clean test jacoco:report
Start-Process target\site\jacoco\index.html
```

## Lưu Ý

- **Lần đầu chạy**: Maven Wrapper sẽ tải Maven về (có thể mất vài phút)
- **Đảm bảo có Internet**: Để tải Maven lần đầu
- **Java 21**: Cần cài đặt JDK 21 để chạy Maven

