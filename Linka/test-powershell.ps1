# LinkA Development Environment - PowerShell Script (Test Version)
# Simplified version for testing PowerShell configuration

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet("health", "status", "stop", "info")]
    [string]$Mode = "info"
)

# Color output functions
function Write-ColorOutput {
    param([string]$Message, [string]$Color = "White")
    Write-Host $Message -ForegroundColor $Color
}

function Write-Success {
    param([string]$Message)
    Write-Host ("✓ " + $Message) -ForegroundColor Green
}

function Write-Info {
    param([string]$Message)
    Write-Host ("ℹ " + $Message) -ForegroundColor Cyan
}

# Header function
function Show-Header {
    Clear-Host
    Write-ColorOutput "=========================================" "Green"
    Write-ColorOutput " LinkA Development Environment (PowerShell)" "Green"
    Write-ColorOutput "=========================================" "Green"
    Write-ColorOutput ""
}

# Check prerequisites
function Test-Prerequisites {
    Write-Info "Checking prerequisites..."
    $prerequisites = @{
        "Java" = { java -version 2>$null }
        "Maven" = { mvn -version 2>$null }
        "Node.js" = { node -version 2>$null }
        "npm" = { npm -version 2>$null }
    }
    
    $missing = @()
    
    foreach ($tool in $prerequisites.Keys) {
        try {
            & $prerequisites[$tool] | Out-Null
            Write-Success ("$tool is available")
        }
        catch {
            Write-ColorOutput "$tool: Not available" "Red"
            $missing += $tool
        }
    }
    
    if ($missing.Count -gt 0) {
        Write-ColorOutput ("Missing prerequisites: " + ($missing -join ', ')) "Red"
    } else {
        Write-Success "All prerequisites are available"
    }
}

# Show status
function Show-Status {
    Write-ColorOutput "Service Status:" "Cyan"
    Write-ColorOutput "===============" "Cyan"
    
    # Check background jobs
    $jobs = Get-Job
    if ($jobs) {
        Write-Info "Background Jobs:"
        foreach ($job in $jobs) {
            $status = $job.State
            $color = if ($job.State -eq 'Running') { 'Green' } else { 'Yellow' }
            Write-Host ("  - " + $job.Name + ": " + $status) -ForegroundColor $color
        }
    } else {
        Write-Info "No background jobs running"
    }
    
    Write-ColorOutput "" "White"
    
    # Check port availability
    $ports = @(8080, 5173)
    foreach ($port in $ports) {
        try {
            $connection = Get-NetTCPConnection -LocalPort $port -ErrorAction Stop | Select-Object -First 1
            if ($connection) {
                $processId = $connection.OwningProcess
                $processName = "Process $processId"
                Write-Host ("  Port " + $port + ": In use by " + $processName) -ForegroundColor Yellow
            }
        }
        catch {
            Write-Host ("  Port " + $port + ": Available") -ForegroundColor Green
        }
    }
}

# Show project info
function Show-Info {
    Write-ColorOutput "LinkA Project Information:" "Cyan"
    Write-ColorOutput "==========================" "Cyan"
    
    Write-Info "Current directory: $(Get-Location)"
    
    if (Test-Path "Linka-Backend") {
        Write-Success "Backend directory found"
    } else {
        Write-ColorOutput "Backend directory not found" "Yellow"
    }
    
    if (Test-Path "Linka-Frontend") {
        Write-Success "Frontend directory found"
    } else {
        Write-ColorOutput "Frontend directory not found" "Yellow"
    }
    
    # Show VS Code settings
    if (Test-Path ".vscode/settings.json") {
        Write-Success "VS Code settings configured"
    } else {
        Write-ColorOutput "VS Code settings not found" "Yellow"
    }
    
    # Show PowerShell script
    if (Test-Path "Linka/start-dev.ps1") {
        Write-Success "PowerShell development script found"
    } else {
        Write-ColorOutput "PowerShell development script not found" "Yellow"
    }
}

# Main function
function Main {
    Show-Header
    
    switch ($Mode) {
        "health" {
            Test-Prerequisites
        }
        "status" {
            Show-Status
        }
        "stop" {
            Write-Info "Stopping all services..."
            Get-Job | Stop-Job -Force
            Get-Job | Remove-Job -Force
            Write-Success "All services stopped"
        }
        "info" {
        default {
            Show-Info
            Write-ColorOutput "" "White"
            Write-Success "PowerShell configuration is working!"
            Write-ColorOutput "" "White"
            Write-ColorOutput "Available commands:" "Cyan"
            Write-ColorOutput "  .\Linka\start-dev.ps1 -Mode health  - Check prerequisites" "White"
            Write-ColorOutput "  .\Linka\start-dev.ps1 -Mode status  - Show service status" "White"
            Write-ColorOutput "  .\Linka\start-dev.ps1 -Mode stop    - Stop all services" "White"
            Write-ColorOutput "  .\Linka\start-dev.ps1 -Mode info    - Show this information" "White"
        }
    }
}

# Execute main function
Main