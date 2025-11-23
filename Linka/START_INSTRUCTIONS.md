# LinkA Application - Complete Startup Instructions

## ✅ Issues Fixed:
1. **500 Internal Server Error**: Resolved port conflicts and backend connectivity
2. **Deprecated Meta Tag**: Updated `apple-mobile-web-app-capable` to `mobile-web-app-capable`
3. **API Configuration**: Frontend now correctly points to backend on port 8081

## 🚀 To Start the Complete Application:

### Step 1: Start Backend (Already Running)
The backend should already be running on port 8081. If not:
```bash
cd Linka-Backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Step 2: Start Frontend
Open a new terminal and run:
```bash
cd Linka-Frontend
npm install  # Only needed first time
npm run dev
```

### Step 3: Access the Application
- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8081
- **Health Check**: http://localhost:8081/api/health

## 📋 Expected Results:
- ✅ No more 500 errors on /api/health
- ✅ No more deprecated meta tag warnings
- ✅ Frontend connects successfully to backend
- ✅ All app features and design preserved

## 🧪 Test the Fixes:
1. Open browser console (F12)
2. Navigate to http://localhost:5173
3. Check Network tab - /api/health should return 200 OK
4. Verify no deprecation warnings in console

## 🔧 If Issues Persist:
1. Ensure ports 8081 and 5173 are free
2. Clear browser cache
3. Restart both services

The application design and functionality have been completely preserved!