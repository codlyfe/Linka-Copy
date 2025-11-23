Write-Host "LinkA PowerShell Test" -ForegroundColor Green
Write-Host "==================" -ForegroundColor Green

Write-Host ""
Write-Host "PowerShell Version:" -ForegroundColor Cyan
$PSVersionTable.PSVersion

Write-Host ""
Write-Host "Current Directory:" -ForegroundColor Cyan
Get-Location

Write-Host ""
Write-Host "VS Code Configuration:" -ForegroundColor Cyan
if (Test-Path ".vscode/settings.json") {
    Write-Host "  Settings file: Found" -ForegroundColor Green
} else {
    Write-Host "  Settings file: Missing" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "PowerShell Scripts:" -ForegroundColor Cyan
if (Test-Path "Linka/start-dev.ps1") {
    Write-Host "  Main script: Found" -ForegroundColor Green
} else {
    Write-Host "  Main script: Missing" -ForegroundColor Yellow
}

if (Test-Path "Linka/test-powershell.ps1") {
    Write-Host "  Test script: Found" -ForegroundColor Green
} else {
    Write-Host "  Test script: Missing" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "PowerShell configuration test completed!" -ForegroundColor Green