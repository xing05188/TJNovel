# Start all microservices containers
# This script ensures only project containers are started

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  Starting Microservices" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Check if Docker is running
Write-Host "Checking Docker..." -ForegroundColor Yellow
try {
    docker ps | Out-Null
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error: Docker is not running. Please start Docker Desktop." -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "Error: Docker is not running. Please start Docker Desktop." -ForegroundColor Red
    exit 1
}
Write-Host "Docker is running." -ForegroundColor Green
Write-Host ""

# Check for port conflicts
Write-Host "Checking for port conflicts..." -ForegroundColor Yellow
$ports = @(7080, 7081, 7082, 7083, 7084, 7085)
$conflicts = @()

foreach ($port in $ports) {
    $result = netstat -ano | findstr ":$port " | findstr "LISTENING"
    if ($result) {
        $conflicts += $port
    }
}

if ($conflicts.Count -gt 0) {
    Write-Host "Warning: Port conflicts detected on ports: $($conflicts -join ', ')" -ForegroundColor Yellow
    Write-Host "Checking if conflicts are from other projects..." -ForegroundColor Yellow
    
    # Check if conflicts are from other Docker containers
    $otherContainers = docker ps --format "{{.Names}}\t{{.Ports}}" | Select-String -Pattern "7080|7081|7082|7083|7084|7085" | Where-Object { $_ -notmatch "api-gateway|user-service|content-service|transaction-service|admin-service|notification-service" }
    
    if ($otherContainers) {
        Write-Host ""
        Write-Host "Found other containers using project ports:" -ForegroundColor Yellow
        $otherContainers | ForEach-Object { Write-Host "  $_" -ForegroundColor Gray }
        Write-Host ""
        $response = Read-Host "Do you want to stop these containers? (y/n)"
        if ($response -eq 'y' -or $response -eq 'Y') {
            # Extract container names and stop them
            $containerNames = $otherContainers | ForEach-Object {
                ($_ -split "`t")[0]
            }
            foreach ($name in $containerNames) {
                Write-Host "Stopping container: $name" -ForegroundColor Yellow
                docker stop $name | Out-Null
            }
            Write-Host "Other containers stopped." -ForegroundColor Green
        } else {
            Write-Host "Please stop conflicting containers manually and try again." -ForegroundColor Yellow
            exit 1
        }
    }
}
Write-Host ""

# Start services using docker-compose (project name is set in docker-compose.yml)
Write-Host "Starting services..." -ForegroundColor Yellow
docker-compose up -d

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "All services started successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Service status:" -ForegroundColor Cyan
    docker-compose ps
    Write-Host ""
    Write-Host "To view logs: docker-compose logs -f" -ForegroundColor Gray
    Write-Host "To stop services: .\stop-services.ps1" -ForegroundColor Gray
} else {
    Write-Host ""
    Write-Host "Error: Failed to start some services" -ForegroundColor Red
    Write-Host "Check logs with: docker-compose logs" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

