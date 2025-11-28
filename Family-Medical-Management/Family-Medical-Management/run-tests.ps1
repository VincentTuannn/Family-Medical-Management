# Script để chạy Maven Tests với Maven Wrapper
# Sử dụng: .\run-tests.ps1

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Chạy Maven Tests với Maven Wrapper" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Kiểm tra xem có mvnw.cmd không
if (-not (Test-Path "mvnw.cmd")) {
    Write-Host "❌ Không tìm thấy mvnw.cmd trong thư mục hiện tại!" -ForegroundColor Red
    Write-Host "   Đảm bảo bạn đang ở thư mục: Family-Medical-Management" -ForegroundColor Yellow
    exit 1
}

Write-Host "✅ Tìm thấy Maven Wrapper" -ForegroundColor Green
Write-Host ""

# Chạy tests
Write-Host "🧪 Đang chạy tests..." -ForegroundColor Yellow
& .\mvnw.cmd clean test

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✅ Tests đã chạy thành công!" -ForegroundColor Green
    Write-Host ""
    Write-Host "📊 Đang tạo coverage report..." -ForegroundColor Yellow
    & .\mvnw.cmd jacoco:report
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "✅ Coverage report đã được tạo!" -ForegroundColor Green
        Write-Host ""
        Write-Host "📄 Xem report tại: target\site\jacoco\index.html" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "Để mở report, chạy lệnh:" -ForegroundColor Yellow
        Write-Host "  Start-Process target\site\jacoco\index.html" -ForegroundColor White
    }
} else {
    Write-Host ""
    Write-Host "❌ Tests thất bại!" -ForegroundColor Red
    exit 1
}


