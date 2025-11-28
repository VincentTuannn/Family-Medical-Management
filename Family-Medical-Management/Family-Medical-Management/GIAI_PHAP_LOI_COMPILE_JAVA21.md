# Giải Pháp Lỗi Compile với Java 21

## Lỗi Hiện Tại

```
Fatal error compiling: java.lang.ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN
```

## Nguyên Nhân

Lỗi này xảy ra do xung đột giữa:
- **Lombok** annotation processor
- **Maven Compiler Plugin** version
- **Java 21** compiler

## Giải Pháp Đã Thử

### 1. ✅ Đã cập nhật Java version: 17 → 21
### 2. ✅ Đã downgrade Lombok: 1.18.36 → 1.18.30
### 3. ✅ Đã cập nhật Maven Compiler Plugin: 3.11.0 → 3.13.0
### 4. ✅ Đã bỏ annotationProcessorPaths (để Spring Boot tự quản lý)

## Giải Pháp Khuyến Nghị

### Cách 1: Sử dụng Java 17 (Ổn Định Nhất) ⭐

Nếu bạn có thể cài Java 17:

1. **Cài đặt Java 17**:
   - Tải từ: https://adoptium.net/temurin/releases/?version=17
   - Cài đặt và set JAVA_HOME

2. **Cập nhật pom.xml**:
   ```xml
   <properties>
       <java.version>17</java.version>
   </properties>
   ```

3. **Cập nhật maven-compiler-plugin**:
   ```xml
   <plugin>
       <groupId>org.apache.maven.plugins</groupId>
       <artifactId>maven-compiler-plugin</artifactId>
       <version>3.11.0</version>
       <configuration>
           <release>17</release>
           <encoding>UTF-8</encoding>
       </configuration>
   </plugin>
   ```

### Cách 2: Cập Nhật Lombok Lên Version Mới Nhất

Thử cập nhật Lombok lên version mới nhất hỗ trợ Java 21:

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.32</version>
    <scope>provided</scope>
</dependency>
```

Và trong maven-compiler-plugin:
```xml
<annotationProcessorPaths>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.32</version>
    </path>
</annotationProcessorPaths>
```

### Cách 3: Bỏ Lombok Annotation Processor Path

Để Spring Boot tự quản lý Lombok (đã thử):

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.13.0</version>
    <configuration>
        <release>21</release>
        <encoding>UTF-8</encoding>
        <!-- Không có annotationProcessorPaths -->
    </configuration>
</plugin>
```

### Cách 4: Sử dụng Gradle Thay Vì Maven

Gradle thường xử lý tốt hơn với Java 21 và Lombok.

## Kiểm Tra Java Version

```powershell
java -version
javac -version
```

Đảm bảo cả hai đều là Java 21.

## Clean và Rebuild

Sau mỗi thay đổi:

```powershell
cd Family-Medical-Management

# Xóa cache
Remove-Item -Recurse -Force target -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force $env:USERPROFILE\.m2\repository\org\projectlombok -ErrorAction SilentlyContinue

# Rebuild
.\mvnw.cmd clean compile
```

## Thông Tin Tham Khảo

- **Spring Boot 3.3.5** hỗ trợ: Java 17, 19, 21
- **Lombok 1.18.30+** hỗ trợ Java 21
- **Maven Compiler Plugin 3.13.0** hỗ trợ Java 21

## Khuyến Nghị Cuối Cùng

**Nếu vẫn gặp lỗi**, khuyến nghị:
1. **Cài Java 17** (ổn định nhất với Spring Boot 3.3.5)
2. Hoặc **chờ Lombok version mới hơn** hỗ trợ tốt hơn Java 21
3. Hoặc **downgrade Spring Boot** về version hỗ trợ tốt hơn Java 21


