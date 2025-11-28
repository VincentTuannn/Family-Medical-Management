# Cấu Hình Java 25 với Language Level 23

## Vấn Đề

- Project cần **Java 25**
- IntelliJ chỉ hỗ trợ **Language level đến 23**
- Không muốn downgrade project xuống Java 21

## Giải Pháp

### Cấu Hình IntelliJ

1. **File → Project Structure** (Ctrl+Alt+Shift+S)
2. **Project Settings → Project**
   - **SDK:** Chọn **Java 25** (openjdk-25 25.0.1)
   - **Language level:** Chọn **`23`** (mức cao nhất có sẵn)
3. Click **Apply** và **OK**

### Cấu Hình Module

1. **Project Settings → Modules**
2. Chọn module `Family-Medical-Management`
3. Tab **Dependencies**
   - **Module SDK:** Chọn **Java 25**
4. Tab **Sources**
   - **Language level:** Chọn **`23`** hoặc **`Project default`**
5. Click **Apply** và **OK**

### Cấu Hình Maven Runner

1. **File → Settings** (Ctrl+Alt+S)
2. **Build, Execution, Deployment → Build Tools → Maven → Runner**
3. **JRE:** Chọn **Java 25**
4. Click **Apply** và **OK**

## Giải Thích

- **SDK = Java 25:** IntelliJ sẽ dùng Java 25 để compile (đúng)
- **Language level = 23:** Chỉ là giới hạn syntax checking trong IDE
- **Maven compile:** Vẫn dùng Java 25 (từ `pom.xml` và Maven Runner)

**Kết quả:**
- ✅ Code được compile bằng **Java 25** (từ Maven)
- ✅ IntelliJ syntax checking dùng **level 23** (không ảnh hưởng compile)
- ✅ Project vẫn ở **Java 25**

## Sau Khi Cấu Hình

1. **File → Invalidate Caches...** → **Invalidate and Restart**
2. **Maven → Reload All Maven Projects**
3. **Build → Rebuild Project**

## Kiểm Tra

### Trong IntelliJ Terminal:

```powershell
java -version
```

Kết quả: `java version "25.0.1"`

### Kiểm Tra Maven Compile:

```powershell
.\mvnw.cmd clean compile
```

Kết quả: `BUILD SUCCESS` với Java 25

## Lưu Ý

- **Language level 23** chỉ ảnh hưởng syntax checking trong IDE
- **Maven compile** vẫn dùng **Java 25** (từ `pom.xml`)
- Code thực tế vẫn được compile bằng **Java 25**

## Nếu Muốn Language Level 25

Cần **IntelliJ IDEA 2024.3** hoặc mới hơn:
1. **Help → Check for Updates**
2. Update lên version mới nhất
3. Sau đó sẽ có Language level 25


