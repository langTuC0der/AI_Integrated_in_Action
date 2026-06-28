# Helper Script to deploy Rikkei AI Course Hub to a new GitHub Repository
$ErrorActionPreference = "Stop"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "   RIKKEI AI COURSE HUB - DEPLOYMENT HELPER" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Prompt for GitHub Repo URL
$repoUrl = Read-Host "Nhập link HTTPS repository GitHub mới (ví dụ: https://github.com/username/repo-name.git)"
$repoUrl = $repoUrl.Trim()

if (-not $repoUrl) {
    Write-Host "Lỗi: Link repository không được để trống!" -ForegroundColor Red
    Exit
}

Write-Host ""
Write-Host "Đang cấu hình remote và push lên $repoUrl..." -ForegroundColor Yellow

try {
    # Check if remote 'origin' already exists, if so, remove it
    $remoteCheck = git remote get-url origin 2>$null
    if ($remoteCheck) {
        git remote remove origin
    }

    # Add new remote
    git remote add origin $repoUrl

    # Push to new repository
    Write-Host "Đang push mã nguồn lên nhánh 'main'..." -ForegroundColor Yellow
    git push -u origin main

    Write-Host ""
    Write-Host "==========================================================" -ForegroundColor Green
    Write-Host " ĐÃ PUSH THÀNH CÔNG LÊN GITHUB!" -ForegroundColor Green
    Write-Host "==========================================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Các bước tiếp theo để kích hoạt trang web học liệu:" -ForegroundColor White
    Write-Host "1. Truy cập vào link repository của bạn trên trình duyệt." -ForegroundColor White
    Write-Host "2. Vào phần 'Settings' -> 'Pages' (ở menu bên trái)." -ForegroundColor White
    Write-Host "3. Tại 'Build and deployment' -> 'Source', chọn 'Deploy from a branch'." -ForegroundColor White
    Write-Host "4. Chọn nhánh 'main' và thư mục gốc '/ (root)', rồi bấm 'Save'." -ForegroundColor White
    Write-Host "5. Đợi 1-2 phút và truy cập vào link GitHub Pages được hiển thị!" -ForegroundColor Green
}
catch {
    Write-Host "Có lỗi xảy ra: $_" -ForegroundColor Red
}
