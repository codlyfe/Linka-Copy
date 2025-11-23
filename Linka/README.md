# LinkA - Uganda's Trusted Marketplace

A full-stack marketplace application built with React/TypeScript frontend and Spring Boot backend, designed with mobile-first responsive design and secure mobile money integration for Uganda.

## 🌟 Features

- **Mobile-First Responsive Design** - Optimized for smartphones and tablets
- **Secure Mobile Money Integration** - MTN Mobile Money, Airtel Money, Mula by Stanbic
- **Backend-Frontend Communication** - RESTful API with CORS support
- **Progressive Web App (PWA)** - Installable on mobile devices
- **Multiplatform Support** - Web, mobile browsers, and potential native app conversion

## 🚀 Quick Start

### Prerequisites
- Java 17+ and Maven (for backend)
- Node.js 18+ and npm/pnpm/yarn (for frontend)
- Git

### Backend Setup
```bash
cd Linka-Backend
./mvnw spring-boot:run
# or on Windows: mvn spring-boot:run
```
Backend runs on: http://localhost:8081

### Frontend Setup
```bash
cd Linka-Frontend
npm install
npm run dev
```
Frontend runs on: http://localhost:5173

### All-in-One Scripts

**Windows:**
```bash
# Start both servers
Linka\start-dev.bat

# Start mobile-optimized environment
Linka\start-mobile.bat
```

**Linux/macOS:**
```bash
# Start both servers
bash Linka/start-dev.sh
```

## 📱 Mobile Testing

To test on mobile devices:

1. **Find your local IP:**
   ```bash
   # Windows
   ipconfig
   
   # Linux/macOS
   ifconfig
   ```

2. **Update CORS configuration** in `Linka-Backend/src/main/java/com/Linka/backend/config/WebConfig.java`

3. **Access the app** on mobile: `http://[YOUR_IP]:5173`

4. **Install as PWA** - Mobile browsers will prompt to install as native app

## 🔧 Mobile Money Integration

The app includes support for Uganda's major mobile money providers:

- **MTN Mobile Money** (MIN: 500 UGX, MAX: 5,000,000 UGX, Fee: 1%)
- **Airtel Money** (MIN: 500 UGX, MAX: 3,000,000 UGX, Fee: 1.5%)
- **Mula by Stanbic** (MIN: 1,000 UGX, MAX: 2,000,000 UGX, Fee: 0.5%)

### API Endpoints
- `GET /api/health` - Health check
- `GET /api/mobile-money/providers` - List providers
- `POST /api/mobile-money/payment` - Initiate payment
- `GET /api/mobile-money/transaction/{id}` - Check transaction status
- `GET /api/mobile-money/validate-phone/{phone}` - Validate phone number

## 📂 Project Structure

```
LinkA/
├── Linka-Backend/          # Spring Boot backend
│   ├── src/main/java/...
│   └── src/main/resources/...
├── Linka-Frontend/         # React/TypeScript frontend
│   ├── src/
│   │   ├── components/     # Reusable UI components
│   │   ├── pages/         # Page components
│   │   ├── services/      # API services
│   │   ├── hooks/         # Custom hooks
│   │   └── lib/           # Utilities
│   ├── public/            # Static assets
│   └── ...
└── start-*.{bat,sh,ps1}  # Development scripts
```

## 🎯 Mobile-First Features

1. **Responsive Navigation** - Mobile bottom nav + desktop top nav
2. **Touch-Optimized** - `touch-manipulation` and proper tap handling
3. **Safe Area Support** - iPhone notch compatibility
4. **PWA Manifest** - App-like installation
5. **Service Worker Ready** - Offline functionality foundation
6. **Mobile-First CSS** - Progressive enhancement approach

## 🛠️ Technical Stack

### Frontend
- **React 18** with TypeScript
- **Vite** for fast development
- **Tailwind CSS** for styling
- **Lucide React** for icons
- **React Query** for state management
- **React Router** for navigation

### Backend
- **Spring Boot 3** with Java 17
- **Spring MVC** for REST APIs
- **CORS** configuration for cross-origin requests
- **Jackson** for JSON processing

## 🚦 Development Workflow

1. **Start Backend** - `cd Linka-Backend && mvn spring-boot:run`
2. **Start Frontend** - `cd Linka-Frontend && npm run dev`
3. **Test APIs** - Visit http://localhost:8081/api/health
4. **Test Frontend** - Visit http://localhost:5173
5. **Mobile Testing** - Use your local IP on mobile device

## 📝 Environment Variables

### Frontend (.env)
```
VITE_API_BASE_URL=http://localhost:8081/api
```

### Backend (application.yaml)
```yaml
server:
  port: 8081
  
spring:
  profiles:
    active: dev
```

## 🔄 Production Build

### Frontend
```bash
cd Linka-Frontend
npm run build
npm run preview
```

### Backend
```bash
cd Linka-Backend
./mvnw clean package
java -jar target/backend-1.0.0.jar
```

## 📞 Support

For development issues:
1. Check console logs in browser DevTools
2. Verify backend is running on port 8081
3. Check CORS configuration
4. Ensure mobile device can reach development server

## 🎉 Next Steps

- Add database integration (PostgreSQL/MySQL)
- Implement user authentication
- Add real mobile money provider APIs
- Create product listing functionality
- Add image upload and management
- Implement search and filtering

---

**Built with ❤️ for Uganda's digital marketplace**