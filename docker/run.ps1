# Script PowerShell para rodar o projeto com Docker

Write-Host "Building application..." -ForegroundColor Cyan
Set-Location ..
mvn clean package -DskipTests

Write-Host "`nBuilding Docker image..." -ForegroundColor Cyan
docker build -t coupon-api:latest -f docker/Dockerfile .

Write-Host "`nStarting containers..." -ForegroundColor Cyan
Set-Location docker
docker-compose up -d

Write-Host "`nWaiting for application to be ready..." -ForegroundColor Yellow

$maxAttempts = 30
$attempt = 0
$ready = $false

while ($attempt -lt $maxAttempts) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing -TimeoutSec 2 -ErrorAction SilentlyContinue
        if ($response.StatusCode -eq 200) {
            Write-Host "Application is ready!" -ForegroundColor Green
            $ready = $true
            break
        }
    }
    catch {
        # Ignora erros e continua tentando
    }

    $attempt++
    Write-Host "Waiting... ($attempt/$maxAttempts)"
    Start-Sleep -Seconds 2
}

if (-not $ready) {
    Write-Host "`nWarning: Application took too long to start" -ForegroundColor Red
    Write-Host "Check logs with: docker-compose logs -f"
}
else {
    Write-Host "`nOpening Swagger UI..." -ForegroundColor Cyan
    Start-Process "http://localhost:8080/swagger-ui.html"
}

Write-Host "`nApplication URLs:" -ForegroundColor Green
Write-Host "  Swagger: http://localhost:8080/swagger-ui.html"
Write-Host "  API: http://localhost:8080"
Write-Host "  H2 Console: http://localhost:8080/h2-console"
Write-Host "`nRun 'docker-compose logs -f' to see logs"
