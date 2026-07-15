# Display Swagger UI access URLs

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

Write-Host "Note: Make sure services are running before accessing Swagger UI" -ForegroundColor Gray
Write-Host ""

