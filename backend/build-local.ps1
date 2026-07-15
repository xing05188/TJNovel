# PowerShell script to build Docker images locally

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  Building Docker Images Locally" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Compile project (跳过 clean，避免运行中服务锁定 JAR)
Write-Host "[1/2] Compiling Maven project (package only, skip clean)..." -ForegroundColor Yellow
mvn package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "Error: Compilation failed!" -ForegroundColor Red
    Write-Host "Hint: 如果 JAR 被运行中的服务锁定，请先停止 java 进程 (Get-Process java | Stop-Process -Force)" -ForegroundColor Yellow
    exit 1
}

Write-Host "OK: Compilation successful" -ForegroundColor Green
Write-Host ""

# Build Docker images
Write-Host "[2/2] Building Docker images..." -ForegroundColor Yellow
Write-Host ""

# Check if base image exists locally, if not try to pull it
Write-Host "Checking for base image eclipse-temurin:17-jre..." -ForegroundColor Yellow
$baseImageExists = docker images eclipse-temurin:17-jre --format "{{.Repository}}:{{.Tag}}" | Select-String "eclipse-temurin:17-jre"
if (-not $baseImageExists) {
    Write-Host "Base image not found locally. Attempting to pull..." -ForegroundColor Yellow
    docker pull eclipse-temurin:17-jre
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Warning: Failed to pull base image. This might be a network issue." -ForegroundColor Yellow
        Write-Host "Troubleshooting:" -ForegroundColor Yellow
        Write-Host "  1. Check your internet connection" -ForegroundColor Yellow
        Write-Host "  2. Check Docker Desktop is running" -ForegroundColor Yellow
        Write-Host "  3. Try: docker pull eclipse-temurin:17-jre" -ForegroundColor Yellow
        Write-Host "  4. Check proxy/firewall settings if behind corporate network" -ForegroundColor Yellow
    }
} else {
    Write-Host "Base image found locally." -ForegroundColor Green
}
Write-Host ""

# Build each service with error checking
$services = @(
    @{Name="api-gateway"; Path="./api-gateway"},
    @{Name="user-service"; Path="./user-service"},
    @{Name="content-service"; Path="./content-service"},
    @{Name="transaction-service"; Path="./transaction-service"},
    @{Name="admin-service"; Path="./admin-service"},
    @{Name="notification-service"; Path="./notification-service"}
)

$buildFailed = $false
foreach ($service in $services) {
    Write-Host "Building $($service.Name)..." -ForegroundColor Yellow
    docker build -t "ournovel/$($service.Name):local" $service.Path
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error: Failed to build $($service.Name)!" -ForegroundColor Red
        $buildFailed = $true
    } else {
        Write-Host "OK: $($service.Name) image built" -ForegroundColor Green
    }
    Write-Host ""
}

if ($buildFailed) {
    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Red
    Write-Host "  Build completed with errors!" -ForegroundColor Red
    Write-Host "==========================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Some images failed to build. Please check the errors above." -ForegroundColor Yellow
    exit 1
} else {
    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Cyan
    Write-Host "  All images built successfully!" -ForegroundColor Cyan
    Write-Host "==========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Image list:" -ForegroundColor Yellow
    docker images | Select-String "ournovel"
    Write-Host ""
    Write-Host "Start services: docker-compose up -d" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Cyan
    Write-Host "  Swagger UI Access URLs" -ForegroundColor Cyan
    Write-Host "==========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Direct access to services:" -ForegroundColor Yellow
    Write-Host "  User Service:        http://localhost:7081/swagger-ui.html" -ForegroundColor White
    Write-Host "  Content Service:     http://localhost:7082/swagger-ui.html" -ForegroundColor White
    Write-Host "  Transaction Service: http://localhost:7083/swagger-ui.html" -ForegroundColor White
    Write-Host "  Admin Service:       http://localhost:7084/swagger-ui.html" -ForegroundColor White
    Write-Host "  Notification Service: http://localhost:7085/swagger-ui.html" -ForegroundColor White
    Write-Host "  API Gateway:         http://localhost:7080/swagger-ui.html" -ForegroundColor White
    Write-Host ""
}
