# 1. TỔNG QUAN HỆ THỐNG CINEMA BOOKING

## 1.1. Giới thiệu

Hệ thống Cinema Booking là một nền tảng đặt vé xem phim trực tuyến quy mô trung bình đến lớn, phục vụ chuỗi rạp chiếu phim có nhiều cụm rạp trên khắp các tỉnh thành tại Việt Nam (tương tự CGV, Lotte Cinema, Galaxy Cinema).

### Đặc điểm của hệ thống:
- **Quy mô**: Phục vụ 50-100+ rạp chiếu phim trên toàn quốc
- **Người dùng đồng thời**: 10,000 - 50,000 concurrent users trong giờ cao điểm
- **Giao dịch**: Hàng triệu vé được đặt mỗi tháng
- **Độ khả dụng**: 99.9% uptime requirement
- **Phạm vi**: Web application (desktop + mobile responsive)

## 1.2. Mục tiêu dự án

### 1.2.1. Mục tiêu kinh doanh
- Tăng doanh thu từ bán vé online lên 80%
- Giảm thời gian chờ đợi tại quầy vé xuống 70%
- Tăng trải nghiệm khách hàng thông qua việc đặt vé nhanh chóng, tiện lợi
- Tối ưu hóa việc quản lý lịch chiếu và phòng chiếu
- Thu thập dữ liệu khách hàng để phân tích và marketing

### 1.2.2. Mục tiêu kỹ thuật
- Xây dựng hệ thống có khả năng mở rộng (scalable)
- Đảm bảo hiệu năng cao trong giờ cao điểm
- Tích hợp đa phương thức thanh toán
- Đảm bảo tính nhất quán dữ liệu (consistency)
- Dễ dàng bảo trì và phát triển tính năng mới
- Áp dụng kiến trúc Modular Monolith để cân bằng giữa đơn giản và khả năng mở rộng

## 1.3. Công nghệ sử dụng (Technology Stack)

### 1.3.1. Backend Stack (Bắt buộc)
```
- Java 21 (LTS)
- Spring Boot 3.3.x
  - Spring Web (RESTful API)
  - Spring Security 6 (JWT Authentication)
  - Spring Data JPA (Hibernate)
  - Spring Data Redis
  - Spring Kafka / Spring AMQP (RabbitMQ)
  - Spring Validation
  - Spring Boot Actuator (Health check, metrics)

- PostgreSQL 16 hoặc MySQL 8.x (Primary Database)
- Redis 7.**x**
  - Caching (Movie list, Showtimes)
  - Session Management
  - Distributed Locking (Seat reservation)

- Apache Kafka 3.x HOẶC RabbitMQ 3.x HOẶC cả hai
  - Event-driven architecture
  - Async processing (Email, notifications)
  - Service decoupling

- Docker & Docker Compose
  - Container deployment
  - Local development environment

- Maven (Build tool)
```

### 1.3.2. Frontend Stack (Bắt buộc)
```
- React 18.x
- TypeScript 5.x
- Vite (Build tool)
- React Router 6 (Routing)
- Redux Toolkit hoặc Zustand (State Management)
- React Query / TanStack Query (Server state management)
- Axios (HTTP Client)
- Tailwind CSS hoặc Material-UI (Styling)
- Socket.io-client hoặc SockJS (Real-time updates - optional)
```

### 1.3.3. Công nghệ bổ sung (Optional/Nice to have)
```
- Elasticsearch (Search engine) - Optional
- Prometheus + Grafana (Monitoring)
- ELK Stack / Loki (Centralized logging)
- MinIO / AWS S3 (Object storage cho images)
- Nginx (Reverse proxy, Load balancer)
- Jenkins / GitHub Actions (CI/CD)
- SonarQube (Code quality)
- JUnit 5 + Mockito (Unit testing)
- Testcontainers (Integration testing)
```

## 1.4. Kiến trúc hệ thống

### 1.4.1. Modular Monolith Architecture

Hệ thống áp dụng kiến trúc **Modular Monolith** - một kiến trúc nằm giữa Monolith truyền thống và Microservices, kết hợp ưu điểm của cả hai:

**Đặc điểm:**
- Single deployment unit (1 JAR/WAR file)
- Modules độc lập với boundaries rõ ràng
- Low coupling, high cohesion
- Shared database nhưng schema isolation
- Dễ dàng extract thành microservice nếu cần

**Cấu trúc modules:**

```
cinema-booking-monolith/
│
├── src/main/java/com/cinema/
│   │
│   ├── CinemaBookingApplication.java (Main application)
│   │
│   ├── modules/
│   │   │
│   │   ├── user/                        # Module quản lý người dùng
│   │   │   ├── domain/                  # Domain models
│   │   │   │   ├── User.java
│   │   │   │   ├── Role.java
│   │   │   │   └── UserProfile.java
│   │   │   ├── repository/              # Data access
│   │   │   │   └── UserRepository.java
│   │   │   ├── service/                 # Business logic
│   │   │   │   └── UserService.java
│   │   │   ├── controller/              # API endpoints
│   │   │   │   └── UserController.java
│   │   │   └── dto/                     # Data transfer objects
│   │   │       └── UserDTO.java
│   │   │
│   │   ├── auth/                        # Module authentication
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── service/
│   │   │   │   └── AuthService.java
│   │   │   └── controller/
│   │   │       └── AuthController.java
│   │   │
│   │   ├── movie/                       # Module quản lý phim
│   │   │   ├── domain/
│   │   │   │   ├── Movie.java
│   │   │   │   ├── Genre.java
│   │   │   │   └── MovieCast.java
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   │   └── MovieService.java
│   │   │   └── controller/
│   │   │       └── MovieController.java
│   │   │
│   │   ├── cinema/                      # Module quản lý rạp
│   │   │   ├── domain/
│   │   │   │   ├── Cinema.java
│   │   │   │   ├── Hall.java
│   │   │   │   ├── Seat.java
│   │   │   │   └── SeatType.java
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   │   └── CinemaService.java
│   │   │   └── controller/
│   │   │       └── CinemaController.java
│   │   │
│   │   ├── showtime/                    # Module quản lý suất chiếu
│   │   │   ├── domain/
│   │   │   │   ├── Showtime.java
│   │   │   │   └── ShowtimePricing.java
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   │   └── ShowtimeService.java
│   │   │   └── controller/
│   │   │       └── ShowtimeController.java
│   │   │
│   │   ├── booking/                     # Module đặt vé
│   │   │   ├── domain/
│   │   │   │   ├── Booking.java
│   │   │   │   ├── BookingStatus.java
│   │   │   │   └── BookingSeat.java
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   │   ├── BookingService.java
│   │   │   │   └── SeatLockService.java (Redis locking)
│   │   │   ├── controller/
│   │   │   │   └── BookingController.java
│   │   │   └── dto/
│   │   │       ├── BookingRequestDTO.java
│   │   │       └── BookingResponseDTO.java
│   │   │
│   │   ├── payment/                     # Module thanh toán
│   │   │   ├── domain/
│   │   │   │   ├── Payment.java
│   │   │   │   └── PaymentMethod.java
│   │   │   ├── service/
│   │   │   │   ├── PaymentService.java
│   │   │   │   ├── VNPayService.java
│   │   │   │   └── MomoService.java
│   │   │   ├── controller/
│   │   │   │   └── PaymentController.java
│   │   │   └── config/
│   │   │       └── PaymentGatewayConfig.java
│   │   │
│   │   ├── concession/                  # Module đồ ăn combo
│   │   │   ├── domain/
│   │   │   │   ├── Product.java
│   │   │   │   └── ComboOrder.java
│   │   │   ├── service/
│   │   │   └── controller/
│   │   │
│   │   ├── promotion/                   # Module khuyến mãi
│   │   │   ├── domain/
│   │   │   │   ├── Voucher.java
│   │   │   │   └── Promotion.java
│   │   │   ├── service/
│   │   │   │   └── PromotionService.java
│   │   │   └── controller/
│   │   │
│   │   ├── notification/                # Module thông báo
│   │   │   ├── service/
│   │   │   │   ├── EmailService.java
│   │   │   │   ├── SMSService.java
│   │   │   │   └── NotificationConsumer.java (Kafka/RabbitMQ)
│   │   │   └── template/
│   │   │       └── email-templates/
│   │   │
│   │   └── analytics/                   # Module báo cáo
│   │       ├── service/
│   │       │   ├── RevenueReportService.java
│   │       │   └── OccupancyReportService.java
│   │       └── controller/
│   │           └── AnalyticsController.java
│   │
│   ├── shared/                          # Code dùng chung
│   │   ├── domain/
│   │   │   ├── BaseEntity.java
│   │   │   └── AuditableEntity.java
│   │   ├── dto/
│   │   │   ├── ApiResponse.java
│   │   │   └── PagedResponse.java
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── ResourceNotFoundException.java
│   │   │   ├── BusinessException.java
│   │   │   └── ErrorCode.java
│   │   ├── util/
│   │   │   ├── DateTimeUtil.java
│   │   │   ├── QRCodeUtil.java
│   │   │   └── ValidationUtil.java
│   │   └── constant/
│   │       └── AppConstants.java
│   │
│   ├── infrastructure/                  # Infrastructure layer
│   │   ├── config/
│   │   │   ├── DatabaseConfig.java
│   │   │   ├── RedisConfig.java
│   │   │   ├── KafkaConfig.java (hoặc RabbitMQConfig.java)
│   │   │   ├── AsyncConfig.java
│   │   │   └── CorsConfig.java
│   │   ├── messaging/
│   │   │   ├── KafkaProducer.java
│   │   │   └── KafkaConsumer.java
│   │   ├── cache/
│   │   │   └── CacheService.java
│   │   └── scheduling/
│   │       └── ScheduledTasks.java
│   │
│   └── event/                           # Domain events (Event-driven)
│       ├── BookingCreatedEvent.java
│       ├── PaymentCompletedEvent.java
│       ├── SeatLockedEvent.java
│       └── EventPublisher.java
│
└── src/main/resources/
    ├── application.yml
    ├── application-dev.yml
    ├── application-prod.yml
    └── db/migration/
        └── V1__initial_schema.sql (Flyway migration)
```

### 1.4.2. Lợi ích của Modular Monolith

#### So với Monolith truyền thống:
✅ **Tổ chức code tốt hơn**: Modules có boundary rõ ràng, dễ navigate
✅ **Dễ test**: Có thể test từng module độc lập
✅ **Team collaboration**: Nhiều team có thể làm việc trên các modules khác nhau
✅ **Maintainability**: Thay đổi trong một module ít ảnh hưởng đến modules khác

#### So với Microservices:
✅ **Đơn giản hơn**: Không cần service discovery, API gateway, distributed tracing
✅ **Deployment dễ hơn**: Chỉ deploy 1 artifact thay vì deploy nhiều services
✅ **Performance tốt hơn**: In-process communication (không qua network)
✅ **Transaction dễ hơn**: Shared database, dùng local transaction thay vì distributed transaction
✅ **Development nhanh hơn**: Không phải setup infrastructure phức tạp
✅ **Chi phí thấp hơn**: Ít server resources hơn

#### Khả năng chuyển đổi sang Microservices:
- Nếu một module cần scale riêng (ví dụ: booking module trong giờ cao điểm), có thể extract thành microservice
- Module boundaries đã rõ ràng nên việc extract tương đối dễ dàng
- Sử dụng event-driven architecture giúp decoupling sẵn

### 1.4.3. Communication giữa các Modules

**1. Direct Method Call (Synchronous)**
```java
// BookingService gọi trực tiếp ShowtimeService
@Service
public class BookingService {
    @Autowired
    private ShowtimeService showtimeService;

    public Booking createBooking(BookingRequest request) {
        Showtime showtime = showtimeService.getShowtime(request.getShowtimeId());
        // Business logic
    }
}
```
- **Ưu điểm**: Đơn giản, fast, transaction local
- **Nhược điểm**: Tight coupling
- **Khi nào dùng**: Query data, read operations

**2. Domain Events (Asynchronous - Loosely Coupled)**
```java
// BookingService publish event
@Service
public class BookingService {
    @Autowired
    private EventPublisher eventPublisher;

    @Transactional
    public Booking createBooking(BookingRequest request) {
        Booking booking = new Booking(/*...*/);
        bookingRepository.save(booking);

        // Publish event
        eventPublisher.publish(new BookingCreatedEvent(booking.getId(), booking.getUserId()));

        return booking;
    }
}

// NotificationService lắng nghe event
@Service
public class NotificationService {
    @EventListener
    public void handleBookingCreated(BookingCreatedEvent event) {
        // Send email confirmation
        emailService.sendBookingConfirmation(event.getUserId(), event.getBookingId());
    }
}
```
- **Ưu điểm**: Loosely coupled, async, non-blocking
- **Nhược điểm**: Eventual consistency, phức tạp hơn
- **Khi nào dùng**: Side effects (send email, update analytics), background tasks

**3. Message Queue (Kafka/RabbitMQ - Asynchronous)**
```java
// PaymentService gửi message khi thanh toán thành công
@Service
public class PaymentService {
    @Autowired
    private KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate;

    public void processPayment(Payment payment) {
        // Process payment logic

        // Send message to Kafka
        PaymentCompletedEvent event = new PaymentCompletedEvent(payment.getId(), payment.getBookingId());
        kafkaTemplate.send("payment-completed-topic", event);
    }
}

// BookingService consume message
@Service
public class BookingService {
    @KafkaListener(topics = "payment-completed-topic")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        // Update booking status to CONFIRMED
        Booking booking = bookingRepository.findById(event.getBookingId());
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
    }
}
```
- **Ưu điểm**: Decoupled, scalable, reliable (message không bị mất)
- **Nhược điểm**: Infrastructure overhead (Kafka/RabbitMQ cluster)
- **Khi nào dùng**: Critical async operations, need retry mechanism, event sourcing

### 1.4.4. Module Dependencies Rules

**Nguyên tắc thiết kế modules:**

1. **Không có circular dependency**:
   ```
   ❌ BAD: Module A → Module B → Module A
   ✅ GOOD: Module A → Module B → Shared Module
   ```

2. **Module chỉ depend vào Shared Module**:
   ```
   ✅ booking module → shared (domain, dto, exception)
   ✅ payment module → shared
   ❌ booking module → payment module (direct dependency)
   ```

3. **Communication qua interface hoặc events**:
   ```java
   // Shared module
   public interface PaymentServiceInterface {
       PaymentResult processPayment(PaymentRequest request);
   }

   // Payment module implements
   @Service
   public class PaymentService implements PaymentServiceInterface {
       // Implementation
   }

   // Booking module chỉ depend vào interface
   @Service
   public class BookingService {
       @Autowired
       private PaymentServiceInterface paymentService;
   }
   ```

4. **Database access isolation**:
   - Mỗi module chỉ truy cập vào tables của mình
   - Nếu cần data từ module khác, gọi qua service layer

## 1.5. Distributed Locking với Redis (Concurrency Control)

### 1.5.1. Vấn đề Double Booking

Khi 2 users cùng chọn 1 ghế cùng lúc:
```
Time T0: User A click chọn ghế A5
Time T1: User B click chọn ghế A5 (0.5s sau A)
Time T2: Server check DB, ghế A5 available
Time T3: Server check DB, ghế A5 available (vẫn chưa update từ request A)
Time T4: Server save booking cho A
Time T5: Server save booking cho B
Result: ❌ Cả A và B đều đặt được ghế A5 → Double booking!
```

### 1.5.2. Giải pháp với Redis Distributed Lock

```java
@Service
public class SeatLockService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final int LOCK_TIMEOUT = 300; // 5 phút

    /**
     * Try to lock a seat for a user
     * @return true if lock successful, false if seat already locked
     */
    public boolean lockSeat(Long showtimeId, String seatNumber, Long userId) {
        String lockKey = "seat:lock:" + showtimeId + ":" + seatNumber;
        String lockValue = userId.toString();

        // SET key value NX EX timeout
        // NX = Not eXists (chỉ set nếu key chưa tồn tại)
        // EX = EXpire (set TTL)
        Boolean success = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, lockValue, LOCK_TIMEOUT, TimeUnit.SECONDS);

        return Boolean.TRUE.equals(success);
    }

    /**
     * Release lock when user completes booking or cancels
     */
    public void unlockSeat(Long showtimeId, String seatNumber, Long userId) {
        String lockKey = "seat:lock:" + showtimeId + ":" + seatNumber;
        String lockValue = redisTemplate.opsForValue().get(lockKey);

        // Chỉ unlock nếu lock thuộc về user này (tránh unlock nhầm)
        if (lockValue != null && lockValue.equals(userId.toString())) {
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * Extend lock timeout nếu user cần thêm thời gian
     */
    public boolean extendLock(Long showtimeId, String seatNumber, Long userId) {
        String lockKey = "seat:lock:" + showtimeId + ":" + seatNumber;
        String lockValue = redisTemplate.opsForValue().get(lockKey);

        if (lockValue != null && lockValue.equals(userId.toString())) {
            return redisTemplate.expire(lockKey, LOCK_TIMEOUT, TimeUnit.SECONDS);
        }
        return false;
    }
}
```

**Flow khi user chọn ghế:**
```
1. User A click chọn ghế A5
2. Frontend gọi API: POST /api/bookings/lock-seat
3. Backend:
   - SeatLockService.lockSeat(showtimeId=123, seatNumber="A5", userId=1)
   - Redis: SET seat:lock:123:A5 "1" NX EX 300
   - Redis returns: OK (success)
4. Backend update DB: seat.status = LOCKED, seat.locked_until = now() + 5 minutes
5. Return to frontend: {success: true, locked_until: "2025-01-15T10:05:00"}

6. User B click chọn ghế A5 (1 giây sau A)
7. Frontend gọi API: POST /api/bookings/lock-seat
8. Backend:
   - SeatLockService.lockSeat(showtimeId=123, seatNumber="A5", userId=2)
   - Redis: SET seat:lock:123:A5 "2" NX EX 300
   - Redis returns: NULL (key đã tồn tại, lock failed)
9. Return to frontend: {success: false, message: "Ghế đã có người chọn"}
10. Frontend hiển thị thông báo lỗi, user B phải chọn ghế khác
```

### 1.5.3. Scheduled Task tự động giải phóng ghế

```java
@Component
public class SeatUnlockScheduler {
    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private SeatLockService seatLockService;

    /**
     * Chạy mỗi 1 phút để unlock các ghế đã hết hạn
     */
    @Scheduled(fixedRate = 60000) // 60 seconds
    public void unlockExpiredSeats() {
        // Query tất cả ghế có status = LOCKED và locked_until < now()
        List<Seat> expiredSeats = seatRepository.findExpiredLockedSeats(LocalDateTime.now());

        for (Seat seat : expiredSeats) {
            // Update DB
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setLockedUntil(null);
            seatRepository.save(seat);

            // Delete Redis lock key
            String lockKey = "seat:lock:" + seat.getShowtimeId() + ":" + seat.getSeatNumber();
            redisTemplate.delete(lockKey);

            log.info("Auto unlocked seat: {} for showtime: {}", seat.getSeatNumber(), seat.getShowtimeId());
        }
    }
}
```

## 1.6. Event-Driven Architecture với Kafka/RabbitMQ

### 1.6.1. Tại sao cần Message Queue?

**Use cases:**
1. **Async processing**: Gửi email xác nhận không cần đợi đồng bộ
2. **Decoupling**: Booking module không cần biết về Notification module
3. **Reliability**: Message không bị mất khi service down (message queue persist to disk)
4. **Scalability**: Có thể scale consumer độc lập
5. **Event sourcing**: Lưu trữ tất cả events để audit trail

### 1.6.2. Kafka vs RabbitMQ

| Feature | Kafka | RabbitMQ |
|---------|-------|----------|
| **Pattern** | Pub/Sub (Topic-based) | Pub/Sub, Point-to-Point (Queue) |
| **Throughput** | Very High (millions/sec) | High (tens of thousands/sec) |
| **Message Retention** | Persistent (configurable) | Deleted after consumed |
| **Use Case** | Event streaming, Log aggregation | Task queues, RPC |
| **Learning Curve** | Steeper | Easier |
| **Best For** | High throughput, Event sourcing | Traditional messaging |

**Recommendation**:
- Nếu cần high throughput + event sourcing → **Kafka**
- Nếu cần đơn giản, traditional message queue → **RabbitMQ**
- Có thể dùng **cả hai**: Kafka cho events, RabbitMQ cho task queues

### 1.6.3. Ví dụ: Booking Flow với Kafka

```java
// 1. BookingService tạo booking và publish event
@Service
public class BookingService {
    @Autowired
    private KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate;

    @Transactional
    public Booking createBooking(BookingRequest request) {
        // Validate và tạo booking
        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setShowtimeId(request.getShowtimeId());
        booking.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking);

        // Publish event to Kafka
        BookingCreatedEvent event = BookingCreatedEvent.builder()
            .bookingId(booking.getId())
            .userId(booking.getUserId())
            .showtimeId(booking.getShowtimeId())
            .totalAmount(booking.getTotalAmount())
            .timestamp(LocalDateTime.now())
            .build();

        kafkaTemplate.send("booking-events", event.getBookingId().toString(), event);

        return booking;
    }
}

// 2. NotificationService consume event và gửi email
@Service
public class NotificationConsumer {
    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "booking-events", groupId = "notification-service")
    public void handleBookingCreated(BookingCreatedEvent event) {
        log.info("Received booking event: {}", event.getBookingId());

        // Gửi email xác nhận
        emailService.sendBookingConfirmation(
            event.getUserId(),
            event.getBookingId(),
            event.getTotalAmount()
        );
    }
}

// 3. AnalyticsService consume event để update thống kê
@Service
public class AnalyticsConsumer {
    @Autowired
    private AnalyticsService analyticsService;

    @KafkaListener(topics = "booking-events", groupId = "analytics-service")
    public void handleBookingCreated(BookingCreatedEvent event) {
        log.info("Update analytics for booking: {}", event.getBookingId());

        // Update revenue report
        analyticsService.recordBooking(
            event.getShowtimeId(),
            event.getTotalAmount()
        );
    }
}
```

**Flow diagram:**
```
BookingService
    │
    ├─[CREATE]─> Booking (DB)
    │
    └─[PUBLISH]─> Kafka Topic: "booking-events"
                      │
                      ├─[CONSUME]─> NotificationService → Send Email
                      │
                      ├─[CONSUME]─> AnalyticsService → Update Stats
                      │
                      └─[CONSUME]─> (Future services...)
```

**Lợi ích:**
- Booking service không biết về Notification/Analytics service
- Dễ dàng thêm consumers mới (ví dụ: SMS service)
- Nếu Email service down, message vẫn được lưu trong Kafka, sẽ xử lý khi service up lại

## 1.7. Caching Strategy với Redis

### 1.7.1. Các layer cache

```
[Client Browser]
      ↓
[HTTP Cache] (CDN, nginx)
      ↓
[Application Cache] (Redis)
      ↓
[Database Query Cache]
      ↓
[PostgreSQL]
```

### 1.7.2. Cache patterns

**1. Cache-Aside (Lazy Loading)**
```java
@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RedisTemplate<String, Movie> redisTemplate;

    public Movie getMovie(Long movieId) {
        String cacheKey = "movie:" + movieId;

        // 1. Try cache first
        Movie movie = redisTemplate.opsForValue().get(cacheKey);
        if (movie != null) {
            return movie; // Cache hit
        }

        // 2. Cache miss → Load from DB
        movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        // 3. Store in cache
        redisTemplate.opsForValue().set(cacheKey, movie, 1, TimeUnit.HOURS);

        return movie;
    }
}
```

**2. Write-Through Cache**
```java
public void updateMovie(Long movieId, MovieDTO dto) {
    // 1. Update DB
    Movie movie = movieRepository.findById(movieId).orElseThrow();
    movie.setTitle(dto.getTitle());
    movieRepository.save(movie);

    // 2. Update cache immediately
    String cacheKey = "movie:" + movieId;
    redisTemplate.opsForValue().set(cacheKey, movie, 1, TimeUnit.HOURS);
}
```

**3. Cache Invalidation**
```java
public void deleteMovie(Long movieId) {
    // 1. Delete from DB
    movieRepository.deleteById(movieId);

    // 2. Invalidate cache
    String cacheKey = "movie:" + movieId;
    redisTemplate.delete(cacheKey);

    // 3. Invalidate list cache
    redisTemplate.delete("movies:list:*");
}
```

### 1.7.3. Các data nên cache

| Data | TTL | Pattern |
|------|-----|---------|
| **Movie details** | 1 hour | Cache-Aside |
| **Cinema list** | 1 day | Cache-Aside |
| **Showtime schedule** | 15 mins | Cache-Aside |
| **Available seats** | NO CACHE | (Real-time from DB) |
| **User session** | 24 hours | Write-Through |
| **Seat locks** | 5 mins | Write-Through + Auto-expire |

**Lưu ý**:
- Không nên cache available seats vì cần real-time accuracy
- Seat locks dùng Redis làm source of truth (không store DB)

## 1.8. Database Design Overview

### 1.8.1. PostgreSQL vs MySQL

| Feature | PostgreSQL | MySQL |
|---------|-----------|-------|
| **ACID Compliance** | Full | Full (InnoDB) |
| **JSON Support** | Native JSONB (fast) | JSON (slower) |
| **Full-text Search** | Built-in | Limited |
| **Window Functions** | Excellent | Good (8.0+) |
| **Performance** | Better for complex queries | Better for simple queries |
| **Use Case** | OLAP, Analytics | OLTP, Web apps |

**Recommendation**: **PostgreSQL** vì:
- JSONB hỗ trợ flexible data (movie cast, hall configuration)
- Full-text search cho tìm kiếm phim
- Window functions cho báo cáo doanh thu
- Better consistency guarantees

### 1.8.2. Tables Overview

**Core Tables:**
1. `users` - Người dùng
2. `roles` - Vai trò (ADMIN, MANAGER, STAFF, CUSTOMER)
3. `movies` - Phim
4. `genres` - Thể loại phim
5. `movie_genres` - Liên kết phim và thể loại (many-to-many)
6. `cinemas` - Cụm rạp
7. `halls` - Phòng chiếu
8. `seats` - Ghế ngồi
9. `showtimes` - Suất chiếu
10. `bookings` - Đặt vé
11. `booking_seats` - Chi tiết ghế đã đặt
12. `payments` - Thanh toán
13. `vouchers` - Voucher giảm giá
14. `products` - Đồ ăn nước uống
15. `combo_orders` - Đặt combo

**Relationships:**
```
users 1───N bookings N───1 showtimes N───1 movies
                │                 │
                N                 N
                │                 │
        booking_seats         seats N───1 halls N───1 cinemas
                │
                N───1
                  │
              payments
```

## 1.9. Deployment Architecture

### 1.9.1. Docker Compose (Development)

```yaml
version: '3.8'

services:
  # PostgreSQL Database
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: cinema_booking
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin123
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  # Redis Cache & Lock
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes
    volumes:
      - redis_data:/data

  # Kafka (Event Streaming)
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092

  # Spring Boot Backend
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - redis
      - kafka
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/cinema_booking
      SPRING_REDIS_HOST: redis
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092

  # React Frontend
  frontend:
    build: ./frontend
    ports:
      - "3000:80"
    depends_on:
      - backend

volumes:
  postgres_data:
  redis_data:
```

### 1.9.2. Production Deployment (Cloud)

```
[Load Balancer] (AWS ALB / Azure Load Balancer)
      │
      ├─> [Backend Instances] x3 (Docker containers)
      │         │
      │         ├─> [PostgreSQL] (RDS / Azure Database)
      │         ├─> [Redis Cluster] (ElastiCache / Azure Cache)
      │         └─> [Kafka Cluster] (MSK / Azure Event Hubs)
      │
      └─> [Frontend] (S3 + CloudFront / Azure Static Web Apps)
```

## 1.10. Kết luận chương

Chương 1 đã trình bày tổng quan về hệ thống Cinema Booking với:
- Mục tiêu xây dựng hệ thống quy mô trung-lớn cho chuỗi rạp multi-location
- Công nghệ stack bắt buộc: Spring Boot 3.x, React+TS, PostgreSQL, Redis, Kafka/RabbitMQ, Docker
- Kiến trúc Modular Monolith để cân bằng giữa đơn giản và khả năng mở rộng
- Giải pháp distributed locking với Redis để xử lý concurrency
- Event-driven architecture với Kafka/RabbitMQ cho async processing

Các chương tiếp theo sẽ đi sâu vào:
- **Chương 2**: Xác định yêu cầu chi tiết (Functional & Non-functional requirements)
- **Chương 3**: Phân tích nhu cầu (Use cases, Activity diagrams, Sequence diagrams)
- **Chương 4**: Thiết kế kiến trúc chi tiết (Class diagrams, Design patterns, API design)
- **Chương 5**: Thiết kế cơ sở dữ liệu (ER diagram, Schema, Indexing strategy)
- **Chương 6**: Xây dựng mẫu thử (Implementation details, Code samples, Testing)
- **Chương 7**: Kết luận và hướng phát triển

---

**Tài liệu tiếp theo**: [02_XAC_DINH_YEU_CAU.md](./02_XAC_DINH_YEU_CAU.md)
