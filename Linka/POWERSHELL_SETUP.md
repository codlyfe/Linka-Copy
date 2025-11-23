# LinkA PowerShell Development Environment

This document describes the PowerShell-based development environment that has been configured to replace all other terminal types and provide a unified, consistent development experience.

## 🚀 Quick Start

### Prerequisites
Ensure you have the following installed:
- **PowerShell 5.1+** (Windows PowerShell) or PowerShell 7+
- **Java 11+** and **Maven**
- **Node.js 16+** and **npm**
- **VS Code** with the PowerShell extension

### Starting the Development Environment

Open VS Code in the LinkA project root and use one of these methods:

#### Method 1: Using the Command Palette
1. Press `Ctrl+Shift+P` (Windows/Linux) or `Cmd+Shift+P` (Mac)
2. Type "Tasks: Run Task"
3. Select "Start Full Development Environment"

#### Method 2: Using PowerShell Commands
Open the integrated terminal (`Ctrl+``) and run:
```powershell
.\Linka\start-dev.ps1 -Mode full
```

#### Method 3: Using PowerShell Profile Commands
Once PowerShell loads, use the convenient shortcuts:
```powershell
start-linka          # Start full development environment
stop-linka           # Stop all services
status-linka         # Check service status
```

## 🔧 Configuration Details

### VS Code Settings (`.vscode/settings.json`)
- **Default Terminal**: PowerShell is configured as the default terminal
- **Terminal Profiles**: Only PowerShell and limited Command Prompt options available
- **Disabled Terminal Types**: Git Bash, WSL, and other shell types are restricted
- **Terminal Appearance**: Optimized font settings and colors for better readability

### Available Tasks (`.vscode/tasks.json`)
The following tasks are available via `Ctrl+Shift+P` → "Tasks: Run Task":

| Task Name | Description | Command |
|-----------|-------------|---------|
| Start Full Development Environment | Starts both backend and frontend | `start-dev.ps1 -Mode full` |
| Start Backend Only | Starts only the Spring Boot backend | `start-dev.ps1 -Mode backend` |
| Start Frontend Only | Starts only the Vite frontend | `start-dev.ps1 -Mode frontend` |
| Start Mobile Development | Starts services optimized for mobile testing | `start-dev.ps1 -Mode mobile` |
| Stop All Services | Stops all running services | `start-dev.ps1 -Mode stop` |
| Check Service Status | Shows status of all services | `start-dev.ps1 -Mode status` |
| Clean Build Artifacts | Removes build directories and cache | `start-dev.ps1 -Mode clean` |
| Build Projects | Builds both backend and frontend | `start-dev.ps1 -Mode build` |
| Health Check | Runs comprehensive system check | `start-dev.ps1 -Mode health` |
| Install Dependencies | Installs all project dependencies | `start-dev.ps1 -Mode full -AutoInstall` |
| PowerShell: Open in PowerShell ISE | Opens script in PowerShell ISE | - |
| PowerShell: Set Execution Policy | Sets PowerShell execution policy | - |

## 📋 PowerShell Script Usage

### Basic Usage
```powershell
# Start full development environment
.\Linka\start-dev.ps1

# Start with verbose output
.\Linka\start-dev.ps1 -Verbose

# Start with automatic dependency installation
.\Linka\start-dev.ps1 -AutoInstall

# Start without pausing at the end
.\Linka\start-dev.ps1 -NoPause
```

### Available Modes
- **`full`** - Starts both backend (port 8080) and frontend (port 5173)
- **`backend`** - Starts only the Spring Boot backend
- **`frontend`** - Starts only the Vite frontend
- **`mobile`** - Starts services optimized for mobile device testing
- **`stop`** - Stops all running services
- **`status`** - Shows current status of all services
- **`clean`** - Removes build artifacts and cache files
- **`build`** - Builds both projects
- **`health`** - Runs comprehensive health and prerequisite checks

## 🎛️ PowerShell Profile Features

The custom PowerShell profile (`Linka/Microsoft.PowerShell_profile.ps1`) provides enhanced functionality:

### LinkA-Specific Commands
```powershell
start-linka          # Start full development environment
stop-linka           # Stop all services
status-linka         # Check service status
health-linka         # Run health check
build-linka          # Build all projects
clean-linka          # Clean build artifacts
```

### File Navigation
```powershell
ll                   # List files (detailed)
la                   # List files (including hidden)
lla                  # List files (detailed + hidden)
..                   # Go up one directory
...                  # Go up two directories
....                 # Go up three directories
find-file <name>     # Find files by name
grep <pattern>       # Search for text in files
```

### Git Shortcuts
```powershell
gs                   # git status
ga                   # git add .
gc <message>         # git commit -m "<message>"
gp                   # git pull
gps                  # git push
gl                   # git log --oneline -10
```

### Build Tools
```powershell
mvn-clean            # mvn clean
mvn-test             # mvn test
mvn-package          # mvn clean package
mvn-run              # mvn spring-boot:run
npm-start            # npm run dev
npm-build            # npm run build
npm-test             # npm run test
```

### System Utilities
```powershell
port-check <port>    # Check if port is in use
kill-port <port>     # Kill process running on port
show-processes       # Show top 20 processes by CPU usage
kill-process <name>  # Kill process by name
sysinfo              # Show system information
ipconfig-all         # Show all network interfaces
ps-info              # Show PowerShell version and profile info
```

### Text Processing
```powershell
json-beautify        # Beautify JSON input
base64-encode <text> # Encode text to base64
base64-decode <text> # Decode base64 string
url-encode <text>    # URL encode text
url-decode <text>    # URL decode text
```

### Development Tools
```powershell
open-vscode          # Open VS Code in current directory
open-explorer        # Open file explorer in current directory
open-powershell-ise  # Open PowerShell ISE
```

## 🔒 Security and Execution Policy

The scripts are configured to use PowerShell's `RemoteSigned` execution policy, which:
- Allows running locally created scripts
- Requires digital signatures for scripts downloaded from the internet
- Provides a good balance between security and usability

To set the execution policy, use:
```powershell
# In an elevated PowerShell session
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

## 🌐 Access URLs

Once the development environment is running:

| Service | URL | Description |
|---------|-----|-------------|
| Backend API | http://localhost:8080 | Spring Boot REST API |
| Frontend | http://localhost:5173 | Vite development server |
| Backend Health | http://localhost:8080/api/health | Backend health check endpoint |
| Mobile Frontend | http://[your-ip]:5173 | Mobile-accessible frontend (mobile mode) |

## 🛠️ Troubleshooting

### Common Issues

1. **Execution Policy Error**
   ```
   cannot be loaded because running scripts is disabled on this system
   ```
   **Solution**: Run in elevated PowerShell:
   ```powershell
   Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
   ```

2. **Port Already in Use**
   ```
   Address already in use: bind
   ```
   **Solution**: Use the provided utility:
   ```powershell
   kill-port 8080  # Kill process on port 8080
   kill-port 5173  # Kill process on port 5173
   ```

3. **Missing Dependencies**
   ```
   Module not found or npm dependencies missing
   ```
   **Solution**: Use auto-install mode:
   ```powershell
   .\Linka\start-dev.ps1 -AutoInstall
   ```

4. **Java/Maven Not Found**
   ```
   java: command not found
   ```
   **Solution**: Ensure Java and Maven are in your PATH environment variable

### Health Check
Run a comprehensive health check to identify issues:
```powershell
.\Linka\start-dev.ps1 -Mode health
```

This will check:
- Required software installation (Java, Maven, Node.js, npm)
- Port availability
- Service health endpoints
- Network connectivity

## 🔄 Migration from Other Terminal Types

If you were previously using:
- **Command Prompt (cmd.exe)**: PowerShell provides all cmd functionality plus enhancements
- **Git Bash**: All git commands work the same, plus PowerShell-specific features
- **WSL**: Use PowerShell's Linux-like commands and file navigation shortcuts

## 📚 Additional Resources

- **PowerShell Documentation**: https://docs.microsoft.com/powershell/
- **VS Code PowerShell Extension**: https://marketplace.visualstudio.com/items?itemName=ms-vscode.PowerShell
- **PowerShell Profile Documentation**: https://docs.microsoft.com/powershell/module/microsoft.powershell.core/about/about_profiles

## 🎨 Customization

### Modifying the PowerShell Profile
1. Edit `Linka/Microsoft.PowerShell_profile.ps1`
2. Add your own functions and aliases
3. Restart PowerShell or run: `. $PROFILE`

### Adding Custom Tasks
1. Edit `.vscode/tasks.json`
2. Add new task definitions following the existing pattern
3. Tasks will appear in the Command Palette

### Modifying VS Code Settings
1. Edit `.vscode/settings.json`
2. Modify terminal settings, appearance, or behavior
3. Changes take effect immediately

---

**Note**: This PowerShell configuration permanently replaces all other terminal types in VS Code. If you need to use other shells occasionally, you can temporarily modify the settings or use external terminal applications.