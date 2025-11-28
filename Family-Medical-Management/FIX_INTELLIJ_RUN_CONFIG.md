# Sửa Lỗi: Spring ASM Không Đọc Class Files Java 25

## Vấn Đề

IntelliJ đã chạy với **Java 25** (`Starting FMMApplication using Java 25.0.1`), nhưng Spring ASM vẫn không thể đọc class files version 69:

```
Unsupported class file major version 69
```

**Nguyên nhân:** Spring ASM trong Spring Boot 3.3.5 chưa hỗ trợ Java 25.

## Giải Pháp: Thêm VM Options trong IntelliJ Run Configuration

### Bước 1: Mở Run Configuration

1. **Run → Edit Configurations...**
2. Hoặc click dropdown **Run/Debug** → **Edit Configurations...**

### Bước 2: Chọn FMMApplication Configuration

1. Tìm **Application → FMMApplication** (hoặc tên configuration của bạn)
2. Nếu không có, click **+** → **Application**
3. Đặt tên: `FMMApplication`
4. **Main class:** `Backend.FMM.FMMApplication`

### Bước 3: Thêm VM Options

1. Tìm field **VM options** (hoặc **VM options:**)
2. Thêm:
   ```
   -Dspring.classformat.ignore=true
   ```
3. Hoặc nếu đã có options khác, thêm vào cuối:
   ```
   -Dspring.classformat.ignore=true
   ```

### Bước 4: Đảm Bảo JRE Đúng

1. **JRE:** Chọn **Java 25** (openjdk-25 25.0.1)
2. **Use classpath of module:** Chọn `Family-Medical-Management`

### Bước 5: Apply và Run

1. Click **Apply** và **OK**
2. **Run → Run 'FMMApplication'**

## Nếu Không Thấy VM Options

### Option 1: Modify Options

1. Trong Run Configuration
2. Click **Modify options** (dropdown)
3. Chọn **Add VM options**
4. Field **VM options** sẽ xuất hiện

### Option 2: Environment Variables

1. Trong Run Configuration
2. **Environment variables:**
   - Click **...**
   - Thêm: `SPRING_CLASSFORMAT_IGNORE=true`
3. Click **OK**

## Xóa Target và Recompile

Nếu vẫn không được, xóa target và recompile:

1. **Build → Rebuild Project**
2. Hoặc trong Terminal:
   ```powershell
   .\mvnw.cmd clean compile
   ```

## Kiểm Tra

Sau khi thêm VM option, khi chạy application, log sẽ không còn lỗi:
```
Unsupported class file major version 69
```

Application sẽ start thành công.

## Lưu Ý

- **VM option** `-Dspring.classformat.ignore=true` chỉ áp dụng cho runtime
- **Maven surefire plugin** đã có system property cho tests
- Cần thêm vào **IntelliJ Run Configuration** cho application runtime


