# LinkA Mobile Development Environment Starter
# Optimized for mobile device testing

Write-Host "=========================================" -ForegroundColor Green
Write-Host " LinkA Mobile Development Environment" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Green
Write-Host ""

Write-Host "[1/3] Starting Backend Server..." -ForegroundColor Yellow
Set-Location "Linka-Backend"
Write-Host "Starting Spring Boot application on port 8080..." -ForegroundColor Cyan

# Start backend in background
$backendProcess = Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run", "-Dspring-boot.run.profiles=dev" -PassThru -WindowStyle Normal

# Wait a bit for backend to initialize
Start-Sleep -Seconds 10

Write-Host ""
Write-Host "[2/3] Starting Frontend Server..." -ForegroundColor Yellow
Set-Location "../Linka-Frontend"

# Check if node_modules exists
if (!(Test-Path "node_modules")) {
    Write-Host "Installing dependencies..." -ForegroundColor Cyan
    npm install
}

Write-Host "Starting Vite development server on port 5173..." -ForegroundColor Cyan
Write-Host "Access URL: http://$(ifconfig.me 2>/dev/null || hostname):5173" -ForegroundColor Green

# Start frontend
npm run dev