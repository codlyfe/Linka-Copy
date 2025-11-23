# LinkA Development Environment - PowerShell Script
# Unified script for all development operations
# This script replaces start-dev.bat, start-dev.sh, and start-mobile.ps1

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet("full", "backend", "frontend", "mobile", "stop", "status", "clean", "test", "build", "health")]
    [string]$Mode = "full",
    
    [Parameter(Mandatory=$false)]
    [switch]$Verbose,
    
    [Parameter(Mandatory=$false)]
    [switch]$AutoInstall,
    
    [Parameter(Mandatory=$false)]
    [switch]$NoPause
)

# Import required modules and set execution policy
Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

# Color output functions
function Write-ColorOutput {
    param([string]$Message, [string]$Color = "White")
    Write-Host $Message -ForegroundColor $Color
}

function Write-Success {
    param([string]$Message)
    Write-Host ("✓ " + $Message) -ForegroundColor Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host ("⚠ " + $Message) -ForegroundColor Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host ("✗ " + $Message) -ForegroundColor Red
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
    $prerequisites = @{
        "Java" = { java -version 2>$null }
        "Maven" = { mvn -version 2>$null }
        "Node.js" = { node -version 2>$null }
        "npm" = { npm -version 2>$null }
    }
    
    $missing = @()
    
    foreach ($tool in $prerequisites.Keys) {
        try {
            & $prerequisites[$tool]
            Write-Success ("$tool is available")
        }
        catch {
            Write-Error ("$tool is not installed or not in PATH")
            $missing += $tool
        }
    }
    
    if ($missing.Count -gt 0) {
        Write-Error ("Missing prerequisites: " + ($missing -join ', '))
        Write-Info "Please install the missing tools and try again"
        exit 1
    }
}

# Kill processes function
function Stop-Processes {
    param([string[]]$ProcessNames)
    
    foreach ($processName in $ProcessNames) {
        $processes = Get-Process -Name $processName -ErrorAction SilentlyContinue
        foreach ($process in $processes) {
            try {
                $process.CloseMainWindow()
                Start-Sleep -Seconds 2
                if (!$process.HasExited) {
                    $process.Kill()
                }
                Write-Success ("Stopped $processName process (PID: " + $process.Id + ")")
            }
            catch {
                Write-Warning ("Could not stop $processName process: " + $_)
            }
        }
    }
}

# Start backend server
function Start-Backend {
    Write-ColorOutput "[1/3] Starting Backend Server..." "Yellow"
    Set-Location "Linka-Backend"
    
    Write-Info "Starting Spring Boot application on port 8080..."
    Write-Info "Backend URL: http://localhost:8080"
    Write-Info "Health Check: http://localhost:8080/api/health"
    
    # Check if Maven wrapper exists
    if (Test-Path "mvnw.cmd") {
        $mvnCmd = ".\mvnw.cmd"
    } elseif (Test-Path "mvnw") {
        $mvnCmd = ".\mvnw"
    } else {
        $mvnCmd = "mvn"
    }
    
    # Start backend in background
    $scriptBlock = {
        param($mvnCmd)
        Set-Location "Linka-Backend"
        & $mvnCmd spring-boot:run -Dspring-boot.run.profiles=dev
    }
    
    $job = Start-Job -ScriptBlock $scriptBlock -ArgumentList $mvnCmd -Name "BackendServer"
    
    Write-Success ("Backend server started (Job ID: " + $job.Id + ")")
    Write-Info "Waiting for backend to initialize..."
    Start-Sleep -Seconds 15
    
    # Check if backend is healthy
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/health" -TimeoutSec 5
        Write-Success "Backend is healthy and ready"
        return $job
    }
    catch {
        Write-Warning "Backend may still be starting up. Check logs for details."
        return $job
    }
}

# Start frontend server
function Start-Frontend {
    Write-ColorOutput "[2/3] Starting Frontend Server..." "Yellow"
    Set-Location "Linka-Frontend"
    
    # Check if dependencies are installed
    if (!(Test-Path "node_modules") -or $AutoInstall) {
        if ($AutoInstall -or (Read-Host "Dependencies not found. Install now? (y/n)") -eq "y") {
            Write-Info "Installing dependencies..."
            npm install
            Write-Success "Dependencies installed"
        } else {
            Write-Error "Dependencies required. Run with -AutoInstall or install manually"
            return $null
        }
    }
    
    Write-Info "Starting Vite development server on port 5173..."
    Write-Info "Frontend URL: http://localhost:5173"
    
    # Start frontend in background
    $scriptBlock = {
        Set-Location "Linka-Frontend"
        npm run dev
    }
    
    $job = Start-Job -ScriptBlock $scriptBlock -Name "FrontendServer"
    
    Write-Success ("Frontend server started (Job ID: " + $job.Id + ")")
    Write-Info "Waiting for frontend to initialize..."
    Start-Sleep -Seconds 10
    
    return $job
}

# Mobile development mode
function Start-MobileMode {
    Write-ColorOutput "=========================================" "Green"
    Write-ColorOutput " Mobile Development Mode" "Green"
    Write-ColorOutput "=========================================" "Green"
    Write-ColorOutput ""
    
    $backendJob = Start-Backend
    
    Write-ColorOutput "" "Yellow"
    Write-ColorOutput "[2/3] Starting Frontend Server..." "Yellow"
    Set-Location "Linka-Frontend"
    
    if (!(Test-Path "node_modules") -or $AutoInstall) {
        if ($AutoInstall -or (Read-Host "Dependencies not found. Install now? (y/n)") -eq "y") {
            Write-Info "Installing dependencies..."
            npm install
            Write-Success "Dependencies installed"
        } else {
            Write-Error "Dependencies required. Run with -AutoInstall or install manually"
            return $null
        }
    }
    
    Write-Info "Starting Vite development server on port 5173..."
    
    # Get local IP for mobile access
    $localIP = (Get-NetIPAddress -AddressFamily IPv4 | Where-Object {$_.InterfaceAlias -notlike "*Loopback*" -and $_.InterfaceAlias -notlike "*vEthernet*"}).IPAddress[0]
    if ($localIP) {
        Write-Success ("Mobile Access URL: http://" + $localIP + ":5173")
    }
    
    $job = Start-Job -ScriptBlock {
        Set-Location "Linka-Frontend"
        npm run dev -- --host
    } -Name "MobileFrontend"
    
    Write-Success ("Mobile frontend started (Job ID: " + $job.Id + ")")
    return @($backendJob, $job)
}

# Stop all services
function Stop-AllServices {
    Write-ColorOutput "Stopping all services..." "Yellow"
    
    # Stop background jobs
    Get-Job | Stop-Job -Force
    Get-Job | Remove-Job -Force
    
    # Stop processes
    Stop-Processes @("java", "node", "mvn")
    
    Write-Success "All services stopped"
}

# Check service status
function Show-Status {
    Write-ColorOutput "Service Status:" "Cyan"
    Write-ColorOutput "===============" "Cyan"
    
    # Check background jobs
    $jobs = Get-Job
    if ($jobs) {
        Write-Info "Background Jobs:"
        $jobs | ForEach-Object {
            $status = if ($_.State -eq 'Running') { 'Running' } else { $_.State }
            $color = if ($_.State -eq 'Running') { 'Green' } else { 'Red' }
            Write-Host ("  - " + $_.Name + ": " + $status) -ForegroundColor $color
        }
    } else {
        Write-Info "No background jobs running"
    }
    
    Write-ColorOutput "" "White"
    
    # Check port availability
    $ports = @(8080, 5173)
    foreach ($port in $ports) {
        $process = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($process) {
            $processInfo = Get-Process -Id $process.OwningProcess -ErrorAction SilentlyContinue
            $processName = if ($processInfo) { $processInfo.ProcessName } else { "Unknown" }
            Write-Host ("  Port " + $port + ": In use by " + $processName + " (PID: " + $process.OwningProcess + ")") -ForegroundColor Yellow
        } else {
            Write-Host ("  Port " + $port + ": Available") -ForegroundColor Green
        }
    }
    
    # Check service health
    Write-ColorOutput "" "White"
    Write-Info "Service Health Checks:"
    
    try {
        $backendHealth = Invoke-RestMethod -Uri "http://localhost:8080/api/health" -TimeoutSec 3
        Write-Host "  Backend: Healthy" -ForegroundColor Green
    } catch {
        Write-Host "  Backend: Unhealthy or not responding" -ForegroundColor Red
    }
    
    try {
        $frontendHealth = Invoke-RestMethod -Uri "http://localhost:5173" -TimeoutSec 3
        Write-Host "  Frontend: Running" -ForegroundColor Green
    } catch {
        Write-Host "  Frontend: Not responding" -ForegroundColor Red
    }
}

# Clean build artifacts
function Invoke-Clean {
    Write-ColorOutput "Cleaning build artifacts..." "Yellow"
    
    # Clean backend
    if (Test-Path "Linka-Backend") {
        Set-Location "Linka-Backend"
        if (Test-Path "mvnw.cmd") {
            .\mvnw.cmd clean
        } elseif (Test-Path "mvnw") {
            .\mvnw clean
        } else {
            mvn clean
        }
        Write-Success "Backend cleaned"
    }
    
    # Clean frontend
    if (Test-Path "Linka-Frontend") {
        Set-Location "Linka-Frontend"
        if (Test-Path "node_modules") {
            Remove-Item -Recurse -Force "node_modules" -ErrorAction SilentlyContinue
            Write-Success "Frontend node_modules removed"
        }
        if (Test-Path "dist") {
            Remove-Item -Recurse -Force "dist" -ErrorAction SilentlyContinue
            Write-Success "Frontend dist removed"
        }
        if (Test-Path ".vite") {
            Remove-Item -Recurse -Force ".vite" -ErrorAction SilentlyContinue
            Write-Success "Frontend .vite cache cleared"
        }
    }
    
    Write-Success "Clean completed"
}

# Build projects
function Invoke-Build {
    Write-ColorOutput "Building projects..." "Yellow"
    
    # Build backend
    if (Test-Path "Linka-Backend") {
        Set-Location "Linka-Backend"
        if (Test-Path "mvnw.cmd") {
            .\mvnw.cmd clean package -DskipTests
        } elseif (Test-Path "mvnw") {
            .\mvnw clean package -DskipTests
        } else {
            mvn clean package -DskipTests
        }
        Write-Success "Backend built"
    }
    
    # Build frontend
    if (Test-Path "Linka-Frontend") {
        Set-Location "Linka-Frontend"
        npm run build
        Write-Success "Frontend built"
    }
    
    Write-Success "Build completed"
}

# Health check
function Invoke-HealthCheck {
    Write-ColorOutput "Comprehensive Health Check:" "Cyan"
    Write-ColorOutput "===========================" "Cyan"
    
    Test-Prerequisites
    Show-Status
}

# Main execution
function Main {
    Show-Header
    
    # Handle verbose mode
    if ($Verbose) {
        $VerbosePreference = "Continue"
    }
    
    # Ensure we're in the right directory
    if (!(Test-Path "Linka-Backend") -or !(Test-Path "Linka-Frontend")) {
        Write-Error "This script must be run from the LinkA project root directory"
        exit 1
    }
    
    # Handle different modes
    switch ($Mode) {
        "stop" {
            Stop-AllServices
            return
        }
        "status" {
            Show-Status
            return
        }
        "clean" {
            Invoke-Clean
            return
        }
        "build" {
            Invoke-Build
            return
        }
        "health" {
            Invoke-HealthCheck
            return
        }
        "backend" {
            Write-ColorOutput "Starting Backend Only..." "Green"
            $job = Start-Backend
            $jobs = @($job)
            break
        }
        "frontend" {
            Write-ColorOutput "Starting Frontend Only..." "Green"
            $job = Start-Frontend
            $jobs = @($job)
            break
        }
        "mobile" {
            $jobs = Start-MobileMode
            break
        }
        "full" {
        default {
            Write-ColorOutput "Starting Full Development Environment..." "Green"
            $backendJob = Start-Backend
            $frontendJob = Start-Frontend
            $jobs = @($backendJob, $frontendJob)
            break
        }
    }
    
    # Show status and final information (only for modes that start services)
    if ($Mode -notin @("stop", "status", "clean", "build", "health")) {
        Write-ColorOutput "" "White"
        Write-Success "Development environment is ready!"
        Write-ColorOutput "" "White"
        Write-ColorOutput "Access Points:" "Cyan"
        Write-ColorOutput "=============" "Cyan"
        Write-ColorOutput "Backend API:  http://localhost:8080" "White"
        Write-ColorOutput "Frontend:     http://localhost:5173" "White"
        Write-ColorOutput "Health Check: http://localhost:8080/api/health" "White"
        Write-ColorOutput "" "White"
        Write-Info "Press Ctrl+C to stop all services"
        
        # Wait for user interrupt or background job completion
        try {
            while ($true) {
                Start-Sleep -Seconds 5
                # Check if any background jobs have failed
                $failedJobs = Get-Job | Where-Object { $_.State -eq 'Failed' }
                if ($failedJobs) {
                    Write-Error "One or more services have failed"
                    foreach ($job in $failedJobs) {
                        Write-Error ("Job '" + $job.Name + "' failed: " + $job.Error)
                    }
                    break
                }
            }
        }
        catch {
            Write-ColorOutput "Stopping services..." "Yellow"
        }
        finally {
            Stop-AllServices
            if (!$NoPause) {
                Read-Host "Press Enter to exit"
            }
        }
    }
}

# Execute main function
Main