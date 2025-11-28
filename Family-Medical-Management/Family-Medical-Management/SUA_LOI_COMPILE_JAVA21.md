# Hướng Dẫn Sửa Lỗi Compile với Java 21

## Vấn Đề

Bạn đang dùng **Java 21** nhưng project được cấu hình cho **Java 17**, gây ra lỗi compile:
```
Fatal error compiling: java.lang.ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN
```

## Giải Pháp Đã Áp Dụng

### 1. Cập Nhật Java Version trong pom.xml

Đã thay đổi:
- `java.version`: `17` → `21`
- `maven-compiler-plugin` source/target: `17` → `21`

### 2. Cấu Hình Maven Compiler Plugin

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <source>21</source>
        <target>21</target>
        <encoding>UTF-8</encoding>
        ...
    </configuration>
</plugin>
```

## Kiểm Tra Java Version

Chạy lệnh để xác nhận:

```powershell
java -version
```

Kết quả mong đợi:
```
java version "21.x.x"
```

## Chạy Lại Compile

Sau khi cập nhật, chạy:

```powershell
cd Family-Medical-Management
.\mvnw.cmd clean compile
```

## Chạy Tests

Sau khi compile thành công:

```powershell
# Chạy tests
.\mvnw.cmd test

# Chạy tests và tạo coverage report
.\mvnw.cmd clean test jacoco:report
```

## Lưu Ý

- **Spring Boot 3.3.5** hỗ trợ Java 17, 19, và 21 ✅
- **Java 21** là LTS (Long Term Support) version
- Tất cả dependencies đều tương thích với Java 21

## Nếu Vẫn Gặp Lỗi

### 1. Kiểm Tra JAVA_HOME

```powershell
$env:JAVA_HOME
```

Nếu không có, set JAVA_HOME:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
```

### 2. Clean và Rebuild

```powershell
.\mvnw.cmd clean
.\mvnw.cmd compile
```

### 3. Xóa Cache Maven

```powershell
Remove-Item -Recurse -Force $env:USERPROFILE\.m2\repository\org\apache\maven\plugins\maven-compiler-plugin
```

Sau đó chạy lại compile.

## Kết Luận

Project đã được cấu hình để sử dụng **Java 21**. Chạy lại compile và tests để xác nhận mọi thứ hoạt động đúng.


