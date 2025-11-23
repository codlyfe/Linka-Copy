#!/bin/bash

echo "========================================="
echo " LinkA Development Environment Starter"
echo "========================================="
echo

echo "[1/2] Starting Backend Server (Port 8080)..."
cd Linka-Backend
echo "Starting Spring Boot application..."
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev &

BACKEND_PID=$!
echo "Backend PID: $BACKEND_PID"

echo "Backend will start at: http://localhost:8080"
echo "Health check: http://localhost:8080/api/health"
echo

echo "[2/2] Starting Frontend Server (Port 5173)..."
cd ../Linka-Frontend

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo "Installing dependencies..."
    npm install
fi

echo "Starting Vite development server..."
npm run dev

# If we reach here, one of the servers stopped
echo "Server stopped. Cleaning up..."
kill $BACKEND_PID 2>/dev/null

echo "Development environment stopped."