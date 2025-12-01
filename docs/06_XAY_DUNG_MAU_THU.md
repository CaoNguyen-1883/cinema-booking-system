# 6. XÂY DỰNG MẪU THỬ (PROTOTYPE IMPLEMENTATION)

## 6.1. Môi trường phát triển

### 6.1.1. Yêu cầu hệ thống

**Hardware Requirements:**
- CPU: Intel i5/AMD Ryzen 5 trở lên (khuyến nghị i7/Ryzen 7)
- RAM: Tối thiểu 16GB (khuyến nghị 32GB)
- Disk: 50GB trống (SSD khuyến nghị)
- Network: Internet connection ổn định

**Software Requirements:**

**Backend Development:**
```
- JDK 21 (Oracle JDK hoặc OpenJDK)
- Maven 3.9+
- IntelliJ IDEA Ultimate 2024+ (khuyến nghị) hoặc Eclipse
- Docker Desktop 24+
- Postman hoặc Insomnia (API testing)
- DBeaver (PostgreSQL client)
- RedisInsight (Redis GUI)
```

**Frontend Development:**
```
- Node.js 20 LTS
- npm 10+ hoặc yarn 1.22+
- VS Code với extensions:
  - ESLint
  - Prettier
  - TypeScript
  - Tailwind CSS IntelliSense
  - ES7+ React/Redux/React-Native snippets
```

**DevOps Tools:**
```
- Docker Desktop (Windows/Mac) hoặc Docker Engine (Linux)
- Docker Compose 2.20+
- Git 2.40+
- GitHub Desktop (optional)
```

### 6.1.2. Cài đặt môi trường

**Bước 1: Cài đặt JDK 21**

Windows:
```bash
# Download từ: https://www.oracle.com/java/technologies/downloads/#java21
# Hoặc dùng SDKMAN
sdk install java 21.0.1-oracle
```

Linux/Mac:
```bash
# Dùng SDKMAN
curl -s "https://get.sdkman.io" | bash
sdk install java 21.0.1-oracle
```

Verify:
```bash
java -version
# Output: java version "21.0.1" 2023-10-17
```

**Bước 2: Cài đặt Maven**

```bash
# Download từ: https://maven.apache.org/download.cgi
# Hoặc
sdk install maven 3.9.6

# Verify
mvn -version
```

**Bước 3: Cài đặt Node.js**

```bash
# Download từ: https://nodejs.org/en/download/
# Hoặc dùng nvm
nvm install 20
nvm use 20

# Verify
node -v  # v20.11.0
npm -v   # 10.2.4
```

**Bước 4: Cài đặt Docker**

```bash
# Download Docker Desktop:
# - Windows: https://docs.docker.com/desktop/install/windows-install/
# - Mac: https://docs.docker.com/desktop/install/mac-install/
# - Linux: https://docs.docker.com/desktop/install/linux-install/

# Verify
docker --version
docker-compose --version
```

## 6.2. Cấu trúc dự án

### 6.2.1. Tổng quan cấu trúc

```
cinema-booking/
│
├── docker-compose.yml           # Docker orchestration
├── .gitignore
├── README.md
│
├── backend/                     # Spring Boot Application
│   ├── Dockerfile
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   └── src/
│       ├── main/
│       │   ├── java/com/cinema/
│       │   │   ├── CinemaBookingApplication.java
│       │   │   ├── modules/
│       │   │   │   ├── auth/
│       │   │   │   ├── user/
│       │   │   │   ├── movie/
│       │   │   │   ├── cinema/
│       │   │   │   ├── showtime/
│       │   │   │   ├── booking/
│       │   │   │   ├── payment/
│       │   │   │   ├── promotion/
│       │   │   │   ├── notification/
│       │   │   │   └── analytics/
│       │   │   ├── shared/
│       │   │   │   ├── domain/
│       │   │   │   ├── dto/
│       │   │   │   ├── exception/
│       │   │   │   ├── util/
│       │   │   │   └── constant/
│       │   │   ├── infrastructure/
│       │   │   │   ├── config/
│       │   │   │   ├── messaging/
│       │   │   │   ├── cache/
│       │   │   │   └── scheduling/
│       │   │   └── event/
│       │   └── resources/
│       │       ├── application.yml
│       │       ├── application-dev.yml
│       │       ├── application-prod.yml
│       │       ├── db/migration/
│       │       │   ├── V1__create_users_table.sql
│       │       │   ├── V2__create_movies_table.sql
│       │       │   └── ...
│       │       └── templates/
│       │           └── email/
│       └── test/
│           └── java/com/cinema/
│
├── frontend/                    # React + TypeScript Application
│   ├── Dockerfile
│   ├── package.json
│   ├── tsconfig.json
│   ├── vite.config.ts
│   ├── tailwind.config.js
│   ├── .env.development
│   ├── .env.production
│   └── src/
│       ├── main.tsx
│       ├── App.tsx
│       ├── pages/
│       │   ├── Home.tsx
│       │   ├── Movies.tsx
│       │   ├── MovieDetail.tsx
│       │   ├── Showtime.tsx
│       │   ├── SeatSelection.tsx
│       │   ├── Payment.tsx
│       │   ├── BookingConfirmation.tsx
│       │   ├── Profile.tsx
│       │   └── Admin/
│       ├── components/
│       │   ├── common/
│       │   │   ├── Header.tsx
│       │   │   ├── Footer.tsx
│       │   │   ├── Loading.tsx
│       │   │   └── ErrorBoundary.tsx
│       │   ├── movie/
│       │   │   ├── MovieCard.tsx
│       │   │   ├── MovieList.tsx
│       │   │   └── MovieFilter.tsx
│       │   ├── booking/
│       │   │   ├── SeatMap.tsx
│       │   │   ├── SeatLegend.tsx
│       │   │   └── BookingSummary.tsx
│       │   └── admin/
│       ├── store/
│       │   ├── authStore.ts
│       │   ├── bookingStore.ts
│       │   └── movieStore.ts
│       ├── services/
│       │   ├── api.ts
│       │   ├── authService.ts
│       │   ├── movieService.ts
│       │   ├── bookingService.ts
│       │   └── paymentService.ts
│       ├── types/
│       │   ├── auth.ts
│       │   ├── movie.ts
│       │   ├── booking.ts
│       │   └── common.ts
│       ├── utils/
│       │   ├── dateTime.ts
│       │   ├── currency.ts
│       │   └── validation.ts
│       └── styles/
│           └── global.css
│
└── docs/                        # Documentation
    ├── 01_TONG_QUAN.md
    ├── 02_XAC_DINH_YEU_CAU.md
    ├── 03_PHAN_TICH_NHU_CAU.md
    ├── 04_THIET_KE_KIEN_TRUC.md
    ├── 05_THIET_KE_CO_SO_DU_LIEU.md
    ├── 06_XAY_DUNG_MAU_THU.md
    ├── 07_KET_LUAN.md
    └── api/
        └── API_DOCUMENTATION.md
```

## 6.3. Triển khai Infrastructure với Docker

### 6.3.1. Docker Compose Configuration

**File: `docker-compose.yml`**

```yaml
version: '3.8'

services:
  # PostgreSQL Database
  postgres:
    image: postgres:16-alpine
    container_name: cinema-postgres
    environment:
      POSTGRES_DB: cinema_booking
      POSTGRES_USER: cinema_admin
      POSTGRES_PASSWORD: cinema_password_2024
      POSTGRES_INITDB_ARGS: "--encoding=UTF8"
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./backend/src/main/resources/db/init:/docker-entrypoint-initdb.d
    networks:
      - cinema-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U cinema_admin -d cinema_booking"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

  # Redis Cache & Distributed Lock
  redis:
    image: redis:7-alpine
    container_name: cinema-redis
    command: >
      redis-server
      --appendonly yes
      --appendfsync everysec
      --maxmemory 512mb
      --maxmemory-policy allkeys-lru
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - cinema-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 3s
      retries: 5
    restart: unless-stopped

  # Zookeeper (Kafka dependency)
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    container_name: cinema-zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    networks:
      - cinema-network
    restart: unless-stopped

  # Kafka Message Broker
  kafka:
    image: confluentinc/cp-kafka:7.5.0
    container_name: cinema-kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
      - "9093:9093"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:9093
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "true"
    networks:
      - cinema-network
    healthcheck:
      test: ["CMD", "kafka-broker-api-versions", "--bootstrap-server", "localhost:9092"]
      interval: 10s
      timeout: 10s
      retries: 5
    restart: unless-stopped

  # Kafka UI (Optional - for monitoring)
  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    container_name: cinema-kafka-ui
    depends_on:
      - kafka
    ports:
      - "8090:8080"
    environment:
      KAFKA_CLUSTERS_0_NAME: cinema-cluster
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:9092
      KAFKA_CLUSTERS_0_ZOOKEEPER: zookeeper:2181
    networks:
      - cinema-network
    restart: unless-stopped

  # Spring Boot Backend
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: cinema-backend
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
      kafka:
        condition: service_healthy
    ports:
      - "8080:8080"
      - "5005:5005"  # Debug port
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/cinema_booking
      SPRING_DATASOURCE_USERNAME: cinema_admin
      SPRING_DATASOURCE_PASSWORD: cinema_password_2024
      SPRING_REDIS_HOST: redis
      SPRING_REDIS_PORT: 6379
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
      JAVA_TOOL_OPTIONS: -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
    volumes:
      - ./backend/logs:/app/logs
    networks:
      - cinema-network
    restart: unless-stopped

  # React Frontend
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
      args:
        VITE_API_URL: http://localhost:8080/api
    container_name: cinema-frontend
    depends_on:
      - backend
    ports:
      - "3000:80"
    networks:
      - cinema-network
    restart: unless-stopped

  # pgAdmin (Database Management UI) - Optional
  pgadmin:
    image: dpage/pgadmin4:latest
    container_name: cinema-pgadmin
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@cinema.com
      PGADMIN_DEFAULT_PASSWORD: admin123
      PGADMIN_CONFIG_SERVER_MODE: 'False'
    ports:
      - "5050:80"
    volumes:
      - pgadmin_data:/var/lib/pgadmin
    networks:
      - cinema-network
    restart: unless-stopped

  # RedisInsight (Redis Management UI) - Optional
  redis-insight:
    image: redislabs/redisinsight:latest
    container_name: cinema-redis-insight
    ports:
      - "8001:8001"
    volumes:
      - redis_insight_data:/db
    networks:
      - cinema-network
    restart: unless-stopped

volumes:
  postgres_data:
    driver: local
  redis_data:
    driver: local
  pgadmin_data:
    driver: local
  redis_insight_data:
    driver: local

networks:
  cinema-network:
    driver: bridge
```

### 6.3.2. Backend Dockerfile

**File: `backend/Dockerfile`**

```dockerfile
# Multi-stage build for optimized image size

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080 5005

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 6.3.3. Frontend Dockerfile

**File: `frontend/Dockerfile`**

```dockerfile
# Multi-stage build

# Stage 1: Build
FROM node:20-alpine AS build
WORKDIR /app

# Copy package files
COPY package*.json ./
RUN npm ci --only=production

# Copy source code
COPY . .

# Build argument for API URL
ARG VITE_API_URL
ENV VITE_API_URL=$VITE_API_URL

# Build application
RUN npm run build

# Stage 2: Serve with nginx
FROM nginx:1.25-alpine
WORKDIR /usr/share/nginx/html

# Remove default nginx files
RUN rm -rf ./*

# Copy built files from build stage
COPY --from=build /app/dist .

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80 || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
```

**File: `frontend/nginx.conf`**

```nginx
server {
    listen 80;
    server_name _;
    root /usr/share/nginx/html;
    index index.html;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript
               application/x-javascript application/xml+rss
               application/json application/javascript;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;

    # Cache static assets
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # React Router - serve index.html for all routes
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Proxy API calls to backend
    location /api {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 6.3.4. Khởi chạy hệ thống

```bash
# Clone repository
git clone https://github.com/your-repo/cinema-booking.git
cd cinema-booking

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f backend
docker-compose logs -f frontend

# Check service status
docker-compose ps

# Stop all services
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v
```

**Truy cập các services:**
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Backend Swagger UI: http://localhost:8080/swagger-ui.html
- PostgreSQL: localhost:5432
- Redis: localhost:6379
- Kafka: localhost:9093
- Kafka UI: http://localhost:8090
- pgAdmin: http://localhost:5050
- RedisInsight: http://localhost:8001

## 6.4. Backend Implementation

### 6.4.1. Application Configuration

**File: `backend/src/main/resources/application.yml`**

```yaml
spring:
  application:
    name: cinema-booking-api

  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}

  # JPA Configuration
  jpa:
    open-in-view: false
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true

  # Jackson Configuration
  jackson:
    default-property-inclusion: non_null
    time-zone: Asia/Ho_Chi_Minh
    serialization:
      write-dates-as-timestamps: false

  # Server Configuration
server:
  port: 8080
  compression:
    enabled: true
    mime-types: application/json,application/xml,text/html,text/xml,text/plain
  error:
    include-message: always
    include-binding-errors: always
    include-stacktrace: never
    include-exception: false

# Actuator (Health check, metrics)
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
  metrics:
    export:
      prometheus:
        enabled: true

# Application specific configuration
app:
  jwt:
    secret: ${JWT_SECRET:cinema_booking_secret_key_change_in_production_2024}
    access-token-expiry: 3600000    # 1 hour in milliseconds
    refresh-token-expiry: 604800000  # 7 days in milliseconds

  seat-lock:
    timeout: 300  # 5 minutes in seconds

  payment:
    vnpay:
      tmn-code: ${VNPAY_TMN_CODE:}
      hash-secret: ${VNPAY_HASH_SECRET:}
      url: ${VNPAY_URL:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}
      return-url: ${VNPAY_RETURN_URL:http://localhost:3000/payment/callback}

    momo:
      partner-code: ${MOMO_PARTNER_CODE:}
      access-key: ${MOMO_ACCESS_KEY:}
      secret-key: ${MOMO_SECRET_KEY:}
      url: ${MOMO_URL:https://test-payment.momo.vn/v2/gateway/api/create}
      return-url: ${MOMO_RETURN_URL:http://localhost:3000/payment/callback}
      notify-url: ${MOMO_NOTIFY_URL:http://localhost:8080/api/payments/momo/notify}
```

**File: `backend/src/main/resources/application-dev.yml`**

```yaml
spring:
  # Database Configuration
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:cinema_booking}
    username: ${DB_USERNAME:cinema_admin}
    password: ${DB_PASSWORD:cinema_password_2024}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

  # JPA Configuration
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  # Redis Configuration
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      timeout: 60000
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 2

  # Kafka Configuration
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9093}
    consumer:
      group-id: cinema-booking-consumer
      auto-offset-reset: earliest
      enable-auto-commit: false
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all
      retries: 3

  # Mail Configuration (for email notifications)
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME:}
    password: ${MAIL_PASSWORD:}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true

# Logging Configuration
logging:
  level:
    root: INFO
    com.cinema: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/cinema-booking.log
    max-size: 10MB
    max-history: 30
```

### 6.4.2. Main Application Class

**File: `backend/src/main/java/com/cinema/CinemaBookingApplication.java`**

```java
package com.cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableKafka
public class CinemaBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CinemaBookingApplication.class, args);
        System.out.println("""

            ╔═══════════════════════════════════════════════════╗
            ║   Cinema Booking System Started Successfully     ║
            ║   API Documentation: http://localhost:8080/swagger-ui.html
            ║   Health Check: http://localhost:8080/actuator/health
            ╚═══════════════════════════════════════════════════╝
            """);
    }
}
```

### 6.4.3. Code mẫu cho module Booking với Redis Locking

**File: `backend/src/main/java/com/cinema/modules/booking/service/SeatLockService.java`**

```java
package com.cinema.modules.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatLockService {

    private final StringRedisTemplate redisTemplate;

    @Value("${app.seat-lock.timeout:300}")
    private int lockTimeout; // seconds

    private static final String LOCK_KEY_PREFIX = "seat:lock:";

    /**
     * Try to acquire lock for a seat
     * @param showtimeId The showtime ID
     * @param seatNumber The seat number (e.g., "A5")
     * @param userId The user ID trying to lock the seat
     * @return true if lock acquired successfully, false otherwise
     */
    public boolean lockSeat(Long showtimeId, String seatNumber, Long userId) {
        String lockKey = buildLockKey(showtimeId, seatNumber);
        String lockValue = userId.toString();

        Boolean success = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, lockValue, lockTimeout, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(success)) {
            log.info("Seat locked successfully: showtime={}, seat={}, user={}",
                showtimeId, seatNumber, userId);
            return true;
        }

        log.warn("Failed to lock seat (already locked): showtime={}, seat={}, user={}",
            showtimeId, seatNumber, userId);
        return false;
    }

    /**
     * Release lock for a seat
     * Only the user who locked it can unlock it
     */
    public boolean unlockSeat(Long showtimeId, String seatNumber, Long userId) {
        String lockKey = buildLockKey(showtimeId, seatNumber);
        String currentLockValue = redisTemplate.opsForValue().get(lockKey);

        // Verify ownership before unlocking
        if (currentLockValue != null && currentLockValue.equals(userId.toString())) {
            Boolean deleted = redisTemplate.delete(lockKey);
            if (Boolean.TRUE.equals(deleted)) {
                log.info("Seat unlocked successfully: showtime={}, seat={}, user={}",
                    showtimeId, seatNumber, userId);
                return true;
            }
        }

        log.warn("Failed to unlock seat (not owned or not locked): showtime={}, seat={}, user={}",
            showtimeId, seatNumber, userId);
        return false;
    }

    /**
     * Check if seat is currently locked
     */
    public boolean isSeatLocked(Long showtimeId, String seatNumber) {
        String lockKey = buildLockKey(showtimeId, seatNumber);
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
    }

    /**
     * Get the user ID who currently holds the lock
     */
    public Long getLockedByUserId(Long showtimeId, String seatNumber) {
        String lockKey = buildLockKey(showtimeId, seatNumber);
        String lockValue = redisTemplate.opsForValue().get(lockKey);

        if (lockValue != null) {
            try {
                return Long.parseLong(lockValue);
            } catch (NumberFormatException e) {
                log.error("Invalid lock value format: {}", lockValue, e);
            }
        }
        return null;
    }

    /**
     * Extend lock timeout for a seat (when user needs more time)
     */
    public boolean extendLock(Long showtimeId, String seatNumber, Long userId) {
        String lockKey = buildLockKey(showtimeId, seatNumber);
        String currentLockValue = redisTemplate.opsForValue().get(lockKey);

        // Verify ownership before extending
        if (currentLockValue != null && currentLockValue.equals(userId.toString())) {
            Boolean extended = redisTemplate.expire(lockKey, lockTimeout, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(extended)) {
                log.info("Seat lock extended: showtime={}, seat={}, user={}",
                    showtimeId, seatNumber, userId);
                return true;
            }
        }

        return false;
    }

    /**
     * Get remaining lock time in seconds
     */
    public long getRemainingLockTime(Long showtimeId, String seatNumber) {
        String lockKey = buildLockKey(showtimeId, seatNumber);
        Long ttl = redisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
        return ttl != null ? ttl : 0;
    }

    /**
     * Unlock all seats for a showtime (admin operation)
     */
    public void unlockAllSeatsForShowtime(Long showtimeId) {
        String pattern = LOCK_KEY_PREFIX + showtimeId + ":*";
        var keys = redisTemplate.keys(pattern);

        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("Unlocked {} seats for showtime={}", keys.size(), showtimeId);
        }
    }

    private String buildLockKey(Long showtimeId, String seatNumber) {
        return LOCK_KEY_PREFIX + showtimeId + ":" + seatNumber;
    }
}
```

**File: `backend/src/main/java/com/cinema/modules/booking/controller/BookingController.java`**

```java
package com.cinema.modules.booking.controller;

import com.cinema.modules.booking.dto.SeatLockRequest;
import com.cinema.modules.booking.dto.SeatLockResponse;
import com.cinema.modules.booking.service.SeatLockService;
import com.cinema.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final SeatLockService seatLockService;

    /**
     * Lock a seat for booking
     * POST /api/bookings/lock-seat
     */
    @PostMapping("/lock-seat")
    public ResponseEntity<ApiResponse<SeatLockResponse>> lockSeat(
            @Valid @RequestBody SeatLockRequest request,
            @AuthenticationPrincipal Long userId) {

        boolean locked = seatLockService.lockSeat(
            request.getShowtimeId(),
            request.getSeatNumber(),
            userId
        );

        if (locked) {
            long remainingTime = seatLockService.getRemainingLockTime(
                request.getShowtimeId(),
                request.getSeatNumber()
            );

            SeatLockResponse response = SeatLockResponse.builder()
                .success(true)
                .seatNumber(request.getSeatNumber())
                .showtimeId(request.getShowtimeId())
                .lockedUntil(LocalDateTime.now().plusSeconds(remainingTime))
                .remainingSeconds(remainingTime)
                .message("Seat locked successfully")
                .build();

            return ResponseEntity.ok(ApiResponse.success(response));
        } else {
            SeatLockResponse response = SeatLockResponse.builder()
                .success(false)
                .seatNumber(request.getSeatNumber())
                .showtimeId(request.getShowtimeId())
                .message("Seat is already locked by another user")
                .build();

            return ResponseEntity.ok(ApiResponse.success(response));
        }
    }

    /**
     * Unlock a seat (cancel booking)
     * POST /api/bookings/unlock-seat
     */
    @PostMapping("/unlock-seat")
    public ResponseEntity<ApiResponse<Void>> unlockSeat(
            @Valid @RequestBody SeatLockRequest request,
            @AuthenticationPrincipal Long userId) {

        boolean unlocked = seatLockService.unlockSeat(
            request.getShowtimeId(),
            request.getSeatNumber(),
            userId
        );

        if (unlocked) {
            return ResponseEntity.ok(ApiResponse.success("Seat unlocked successfully"));
        } else {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to unlock seat"));
        }
    }
}
```

### 6.4.4. Scheduled Task tự động unlock ghế

**File: `backend/src/main/java/com/cinema/infrastructure/scheduling/SeatUnlockScheduler.java`**

```java
package com.cinema.infrastructure.scheduling;

import com.cinema.modules.booking.repository.BookingRepository;
import com.cinema.modules.cinema.repository.SeatRepository;
import com.cinema.shared.constant.SeatStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatUnlockScheduler {

    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;

    /**
     * Automatically unlock expired seat locks
     * Runs every 1 minute
     */
    @Scheduled(fixedRate = 60000) // 60 seconds
    @Transactional
    public void unlockExpiredSeats() {
        LocalDateTime now = LocalDateTime.now();

        int unlockedCount = seatRepository.unlockExpiredSeats(now);

        if (unlockedCount > 0) {
            log.info("Auto-unlocked {} expired seats at {}", unlockedCount, now);
        }
    }

    /**
     * Cancel expired pending bookings
     * Runs every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    @Transactional
    public void cancelExpiredBookings() {
        LocalDateTime now = LocalDateTime.now();

        int cancelledCount = bookingRepository.cancelExpiredBookings(now);

        if (cancelledCount > 0) {
            log.info("Auto-cancelled {} expired bookings at {}", cancelledCount, now);
        }
    }
}
```


### 6.4.5. Error Codes và Exception Handling

**ErrorCode.java:**

```java
package com.cinema.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Authentication (1xxx)
    AUTH_INVALID_CREDENTIALS(1001, "Invalid email or password", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_EXPIRED(1002, "Access token has expired", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_INVALID(1003, "Invalid access token", HttpStatus.UNAUTHORIZED),
    AUTH_ACCESS_DENIED(1004, "Access denied", HttpStatus.FORBIDDEN),

    // User (2xxx)
    USER_NOT_FOUND(2001, "User not found", HttpStatus.NOT_FOUND),
    USER_EMAIL_EXISTS(2002, "Email already registered", HttpStatus.CONFLICT),
    USER_INSUFFICIENT_POINTS(2004, "Insufficient loyalty points", HttpStatus.BAD_REQUEST),

    // Movie (3xxx)
    MOVIE_NOT_FOUND(3001, "Movie not found", HttpStatus.NOT_FOUND),
    MOVIE_NOT_SHOWING(3002, "Movie is not currently showing", HttpStatus.BAD_REQUEST),

    // Show (4xxx)
    SHOW_NOT_FOUND(4001, "Show not found", HttpStatus.NOT_FOUND),
    SHOW_ALREADY_STARTED(4002, "Show has already started", HttpStatus.BAD_REQUEST),

    // Seat (5xxx)
    SEAT_NOT_FOUND(5001, "Seat not found", HttpStatus.NOT_FOUND),
    SEAT_ALREADY_LOCKED(5002, "Seat is locked by another user", HttpStatus.CONFLICT),
    SEAT_ALREADY_SOLD(5003, "Seat has already been sold", HttpStatus.CONFLICT),
    SEAT_LOCK_EXPIRED(5004, "Seat lock has expired", HttpStatus.BAD_REQUEST),

    // Booking (6xxx)
    BOOKING_NOT_FOUND(6001, "Booking not found", HttpStatus.NOT_FOUND),
    BOOKING_ALREADY_CANCELLED(6002, "Booking already cancelled", HttpStatus.BAD_REQUEST),
    BOOKING_PAYMENT_TIMEOUT(6004, "Payment timeout", HttpStatus.REQUEST_TIMEOUT),

    // Payment (7xxx)
    PAYMENT_NOT_FOUND(7001, "Payment not found", HttpStatus.NOT_FOUND),
    PAYMENT_FAILED(7003, "Payment failed", HttpStatus.BAD_REQUEST),
    PAYMENT_INVALID_SIGNATURE(7004, "Invalid payment signature", HttpStatus.BAD_REQUEST),

    // System (10xxx)
    INTERNAL_ERROR(10001, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    RATE_LIMIT_EXCEEDED(10003, "Too many requests", HttpStatus.TOO_MANY_REQUESTS);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
```

**BusinessException.java:**

```java
package com.cinema.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Object details;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = null;
    }

    public BusinessException(ErrorCode errorCode, Object details) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
    }
}
```

**GlobalExceptionHandler.java:**

```java
package com.cinema.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("Business exception: {} - {}", errorCode.getCode(), errorCode.getMessage());

        ErrorResponse response = new ErrorResponse(
            errorCode.getCode(),
            errorCode.getMessage(),
            ex.getDetails()
        );
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }
}
```

### 6.4.6. JWT Authentication hoàn chỉnh

**CustomUserDetails.java:**

```java
package com.cinema.security;

import com.cinema.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {
    private final Long userId;
    private final String email;
    private final String password;
    private final String role;
    private final boolean enabled;

    public CustomUserDetails(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.password = user.getPasswordHash();
        this.role = user.getRole();
        this.enabled = "ACTIVE".equals(user.getStatus());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getUsername() { return email; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return enabled; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return enabled; }
}
```

**Sử dụng trong Controller:**

```java
@PostMapping("/bookings")
public ResponseEntity<BookingResponse> createBooking(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody BookingRequest request) {
    Long userId = userDetails.getUserId();
    return ResponseEntity.ok(bookingService.createBooking(userId, request));
}
```

(Tiếp tục trong message tiếp theo...)

## 6.5. Kafka Event-Driven Implementation

**Tiếp tục trong phần tiếp theo với Frontend, Testing, và Deployment...**

---

**Tài liệu tiếp theo**: [07_KET_LUAN.md](./07_KET_LUAN.md)
