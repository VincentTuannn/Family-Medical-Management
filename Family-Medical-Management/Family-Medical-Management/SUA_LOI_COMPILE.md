# Hướng Dẫn Sửa Lỗi Compile

## Lỗi Gặp Phải

```
Fatal error compiling: java.lang.ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN
```

## Nguyên Nhân

Lỗi này xảy ra do:
1. **Maven Compiler Plugin version 3.13.0** có bug hoặc không tương thích với Java version hiện tại
2. Xung đột giữa Lombok annotation processor và compiler plugin
3. Java version không tương thích

## Giải Pháp Đã Áp Dụng

### 1. Downgrade Maven Compiler Plugin

Đã thay đổi từ version `3.13.0` xuống `3.11.0` (version ổn định hơn).

### 2. Cấu Hình Rõ Ràng

Thay vì dùng `${java.version}`, đã set rõ ràng:
- `source`: 17
- `target`: 17
- `encoding`: UTF-8

### 3. Giữ Nguyên Lombok Version

Lombok version `1.18.36` vẫn được giữ nguyên vì tương thích tốt với Java 17.

## Cách Kiểm Tra

Sau khi sửa, chạy lại:

```powershell
cd Family-Medical-Management
.\mvnw.cmd clean compile
```

Nếu thành công, bạn sẽ thấy:
```
[INFO] BUILD SUCCESS
```

## Nếu Vẫn Gặp Lỗi

### Kiểm Tra Java Version

```powershell
java -version
```

Đảm bảo bạn đang dùng Java 17:
```
java version "17.0.x"
```

### Clean và Rebuild

```powershell
.\mvnw.cmd clean
.\mvnw.cmd compile
```

### Xóa Cache Maven

```powershell
Remove-Item -Recurse -Force $env:USERPROFILE\.m2\repository\org\apache\maven\plugins\maven-compiler-plugin
```

Sau đó chạy lại:
```powershell
.\mvnw.cmd clean compile
```

## Chạy Tests Sau Khi Sửa

Sau khi compile thành công:

```powershell
# Chạy tests
.\mvnw.cmd test

# Chạy tests và tạo coverage report
.\mvnw.cmd clean test jacoco:report
```


