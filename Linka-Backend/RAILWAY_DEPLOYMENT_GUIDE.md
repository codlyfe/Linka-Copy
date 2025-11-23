# Railway Deployment Guide - LinkA Backend

## Overview
This guide provides step-by-step instructions for deploying the LinkA Backend to Railway.

## Prerequisites
- Railway account (sign up at [railway.app](https://railway.app))
- GitHub repository with your code
- Railway CLI installed (`npm install -g @railway/cli`)

## Deployment Steps

### 1. Connect Repository to Railway
```bash
# Login to Railway
railway login

# Link your project
railway link

# Deploy from current directory
railway up
```

### 2. Environment Variables Configuration
Set the following environment variables in Railway dashboard or CLI:

#### Required Variables
```bash
# Core Application
PORT=8080
SPRING_PROFILES_ACTIVE=prod
APP_NAME=linka-backend

# Database Configuration (PostgreSQL)
DB_URL=jdbc:postgresql://postgres.linka.internal:5432/linka_prod
DB_USERNAME=linka_prod_user
DB_PASSWORD=G7!vR9q$2zX@Lm#4pT8wYk^1sB

# JWT Security
JWT_SECRET=<generate-strong-secret-using-powershell-script>

# Redis Configuration
REDIS_HOST=redis.linka.internal
REDIS_PORT=6379
REDIS_PASSWORD=<your-redis-password>
REDIS_DATABASE=0

# Email Configuration
SMTP_HOST=smtp.yourdomain.com
SMTP_PORT=587
SMTP_USERNAME=noreply@linka.com
SMTP_PASSWORD=<your-smtp-password>
EMAIL_ENABLED=true

# CORS Configuration (update with your actual domains)
CORS_ORIGINS=https://your-app.vercel.app,https://your-domain.com
FRONTEND_URLS=https://your-app.vercel.app,https://your-domain.com

# Logging Configuration
LOG_LEVEL=WARN
LOG_FILE_PATH=/var/log/linka/application.log
LOG_MAX_FILE_SIZE=100MB
LOG_MAX_HISTORY=30

# Mobile Money Configuration
MOBILE_MONEY_MTN_MIN=500
MOBILE_MONEY_MTN_MAX=5000000
MOBILE_MONEY_MTN_FEE=0.01
MOBILE_MONEY_AIRTEL_MIN=500
MOBILE_MONEY_AIRTEL_MAX=3000000
MOBILE_MONEY_AIRTEL_FEE=0.015
MOBILE_MONEY_MULA_MIN=1000
MOBILE_MONEY_MULA_MAX=2000000
MOBILE_MONEY_MULA_FEE=0.005

# Feature Flags
DEBUG_MODE=false
SPRING_DEVTOOLS_ENABLED=false
HEALTH_CHECK_ENABLED=true
JSON_LOGGING=true
RATE_LIMIT_ENABLED=true
RATE_LIMIT_REQUESTS_PER_MINUTE=60
RATE_LIMIT_BURST_CAPACITY=100

# Monitoring
PROMETHEUS_ENABLED=true
```

#### Set Variables via CLI
```bash
# Core variables
railway variables set PORT=8080
railway variables set SPRING_PROFILES_ACTIVE=prod
railway variables set APP_NAME=linka-backend

# Database variables
railway variables set DB_URL="jdbc:postgresql://postgres.linka.internal:5432/linka_prod"
railway variables set DB_USERNAME=linka_prod_user
railway variables set DB_PASSWORD="G7!vR9q$2zX@Lm#4pT8wYk^1sB"

# JWT Secret (generate using PowerShell script)
railway variables set JWT_SECRET="<your-generated-secret>"

# Add other variables...
```

### 3. PostgreSQL Setup
If you don't have PostgreSQL set up yet:

#### Option A: Railway PostgreSQL Service
```bash
# Add PostgreSQL service to your Railway project
railway add postgresql

# Get connection details
railway variables
```

#### Option B: External PostgreSQL
Ensure your external PostgreSQL allows connections from Railway's IP ranges.

### 4. Redis Setup
If you don't have Redis set up yet:

#### Option A: Railway Redis Service
```bash
# Add Redis service to your Railway project
railway add redis

# Get connection details
railway variables
```

#### Option B: External Redis
Ensure your external Redis allows connections from Railway's IP ranges.

### 5. Build and Deploy
```bash
# Build the application
./mvnw clean package -DskipTests

# Deploy to Railway
railway up

# Check deployment status
railway status

# View logs
railway logs
```

### 6. Health Check Verification
After deployment, verify the application is running:

```bash
# Check health endpoint
curl -f https://your-app.railway.app/actuator/health

# Check Prometheus metrics (if enabled)
curl -f https://your-app.railway.app/actuator/prometheus
```

## Configuration Files Created

### 1. `railway.json`
- Railway-specific deployment configuration
- Build and start commands
- Health check settings

### 2. `Dockerfile`
- Production-ready Docker image
- Non-root user for security
- JVM optimization for containers
- Health check endpoint

### 3. `application-prod.yaml`
- Railway-optimized configuration
- Environment variable support
- Production logging and monitoring

## Troubleshooting

### Build Issues
```bash
# Clear Railway cache
railway deploy --detach

# Rebuild from scratch
railway up --detach
```

### Runtime Issues
```bash
# Check application logs
railway logs

# Check environment variables
railway variables

# Test locally with production profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### Database Connection Issues
1. Verify PostgreSQL URL format: `jdbc:postgresql://host:port/database`
2. Check firewall rules for Railway IP ranges
3. Ensure database user has proper permissions

### Redis Connection Issues
1. Verify Redis host and port
2. Check Redis authentication if password is set
3. Ensure Redis allows connections from Railway

## Security Considerations

### Environment Variables
- Never commit sensitive values to version control
- Use Railway's environment variable management
- Rotate secrets periodically

### Database Security
- Use strong passwords (already configured)
- Enable SSL connections
- Restrict database access to application servers only

### Application Security
- Non-root user in Docker container
- Production logging levels
- CORS properly configured
- Rate limiting enabled

## Performance Optimization

### JVM Settings
- Already configured in Dockerfile: `-Xms256m -Xmx1g`
- Container-aware memory management
- G1 garbage collector for better performance

### Database Connection Pooling
- HikariCP configured in `application-prod.yaml`
- Optimal pool sizes for production load

## Monitoring

### Health Checks
- `/actuator/health` - Application health
- `/actuator/prometheus` - Metrics for monitoring
- Built-in Railway health monitoring

### Logging
- Production log levels configured
- Log rotation and size limits
- JSON logging for structured logs

## Next Steps

1. **Deploy Backend**: Follow this guide to deploy to Railway
2. **Configure Frontend**: Update frontend API URLs to point to Railway
3. **Test End-to-End**: Verify full application functionality
4. **Set up Monitoring**: Configure alerts and monitoring dashboards
5. **Domain Setup**: Configure custom domain if needed

## Support

- Railway Documentation: [docs.railway.app](https://docs.railway.app)
- Railway Discord: [discord.gg/railway](https://discord.gg/railway)
- Spring Boot on Railway: [docs.railway.app/reference/build-deploys#java](https://docs.railway.app/reference/build-deploys#java)