# Cinema Booking System

A modular monolith cinema booking system built with Spring Boot 3.3, featuring JWT authentication, VNPay payment integration, Redis caching, and Kafka event-driven architecture.

## Features

- **User Authentication** - JWT-based authentication with refresh token (HttpOnly cookie)
- **Movie Management** - CRUD operations for movies with genre categorization
- **Cinema & Hall Management** - Multi-cinema support with hall and seat configuration
- **Show Scheduling** - Schedule shows with automatic seat generation
- **Booking System** - Seat selection with Redis-based locking mechanism
- **Payment Integration** - VNPay sandbox integration
- **Email Notifications** - Booking confirmation with QR code tickets
- **Event-Driven Architecture** - Kafka for async event processing
- **Caching** - Redis caching for improved performance

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.3
- **Database**: PostgreSQL
- **Cache**: Redis
- **Message Queue**: Apache Kafka
- **Authentication**: JWT (JJWT)
- **Payment**: VNPay Sandbox
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Email**: Spring Mail with Thymeleaf templates
- **Build**: Maven

## Prerequisites

- Java 21+
- Docker & Docker Compose
- Maven 3.9+

## Quick Start

### 1. Clone and navigate to project
```bash
cd cinema-booking/cinema
```

### 2. Start infrastructure services
```bash
docker-compose up -d
```
This starts:
- PostgreSQL (port 5432)
- Redis (port 6379)
- Kafka + Zookeeper (port 9092)
- MinIO (port 9000)

### 3. Configure environment variables
Create `.env` file or set environment variables:
```properties
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/cinema_booking
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# Redis
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# JWT
JWT_SECRET=your-secret-key-at-least-256-bits-long
JWT_ACCESS_TOKEN_EXPIRATION=3600000
JWT_REFRESH_TOKEN_EXPIRATION=604800000

# Email (Gmail example)
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password

# VNPay Sandbox
VNPAY_TMN_CODE=your-tmn-code
VNPAY_HASH_SECRET=your-hash-secret
```

### 4. Run the application
```bash
mvn spring-boot:run
```

Or with specific profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 5. Access the application
- **API Base URL**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |
| POST | `/api/auth/refresh` | Refresh access token |
| POST | `/api/auth/logout` | Logout (invalidate tokens) |
| GET | `/api/auth/me` | Get current user info |

### Movies
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/movies` | List all movies |
| GET | `/api/movies/{id}` | Get movie by ID |
| GET | `/api/movies/now-showing` | Get now showing movies |
| GET | `/api/movies/coming-soon` | Get coming soon movies |
| POST | `/api/admin/movies` | Create movie (Admin) |
| PUT | `/api/admin/movies/{id}` | Update movie (Admin) |
| DELETE | `/api/admin/movies/{id}` | Delete movie (Admin) |

### Cinemas
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/cinemas` | List all cinemas |
| GET | `/api/cinemas/{id}` | Get cinema with halls |
| GET | `/api/cinemas/{id}/halls` | Get halls by cinema |
| POST | `/api/admin/cinemas` | Create cinema (Admin) |

### Shows
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/shows/movie/{movieId}` | Get shows by movie |
| GET | `/api/shows/{id}` | Get show details |
| GET | `/api/shows/{id}/seats` | Get available seats |
| POST | `/api/admin/shows` | Create show (Admin) |

### Bookings
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/bookings/lock-seats` | Lock seats for booking |
| POST | `/api/bookings/{id}/checkout` | Checkout booking |
| GET | `/api/bookings` | Get user's bookings |
| GET | `/api/bookings/{code}` | Get booking by code |
| DELETE | `/api/bookings/{id}` | Cancel booking |

### Payments
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/payments/create` | Create VNPay payment |
| GET | `/api/payments/vnpay/return` | VNPay return URL |
| POST | `/api/payments/vnpay/ipn` | VNPay IPN callback |
| GET | `/api/payments/status/{bookingCode}` | Get payment status |

## Project Structure

```
src/main/java/com/cinema/
├── auth/           # Authentication module
├── booking/        # Booking management
├── cinema/         # Cinema, Hall, Seat management
├── movie/          # Movie, Genre management
├── payment/        # Payment processing (VNPay)
├── shared/         # Shared utilities, configs, services
├── show/           # Show scheduling
├── storage/        # File storage (MinIO)
└── user/           # User management
```

## Testing

Run unit tests:
```bash
mvn test
```

Run with coverage:
```bash
mvn test jacoco:report
```

## Docker Deployment

Build Docker image:
```bash
docker build -t cinema-booking .
```

Run with docker-compose:
```bash
docker-compose -f docker-compose.prod.yml up -d
```

## Configuration

### Application Properties
Key configurations in `application.yml`:

```yaml
# Server
server:
  port: 8080

# Database
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cinema_booking

# Redis Caching TTL
# - movies: 30 minutes
# - cinemas: 1 hour
# - shows: 5 minutes
# - show_seats: 30 seconds
# - users: 15 minutes

# Kafka Topics
# - booking.created
# - booking.confirmed
# - booking.cancelled
# - payment.completed
# - payment.failed
# - notification
```

## License

MIT License
