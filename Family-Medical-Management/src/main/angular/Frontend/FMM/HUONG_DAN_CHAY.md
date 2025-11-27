# Hướng Dẫn Chạy Frontend Angular

## Yêu Cầu Hệ Thống

Trước khi chạy ứng dụng, bạn cần cài đặt:
- **Node.js** (phiên bản 18 trở lên)
- **npm** (thường đi kèm với Node.js)

Kiểm tra phiên bản:
```bash
node --version
npm --version
```

## Các Bước Chạy Ứng Dụng

### Bước 1: Di chuyển vào thư mục Frontend

Mở Terminal/PowerShell và di chuyển vào thư mục chứa dự án Angular:

```bash
cd Family-Medical-Management\src\main\angular\Frontend\FMM
```

### Bước 2: Cài đặt Dependencies

Cài đặt tất cả các package cần thiết:

```bash
npm install
```

Lệnh này sẽ tải và cài đặt tất cả các thư viện được liệt kê trong `package.json`.

### Bước 3: Chạy Development Server

Sau khi cài đặt xong, chạy ứng dụng:

```bash
npm start
```

Hoặc:

```bash
ng serve
```

### Bước 4: Truy cập Ứng Dụng

Sau khi server khởi động thành công, bạn sẽ thấy thông báo tương tự:

```
✔ Browser application bundle generation complete.
Initial chunk files   | Names         |  Size
vendor.js             | vendor        |  ...
polyfills.js          | polyfills     |  ...
main.js               | main          |  ...

** Angular Live Development Server is listening on localhost:4200 **
```

Mở trình duyệt và truy cập:
```
http://localhost:4200
```

## Các Lệnh Khác

### Build Production

Để build ứng dụng cho môi trường production:

```bash
npm run build
```

Kết quả sẽ được lưu trong thư mục `dist/`.

### Chạy Tests

Để chạy unit tests:

```bash
npm test
```

### Build với Watch Mode

Để build và tự động rebuild khi có thay đổi:

```bash
npm run watch
```

## Xử Lý Lỗi Thường Gặp

### Lỗi: "ng: command not found"

Nếu gặp lỗi này, có thể Angular CLI chưa được cài đặt globally. Chạy:

```bash
npm install -g @angular/cli
```

### Lỗi: Port 4200 đã được sử dụng

Nếu port 4200 đã bị chiếm, bạn có thể chạy trên port khác:

```bash
ng serve --port 4201
```

### Lỗi: Module không tìm thấy

Xóa `node_modules` và `package-lock.json`, sau đó cài đặt lại:

```bash
rm -rf node_modules package-lock.json
npm install
```

(Trên Windows PowerShell, sử dụng: `Remove-Item -Recurse -Force node_modules, package-lock.json`)

## Lưu Ý

- Ứng dụng sẽ tự động reload khi bạn thay đổi code
- Đảm bảo backend đang chạy nếu frontend cần kết nối API
- Kiểm tra file `environment.ts` để cấu hình đúng API endpoint


