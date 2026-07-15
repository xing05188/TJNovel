# Stop all microservices containers
# Only stops containers from this project

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  Stopping All Microservices" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Stopping project containers..." -ForegroundColor Yellow
docker-compose down

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "All project services stopped successfully!" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "Error: Failed to stop some services" -ForegroundColor Red
}

Write-Host ""


