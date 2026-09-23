# TechShop - Enterprise E-Commerce Microservices Platform

TechShop is a full-stack, enterprise-grade microservices e-commerce platform designed for selling electronic devices (smartphones, laptops, accessories). The platform is built with a Spring Boot backend, a React + TypeScript frontend, and containerized infrastructure supporting event-driven workflows, caching, optimistic locking, and distributed transaction management.

---

## Architecture Overview

```
                                  +------------------------------------+
                                  |     React 18 + TypeScript UI       |
                                  |     (Vite, Tailwind CSS v4)        |
                                  +-----------------+------------------+
                                                    |
                                                    v
                                  +------------------------------------+
                                  |      Spring Cloud API Gateway      |
                                  |            (Port 8000)             |
                                  +-----------------+------------------+
                                                    |
         +-----------------------+------------------+------------------+-----------------------+
         |                       |                                     |                       |
         v                       v                                     v                       v
+-----------------+     +-----------------+                   +-----------------+     +-----------------+
|  User Service   |     | Product Service |                   |  Order Service  |     |Inventory Service|
|   (Port 8084)   |     |   (Port 8081)   |                   |   (Port 8083)   |     |   (Port 8082)   |
+--------+--------+     +--------+--------+                   +--------+--------+     +--------+--------+
         |                       |                                     |                       |
         v                       v                                     v                       v
   (User Database)       (Product Database)                    (Order Database)       (Inventory DB)
     PostgreSQL               MongoDB                             PostgreSQL             PostgreSQL
                                 |                                     |                       |
                                 +------------------+------------------+-----------------------+
                                                    |
                                                    v
                                  +------------------------------------+
                                  |       Apache Kafka Event Bus       |
                                  |            (Port 9092)             |
                                  +-----------------+------------------+
                                                    |
                                                    v
                                  +------------------------------------+
                                  |        Notification Service        |
                                  |            (Port 8085)             |
                                  +------------------------------------+
```

---

## Services & Technical Stack

| Service | Primary Tech | Database / Store | Responsibilities |
| :--- | :--- | :--- | :--- |
| **API Gateway** | Spring Cloud Gateway | N/A | Central ingress (Port 8000), dynamic route forwarding, CORS headers, rate-limiting. |
| **Product Service** | Spring Boot 3.2 | MongoDB 7.0 & Redis 7 | Product catalog, dynamic tech specs, cached category queries (<2ms latency). |
| **Inventory Service** | Spring Boot 3.2 | PostgreSQL 16 | Stock tracking, reservations, optimistic locking (`@Version`) preventing overselling. |
| **Order Service** | Spring Boot 3.2 | PostgreSQL 16 & Kafka | Order lifecycle management, Saga orchestration, immutable event sourcing (`order_events`). |
| **User Service** | Spring Boot 3.2 | PostgreSQL 16 | User profiles, Keycloak SSO integration, multi-tenancy context isolation. |
| **Notification Service** | Spring Boot 3.2 | Apache Kafka | Asynchronous Kafka event consumer, real-time customer alerts. |
| **Frontend UI** | React 18 + TypeScript | N/A | Responsive storefront UI, shopping cart, checkout workflow, fallback data resilience. |

---

## Environment Variables

Copy `.env.example` to `.env` in the root directory before launching:

```env
POSTGRES_USER=postgres
POSTGRES_PASSWORD=SuperSecretPostgresPassword123!
POSTGRES_PORT=5432

MONGO_USER=admin
MONGO_PASSWORD=SuperSecretMongoPassword123!
MONGO_PORT=27017

REDIS_PASSWORD=SuperSecretRedisPassword123!
REDIS_PORT=6379

KEYCLOAK_ADMIN_USER=admin
KEYCLOAK_ADMIN_PASSWORD=SuperSecretKeycloakPassword123!
KEYCLOAK_PORT=8080

KAFKA_PORT=9092
ZOOKEEPER_PORT=2181
```

---

## Local Development Setup

### 1. Prerequisites
Ensure you have installed:
* Docker & Docker Compose
* Java 21 JDK
* Apache Maven 3.9+
* Node.js 20+

### 2. Start Backing Infrastructure
```bash
docker compose up -d
```
Verify that PostgreSQL, MongoDB, Redis, Kafka, Zookeeper, and Keycloak are healthy:
```bash
docker compose ps
```

### 3. Build & Compile Backend Services
```bash
cd backend
mvn clean compile
```

### 4. Run Backend Services
Launch services individually or in separate terminals:
```bash
# Terminal 1: API Gateway (Port 8000)
mvn spring-boot:run -pl api-gateway

# Terminal 2: Product Service (Port 8081)
mvn spring-boot:run -pl product-service

# Terminal 3: Inventory Service (Port 8082)
mvn spring-boot:run -pl inventory-service

# Terminal 4: Order Service (Port 8083)
mvn spring-boot:run -pl order-service
```

### 5. Launch Frontend UI
```bash
cd frontend
npm install
npm run dev
```
Open `http://localhost:5173` in your browser.

---

## API Endpoints Reference

### API Gateway (`http://localhost:8000`)

* **Get All Products:** `GET /api/v1/products`
* **Get Products by Category:** `GET /api/v1/products?category=Smartphones`
* **Create Product:** `POST /api/v1/products`
* **Add Stock:** `POST /api/v1/inventory/add`
* **Check Stock:** `GET /api/v1/inventory/{sku}`
* **Deduct Stock:** `POST /api/v1/inventory/deduct`
* **Place Order:** `POST /api/v1/orders`
* **Order History (Event Sourcing):** `GET /api/v1/orders/{orderNumber}/history`

---

## Testing & Benchmarking

### 1. Integration Testing (Testcontainers)
Run automated database integration tests against real PostgreSQL Docker containers:
```bash
cd backend
mvn test
```

### 2. Frontend Production Build & Playwright E2E Tests
```bash
cd frontend
npm run build
npx playwright test
```

### 3. Load Testing with k6 (2000 Virtual Users)
Run the load benchmark script targeting 2000 concurrent virtual users:
```bash
k6 run load-test.js
```

---

## Kubernetes & CI/CD Deployment

### Kubernetes Manifests (`/k8s`)
Apply deployments and services to your local k3d/Minikube cluster:
```bash
kubectl apply -f k8s/techshop-deployments.yaml
```

### GitHub Actions CI/CD (`.github/workflows/ci-cd.yml`)
Pushes to `main` automatically trigger:
1. Maven backend build and unit tests.
2. React frontend build.
3. Trivy filesystem security vulnerability scanning.
