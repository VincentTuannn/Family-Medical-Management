# Sửa lỗi "JDK 0" trong IntelliJ IDEA

## Lỗi
```
java: Cannot start javac process for Family-Medical-Management: it is configured to use JDK 0
```

## Nguyên nhân
IntelliJ IDEA không nhận diện được JDK cho module, hoặc module bị cấu hình sai.

## Giải pháp

### Bước 1: Kiểm tra Project SDK
1. **File → Project Structure** (hoặc `Ctrl+Alt+Shift+S`)
2. Chọn tab **Project**
3. Kiểm tra **SDK**:
   - Nếu là `<No SDK>` hoặc `JDK 0`: Chọn **New...** hoặc chọn JDK đã cài (Java 25)
   - Nếu đã có SDK: Chọn lại SDK (Java 25)

### Bước 2: Kiểm tra Module SDK
1. Trong **Project Structure**, chọn tab **Modules**
2. Chọn module **Family-Medical-Management**
3. Kiểm tra **SDK**:
   - Nếu là `<No SDK>` hoặc `JDK 0`: Chọn SDK từ dropdown (Java 25)
   - Nếu đã có SDK: Chọn lại SDK (Java 25)

### Bước 3: Kiểm tra Language Level
1. Trong tab **Modules**, chọn module **Family-Medical-Management**
2. Kiểm tra **Language level**:
   - Nếu có Java 25: Chọn **25**
   - Nếu không có 25: Chọn **23** (hoặc version cao nhất có sẵn)

### Bước 4: Kiểm tra Maven Runner JRE
1. **File → Settings** (hoặc `Ctrl+Alt+S`)
2. **Build, Execution, Deployment → Build Tools → Maven → Runner**
3. Kiểm tra **JRE**:
   - Chọn **Use Project JDK** (Java 25)
   - Hoặc chọn **Use Java from path** và chỉ đến JDK 25

### Bước 5: Invalidate Caches và Rebuild
1. **File → Invalidate Caches...**
2. Chọn tất cả options:
   - ✅ Clear file system cache and Local History
   - ✅ Clear downloaded shared indexes
   - ✅ Clear VCS Log caches and indexes
3. Click **Invalidate and Restart**
4. Sau khi restart:
   - **Build → Rebuild Project**

### Bước 6: Reimport Maven Project
1. Mở **Maven** tool window (View → Tool Windows → Maven)
2. Click icon **Reload All Maven Projects** (mũi tên xoay tròn)

## Nếu vẫn không được

### Cách 1: Xóa và Reimport Module
1. **File → Project Structure → Modules**
2. Chọn module **Family-Medical-Management**
3. Click **-** để xóa module
4. Click **+ → Import Module**
5. Chọn thư mục project
6. Chọn **Import module from external model → Maven**
7. Click **Next** và **Finish**

### Cách 2: Xóa .idea folder (Cẩn thận!)
1. Đóng IntelliJ IDEA
2. Xóa folder `.idea` trong project root
3. Mở lại project trong IntelliJ
4. IntelliJ sẽ tự động tạo lại `.idea` folder
5. Cấu hình lại SDK như các bước trên

## Kiểm tra JDK đã cài
Chạy trong terminal:
```powershell
java --version
```

Nếu hiển thị Java 25, thì JDK đã cài đúng.

## Lưu ý
- Project đang sử dụng **Java 25** (theo `pom.xml`)
- IntelliJ có thể không hỗ trợ Language Level 25, nên có thể chọn **23** (version cao nhất có sẵn)
- Maven sẽ vẫn compile với Java 25, chỉ cần IntelliJ nhận diện được SDK


