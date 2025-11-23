@echo off
echo =========================================
echo  LinkA Development Environment Starter
echo =========================================
echo.

echo [1/3] Starting Backend Server...
cd Linka-Backend
echo Starting Spring Boot application on port 8080...
mvn spring-boot:run -Dspring-boot.run.profiles=dev
if %ERRORLEVEL% NEQ 0 (
    echo Error starting backend! Check Maven installation and Java version.
    pause
    exit /b %ERRORLEVEL
)

echo.
echo Backend server is running at: http://localhost:8080
echo Health check: http://localhost:8080/api/health
echo.
pause