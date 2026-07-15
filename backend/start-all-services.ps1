# Start all microservices script
# Each service will start in a new PowerShell window

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Starting All Microservices" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if project is compiled
if (-not (Test-Path "user-service/target/user-service-0.0.1-SNAPSHOT.jar")) {
    Write-Host "Warning: Project not compiled, compiling now..." -ForegroundColor Yellow
    mvn clean package -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error: Compilation failed, please fix errors first" -ForegroundColor Red
        exit 1
    }
}

$services = @(
    @{Name="user-service"; Port=7081; JAR="user-service/target/user-service-0.0.1-SNAPSHOT.jar"},
    @{Name="content-service"; Port=7082; JAR="content-service/target/content-service-0.0.1-SNAPSHOT.jar"},
    @{Name="transaction-service"; Port=7083; JAR="transaction-service/target/transaction-service-0.0.1-SNAPSHOT.jar"},
    @{Name="admin-service"; Port=7084; JAR="admin-service/target/admin-service-0.0.1-SNAPSHOT.jar"},
    @{Name="notification-service"; Port=7085; JAR="notification-service/target/notification-service-0.0.1-SNAPSHOT.jar"},
    @{Name="api-gateway"; Port=7080; JAR="api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar"}
)

Write-Host "Starting services..." -ForegroundColor Yellow
Write-Host ""

foreach ($service in $services) {
    if (Test-Path $service.JAR) {
        Write-Host "Starting $($service.Name) (Port $($service.Port))..." -ForegroundColor Cyan
        $jarPath = Resolve-Path $service.JAR
        Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD'; Write-Host '$($service.Name) - Port $($service.Port)' -ForegroundColor Green; java -jar '$jarPath'"
        Start-Sleep -Seconds 2
    } else {
        Write-Host "Error: JAR file not found for $($service.Name): $($service.JAR)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "All services started!" -ForegroundColor Green
Write-Host "Each service runs in a separate PowerShell window" -ForegroundColor Yellow
Write-Host ""
Write-Host "Wait for services to start, then run test-services.ps1 to test" -ForegroundColor Cyan
Write-Host ""
