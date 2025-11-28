# Hướng Dẫn Sửa Lỗi IntelliJ IDEA

## Vấn Đề

IntelliJ IDEA hiển thị lỗi "cannot access" và "cannot find symbol" nhưng:
- ✅ Maven compile thành công
- ✅ Cursor không hiển thị lỗi
- ✅ Tất cả classes đều tồn tại

**Nguyên nhân:** IntelliJ cache cũ hoặc chưa sync với Maven project.

## Giải Pháp

### Bước 1: Invalidate Caches và Restart

1. **File → Invalidate Caches...**
2. Chọn:
   - ✅ Clear file system cache and Local History
   - ✅ Clear downloaded shared indexes
   - ✅ Clear VCS Log caches and indexes
3. Click **Invalidate and Restart**
4. Đợi IntelliJ restart và re-index project

### Bước 2: Reimport Maven Project

1. **Maven tool window** (View → Tool Windows → Maven)
2. Click **Reload All Maven Projects** (icon refresh)
3. Hoặc: Right-click `pom.xml` → **Maven → Reload Project**

### Bước 3: Enable Lombok Annotation Processing

1. **File → Settings** (Ctrl+Alt+S)
2. **Build, Execution, Deployment → Compiler → Annotation Processors**
3. ✅ Enable annotation processing
4. Click **Apply** và **OK**

### Bước 4: Sync Project với Maven

1. **File → Project Structure** (Ctrl+Alt+Shift+S)
2. **Project Settings → Project**
   - **SDK:** Java 25
   - **Language level:** 25
3. **Project Settings → Modules**
   - Đảm bảo tất cả modules được import
4. Click **Apply** và **OK**

### Bước 5: Rebuild Project

1. **Build → Rebuild Project**
2. Đợi IntelliJ rebuild toàn bộ project

### Bước 6: Kiểm Tra Java Version

1. **File → Settings → Build, Execution, Deployment → Build Tools → Maven → Runner**
2. **JRE:** Chọn Java 25
3. Click **Apply** và **OK**

## Nếu Vẫn Còn Lỗi

### Option 1: Xóa .idea folder (Cẩn thận!)

```powershell
# Backup trước
Copy-Item -Path ".idea" -Destination ".idea.backup" -Recurse

# Xóa .idea
Remove-Item -Path ".idea" -Recurse -Force

# Mở lại project trong IntelliJ
```

### Option 2: Tạo Project Mới

1. **File → Close Project**
2. **File → Open**
3. Chọn folder `Family-Medical-Management`
4. Chọn **Open as Project**
5. IntelliJ sẽ tự động import Maven project

## Tại Sao Cursor Không Hiển Thị Lỗi?

- **Cursor/VS Code** sử dụng Java Language Server (Eclipse JDT hoặc Red Hat)
- Language Server dựa vào Maven compile (đúng)
- **IntelliJ** sử dụng internal compiler riêng (có thể khác với Maven)
- IntelliJ cache có thể bị lỗi sau nhiều lần thay đổi Java version

## Kết Luận

**Lỗi trong IntelliJ là FALSE POSITIVE** - code thực sự không có lỗi.

Nếu Maven compile thành công, bạn có thể:
- ✅ Bỏ qua lỗi trong IntelliJ (nếu không ảnh hưởng)
- ✅ Hoặc làm theo các bước trên để fix IntelliJ


