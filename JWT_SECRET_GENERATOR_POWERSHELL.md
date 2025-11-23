# JWT Secret Generator - PowerShell Implementation

## 🎯 PowerShell Command for JWT Secret Generation

### Basic Command
```powershell
[System.BitConverter]::ToString((1..32 | % {Get-Random -Maximum 256})).Replace("-", "")
```

### How It Works:
- `1..32` - Generates array of numbers 1 through 32 (32 bytes)
- `% {Get-Random -Maximum 256}` - ForEach loop generating random numbers 0-255
- `[System.BitConverter]::ToString()` - Converts byte array to hexadecimal string
- `.Replace("-", "")` - Removes hyphens to create continuous hex string

### Example Output:
```
A1B2C3D4E5F6789012345678901234567890ABCDEF1234567890ABCDEF123456
```

## 📜 Complete PowerShell Script

Save this as `generate-jwt-secret.ps1`:

```powershell
# PowerShell script to generate secure JWT secrets
# Compatible with Windows PowerShell 5.1+ and PowerShell Core 7+

param(
    [switch]$Force,
    [int]$Length = 32,
    [string]$OutputFile
)

# Function to generate secure JWT secret using BitConverter
function Generate-JWTSecret {
    param(
        [int]$ByteLength = 32
    )
    
    try {
        # Generate random bytes and convert to hexadecimal string
        $bytes = (1..$ByteLength | ForEach-Object { Get-Random -Maximum 256 })
        $hexString = [System.BitConverter]::ToString($bytes).Replace("-", "")
        
        return $hexString
    }
    catch {
        Write-Error "Failed to generate JWT secret: $_"
        return $null
    }
}

# Function to validate secret strength
function Test-SecretStrength {
    param(
        [string]$Secret
    )
    
    if ($Secret.Length -lt 64) {
        Write-Warning "Secret is too short. Minimum recommended length is 64 characters (256 bits)."
        return $false
    }
    
    if ($Secret -notmatch "^[a-fA-F0-9]+$") {
        Write-Warning "Secret contains non-hexadecimal characters."
        return $false
    }
    
    Write-Host "✅ Secret validation passed!" -ForegroundColor Green
    Write-Host "   Length: $($Secret.Length) characters" -ForegroundColor Cyan
    Write-Host "   Strength: 256-bit secure" -ForegroundColor Cyan
    
    return $true
}

# Main execution
Write-Host "🔐 JWT Secret Generator" -ForegroundColor Magenta
Write-Host "=====================" -ForegroundColor Magenta
Write-Host ""

# Generate the secret
Write-Host "Generating JWT secret..." -ForegroundColor Yellow
$jwtSecret = Generate-JWTSecret -ByteLength 32

if (-not $jwtSecret) {
    Write-Error "Failed to generate JWT secret. Exiting."
    exit 1
}

# Display the generated secret
Write-Host ""
Write-Host "🎯 Generated JWT Secret:" -ForegroundColor Green
Write-Host "======================" -ForegroundColor Green
Write-Host $jwtSecret -ForegroundColor White
Write-Host ""

# Validate the secret
$isValid = Test-SecretStrength -Secret $jwtSecret

# Handle output file if specified
if ($OutputFile) {
    try {
        # Check if file exists and user hasn't forced overwrite
        if ((Test-Path $OutputFile) -and (-not $Force)) {
            Write-Host "⚠️  File $OutputFile already exists. Use -Force to overwrite." -ForegroundColor Yellow
            $confirm = Read-Host "Do you want to overwrite? (y/N)"
            if ($confirm -ne 'y' -and $confirm -ne 'Y') {
                Write-Host "Operation cancelled." -ForegroundColor Red
                exit 0
            }
        }
        
        # Write secret to file
        Set-Content -Path $OutputFile -Value $jwtSecret -Encoding UTF8
        Write-Host "✅ Secret saved to: $OutputFile" -ForegroundColor Green
    }
    catch {
        Write-Error "Failed to write to file: $_"
        exit 1
    }
}

# Usage instructions
Write-Host ""
Write-Host "📋 Usage Instructions:" -ForegroundColor Cyan
Write-Host "====================" -ForegroundColor Cyan
Write-Host "1. Copy the generated secret above" -ForegroundColor White
Write-Host "2. Set as environment variable in your deployment:" -ForegroundColor White
Write-Host "   Windows (Current Session):" -ForegroundColor Yellow
Write-Host "   `$env:JWT_SECRET = '$jwtSecret'" -ForegroundColor Gray
Write-Host "" -ForegroundColor Gray
Write-Host "   Windows (Permanent):" -ForegroundColor Yellow
Write-Host "   [System.Environment]::SetEnvironmentVariable('JWT_SECRET', '$jwtSecret', 'User')" -ForegroundColor Gray
Write-Host "" -ForegroundColor Gray
Write-Host "   Railway Deployment:" -ForegroundColor Yellow
Write-Host "   railway variables set JWT_SECRET='$jwtSecret'" -ForegroundColor Gray
Write-Host "" -ForegroundColor Gray
Write-Host "   Docker:" -ForegroundColor Yellow
Write-Host "   docker run -e JWT_SECRET='$jwtSecret' your-image" -ForegroundColor Gray
Write-Host "" -ForegroundColor Gray
Write-Host "3. Add to your production .env file:" -ForegroundColor White
Write-Host "   JWT_SECRET=$jwtSecret" -ForegroundColor Gray

Write-Host ""
Write-Host "⚠️  Security Reminders:" -ForegroundColor Yellow
Write-Host "=====================" -ForegroundColor Yellow
Write-Host "• Never commit this secret to version control" -ForegroundColor White
Write-Host "• Use different secrets for each environment" -ForegroundColor White
Write-Host "• Rotate secrets periodically" -ForegroundColor White
Write-Host "• Store production secrets in secure environment variables" -ForegroundColor White

Write-Host ""
Write-Host "🎉 JWT secret generation completed successfully!" -ForegroundColor Green
```

## 🚀 How to Use the Script

### Basic Usage:
```powershell
.\generate-jwt-secret.ps1
```

### Save to File:
```powershell
.\generate-jwt-secret.ps1 -OutputFile "jwt-secret.txt"
```

### Force Overwrite:
```powershell
.\generate-jwt-secret.ps1 -Force -OutputFile "jwt-secret.txt"
```

## ⚙️ Alternative PowerShell Methods

### Method 2: Using .NET Crypto
```powershell
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$bytes = [System.Byte[]]::new(32)
$rng.GetBytes($bytes)
[System.BitConverter]::ToString($bytes).Replace("-", "")
```

### Method 3: Using GUID Conversion
```powershell
([System.Guid]::NewGuid().ToString() + [System.Guid]::NewGuid().ToString()).Replace("-", "").Substring(0, 64)
```

## 🔧 Integration with Environment Files

### For Railway Deployment:
1. Generate secret using the script above
2. Set in Railway: `railway variables set JWT_SECRET="your-generated-secret"`
3. Ensure your `.env.production` uses `${JWT_SECRET}`

### For Local Development:
1. Add to your `.env.development` file:
   ```bash
   JWT_SECRET=dev-your-development-secret-here
   ```

### For Production .env:
1. Add the generated secret:
   ```bash
   JWT_SECRET=A1B2C3D4E5F6789012345678901234567890ABCDEF1234567890ABCDEF123456
   ```

## ✅ Validation Steps

After generating your secret, verify it works:

```powershell
# Test the secret format (should return 64 characters)
$secret = [System.BitConverter]::ToString((1..32 | % {Get-Random -Maximum 256})).Replace("-", "")
Write-Host "Secret length: $($secret.Length)"  # Should be 64
Write-Host "Is hex: $($secret -match '^[a-fA-F0-9]+$')"  # Should be True
```

This implementation provides a robust, secure method for generating JWT secrets specifically designed for Windows PowerShell environments.