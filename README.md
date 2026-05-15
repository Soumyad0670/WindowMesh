# WindowMesh — Distributed Sliding Window API Rate Limiter

A distributed API Gateway rate limiter built using **Spring Cloud Gateway**, **Redis**, and **Lua scripting** with reactive WebFlux architecture.

WindowMesh provides:

* route-aware throttling
* distributed request limiting
* atomic concurrency-safe execution
* Redis-backed traffic control
* real-time metrics monitoring

---

# 🚀 Features

* Distributed rate limiting using Redis
* Sliding Window / Token Bucket style traffic shaping
* Atomic Lua script execution
* Route-level throttling policies
* Per-user and per-IP request isolation
* Reactive non-blocking architecture using WebFlux
* Real-time traffic metrics dashboard
* Docker-based Redis deployment
* Gateway-level enforcement before backend execution

---

# 🧠 System Architecture

```text
Client
   ↓
Spring Cloud Gateway
   ↓
RateLimiterFilter
   ↓
Redis + Lua Script
   ↓
ALLOW / BLOCK Decision
   ↓
Backend Service
```

---

# ⚙️ Tech Stack

| Technology           | Purpose                       |
| -------------------- | ----------------------------- |
| Java 21              | Core backend                  |
| Spring Boot          | Application framework         |
| Spring Cloud Gateway | API Gateway                   |
| Spring WebFlux       | Reactive architecture         |
| Redis                | Distributed state store       |
| Lua                  | Atomic token bucket execution |
| Docker               | Redis containerization        |
| Reactor              | Reactive programming          |

---

# 🧩 Core Components

## RateLimiterFilter

Intercepts all incoming requests and:

* resolves user identity
* resolves route policy
* executes Redis token bucket logic
* blocks or forwards traffic

---

## RedisService

Executes Lua scripts against Redis using reactive RedisTemplate.

Responsible for:

* token consumption
* refill calculations
* distributed consistency

---

## RateLimitPolicyService

Provides dynamic route-level rate limit policies.

Example:

* `/login` → strict limits
* `/search` → relaxed limits
* `/admin` → highly restricted

---

## MetricsService

Tracks:

* total requests
* blocked requests
* allowed requests
* route traffic
* user traffic

---

## DashboardController

Exposes real-time traffic metrics through:

```http
GET /dashboard
```

---

# 🔒 Rate Limiting Strategy

WindowMesh uses a distributed traffic-control algorithm based on:

* Sliding Window principles
* Token Bucket refill behavior
* Atomic Redis Lua execution

Each user maintains:

* available tokens
* last refill timestamp

Every request:

1. refills tokens based on elapsed time
2. consumes token if available
3. blocks request if bucket empty

---

# ⚡ Why Lua?

Without Lua:

* concurrent requests can create race conditions
* multiple requests may consume same token

Lua executes atomically inside Redis:

```text
read → refill → consume → persist
```

as one indivisible operation.

---

# 📦 Project Structure

```text
com.windowmesh
│
├── controller
│   └── DashboardController
│
├── dto
│   └── DashboardMetrics
│
├── filter
│   └── RateLimiterFilter
│
├── policy
│   ├── RateLimitConfig
│   └── RateLimitPolicyService
│
├── service
│   ├── RedisService
│   └── MetricsService
│
├── util
│   └── KeyResolver
│
├── lua
│   └── rate_limiter.lua
│
└── WindowMeshApplication
```

---

# 🐳 Running Redis with Docker

## Start Redis

```bash
docker run --name redis-rate-limiter -p 6379:6379 redis
```

---

## Verify Redis

```bash
docker exec -it redis-rate-limiter redis-cli ping
```

Expected:

```text
PONG
```

---

# ▶️ Running the Application

## Clone Repository

```bash
git clone <your-repository-url>
cd WindowMesh
```

---

## Start Spring Boot Application

```bash
./mvnw spring-boot:run
```

or run directly from IntelliJ.

---

# 🧪 Load Testing

## PowerShell Test

```powershell
1..20 | ForEach-Object {
    Invoke-WebRequest `
      -Uri "http://localhost:8080/login/test" `
      -Headers @{ "X-User-Id" = "alice" }
}
```

---

## Expected Behavior

| Request Count    | Result                |
| ---------------- | --------------------- |
| Initial requests | 200 OK                |
| Excess requests  | 429 Too Many Requests |

---

# 📊 Metrics Dashboard

Access:

```text
http://localhost:8080/dashboard
```

Example response:

```json
{
  "totalRequests": "120",
  "allowedRequests": "90",
  "blockedRequests": "30"
}
```

---

# 🔥 Key Engineering Concepts

* Distributed systems
* Reactive backend architecture
* API Gateway design
* Traffic shaping
* Token Bucket algorithm
* Sliding Window semantics
* Redis atomicity
* Lua scripting
* Concurrent request handling
* Observability and metrics

---

# 📈 Future Improvements

* Dynamic database-backed rate limits
* JWT-aware throttling
* Retry-After headers
* Redis Cluster support
* Prometheus + Grafana integration
* Kubernetes deployment
* Adaptive traffic shaping
* Circuit breaker fallback

---

# 🧠 Learning Outcomes

This project demonstrates:

* backend system design
* distributed coordination
* concurrency-safe infrastructure
* reactive programming
* gateway-level request governance

---