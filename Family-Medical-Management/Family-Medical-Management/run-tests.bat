@echo off
REM Script để chạy Maven Tests với Maven Wrapper
REM Sử dụng: run-tests.bat

echo ========================================
echo   Chạy Maven Tests với Maven Wrapper
echo ========================================
echo.

REM Kiểm tra xem có mvnw.cmd không
if not exist "mvnw.cmd" (
    echo ❌ Không tìm thấy mvnw.cmd trong thư mục hiện tại!
    echo    Đảm bảo bạn đang ở thư mục: Family-Medical-Management
    exit /b 1
)

echo ✅ Tìm thấy Maven Wrapper
echo.

REM Chạy tests
echo 🧪 Đang chạy tests...
call mvnw.cmd clean test

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Tests đã chạy thành công!
    echo.
    echo 📊 Đang tạo coverage report...
    call mvnw.cmd jacoco:report
    
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo ✅ Coverage report đã được tạo!
        echo.
        echo 📄 Xem report tại: target\site\jacoco\index.html
        echo.
        echo Để mở report, chạy lệnh:
        echo   start target\site\jacoco\index.html
    )
) else (
    echo.
    echo ❌ Tests thất bại!
    exit /b 1
)


