# 📊 Hướng Dẫn Chạy Báo Cáo Coverage

## ✅ Kết Quả Coverage

- **Package được kiểm tra**: `Backend.FMM.Service`
- **Coverage đạt được**: **70%** ✅
- **Số lượng tests**: 131 tests - Tất cả đã pass
- **Trạng thái**: ✅ All coverage checks have been met

## 🚀 Cách Chạy Báo Cáo Coverage

### 1. Chạy Test và Tạo Báo Cáo

```powershell
cd Family-Medical-Management
.\mvnw.cmd clean test jacoco:report
```

### 2. Xem Báo Cáo HTML

Sau khi chạy xong, báo cáo sẽ được tạo tại:
```
target/site/jacoco/index.html
```

**Mở báo cáo:**
- Mở file `target/site/jacoco/index.html` trong trình duyệt
- Hoặc chạy lệnh PowerShell:
```powershell
Start-Process "target\site\jacoco\index.html"
```

### 3. Xem Chi Tiết Coverage

1. **Xem tổng quan**: Mở `index.html` để xem coverage của tất cả packages
2. **Xem package Service**: Click vào `Backend.FMM.Service` để xem từng class
3. **Xem từng class**: Click vào tên class để xem source code và coverage từng dòng
4. **Màu sắc**:
   - 🟢 **Màu xanh** = Code đã được test
   - 🔴 **Màu đỏ** = Code chưa được test

## 📈 Thông Tin Coverage Chi Tiết

### Backend.FMM.Service Package
- **Instructions Coverage**: 70%
- **Branches Coverage**: 35%
- **Lines Coverage**: 70%
- **Methods Coverage**: 96% (91/95 methods)
- **Classes Coverage**: 94% (16/17 classes)

### Các Packages Khác (Không yêu cầu 70%)
- `Backend.FMM.Controller`: 56%
- `Backend.FMM.Entity`: 49%
- `Backend.FMM.Configure`: 0% (Configuration classes)
- `Backend.FMM.Security`: 0% (Security classes)
- `Backend.FMM.Mapper`: 0% (Mapper classes)
- `Backend.FMM.Exception`: 0% (Exception classes)

## 🔍 Kiểm Tra Coverage Trong Build

JaCoCo sẽ tự động kiểm tra coverage khi chạy `mvn test`:

```powershell
.\mvnw.cmd test
```

Nếu coverage < 70% cho package `Backend.FMM.Service`, build sẽ **FAIL** với thông báo:
```
[ERROR] Rule violated for bundle Family-Medical-Management: instructions covered ratio is 0.XX, but expected minimum is 0.70
```

## 📝 Cấu Hình JaCoCo

Cấu hình trong `pom.xml`:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.13</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>check</id>
            <phase>test</phase>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>PACKAGE</element>
                        <includes>
                            <include>Backend.FMM.Service</include>
                        </includes>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.70</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## ✅ Kết Luận

✅ **Coverage đã đạt 70% cho package `Backend.FMM.Service`**
✅ **Tất cả 131 tests đã pass**
✅ **Build thành công với coverage check**

Báo cáo HTML đã được tạo và có thể mở trong trình duyệt để xem chi tiết.

