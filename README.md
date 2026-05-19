# SilkRoad 🕸️

SilkRoad is an ephemeral, secure file-sharing platform designed for pure data flow. It creates temporary, encrypted tunnels for users to exchange files in real-time. No storage logs, no persistence—just secure transfers that vanish when the session expires.

## 🚀 Key Features

- **Ephemeral Sessions**: Create time-bound sessions (e.g., 15 minutes, 1 hour) protected by a password.
- **Real-Time Synchronization**: Live updates using WebSockets for user presence and file uploads.
- **Direct-to-Cloud Uploads**: Files bypass the backend server and upload securely and directly to Cloudflare R2 using pre-signed URLs.
- **End-to-End TLS**: All network traffic (HTTP & WebSocket) is secured via HTTPS/WSS.
- **Zero-Footprint**: Sessions and files auto-expire and leave no persistent traces after completion.

## 🏗️ Architecture Overview

The system is split into a robust Java Spring Boot backend and a modern React/TypeScript frontend.

### The Flow
1. **Handshake**: A user creates a session (duration + password) and receives a JWT.
2. **Tunneling**: Other users join via the Session ID and password, also receiving JWTs.
3. **Real-time Link**: All users connect to a WebSocket topic (`/topic/session/{sessionId}`) to sync state.
4. **Data Transfer**: 
   - A user requests a pre-signed Cloudflare R2 URL from the backend.
   - The frontend uploads the binary file directly to R2.
   - Upon success, the frontend notifies the backend.
   - The backend broadcasts the new file to all connected WebSocket clients.

## 💻 Technical Stack

### Frontend (Client)
- **Framework**: React 18 with TypeScript
- **Build Tool**: Bun
- **Styling**: TailwindCSS & Framer Motion (dynamic, glassmorphic UI)
- **Routing**: React Router DOM
- **Network**: Fetch API & STOMP over WebSockets (`@stomp/stompjs`)

### Backend (Server)
- **Framework**: Java 17 + Spring Boot 3
- **Database**: JPA / Hibernate (H2 for dev / MySQL for production)
- **Security**: JWT (JSON Web Tokens) & Spring Security
- **Messaging**: Spring WebSocket with STOMP message broker
- **Storage**: Cloudflare R2 (S3-compatible API via AWS SDK)
- **API Documentation**: Swagger / OpenAPI 3

## 🛠️ Local Development

### Prerequisites
- [Java 17+](https://adoptium.net/)
- [Bun](https://bun.sh/)
- Cloudflare R2 Credentials

### 1. Backend Setup
1. Navigate to the `backend/` directory.
2. Ensure `keystore.p12` exists in your resources for TLS/HTTPS support on port `8443`.
3. Run the Spring Boot application:
   ```bash
   ./gradlew bootRun
   ```
4. Access Swagger UI at `https://localhost:8443/swagger-ui/index.html` (Accept the self-signed certificate warning).

### 2. Frontend Setup
1. Navigate to the `frontend/` directory.
2. Install dependencies via Bun:
   ```bash
   bun install
   ```
3. Start the Bun dev server (bundles and serves with hot-reload):
   ```bash
   bun run dev
   ```
4. Open the application in your browser.

## 🔒 Security Notes
- During local development, self-signed certificates are used to enable `https://` and `wss://`. Browsers will flag this as insecure—you must manually accept the risk/bypass for local testing.
- Passwords protect session access, while JWTs protect subsequent API interactions and WebSocket connections.
