# LinkA PowerShell Profile
# This profile is automatically loaded when PowerShell starts
# It enhances the development environment with custom functions and aliases

# Set strict mode for better error handling
Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

# Enable PowerShell command history
$MaximumHistoryCount = 10000

# Custom functions for LinkA development
function ll {
    Get-ChildItem -Path $args[0] -Force | Format-Table -AutoSize
}

function la {
    Get-ChildItem -Path $args[0] -Force -Hidden | Format-Table -AutoSize
}

function lla {
    Get-ChildItem -Path $args[0] -Force -Hidden | Format-Table -AutoSize
}

function which {
    Get-Command $args[0] -ErrorAction SilentlyContinue
}

function find-file {
    param([string]$Name)
    Get-ChildItem -Recurse -Filter "*$Name*" -ErrorAction SilentlyContinue
}

function grep {
    param([string]$Pattern)
    Get-ChildItem -Recurse | Select-String -Pattern $Pattern
}

function .. {
    Set-Location ".."
}

function ... {
    Set-Location "..\.."
}

function .... {
    Set-Location "..\..\.."
}

# LinkA specific functions
function start-linka {
    param([string]$Mode = "full")
    $scriptPath = Join-Path $PSScriptRoot "Linka\start-dev.ps1"
    if (Test-Path $scriptPath) {
        & $scriptPath -Mode $Mode
    } else {
        Write-Host "LinkA start script not found at: $scriptPath" -ForegroundColor Red
    }
}

function stop-linka {
    start-linka -Mode "stop"
}

function status-linka {
    start-linka -Mode "status"
}

function health-linka {
    start-linka -Mode "health"
}

function build-linka {
    start-linka -Mode "build"
}

function clean-linka {
    start-linka -Mode "clean"
}

# Git shortcuts
function gs {
    git status
}

function ga {
    git add .
}

function gc {
    param([string]$Message)
    git commit -m $Message
}

function gp {
    git pull
}

function gps {
    git push
}

function gl {
    git log --oneline -10
}

# Maven shortcuts
function mvn-clean {
    mvn clean
}

function mvn-test {
    mvn test
}

function mvn-package {
    mvn clean package
}

function mvn-run {
    mvn spring-boot:run
}

# NPM shortcuts
function npm-start {
    npm run dev
}

function npm-build {
    npm run build
}

function npm-test {
    npm run test
}

function npm-audit {
    npm audit
}

# Java shortcuts
function java-version {
    java -version
}

function javac-version {
    javac -version
}

# System information
function sysinfo {
    Get-ComputerInfo | Format-List
}

function ipconfig-all {
    Get-NetIPAddress -AddressFamily IPv4 | Format-Table InterfaceAlias, IPAddress, PrefixLength
}

# Network utilities
function port-check {
    param([int]$Port)
    Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue
}

function kill-port {
    param([int]$Port)
    $process = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($process) {
        Stop-Process -Id $process.OwningProcess -Force
        Write-Host "Killed process on port $Port" -ForegroundColor Green
    } else {
        Write-Host "No process found on port $Port" -ForegroundColor Yellow
    }
}

# Process management
function show-processes {
    Get-Process | Sort-Object CPU -Descending | Select-Object -First 20 ProcessName, Id, CPU, WorkingSet
}

function kill-process {
    param([string]$Name)
    $processes = Get-Process -Name $Name -ErrorAction SilentlyContinue
    foreach ($process in $processes) {
        $process.Kill()
        Write-Host "Killed process: $($process.ProcessName) (PID: $($process.Id))" -ForegroundColor Green
    }
}

# Development shortcuts
function open-vscode {
    code .
}

function open-explorer {
    Start-Process explorer.exe .
}

function open-powershell-ise {
    Start-Process powershell_ise.exe
}

# Text manipulation
function json-beautify {
    param([string]$InputText)
    $inputText | ConvertFrom-Json | ConvertTo-Json -Depth 10
}

function base64-encode {
    param([string]$Text)
    [System.Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes($Text))
}

function base64-decode {
    param([string]$EncodedText)
    [System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String($EncodedText))
}

# URL encoding/decoding
function url-encode {
    param([string]$Text)
    [System.Web.HttpUtility]::UrlEncode($Text)
}

function url-decode {
    param([string]$EncodedText)
    [System.Web.HttpUtility]::UrlDecode($EncodedText)
}

# PowerShell version and profile information
function ps-info {
    Write-Host "PowerShell Version: $($PSVersionTable.PSVersion)" -ForegroundColor Cyan
    Write-Host "PSEdition: $($PSVersionTable.PSEdition)" -ForegroundColor Cyan
    Write-Host "OS: $($PSVersionTable.OS)" -ForegroundColor Cyan
    Write-Host "Platform: $($PSVersionTable.Platform)" -ForegroundColor Cyan
    Write-Host "Profile: $PROFILE" -ForegroundColor Cyan
    Write-Host "Profile Current User All Hosts: $PROFILE.CurrentUserAllHosts" -ForegroundColor Cyan
}

# Clear screen and show welcome message
function Clear-HostWithWelcome {
    Clear-Host
    Write-Host "======================================" -ForegroundColor Green
    Write-Host "  PowerShell Profile Loaded" -ForegroundColor Green
    Write-Host "  LinkA Development Environment" -ForegroundColor Green
    Write-Host "======================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Quick Commands:" -ForegroundColor Yellow
    Write-Host "  start-linka          - Start full development environment" -ForegroundColor White
    Write-Host "  stop-linka           - Stop all services" -ForegroundColor White
    Write-Host "  status-linka         - Check service status" -ForegroundColor White
    Write-Host "  health-linka         - Run health check" -ForegroundColor White
    Write-Host "  build-linka          - Build all projects" -ForegroundColor White
    Write-Host "  clean-linka          - Clean build artifacts" -ForegroundColor White
    Write-Host ""
    Write-Host "Navigation:" -ForegroundColor Yellow
    Write-Host "  ll, la, lla          - List files (various formats)" -ForegroundColor White
    Write-Host "  .., ..., ....        - Go up directories" -ForegroundColor White
    Write-Host "  find-file <name>     - Find files by name" -ForegroundColor White
    Write-Host ""
    Write-Host "Git shortcuts:" -ForegroundColor Yellow
    Write-Host "  gs                   - git status" -ForegroundColor White
    Write-Host "  ga                   - git add ." -ForegroundColor White
    Write-Host "  gc <message>         - git commit -m" -ForegroundColor White
    Write-Host "  gp, gps              - git pull/push" -ForegroundColor White
    Write-Host ""
    Write-Host "System utilities:" -ForegroundColor Yellow
    Write-Host "  port-check <port>    - Check if port is in use" -ForegroundColor White
    Write-Host "  kill-port <port>     - Kill process on port" -ForegroundColor White
    Write-Host "  show-processes       - Show top processes" -ForegroundColor White
    Write-Host "  ps-info              - Show PowerShell info" -ForegroundColor White
    Write-Host ""
    Write-Host "Type 'ps-info' for more information about your PowerShell environment" -ForegroundColor Cyan
}

# Set console title
$Host.UI.RawUI.WindowTitle = "PowerShell - LinkA Development"

# Set console colors for better visibility
$Host.UI.RawUI.WindowTitle = "PowerShell - LinkA Development"
$PSStyle.OutputRendering = 'Console'

# Auto-load the welcome message
if (-not $Global:ProfileLoaded) {
    $Global:ProfileLoaded = $true
    Clear-HostWithWelcome
}

# Enable PSReadLine enhancements if available
if (Get-Module -ListAvailable -Name PSReadLine) {
    Import-Module PSReadLine
    
    # Set PSReadLine options
    Set-PSReadLineKeyHandler -Key Enter -Function AcceptLine
    Set-PSReadLineKeyHandler -Key Shift+Enter -Function AddLine
    
    # Enable syntax highlighting
    if (Get-Command Set-PSReadLineOption -ErrorAction SilentlyContinue) {
        Set-PSReadLineOption -BellStyle None -Colors @{
            "Parameter" = "Cyan"
            "String" = "Green"
            "Number" = "Yellow"
            "Comment" = "DarkGray"
            "Keyword" = "Magenta"
            "Operator" = "White"
            "Variable" = "White"
            "Command" = "White"
            "Type" = "White"
        }
    }
}

# Export functions and aliases
Export-ModuleMember -Function @(
    'start-linka', 'stop-linka', 'status-linka', 'health-linka', 'build-linka', 'clean-linka',
    'll', 'la', 'lla', 'which', 'find-file', 'grep', '..', '...', '....',
    'gs', 'ga', 'gc', 'gp', 'gps', 'gl',
    'mvn-clean', 'mvn-test', 'mvn-package', 'mvn-run',
    'npm-start', 'npm-build', 'npm-test', 'npm-audit',
    'java-version', 'javac-version',
    'sysinfo', 'ipconfig-all', 'port-check', 'kill-port',
    'show-processes', 'kill-process',
    'open-vscode', 'open-explorer', 'open-powershell-ise',
    'json-beautify', 'base64-encode', 'base64-decode', 'url-encode', 'url-decode',
    'ps-info', 'Clear-HostWithWelcome'
) -Alias @()