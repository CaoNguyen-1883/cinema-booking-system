# CHƯƠNG 4: THIẾT KẾ KIẾN TRÚC HỆ THỐNG

## Giới thiệu

Sau khi đã phân tích chi tiết các yêu cầu và mô hình hóa hành vi của hệ thống ở Chương 2 và 3, chương này sẽ trình bày thiết kế kiến trúc chi tiết của hệ thống đặt vé rạp chiếu phim. Kiến trúc phần mềm đóng vai trò nền tảng, quyết định đến khả năng mở rộng, bảo trì, hiệu năng và độ tin cậy của hệ thống.

Chương này sẽ trình bày các nội dung sau:

1. **Kiến trúc tổng thể:** Mô hình 3-tier, các thành phần chính và mối quan hệ giữa chúng
2. **Kiến trúc phân lớp:** Chi tiết từng lớp (Presentation, Business Logic, Data Access, Infrastructure)
3. **Thiết kế module:** Class diagram và mối quan hệ giữa các class trong từng module
4. **Design Patterns:** Các mẫu thiết kế được áp dụng để giải quyết vấn đề cụ thể
5. **API Design:** Thiết kế RESTful API với định nghĩa endpoints, request/response format
6. **Xử lý concurrency:** Giải pháp distributed locking và transaction management

Các quyết định kiến trúc được đưa ra dựa trên:
- Yêu cầu phi chức năng (NFR) đã xác định ở Chương 2
- Best practices của Spring Boot framework
- Kinh nghiệm thực tế từ các hệ thống tương tự
- Khả năng mở rộng trong tương lai

---

## 4.1 Kiến trúc tổng thể

### 4.1.1 Mô hình 3-Tier Architecture với Modular Monolith

Hệ thống áp dụng kiến trúc **3-tier** kết hợp với **Modular Monolith** - một kiến trúc hiện đại giúp tổ chức code theo business domain thay vì technical layer:

**Tại sao chọn Modular Monolith?**
- **Domain-driven:** Code được tổ chức theo nghiệp vụ (auth, movie, booking...) thay vì layer (controller, service, repository)
- **Dễ maintain:** Khi sửa một tính năng, tất cả code liên quan nằm trong cùng module
- **Microservices-ready:** Dễ dàng tách thành microservices khi cần scale
- **Clear boundaries:** Mỗi module có trách nhiệm riêng, giảm coupling

Hệ thống áp dụng kiến trúc 3-tier (3 tầng) phổ biến trong các ứng dụng web enterprise:

```
┌─────────────────────────────────────────────────────────────────┐
│                    PRESENTATION TIER (Client)                   │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │
│  │   Web App    │  │  Mobile App  │  │  Admin Panel │        │
│  │   (React)    │  │  (Optional)  │  │   (React)    │        │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘        │
│         │                 │                  │                 │
└─────────┼─────────────────┼──────────────────┼─────────────────┘
          │                 │                  │
          │    HTTPS/JSON   │                  │
          ▼                 ▼                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                    APPLICATION TIER (Server)                    │
│                                                                 │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │              Spring Boot Application                      │ │
│  │                                                           │ │
│  │  ┌──────────────────────────────────────────────────┐    │ │
│  │  │         Presentation Layer                       │    │ │
│  │  │  - REST Controllers                              │    │ │
│  │  │  - Request/Response DTOs                         │    │ │
│  │  │  - Input Validation                              │    │ │
│  │  │  - Exception Handlers                            │    │ │
│  │  └─────────────────────┬────────────────────────────┘    │ │
│  │                        │                                  │ │
│  │  ┌─────────────────────▼────────────────────────────┐    │ │
│  │  │         Business Logic Layer                     │    │ │
│  │  │  - Services (Business Logic)                     │    │ │
│  │  │  - Domain Models                                 │    │ │
│  │  │  - Business Rules Validation                     │    │ │
│  │  │  - Transaction Management                        │    │ │
│  │  └─────────────────────┬────────────────────────────┘    │ │
│  │                        │                                  │ │
│  │  ┌─────────────────────▼────────────────────────────┐    │ │
│  │  │         Data Access Layer                        │    │ │
│  │  │  - Repositories (JPA)                            │    │ │
│  │  │  - Entity Models                                 │    │ │
│  │  │  - Database Queries                              │    │ │
│  │  └─────────────────────┬────────────────────────────┘    │ │
│  │                        │                                  │ │
│  │  ┌─────────────────────▼────────────────────────────┐    │ │
│  │  │         Infrastructure Layer                     │    │ │
│  │  │  - Redis Client                                  │    │ │
│  │  │  - Payment Gateway Client                        │    │ │
│  │  │  - Email Service Client                          │    │ │
│  │  │  - Scheduler Jobs                                │    │ │
│  │  └──────────────────────────────────────────────────┘    │ │
│  │                                                           │ │
│  └───────────────────────────────────────────────────────────┘ │
│                                                                 │
└─────────┬───────────────────┬──────────────────────┬───────────┘
          │                   │                      │
          ▼                   ▼                      ▼
┌─────────────────────────────────────────────────────────────────┐
│                        DATA TIER                                │
│                                                                 │
│  ┌─────────────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │   PostgreSQL    │  │    Redis     │  │  External APIs   │  │
│  │                 │  │              │  │                  │  │
│  │  - Users        │  │  - Session   │  │  - VNPay        │  │
│  │  - Movies       │  │  - Cache     │  │  - Momo         │  │
│  │  - Theaters     │  │  - Locks     │  │  - SMTP/Email   │  │
│  │  - Shows        │  │              │  │                  │  │
│  │  - Bookings     │  │              │  │                  │  │
│  │  - Seats        │  │              │  │                  │  │
│  └─────────────────┘  └──────────────┘  └──────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

**Giải thích các tier:**

1. **Presentation Tier (Client):**
   - Chạy trên trình duyệt của người dùng
   - Hiển thị giao diện, xử lý tương tác người dùng
   - Gửi HTTP requests đến Application Tier
   - Nhận JSON responses và render UI
   - Không chứa business logic

2. **Application Tier (Server):**
   - Chạy trên server (Spring Boot application)
   - Xử lý toàn bộ business logic
   - Quản lý authentication, authorization
   - Gọi Data Tier để lưu/đọc dữ liệu
   - Tích hợp với external services
   - Stateless (không lưu trạng thái client)

3. **Data Tier:**
   - PostgreSQL: Lưu trữ dữ liệu persistent
   - Redis: Caching, session, distributed locking
   - External APIs: Payment gateway, email service

**Lợi ích của 3-tier:**
- **Separation of Concerns:** Mỗi tier có trách nhiệm riêng biệt
- **Scalability:** Có thể scale từng tier độc lập (scale-out app tier, scale-up database tier)
- **Maintainability:** Dễ bảo trì, thay đổi một tier không ảnh hưởng tier khác
- **Security:** Presentation tier không truy cập trực tiếp database
- **Reusability:** Business logic có thể tái sử dụng cho nhiều clients (web, mobile)

### 4.1.2 Component Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                   Spring Boot Application                           │
│                                                                     │
│  ┌────────────────────────────────────────────────────────────┐   │
│  │                    API Gateway / Router                    │   │
│  │         (Spring MVC DispatcherServlet)                     │   │
│  └─────────────────────────┬──────────────────────────────────┘   │
│                            │                                       │
│  ┌─────────────────────────▼──────────────────────────────────┐   │
│  │              Security Filter Chain                         │   │
│  │  - JWT Authentication Filter                               │   │
│  │  - CORS Filter                                             │   │
│  │  - Rate Limiting Filter                                    │   │
│  └─────────────────────────┬──────────────────────────────────┘   │
│                            │                                       │
│         ┌──────────────────┼──────────────────┐                   │
│         │                  │                  │                   │
│  ┌──────▼───────┐  ┌───────▼────────┐  ┌─────▼──────┐           │
│  │   User       │  │   Movie        │  │  Booking   │           │
│  │  Controller  │  │  Controller    │  │ Controller │  ...      │
│  └──────┬───────┘  └───────┬────────┘  └─────┬──────┘           │
│         │                  │                  │                   │
│  ┌──────▼───────┐  ┌───────▼────────┐  ┌─────▼──────┐           │
│  │   User       │  │   Movie        │  │  Booking   │           │
│  │   Service    │  │   Service      │  │  Service   │  ...      │
│  └──────┬───────┘  └───────┬────────┘  └─────┬──────┘           │
│         │                  │                  │                   │
│  ┌──────▼───────┐  ┌───────▼────────┐  ┌─────▼──────┐           │
│  │   User       │  │   Movie        │  │  Booking   │           │
│  │ Repository   │  │ Repository     │  │ Repository │  ...      │
│  └──────┬───────┘  └───────┬────────┘  └─────┬──────┘           │
│         │                  │                  │                   │
│         └──────────────────┼──────────────────┘                   │
│                            │                                       │
│  ┌─────────────────────────▼──────────────────────────────────┐   │
│  │                  Data Source Manager                       │   │
│  │         (HikariCP Connection Pool)                         │   │
│  └─────────────────────────┬──────────────────────────────────┘   │
│                            │                                       │
└────────────────────────────┼───────────────────────────────────────┘
                             │
                             ▼
                   ┌──────────────────┐
                   │   PostgreSQL     │
                   │    Database      │
                   └──────────────────┘


┌─────────────────────────────────────────────────────────────────────┐
│                   Infrastructure Components                         │
│                                                                     │
│  ┌─────────────────┐  ┌──────────────────┐  ┌──────────────────┐  │
│  │  Redis Client   │  │  Payment Gateway │  │  Email Service   │  │
│  │                 │  │     Client       │  │     Client       │  │
│  │  - Jedis Pool   │  │  - VNPay SDK     │  │  - JavaMailSender│  │
│  │  - Lock Manager │  │  - Momo SDK      │  │  - SMTP Config   │  │
│  └─────────────────┘  └──────────────────┘  └──────────────────┘  │
│                                                                     │
│  ┌─────────────────┐  ┌──────────────────┐  ┌──────────────────┐  │
│  │ Scheduler Jobs  │  │  QR Code         │  │  File Storage    │  │
│  │                 │  │  Generator       │  │                  │  │
│  │ - Lock Cleanup  │  │  - ZXing Library │  │  - Local/S3      │  │
│  │ - Report Gen    │  │                  │  │                  │  │
│  └─────────────────┘  └──────────────────┘  └──────────────────┘  │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 4.1.3 Deployment Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                        Production Environment                     │
│                                                                  │
│  ┌────────────────────────────────────────────────────────┐     │
│  │                    Load Balancer                       │     │
│  │                 (Nginx / AWS ALB)                      │     │
│  └─────────────────────┬──────────────────────────────────┘     │
│                        │                                         │
│            ┌───────────┴───────────┐                            │
│            │                       │                            │
│  ┌─────────▼──────┐      ┌─────────▼──────┐                    │
│  │  App Server 1  │      │  App Server 2  │                    │
│  │                │      │                │                    │
│  │ Spring Boot    │      │ Spring Boot    │                    │
│  │ (JAR)          │      │ (JAR)          │                    │
│  │ Port: 8080     │      │ Port: 8080     │  (Scale-out)       │
│  └────────┬───────┘      └────────┬───────┘                    │
│           │                       │                            │
│           └───────────┬───────────┘                            │
│                       │                                         │
│         ┌─────────────┼─────────────┬────────────┐             │
│         │             │             │            │             │
│  ┌──────▼──────┐  ┌───▼────────┐ ┌─▼────────┐ ┌▼──────────┐   │
│  │ PostgreSQL  │  │   Redis    │ │  VNPay   │ │   Email   │   │
│  │  Primary    │  │  Cluster   │ │ Gateway  │ │  Service  │   │
│  │             │  │            │ │ (External│ │ (External)│   │
│  │ Port: 5432  │  │ Port: 6379 │ │          │ │           │   │
│  └──────┬──────┘  └────────────┘ └──────────┘ └───────────┘   │
│         │                                                       │
│  ┌──────▼──────┐                                               │
│  │ PostgreSQL  │                                               │
│  │  Replica    │  (Read-only, for reporting)                  │
│  │             │                                               │
│  └─────────────┘                                               │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

**Deployment considerations:**

1. **Load Balancer:**
   - Phân phối traffic đều giữa các app servers
   - Health check endpoints: `/actuator/health`
   - Sticky session không cần thiết (stateless JWT)

2. **Application Servers:**
   - Chạy 2+ instances để đảm bảo high availability
   - Horizontal scaling khi traffic tăng
   - Mỗi instance là stateless, không chia sẻ memory

3. **Database:**
   - Primary: Xử lý write operations
   - Replica: Xử lý read operations (reports, analytics)
   - Connection pooling: HikariCP (max 20 connections/instance)

4. **Redis:**
   - Redis Cluster cho high availability
   - Persistence: AOF (Append-Only File) mode
   - Eviction policy: allkeys-lru (cho cache)

---

## 4.2 Kiến trúc phân lớp (Layered Architecture)

Hệ thống áp dụng kiến trúc 4 lớp (4-layer architecture) với sự phân tách rõ ràng trách nhiệm:

```
┌────────────────────────────────────────────────────────────────┐
│                    Presentation Layer                          │
│  (Controllers, DTOs, Exception Handlers)                       │
└────────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌────────────────────────────────────────────────────────────────┐
│                   Business Logic Layer                         │
│  (Services, Domain Models, Business Rules)                     │
└────────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌────────────────────────────────────────────────────────────────┐
│                    Data Access Layer                           │
│  (Repositories, Entities, JPA Queries)                         │
└────────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌────────────────────────────────────────────────────────────────┐
│                   Infrastructure Layer                         │
│  (Redis, Payment, Email, Scheduler)                            │
└────────────────────────────────────────────────────────────────┘
```

### 4.2.1 Presentation Layer

**Trách nhiệm:**
- Nhận HTTP requests từ client
- Validate input data (format, required fields)
- Gọi Business Logic Layer để xử lý
- Transform domain models thành DTOs
- Trả về HTTP responses (JSON)
- Xử lý exceptions thành error responses

**Các thành phần:**

1. **Controllers:** RESTful endpoints
   ```
   @RestController
   @RequestMapping("/api/bookings")
   - POST /api/bookings/seats/lock
   - POST /api/bookings/checkout
   - GET /api/bookings/history
   - DELETE /api/bookings/{id}
   ```

2. **Request DTOs:** Input từ client
   ```
   SeatLockRequest {
     showId: Long
     seatIds: List<String>
   }

   CheckoutRequest {
     bookingId: Long
     paymentMethod: String
     pointsToUse: Integer
   }
   ```

3. **Response DTOs:** Output cho client
   ```
   BookingResponse {
     bookingId: Long
     showInfo: ShowInfoDTO
     seats: List<SeatDTO>
     totalAmount: BigDecimal
     status: String
     qrCode: String
   }

   ErrorResponse {
     timestamp: LocalDateTime
     status: Integer
     error: String
     message: String
     path: String
   }
   ```

4. **Exception Handlers:**
   ```
   @RestControllerAdvice
   - @ExceptionHandler(SeatAlreadyLockedException.class)
   - @ExceptionHandler(BookingNotFoundException.class)
   - @ExceptionHandler(PaymentFailedException.class)
   - @ExceptionHandler(ValidationException.class)
   ```

5. **Validators:**
   ```
   @Valid annotations
   - @NotNull, @NotBlank
   - @Email, @Size
   - @Pattern, @Min, @Max
   - Custom validators: @ValidSeatSelection
   ```

**Package structure (Modular Monolith):**

Thay vì tổ chức theo layer truyền thống, hệ thống sử dụng Modular Monolith với mỗi module chứa đầy đủ các layer:

```
com.cinema
├── auth/                          # Module xác thực
│   ├── controller/
│   │   └── AuthController.java
│   ├── dto/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   └── AuthResponse.java
│   ├── service/
│   │   └── AuthService.java
│   └── security/
│       ├── JwtTokenProvider.java
│       └── JwtAuthenticationFilter.java
│
├── user/                          # Module người dùng
│   ├── controller/
│   │   ├── UserController.java
│   │   └── AdminUserController.java
│   ├── dto/
│   ├── entity/
│   │   └── User.java
│   ├── repository/
│   │   └── UserRepository.java
│   └── service/
│       └── UserService.java
│
├── movie/                         # Module phim
│   ├── controller/
│   │   ├── MovieController.java
│   │   ├── AdminMovieController.java
│   │   └── GenreController.java
│   ├── dto/
│   ├── entity/
│   │   ├── Movie.java
│   │   └── Genre.java
│   ├── repository/
│   └── service/
│
├── cinema/                        # Module rạp chiếu
│   ├── controller/
│   │   ├── CinemaController.java
│   │   └── AdminCinemaController.java
│   ├── entity/
│   │   ├── Cinema.java
│   │   ├── Hall.java
│   │   └── Seat.java
│   ├── repository/
│   └── service/
│
├── show/                          # Module suất chiếu
│   ├── controller/
│   │   ├── ShowController.java
│   │   └── AdminShowController.java
│   ├── entity/
│   │   ├── Show.java
│   │   └── ShowSeat.java
│   ├── repository/
│   └── service/
│
├── booking/                       # Module đặt vé
│   ├── controller/
│   ├── entity/
│   │   ├── Booking.java
│   │   └── BookingSeat.java
│   ├── repository/
│   └── service/
│
├── notification/                  # Module thông báo
│   └── service/
│       └── EmailService.java
│
├── storage/                       # Module lưu trữ file
│   └── service/
│       └── FileStorageService.java
│
└── shared/                        # Module dùng chung
    ├── config/
    │   ├── SecurityConfig.java
    │   └── OpenApiConfig.java
    ├── dto/
    │   └── ApiResponse.java
    ├── entity/
    │   └── BaseEntity.java        # Audit fields
    └── exception/
        ├── GlobalExceptionHandler.java
        ├── BusinessException.java
        └── ErrorCode.java
```

**Lợi ích của cấu trúc này:**
- Mỗi module là một bounded context hoàn chỉnh
- Dễ tìm code: muốn sửa Movie → vào module movie
- Dễ test: test từng module độc lập
- Dễ tách microservices: mỗi module có thể trở thành một service

### 4.2.2 Business Logic Layer

**Trách nhiệm:**
- Chứa toàn bộ business logic của hệ thống
- Thực thi business rules
- Quản lý transactions
- Orchestrate các operations phức tạp
- Không phụ thuộc vào framework (framework-agnostic)

**Các thành phần:**

1. **Services:** Chứa business logic
   ```
   UserService:
   - register(UserRegistrationRequest)
   - login(LoginRequest): TokenResponse
   - updateProfile(userId, ProfileUpdateRequest)
   - changePassword(userId, PasswordChangeRequest)

   BookingService:
   - lockSeats(userId, showId, seatIds): BookingId
   - checkout(bookingId, paymentMethod, points): PaymentUrl
   - confirmBooking(bookingId, paymentData): Booking
   - cancelBooking(userId, bookingId): RefundId
   - getBookingHistory(userId, pageable): Page<Booking>

   ShowService:
   - getAvailableShows(movieId, date, cinemaId): List<Show>
   - getSeatMap(showId): SeatMap
   - createShow(CreateShowRequest): ShowId
   - checkScheduleConflict(hallId, date, time, duration): boolean
   ```

2. **Domain Models:** Represent business entities
   ```
   Booking {
     - bookingId: Long
     - user: User
     - show: Show
     - seats: List<Seat>
     - totalAmount: BigDecimal
     - status: BookingStatus
     - createdAt: LocalDateTime
     - paidAt: LocalDateTime

     + calculateTotalAmount(): BigDecimal
     + canBeCancelled(): boolean
     + isExpired(): boolean
   }

   Show {
     - showId: Long
     - movie: Movie
     - hall: Hall
     - showDate: LocalDate
     - showTime: LocalTime
     - basePrice: BigDecimal

     + getEndTime(): LocalTime
     + isConflictWith(otherShow): boolean
     + hasAvailableSeats(): boolean
   }
   ```

3. **Business Rules:**
   ```
   - User chỉ được chọn tối đa 10 ghế/booking
   - Booking phải thanh toán trong 15 phút
   - Hủy vé phải trước 1 giờ so với giờ chiếu
   - Điểm tích lũy: 1,000 VND = 1 điểm
   - Chỉ được dùng tối đa 50% giá vé bằng điểm
   ```

4. **Transaction Management:**
   ```java
   @Transactional
   public Booking confirmBooking(Long bookingId, PaymentData data) {
       // 1. Validate payment data
       // 2. Update booking status
       // 3. Update seats status
       // 4. Add user points
       // 5. Generate QR code
       // All in one transaction
   }
   ```

**Package structure:**
```
com.cinema.business
├── service
│   ├── UserService.java
│   ├── AuthenticationService.java
│   ├── MovieService.java
│   ├── ShowService.java
│   ├── BookingService.java
│   ├── PaymentService.java
│   ├── MembershipService.java
│   └── ReportService.java
├── domain
│   ├── User.java
│   ├── Movie.java
│   ├── Cinema.java
│   ├── Hall.java
│   ├── Show.java
│   ├── Seat.java
│   ├── Booking.java
│   └── BookingStatus.java (enum)
└── rule
    ├── BookingRule.java
    ├── PricingRule.java
    └── MembershipRule.java
```

### 4.2.3 Data Access Layer

**Trách nhiệm:**
- Truy cập database (CRUD operations)
- Chuyển đổi giữa domain models và entities
- Thực thi queries (JPQL, native SQL)
- Không chứa business logic

**Các thành phần:**

1. **Repositories:** Data access interface
   ```java
   public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

       @Query("SELECT b FROM BookingEntity b " +
              "WHERE b.user.id = :userId " +
              "AND b.status = :status " +
              "ORDER BY b.createdAt DESC")
       Page<BookingEntity> findByUserIdAndStatus(
           @Param("userId") Long userId,
           @Param("status") String status,
           Pageable pageable
       );

       @Query("SELECT b FROM BookingEntity b " +
              "WHERE b.status = 'PENDING' " +
              "AND b.createdAt < :timeoutThreshold")
       List<BookingEntity> findTimeoutBookings(
           @Param("timeoutThreshold") LocalDateTime timeoutThreshold
       );

       @Modifying
       @Query("UPDATE BookingEntity b " +
              "SET b.status = 'CANCELLED' " +
              "WHERE b.id IN :bookingIds")
       int cancelBookings(@Param("bookingIds") List<Long> bookingIds);
   }
   ```

2. **Entities:** Database table mapping
   ```java
   @Entity
   @Table(name = "bookings")
   public class BookingEntity {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;

       @ManyToOne(fetch = FetchType.LAZY)
       @JoinColumn(name = "user_id", nullable = false)
       private UserEntity user;

       @ManyToOne(fetch = FetchType.LAZY)
       @JoinColumn(name = "show_id", nullable = false)
       private ShowEntity show;

       @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
       private List<BookingSeatEntity> bookingSeats;

       @Column(nullable = false)
       private BigDecimal totalAmount;

       @Enumerated(EnumType.STRING)
       @Column(nullable = false)
       private BookingStatus status;

       @Column(nullable = false)
       private LocalDateTime createdAt;

       private LocalDateTime paidAt;
       private LocalDateTime cancelledAt;
   }
   ```

3. **Custom Queries:**
   ```java
   @Repository
   public class ShowRepositoryCustomImpl implements ShowRepositoryCustom {

       @PersistenceContext
       private EntityManager entityManager;

       @Override
       public List<ShowEntity> findShowsWithAvailableSeats(
           Long movieId,
           LocalDate date,
           Long cinemaId
       ) {
           String sql = """
               SELECT DISTINCT s.* FROM shows s
               JOIN show_seats ss ON s.id = ss.show_id
               WHERE s.movie_id = :movieId
               AND s.show_date = :date
               AND s.hall_id IN (
                   SELECT id FROM halls WHERE cinema_id = :cinemaId
               )
               AND ss.status = 'AVAILABLE'
               """;

           return entityManager.createNativeQuery(sql, ShowEntity.class)
               .setParameter("movieId", movieId)
               .setParameter("date", date)
               .setParameter("cinemaId", cinemaId)
               .getResultList();
       }
   }
   ```

4. **Mappers:** Entity ↔ Domain conversion
   ```java
   @Component
   public class BookingMapper {

       public Booking toDomain(BookingEntity entity) {
           return Booking.builder()
               .bookingId(entity.getId())
               .user(userMapper.toDomain(entity.getUser()))
               .show(showMapper.toDomain(entity.getShow()))
               .seats(entity.getBookingSeats().stream()
                   .map(seatMapper::toDomain)
                   .collect(Collectors.toList()))
               .totalAmount(entity.getTotalAmount())
               .status(entity.getStatus())
               .createdAt(entity.getCreatedAt())
               .paidAt(entity.getPaidAt())
               .build();
       }

       public BookingEntity toEntity(Booking domain) {
           BookingEntity entity = new BookingEntity();
           entity.setId(domain.getBookingId());
           entity.setUser(userMapper.toEntity(domain.getUser()));
           entity.setShow(showMapper.toEntity(domain.getShow()));
           entity.setTotalAmount(domain.getTotalAmount());
           entity.setStatus(domain.getStatus());
           entity.setCreatedAt(domain.getCreatedAt());
           entity.setPaidAt(domain.getPaidAt());
           return entity;
       }
   }
   ```

**Package structure:**
```
com.cinema.data
├── repository
│   ├── UserRepository.java
│   ├── MovieRepository.java
│   ├── CinemaRepository.java
│   ├── HallRepository.java
│   ├── ShowRepository.java
│   ├── SeatRepository.java
│   ├── BookingRepository.java
│   └── custom
│       ├── ShowRepositoryCustom.java
│       └── ShowRepositoryCustomImpl.java
├── entity
│   ├── UserEntity.java
│   ├── MovieEntity.java
│   ├── CinemaEntity.java
│   ├── HallEntity.java
│   ├── ShowEntity.java
│   ├── SeatEntity.java
│   ├── BookingEntity.java
│   └── BookingSeatEntity.java
└── mapper
    ├── UserMapper.java
    ├── MovieMapper.java
    ├── ShowMapper.java
    ├── SeatMapper.java
    └── BookingMapper.java
```

### 4.2.4 Infrastructure Layer

**Trách nhiệm:**
- Tích hợp với external services
- Cung cấp technical capabilities (caching, locking, scheduling)
- Không chứa business logic
- Implement interfaces định nghĩa trong Business Layer

**Các thành phần:**

1. **Redis Client:**
   ```java
   @Component
   public class RedisLockManager {

       @Autowired
       private RedisTemplate<String, String> redisTemplate;

       public boolean tryLock(String key, String value, long ttlSeconds) {
           Boolean success = redisTemplate.opsForValue()
               .setIfAbsent(key, value, ttlSeconds, TimeUnit.SECONDS);
           return Boolean.TRUE.equals(success);
       }

       public void unlock(String key) {
           redisTemplate.delete(key);
       }

       public String getLockOwner(String key) {
           return redisTemplate.opsForValue().get(key);
       }
   }

   @Component
   public class RedisCacheManager {

       @Autowired
       private RedisTemplate<String, Object> redisTemplate;

       public void cache(String key, Object value, long ttlSeconds) {
           redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
       }

       public <T> T get(String key, Class<T> type) {
           return (T) redisTemplate.opsForValue().get(key);
       }

       public void invalidate(String key) {
           redisTemplate.delete(key);
       }
   }
   ```

2. **Payment Gateway Client:**
   ```java
   public interface PaymentGateway {
       PaymentUrlResponse createPaymentUrl(PaymentRequest request);
       PaymentResult processCallback(Map<String, String> params);
       RefundResult refund(RefundRequest request);
   }

   @Component
   public class VNPayGateway implements PaymentGateway {

       @Value("${vnpay.api.url}")
       private String apiUrl;

       @Value("${vnpay.merchant.id}")
       private String merchantId;

       @Value("${vnpay.secret.key}")
       private String secretKey;

       @Override
       public PaymentUrlResponse createPaymentUrl(PaymentRequest request) {
           // Build VNPay parameters
           Map<String, String> params = new TreeMap<>();
           params.put("vnp_Version", "2.1.0");
           params.put("vnp_Command", "pay");
           params.put("vnp_TmnCode", merchantId);
           params.put("vnp_Amount", String.valueOf(request.getAmount() * 100));
           params.put("vnp_OrderInfo", request.getOrderInfo());
           params.put("vnp_OrderType", "billpayment");
           params.put("vnp_ReturnUrl", request.getReturnUrl());
           params.put("vnp_IpnUrl", request.getIpnUrl());
           params.put("vnp_TxnRef", request.getOrderId());

           // Generate signature (HMAC SHA256)
           String signature = generateSignature(params, secretKey);
           params.put("vnp_SecureHash", signature);

           // Build URL
           String paymentUrl = apiUrl + "?" + buildQueryString(params);

           return new PaymentUrlResponse(paymentUrl, request.getOrderId());
       }

       @Override
       public PaymentResult processCallback(Map<String, String> params) {
           // Validate signature
           String receivedSignature = params.get("vnp_SecureHash");
           params.remove("vnp_SecureHash");
           String calculatedSignature = generateSignature(params, secretKey);

           if (!calculatedSignature.equals(receivedSignature)) {
               throw new InvalidSignatureException("Invalid payment callback signature");
           }

           // Parse result
           String responseCode = params.get("vnp_ResponseCode");
           boolean success = "00".equals(responseCode);

           return PaymentResult.builder()
               .orderId(params.get("vnp_TxnRef"))
               .transactionId(params.get("vnp_TransactionNo"))
               .amount(Long.parseLong(params.get("vnp_Amount")) / 100)
               .success(success)
               .message(params.get("vnp_Message"))
               .build();
       }

       private String generateSignature(Map<String, String> params, String key) {
           String data = params.entrySet().stream()
               .map(e -> e.getKey() + "=" + e.getValue())
               .collect(Collectors.joining("&"));
           return HmacUtil.hmacSHA256(data, key);
       }
   }
   ```

3. **Email Service:**
   ```java
   @Service
   public class EmailService {

       @Autowired
       private JavaMailSender mailSender;

       @Autowired
       private TemplateEngine templateEngine;

       @Async
       public void sendBookingConfirmation(Booking booking) {
           try {
               MimeMessage message = mailSender.createMimeMessage();
               MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

               helper.setTo(booking.getUser().getEmail());
               helper.setSubject("Xác nhận đặt vé - " + booking.getShow().getMovie().getTitle());

               // Build email from Thymeleaf template
               Context context = new Context();
               context.setVariable("booking", booking);
               context.setVariable("qrCode", booking.getQrCode());
               String html = templateEngine.process("email/booking-confirmation", context);

               helper.setText(html, true);

               // Attach QR code image
               byte[] qrCodeImage = generateQRCodeImage(booking.getQrCode());
               helper.addInline("qrcode", new ByteArrayResource(qrCodeImage), "image/png");

               mailSender.send(message);

           } catch (Exception e) {
               log.error("Failed to send booking confirmation email", e);
               // Don't throw exception, email failure shouldn't break booking flow
           }
       }

       @Async
       public void sendCancellationNotification(Booking booking) {
           // Similar implementation
       }
   }
   ```

4. **Scheduler Jobs:**
   ```java
   @Component
   public class BookingCleanupScheduler {

       @Autowired
       private BookingService bookingService;

       @Autowired
       private RedisLockManager redisLockManager;

       @Scheduled(cron = "0 * * * * *") // Every minute
       public void cleanupTimeoutBookings() {
           log.info("Starting cleanup of timeout bookings");

           LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(15);
           List<Booking> timeoutBookings = bookingService.findTimeoutBookings(timeoutThreshold);

           for (Booking booking : timeoutBookings) {
               try {
                   // Cancel booking
                   bookingService.cancelTimeoutBooking(booking.getBookingId());

                   // Release seat locks in Redis
                   for (Seat seat : booking.getSeats()) {
                       String lockKey = String.format("seat:lock:%d:%s",
                           booking.getShow().getShowId(),
                           seat.getSeatId());
                       redisLockManager.unlock(lockKey);
                   }

                   log.info("Cancelled timeout booking: {}", booking.getBookingId());

               } catch (Exception e) {
                   log.error("Failed to cleanup booking: " + booking.getBookingId(), e);
               }
           }

           log.info("Cleanup completed. Cancelled {} bookings", timeoutBookings.size());
       }

       @Scheduled(cron = "0 0 3 * * *") // 3 AM daily
       public void generateDailyReports() {
           log.info("Generating daily reports");
           // Implementation
       }
   }
   ```

5. **QR Code Generator:**
   ```java
   @Component
   public class QRCodeGenerator {

       public String generateQRCode(Booking booking) {
           try {
               // Build QR data
               String qrData = String.format(
                   "BOOKING:%d|SHOW:%d|USER:%d|SEATS:%s|AMOUNT:%s",
                   booking.getBookingId(),
                   booking.getShow().getShowId(),
                   booking.getUser().getUserId(),
                   booking.getSeats().stream()
                       .map(Seat::getSeatNumber)
                       .collect(Collectors.joining(",")),
                   booking.getTotalAmount()
               );

               // Generate QR code using ZXing
               QRCodeWriter qrCodeWriter = new QRCodeWriter();
               BitMatrix bitMatrix = qrCodeWriter.encode(
                   qrData,
                   BarcodeFormat.QR_CODE,
                   300, 300
               );

               // Convert to image
               ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
               MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
               byte[] imageBytes = outputStream.toByteArray();

               // Encode to Base64
               return Base64.getEncoder().encodeToString(imageBytes);

           } catch (Exception e) {
               throw new QRCodeGenerationException("Failed to generate QR code", e);
           }
       }

       public BookingData parseQRCode(String qrCodeImage) {
           // Implementation for scanning QR code
       }
   }
   ```

**Package structure:**
```
com.cinema.infrastructure
├── redis
│   ├── RedisLockManager.java
│   ├── RedisCacheManager.java
│   └── RedisConfig.java
├── payment
│   ├── PaymentGateway.java
│   ├── VNPayGateway.java
│   ├── MomoGateway.java
│   └── PaymentConfig.java
├── email
│   ├── EmailService.java
│   └── EmailConfig.java
├── scheduler
│   ├── BookingCleanupScheduler.java
│   └── ReportScheduler.java
├── qrcode
│   └── QRCodeGenerator.java
└── config
    ├── RedisConfig.java
    ├── SchedulerConfig.java
    └── AsyncConfig.java
```

---

## 4.3 Thiết kế module (Module Design)

### 4.3.1 User Management Module

**Class Diagram:**

```
┌─────────────────────────────────────────────────────────────────┐
│                       User Management Module                     │
└─────────────────────────────────────────────────────────────────┘

┌──────────────────────┐
│   UserController     │
├──────────────────────┤
│ + register()         │
│ + login()            │
│ + logout()           │
│ + getProfile()       │
│ + updateProfile()    │
│ + changePassword()   │
└──────────┬───────────┘
           │
           │ uses
           ▼
┌──────────────────────┐         ┌──────────────────────┐
│   UserService        │────────>│ AuthenticationService│
├──────────────────────┤         ├──────────────────────┤
│ - userRepository     │         │ - jwtTokenProvider   │
│ - passwordEncoder    │         │ - authenticationMgr  │
│ - authService        │         ├──────────────────────┤
├──────────────────────┤         │ + authenticate()     │
│ + register()         │         │ + generateToken()    │
│ + findById()         │         │ + validateToken()    │
│ + updateProfile()    │         │ + refreshToken()     │
│ + changePassword()   │         └──────────────────────┘
│ + checkEmailExists() │
└──────────┬───────────┘
           │
           │ uses
           ▼
┌──────────────────────┐
│   UserRepository     │
├──────────────────────┤
│ + findById()         │
│ + findByEmail()      │
│ + findByUsername()   │
│ + save()             │
│ + existsByEmail()    │
└──────────┬───────────┘
           │
           │ maps
           ▼
┌──────────────────────┐         ┌──────────────────────┐
│     UserEntity       │────────>│       User           │
├──────────────────────┤         ├──────────────────────┤
│ - id: Long           │         │ - userId: Long       │
│ - email: String      │         │ - email: String      │
│ - username: String   │         │ - fullName: String   │
│ - password: String   │         │ - phoneNumber: String│
│ - fullName: String   │         │ - points: Integer    │
│ - phoneNumber: String│         │ - role: UserRole     │
│ - points: Integer    │         ├──────────────────────┤
│ - role: String       │         │ + canBookSeats()     │
│ - createdAt: DateTime│         │ + hasEnoughPoints()  │
│ - updatedAt: DateTime│         │ + addPoints()        │
│ - status: String     │         │ + deductPoints()     │
└──────────────────────┘         └──────────────────────┘
     Domain Model                    Business Object
```

**Key classes:**

1. **User (Domain Model):**
   ```java
   public class User {
       private Long userId;
       private String email;
       private String fullName;
       private String phoneNumber;
       private Integer points;
       private UserRole role;

       public boolean canBookSeats(int seatCount) {
           return seatCount <= 10;
       }

       public boolean hasEnoughPoints(int pointsRequired) {
           return this.points >= pointsRequired;
       }

       public void addPoints(BigDecimal amount) {
           int earnedPoints = amount.divide(BigDecimal.valueOf(1000))
               .intValue();
           this.points += earnedPoints;
       }

       public void deductPoints(int pointsToUse) {
           if (!hasEnoughPoints(pointsToUse)) {
               throw new InsufficientPointsException();
           }
           this.points -= pointsToUse;
       }
   }
   ```

2. **UserService:**
   ```java
   @Service
   @Transactional
   public class UserService {

       @Autowired
       private UserRepository userRepository;

       @Autowired
       private PasswordEncoder passwordEncoder;

       @Autowired
       private AuthenticationService authService;

       public User register(UserRegistrationRequest request) {
           // Validate email not exists
           if (userRepository.existsByEmail(request.getEmail())) {
               throw new EmailAlreadyExistsException();
           }

           // Create user entity
           UserEntity entity = new UserEntity();
           entity.setEmail(request.getEmail());
           entity.setUsername(request.getUsername());
           entity.setPassword(passwordEncoder.encode(request.getPassword()));
           entity.setFullName(request.getFullName());
           entity.setPhoneNumber(request.getPhoneNumber());
           entity.setPoints(0);
           entity.setRole("CUSTOMER");
           entity.setStatus("ACTIVE");

           // Save to database
           UserEntity saved = userRepository.save(entity);

           // Map to domain
           return userMapper.toDomain(saved);
       }

       public User findById(Long userId) {
           UserEntity entity = userRepository.findById(userId)
               .orElseThrow(() -> new UserNotFoundException(userId));
           return userMapper.toDomain(entity);
       }

       public User updateProfile(Long userId, ProfileUpdateRequest request) {
           UserEntity entity = userRepository.findById(userId)
               .orElseThrow(() -> new UserNotFoundException(userId));

           entity.setFullName(request.getFullName());
           entity.setPhoneNumber(request.getPhoneNumber());

           UserEntity updated = userRepository.save(entity);
           return userMapper.toDomain(updated);
       }
   }
   ```

### 4.3.2 Booking Module

**Class Diagram:**

```
┌────────────────────────────────────────────────────────────────────┐
│                        Booking Module                              │
└────────────────────────────────────────────────────────────────────┘

┌──────────────────────┐
│  BookingController   │
├──────────────────────┤
│ + lockSeats()        │
│ + checkout()         │
│ + getHistory()       │
│ + getDetails()       │
│ + cancel()           │
└──────────┬───────────┘
           │ uses
           ▼
┌───────────────────────────────────────────────────────────────────┐
│                     BookingService                                │
├───────────────────────────────────────────────────────────────────┤
│ - bookingRepository                                               │
│ - showSeatRepository                                              │
│ - userService                                                     │
│ - redisLockManager                                                │
│ - paymentService                                                  │
│ - emailService                                                    │
│ - qrCodeGenerator                                                 │
├───────────────────────────────────────────────────────────────────┤
│ + lockSeats(userId, showId, seatIds): BookingId                  │
│ + checkout(bookingId, paymentMethod, points): PaymentUrl         │
│ + confirmBooking(bookingId, paymentData): Booking                │
│ + cancelBooking(userId, bookingId): RefundId                     │
│ + getBookingHistory(userId, pageable): Page<Booking>             │
│ + findTimeoutBookings(threshold): List<Booking>                  │
│ + cancelTimeoutBooking(bookingId): void                          │
└───────────────────────┬───────────────────────────────────────────┘
                        │ uses
            ┌───────────┼───────────┐
            ▼           ▼           ▼
┌────────────────┐ ┌────────────┐ ┌────────────────┐
│BookingRepository│ │ShowSeat    │ │RedisLockManager│
│                 │ │Repository  │ │                │
└────────────────┘ └────────────┘ └────────────────┘
         │
         │ manages
         ▼
┌────────────────────────────────────────────────────────────────┐
│                        Booking                                 │
├────────────────────────────────────────────────────────────────┤
│ - bookingId: Long                                              │
│ - user: User                                                   │
│ - show: Show                                                   │
│ - bookingSeats: List<BookingSeat>                             │
│ - totalAmount: BigDecimal                                      │
│ - pointsUsed: Integer                                          │
│ - pointsEarned: Integer                                        │
│ - status: BookingStatus                                        │
│ - qrCode: String                                               │
│ - createdAt: LocalDateTime                                     │
│ - paidAt: LocalDateTime                                        │
│ - cancelledAt: LocalDateTime                                   │
├────────────────────────────────────────────────────────────────┤
│ + calculateTotalAmount(): BigDecimal                           │
│ + canBeCancelled(): boolean                                    │
│ + isExpired(): boolean                                         │
│ + getSeats(): List<Seat>                                       │
│ + isPending(): boolean                                         │
│ + isConfirmed(): boolean                                       │
│ + confirm(paymentData): void                                   │
│ + cancel(): void                                               │
└────────────────────────────────────────────────────────────────┘
            │
            │ has
            ▼
┌────────────────────────────────────────────────────────────────┐
│                     BookingSeat                                │
├────────────────────────────────────────────────────────────────┤
│ - bookingSeatId: Long                                          │
│ - booking: Booking                                             │
│ - showSeat: ShowSeat                                           │
│ - price: BigDecimal                                            │
└────────────────────────────────────────────────────────────────┘
            │
            │ references
            ▼
┌────────────────────────────────────────────────────────────────┐
│                       ShowSeat                                 │
├────────────────────────────────────────────────────────────────┤
│ - showSeatId: Long                                             │
│ - show: Show                                                   │
│ - seat: Seat                                                   │
│ - price: BigDecimal                                            │
│ - status: SeatStatus (AVAILABLE, LOCKED, SOLD)                │
├────────────────────────────────────────────────────────────────┤
│ + isAvailable(): boolean                                       │
│ + lock(userId): void                                           │
│ + sell(): void                                                 │
│ + release(): void                                              │
└────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────┐
│                  BookingStatus (Enum)                          │
├────────────────────────────────────────────────────────────────┤
│ PENDING                                                        │
│ CONFIRMED                                                      │
│ CANCELLED                                                      │
│ FAILED                                                         │
│ REFUNDED                                                       │
└────────────────────────────────────────────────────────────────┘
```

**Key implementation:**

```java
@Service
@Transactional
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;

    @Autowired
    private RedisLockManager redisLockManager;

    public Long lockSeats(Long userId, Long showId, List<String> seatIds) {
        // Validate seat count
        if (seatIds.size() > 10) {
            throw new ExceedMaxSeatsException("Maximum 10 seats allowed");
        }

        // Get show seats
        List<ShowSeat> showSeats = showSeatRepository.findByShowIdAndSeatIds(showId, seatIds);

        // Try to lock each seat in Redis
        List<ShowSeat> lockedSeats = new ArrayList<>();
        try {
            for (ShowSeat showSeat : showSeats) {
                String lockKey = String.format("seat:lock:%d:%s",
                    showId, showSeat.getSeat().getSeatNumber());

                boolean locked = redisLockManager.tryLock(
                    lockKey,
                    userId.toString(),
                    300 // 5 minutes TTL
                );

                if (!locked) {
                    throw new SeatAlreadyLockedException(showSeat.getSeat().getSeatNumber());
                }

                // Update database
                showSeat.setStatus(SeatStatus.LOCKED);
                showSeatRepository.save(showSeat);

                lockedSeats.add(showSeat);
            }

            // Create booking
            Booking booking = new Booking();
            booking.setUser(userService.findById(userId));
            booking.setShow(showService.findById(showId));
            booking.setStatus(BookingStatus.PENDING);
            booking.setCreatedAt(LocalDateTime.now());

            // Create booking seats
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (ShowSeat showSeat : lockedSeats) {
                BookingSeat bookingSeat = new BookingSeat();
                bookingSeat.setBooking(booking);
                bookingSeat.setShowSeat(showSeat);
                bookingSeat.setPrice(showSeat.getPrice());
                booking.addBookingSeat(bookingSeat);

                totalAmount = totalAmount.add(showSeat.getPrice());
            }
            booking.setTotalAmount(totalAmount);

            // Save booking
            Booking saved = bookingRepository.save(booking);

            return saved.getBookingId();

        } catch (Exception e) {
            // Rollback: Release all locked seats
            for (ShowSeat seat : lockedSeats) {
                String lockKey = String.format("seat:lock:%d:%s",
                    showId, seat.getSeat().getSeatNumber());
                redisLockManager.unlock(lockKey);

                seat.setStatus(SeatStatus.AVAILABLE);
                showSeatRepository.save(seat);
            }
            throw e;
        }
    }

    public String checkout(Long bookingId, String paymentMethod, Integer pointsToUse) {
        Booking booking = findById(bookingId);

        // Validate booking status
        if (!booking.isPending()) {
            throw new InvalidBookingStatusException("Booking is not in PENDING status");
        }

        // Validate locks still valid
        for (BookingSeat bs : booking.getBookingSeats()) {
            String lockKey = String.format("seat:lock:%d:%s",
                booking.getShow().getShowId(),
                bs.getShowSeat().getSeat().getSeatNumber());

            String lockOwner = redisLockManager.getLockOwner(lockKey);
            if (!booking.getUser().getUserId().toString().equals(lockOwner)) {
                throw new LockExpiredException("Seat lock has expired");
            }
        }

        // Apply points discount
        BigDecimal finalAmount = booking.getTotalAmount();
        if (pointsToUse != null && pointsToUse > 0) {
            User user = booking.getUser();
            if (!user.hasEnoughPoints(pointsToUse)) {
                throw new InsufficientPointsException();
            }

            // Max 50% discount with points
            BigDecimal maxDiscount = booking.getTotalAmount()
                .multiply(BigDecimal.valueOf(0.5));
            BigDecimal pointsDiscount = BigDecimal.valueOf(pointsToUse * 1000);
            BigDecimal actualDiscount = pointsDiscount.min(maxDiscount);

            finalAmount = finalAmount.subtract(actualDiscount);
            booking.setPointsUsed(pointsToUse);
        }

        // Create payment URL
        PaymentRequest paymentRequest = PaymentRequest.builder()
            .orderId(bookingId.toString())
            .amount(finalAmount)
            .orderInfo("Booking #" + bookingId)
            .returnUrl(paymentReturnUrl)
            .ipnUrl(paymentIpnUrl)
            .build();

        PaymentUrlResponse response = paymentService.createPaymentUrl(
            paymentMethod,
            paymentRequest
        );

        return response.getPaymentUrl();
    }

    public Booking confirmBooking(Long bookingId, PaymentData paymentData) {
        Booking booking = findById(bookingId);

        // Update booking
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaidAt(LocalDateTime.now());

        // Calculate points earned
        int pointsEarned = booking.getTotalAmount()
            .divide(BigDecimal.valueOf(1000))
            .intValue();
        booking.setPointsEarned(pointsEarned);

        // Update user points
        User user = booking.getUser();
        user.addPoints(booking.getTotalAmount());
        if (booking.getPointsUsed() != null) {
            user.deductPoints(booking.getPointsUsed());
        }
        userService.update(user);

        // Update seats to SOLD
        for (BookingSeat bs : booking.getBookingSeats()) {
            ShowSeat showSeat = bs.getShowSeat();
            showSeat.setStatus(SeatStatus.SOLD);
            showSeatRepository.save(showSeat);

            // Remove Redis lock
            String lockKey = String.format("seat:lock:%d:%s",
                booking.getShow().getShowId(),
                showSeat.getSeat().getSeatNumber());
            redisLockManager.unlock(lockKey);
        }

        // Generate QR code
        String qrCode = qrCodeGenerator.generateQRCode(booking);
        booking.setQrCode(qrCode);

        // Save booking
        Booking confirmed = bookingRepository.save(booking);

        // Send email (async)
        emailService.sendBookingConfirmation(confirmed);

        return confirmed;
    }
}
```

### 4.3.3 Show Management Module

**Class Diagram:**

```
┌──────────────────────────────────────────────────────────────┐
│                    Show Management Module                    │
└──────────────────────────────────────────────────────────────┘

┌──────────────────────┐
│   ShowController     │
├──────────────────────┤
│ + getShows()         │
│ + getSeatMap()       │
│ + createShow()       │
│ + updateShow()       │
│ + cancelShow()       │
└──────────┬───────────┘
           │ uses
           ▼
┌──────────────────────────────────────────────────────────────┐
│                     ShowService                              │
├──────────────────────────────────────────────────────────────┤
│ - showRepository                                             │
│ - hallRepository                                             │
│ - movieService                                               │
├──────────────────────────────────────────────────────────────┤
│ + getAvailableShows(movieId, date, cinemaId): List<Show>   │
│ + getSeatMap(showId): SeatMap                               │
│ + createShow(request): ShowId                               │
│ + checkScheduleConflict(hallId, date, time): boolean        │
│ + cancelShow(showId): void                                  │
└─────────────────────┬────────────────────────────────────────┘
                      │ manages
                      ▼
┌──────────────────────────────────────────────────────────────┐
│                         Show                                 │
├──────────────────────────────────────────────────────────────┤
│ - showId: Long                                               │
│ - movie: Movie                                               │
│ - hall: Hall                                                 │
│ - showDate: LocalDate                                        │
│ - showTime: LocalTime                                        │
│ - basePrice: BigDecimal                                      │
│ - status: ShowStatus                                         │
├──────────────────────────────────────────────────────────────┤
│ + getEndTime(): LocalTime                                    │
│ + isConflictWith(otherShow): boolean                         │
│ + hasAvailableSeats(): boolean                               │
│ + getTotalSeats(): int                                       │
│ + getAvailableSeatsCount(): int                              │
└───────────────────────┬──────────────────────────────────────┘
                        │ held in
                        ▼
┌──────────────────────────────────────────────────────────────┐
│                         Hall                                 │
├──────────────────────────────────────────────────────────────┤
│ - hallId: Long                                               │
│ - cinema: Cinema                                             │
│ - name: String                                               │
│ - totalSeats: Integer                                        │
│ - seatLayout: String (JSON)                                  │
│ - hallType: String (STANDARD, VIP, IMAX)                     │
├──────────────────────────────────────────────────────────────┤
│ + getSeats(): List<Seat>                                     │
│ + getSeatByNumber(seatNumber): Seat                          │
└───────────────────────┬──────────────────────────────────────┘
                        │ contains
                        ▼
┌──────────────────────────────────────────────────────────────┐
│                         Seat                                 │
├──────────────────────────────────────────────────────────────┤
│ - seatId: Long                                               │
│ - hall: Hall                                                 │
│ - rowNumber: String                                          │
│ - seatNumber: String                                         │
│ - seatType: SeatType (NORMAL, VIP, PREMIUM)                  │
├──────────────────────────────────────────────────────────────┤
│ + getFullSeatNumber(): String                                │
└──────────────────────────────────────────────────────────────┘
```

---

## 4.4 Design Patterns

### 4.4.1 Repository Pattern

**Mục đích:** Tách biệt business logic khỏi data access logic, cung cấp interface thống nhất để truy cập dữ liệu.

**Implementation:**

```java
// Interface định nghĩa contract
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    Page<BookingEntity> findByUserIdAndStatus(
        Long userId,
        BookingStatus status,
        Pageable pageable
    );

    List<BookingEntity> findTimeoutBookings(LocalDateTime threshold);
}

// Service sử dụng repository, không biết implementation details
@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository; // Depend on abstraction

    public Page<Booking> getBookingHistory(Long userId, Pageable pageable) {
        Page<BookingEntity> entities = bookingRepository.findByUserIdAndStatus(
            userId,
            BookingStatus.CONFIRMED,
            pageable
        );
        return entities.map(bookingMapper::toDomain);
    }
}
```

**Lợi ích:**
- Business layer không phụ thuộc vào JPA, có thể thay đổi persistence framework
- Dễ test (mock repository)
- Tái sử dụng queries

### 4.4.2 Service Layer Pattern

**Mục đích:** Encapsulate business logic, transaction management, orchestrate operations.

**Implementation:**

```java
@Service
@Transactional
public class BookingService {

    // Service orchestrates multiple repositories and external services
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private EmailService emailService;

    // Complex business operation
    public Booking confirmBooking(Long bookingId, PaymentData data) {
        // All operations in one transaction
        Booking booking = findById(bookingId);
        booking.confirm();

        // Update related entities
        updateSeatsStatus(booking);
        updateUserPoints(booking);
        generateQRCode(booking);

        // Save changes
        Booking confirmed = bookingRepository.save(booking);

        // Async notifications
        emailService.sendConfirmation(confirmed);

        return confirmed;
    }
}
```

**Lợi ích:**
- Transaction boundary rõ ràng
- Business logic tập trung, dễ maintain
- Có thể tái sử dụng operations

### 4.4.3 Factory Pattern

**Mục đích:** Tạo objects mà không cần biết implementation class cụ thể.

**Implementation:**

```java
// Payment gateway factory
@Component
public class PaymentGatewayFactory {

    @Autowired
    private VNPayGateway vnpayGateway;

    @Autowired
    private MomoGateway momoGateway;

    public PaymentGateway getGateway(String paymentMethod) {
        switch (paymentMethod.toUpperCase()) {
            case "VNPAY":
                return vnpayGateway;
            case "MOMO":
                return momoGateway;
            default:
                throw new UnsupportedPaymentMethodException(paymentMethod);
        }
    }
}

// Usage in service
@Service
public class PaymentService {

    @Autowired
    private PaymentGatewayFactory gatewayFactory;

    public PaymentUrlResponse createPaymentUrl(
        String paymentMethod,
        PaymentRequest request
    ) {
        PaymentGateway gateway = gatewayFactory.getGateway(paymentMethod);
        return gateway.createPaymentUrl(request);
    }
}
```

**Lợi ích:**
- Dễ thêm payment methods mới
- Client code không phụ thuộc vào concrete classes
- Centralized object creation

### 4.4.4 Pricing Logic (Fixed Amount Strategy)

**Mục đích:** Tính giá vé linh hoạt dựa trên loại ghế và ngày chiếu.

**Quy tắc định giá (VND cố định):**

| Loại ghế | Phụ thu | Mô tả |
|----------|---------|-------|
| NORMAL | +0 VND | Ghế thường |
| VIP | +20,000 VND | Ghế VIP (hàng giữa, tầm nhìn tốt) |
| COUPLE | x2 giá cơ bản | Ghế đôi (cho 2 người) |
| Weekend | +30,000 VND | Thứ 7, Chủ nhật |

**Ví dụ tính giá:**
- Base price: 80,000 VND
- Ghế VIP ngày thường: 80,000 + 20,000 = **100,000 VND**
- Ghế thường cuối tuần: 80,000 + 30,000 = **110,000 VND**
- Ghế VIP cuối tuần: 80,000 + 20,000 + 30,000 = **130,000 VND**
- Ghế đôi cuối tuần: (80,000 x 2) + 30,000 = **190,000 VND**

**Implementation trong ShowService:**

```java
@Service
public class ShowService {
    // Fixed price increases (VND)
    private static final BigDecimal VIP_PRICE_INCREASE = new BigDecimal("20000");
    private static final BigDecimal WEEKEND_PRICE_INCREASE = new BigDecimal("30000");

    private void generateShowSeats(Show show, Hall hall, BigDecimal basePrice, LocalDate showDate) {
        boolean isWeekend = showDate.getDayOfWeek() == DayOfWeek.SATURDAY ||
                           showDate.getDayOfWeek() == DayOfWeek.SUNDAY;

        for (Seat seat : activeSeats) {
            BigDecimal seatPrice = basePrice;

            // VIP: +20,000 VND
            if (seat.getSeatType() == Seat.SeatType.VIP) {
                seatPrice = seatPrice.add(VIP_PRICE_INCREASE);
            }
            // COUPLE: x2 base price (for 2 people)
            else if (seat.getSeatType() == Seat.SeatType.COUPLE) {
                seatPrice = seatPrice.multiply(new BigDecimal("2"));
            }

            // Weekend: +30,000 VND
            if (isWeekend) {
                seatPrice = seatPrice.add(WEEKEND_PRICE_INCREASE);
            }

            ShowSeat showSeat = ShowSeat.builder()
                    .show(show)
                    .seat(seat)
                    .price(seatPrice)
                    .status(ShowSeatStatus.AVAILABLE)
                    .build();
            showSeats.add(showSeat);
        }
    }
}
```

**Tại sao dùng fixed amount thay vì %:**
- Dễ hiểu với khách hàng ("+20K cho VIP" vs "+50%")
- Giá cố định, dễ kiểm soát ngân sách
- Phù hợp với thị trường Việt Nam
- Tránh giá lẻ khó thanh toán (VD: 127,500 VND)

### 4.4.5 Builder Pattern

**Mục đích:** Construct complex objects step by step.

**Implementation:**

```java
@Builder
public class Booking {
    private Long bookingId;
    private User user;
    private Show show;
    private List<BookingSeat> bookingSeats;
    private BigDecimal totalAmount;
    private Integer pointsUsed;
    private Integer pointsEarned;
    private BookingStatus status;
    private String qrCode;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}

// Usage
Booking booking = Booking.builder()
    .user(user)
    .show(show)
    .bookingSeats(seats)
    .totalAmount(totalAmount)
    .status(BookingStatus.PENDING)
    .createdAt(LocalDateTime.now())
    .build();
```

**Lợi ích:**
- Code readable
- Immutable objects
- Optional parameters

### 4.4.6 Singleton Pattern

**Mục đích:** Ensure a class has only one instance.

**Implementation:**

```java
@Component
public class RedisLockManager {
    // Spring manages as singleton by default

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // All components share same instance
}
```

**Lợi ích:**
- Resource sharing (connection pools, caches)
- Consistent state across application

---

## 4.5 API Design

### 4.5.1 RESTful API Principles

**Principles applied:**
1. **Resource-based URLs:** Nouns, not verbs
2. **HTTP methods:** GET (read), POST (create), PUT (update), DELETE (delete)
3. **Stateless:** Each request contains all information needed
4. **JSON:** Request and response format
5. **HTTP status codes:** 200, 201, 400, 401, 403, 404, 409, 500
6. **Versioning:** `/api/v1/...` (for future compatibility)

### 4.5.2 Authentication API

**Base URL:** `/api/auth`

#### POST /api/auth/register

Register new user account.

**Request:**
```json
{
  "email": "user@example.com",
  "username": "john_doe",
  "password": "SecurePass123!",
  "fullName": "John Doe",
  "phoneNumber": "0901234567"
}
```

**Response:** `201 Created`
```json
{
  "userId": 123,
  "email": "user@example.com",
  "username": "john_doe",
  "fullName": "John Doe",
  "phoneNumber": "0901234567",
  "points": 0,
  "role": "CUSTOMER",
  "createdAt": "2025-01-15T10:30:00"
}
```

**Error:** `400 Bad Request`
```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Email already exists",
  "path": "/api/auth/register"
}
```

#### POST /api/auth/login

Authenticate user and get access token.

**Request:**
```json
{
  "username": "john_doe",
  "password": "SecurePass123!"
}
```

**Response:** `200 OK`
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "user": {
    "userId": 123,
    "email": "user@example.com",
    "fullName": "John Doe",
    "points": 150
  }
}
```

### 4.5.3 Movie API

**Base URL:** `/api/movies`

#### GET /api/movies

Get list of movies currently showing.

**Query params:**
- `genre`: Filter by genre
- `status`: `NOW_SHOWING` | `COMING_SOON`
- `page`: Page number (default: 0)
- `size`: Page size (default: 20)

**Response:** `200 OK`
```json
{
  "content": [
    {
      "movieId": 1,
      "title": "Avatar: The Way of Water",
      "director": "James Cameron",
      "genre": "Action, Sci-Fi",
      "duration": 192,
      "releaseDate": "2025-01-10",
      "rating": "T13",
      "posterUrl": "https://cdn.example.com/posters/avatar2.jpg",
      "trailerUrl": "https://youtube.com/watch?v=...",
      "description": "Set more than a decade after...",
      "status": "NOW_SHOWING"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 15,
  "totalPages": 1
}
```

#### GET /api/movies/{movieId}

Get movie details.

**Response:** `200 OK`
```json
{
  "movieId": 1,
  "title": "Avatar: The Way of Water",
  "director": "James Cameron",
  "cast": "Sam Worthington, Zoe Saldana",
  "genre": "Action, Sci-Fi",
  "duration": 192,
  "releaseDate": "2025-01-10",
  "rating": "T13",
  "language": "English",
  "subtitle": "Vietnamese",
  "posterUrl": "https://cdn.example.com/posters/avatar2.jpg",
  "trailerUrl": "https://youtube.com/watch?v=...",
  "description": "Set more than a decade after the events of the first film...",
  "status": "NOW_SHOWING",
  "upcomingShows": [
    {
      "showId": 501,
      "cinemaName": "CGV Vincom",
      "hallName": "Hall 1",
      "showDate": "2025-01-16",
      "showTime": "14:00",
      "basePrice": 80000,
      "availableSeats": 45
    }
  ]
}
```

### 4.5.4 Show API

**Base URL:** `/api/shows`

#### GET /api/shows

Get available shows.

**Query params:**
- `movieId`: Filter by movie (required)
- `date`: Show date (format: YYYY-MM-DD, required)
- `cinemaId`: Filter by cinema

**Response:** `200 OK`
```json
{
  "shows": [
    {
      "showId": 501,
      "movie": {
        "movieId": 1,
        "title": "Avatar: The Way of Water",
        "duration": 192,
        "posterUrl": "..."
      },
      "cinema": {
        "cinemaId": 10,
        "name": "CGV Vincom Center",
        "address": "123 Nguyen Hue, District 1, HCMC"
      },
      "hall": {
        "hallId": 101,
        "name": "Hall 1",
        "hallType": "STANDARD",
        "totalSeats": 100
      },
      "showDate": "2025-01-16",
      "showTime": "14:00",
      "basePrice": 80000,
      "availableSeats": 45
    }
  ]
}
```

#### GET /api/shows/{showId}/seats

Get seat map for a show.

**Response:** `200 OK`
```json
{
  "showId": 501,
  "hallLayout": {
    "rows": 10,
    "columns": 10,
    "screen": "front"
  },
  "seats": [
    {
      "seatId": "A1",
      "rowNumber": "A",
      "seatNumber": "1",
      "seatType": "NORMAL",
      "price": 80000,
      "status": "AVAILABLE"
    },
    {
      "seatId": "A2",
      "rowNumber": "A",
      "seatNumber": "2",
      "seatType": "NORMAL",
      "price": 80000,
      "status": "LOCKED"
    },
    {
      "seatId": "A3",
      "rowNumber": "A",
      "seatNumber": "3",
      "seatType": "VIP",
      "price": 120000,
      "status": "SOLD"
    }
  ],
  "legend": {
    "AVAILABLE": "Available to book",
    "LOCKED": "Currently locked by another user",
    "SOLD": "Already sold"
  }
}
```

### 4.5.5 Booking API

**Base URL:** `/api/bookings`

#### POST /api/bookings/seats/lock

Lock seats for booking.

**Headers:**
- `Authorization: Bearer {access_token}`

**Request:**
```json
{
  "showId": 501,
  "seatIds": ["A1", "A2", "A3"]
}
```

**Response:** `200 OK`
```json
{
  "bookingId": 1001,
  "showId": 501,
  "seats": [
    {
      "seatId": "A1",
      "seatType": "NORMAL",
      "price": 80000
    },
    {
      "seatId": "A2",
      "seatType": "NORMAL",
      "price": 80000
    },
    {
      "seatId": "A3",
      "seatType": "VIP",
      "price": 120000
    }
  ],
  "totalAmount": 280000,
  "status": "PENDING",
  "lockedUntil": "2025-01-15T10:35:00",
  "message": "Seats locked for 5 minutes. Please complete payment."
}
```

**Error:** `409 Conflict`
```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Seat A2 is already locked by another user",
  "path": "/api/bookings/seats/lock"
}
```

#### POST /api/bookings/{bookingId}/checkout

Proceed to payment.

**Headers:**
- `Authorization: Bearer {access_token}`

**Request:**
```json
{
  "paymentMethod": "VNPAY",
  "pointsToUse": 50
}
```

**Response:** `200 OK`
```json
{
  "bookingId": 1001,
  "paymentUrl": "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?...",
  "orderId": "1001",
  "amount": 230000,
  "discountFromPoints": 50000,
  "expiresAt": "2025-01-15T10:35:00"
}
```

#### GET /api/bookings/history

Get booking history for current user.

**Headers:**
- `Authorization: Bearer {access_token}`

**Query params:**
- `page`: Page number (default: 0)
- `size`: Page size (default: 10)
- `status`: Filter by status

**Response:** `200 OK`
```json
{
  "content": [
    {
      "bookingId": 1001,
      "show": {
        "showId": 501,
        "movie": {
          "title": "Avatar: The Way of Water",
          "posterUrl": "..."
        },
        "cinema": {
          "name": "CGV Vincom Center"
        },
        "hall": {
          "name": "Hall 1"
        },
        "showDate": "2025-01-16",
        "showTime": "14:00"
      },
      "seats": ["A1", "A2", "A3"],
      "totalAmount": 280000,
      "pointsUsed": 50,
      "pointsEarned": 23,
      "status": "CONFIRMED",
      "qrCode": "data:image/png;base64,iVBORw0KGgoAAAANS...",
      "createdAt": "2025-01-15T10:30:00",
      "paidAt": "2025-01-15T10:32:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 5,
  "totalPages": 1
}
```

#### DELETE /api/bookings/{bookingId}

Cancel booking (refund).

**Headers:**
- `Authorization: Bearer {access_token}`

**Response:** `200 OK`
```json
{
  "bookingId": 1001,
  "status": "CANCELLED",
  "refundAmount": 280000,
  "refundId": "REF_1001_20250115",
  "message": "Booking cancelled successfully. Refund will be processed in 3-5 business days."
}
```

**Error:** `400 Bad Request`
```json
{
  "timestamp": "2025-01-16T13:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Cannot cancel booking within 1 hour of show time",
  "path": "/api/bookings/1001"
}
```

### 4.5.6 Payment Callback API

**Base URL:** `/api/payment`

#### POST /api/payment/ipn

Receive IPN (Instant Payment Notification) from payment gateway.

**Request:** (Example from VNPay)
```json
{
  "vnp_Amount": "28000000",
  "vnp_BankCode": "NCB",
  "vnp_CardType": "ATM",
  "vnp_OrderInfo": "Booking #1001",
  "vnp_ResponseCode": "00",
  "vnp_TmnCode": "MERCHANT123",
  "vnp_TransactionNo": "13925232",
  "vnp_TxnRef": "1001",
  "vnp_SecureHash": "abc123def456..."
}
```

**Response:** `200 OK`
```json
{
  "RspCode": "00",
  "Message": "Confirm Success"
}
```

**Processing:**
1. Validate signature
2. Update booking status to CONFIRMED
3. Update seats status to SOLD
4. Remove Redis locks
5. Update user points
6. Generate QR code
7. Send email

#### GET /api/payment/return

Return URL after customer completes payment on gateway.

**Query params:** (same as IPN)

**Response:** Redirect to frontend
```
302 Found
Location: https://frontend.com/booking/success?bookingId=1001
```

### 4.5.7 Admin API

**Base URL:** `/api/admin`

**Authentication:** Requires `ADMIN` role

#### POST /api/admin/shows

Create new show.

**Headers:**
- `Authorization: Bearer {admin_access_token}`

**Request:**
```json
{
  "movieId": 1,
  "hallId": 101,
  "showDate": "2025-01-20",
  "showTime": "14:00",
  "basePrice": 80000
}
```

**Response:** `201 Created`
```json
{
  "showId": 505,
  "movie": {
    "movieId": 1,
    "title": "Avatar: The Way of Water"
  },
  "hall": {
    "hallId": 101,
    "name": "Hall 1",
    "cinemaName": "CGV Vincom Center"
  },
  "showDate": "2025-01-20",
  "showTime": "14:00",
  "basePrice": 80000,
  "totalSeats": 100,
  "availableSeats": 100,
  "status": "SCHEDULED"
}
```

#### GET /api/admin/reports/revenue

Get revenue report.

**Query params:**
- `startDate`: Start date (YYYY-MM-DD)
- `endDate`: End date (YYYY-MM-DD)
- `cinemaId`: Filter by cinema (optional)

**Response:** `200 OK`
```json
{
  "period": {
    "startDate": "2025-01-01",
    "endDate": "2025-01-15"
  },
  "totalRevenue": 125000000,
  "totalBookings": 450,
  "totalTickets": 1200,
  "averageTicketPrice": 104166,
  "byCinema": [
    {
      "cinemaId": 10,
      "cinemaName": "CGV Vincom Center",
      "revenue": 45000000,
      "bookings": 150,
      "tickets": 420
    }
  ],
  "byMovie": [
    {
      "movieId": 1,
      "movieTitle": "Avatar: The Way of Water",
      "revenue": 60000000,
      "bookings": 200,
      "tickets": 580
    }
  ],
  "dailyRevenue": [
    {
      "date": "2025-01-15",
      "revenue": 8500000,
      "bookings": 30,
      "tickets": 85
    }
  ]
}
```

---

## 4.6 Kết luận chương

Chương 4 đã trình bày chi tiết thiết kế kiến trúc của hệ thống đặt vé rạp chiếu phim, bao gồm:

1. **Kiến trúc tổng thể (4.1):**
   - Áp dụng kiến trúc 3-tier (Presentation, Application, Data)
   - Component diagram với các thành phần chính
   - Deployment architecture với load balancer, multiple app servers, database replication

2. **Kiến trúc phân lớp (4.2):**
   - **Presentation Layer:** Controllers, DTOs, Exception Handlers, Validators
   - **Business Logic Layer:** Services, Domain Models, Business Rules, Transaction Management
   - **Data Access Layer:** Repositories, Entities, Mappers
   - **Infrastructure Layer:** Redis, Payment Gateway, Email, Scheduler, QR Code

3. **Thiết kế module (4.3):**
   - User Management Module với authentication/authorization
   - Booking Module với distributed locking mechanism
   - Show Management Module với conflict detection

4. **Design Patterns (4.4):**
   - **Repository Pattern:** Tách biệt data access logic
   - **Service Layer Pattern:** Encapsulate business logic và transactions
   - **Factory Pattern:** Payment gateway creation
   - **Strategy Pattern:** Flexible pricing strategies
   - **Builder Pattern:** Complex object construction
   - **Singleton Pattern:** Shared resources

5. **API Design (4.5):**
   - RESTful principles: Resource-based URLs, HTTP methods, stateless
   - Authentication API: Register, login
   - Movie API: List movies, movie details
   - Show API: Available shows, seat map
   - Booking API: Lock seats, checkout, history, cancel
   - Payment Callback API: IPN, return URL
   - Admin API: Show management, reports

**Các điểm kỹ thuật quan trọng:**

- **Scalability:** Stateless application tier cho phép horizontal scaling
- **Concurrency:** Redis distributed lock với TTL để xử lý race condition
- **Transaction:** ACID transactions cho data consistency
- **Security:** JWT authentication, signature validation cho payment callbacks
- **Reliability:** Idempotent operations, retry mechanisms
- **Performance:** Connection pooling, caching, async operations
- **Maintainability:** Clear separation of concerns, loose coupling

Kiến trúc này đáp ứng đầy đủ các yêu cầu phi chức năng đã xác định ở Chương 2:
- Performance: < 2s response time
- Scalability: Support 10K users, 200-300 concurrent users
- Availability: 99% uptime với multiple app servers
- Security: JWT, BCrypt, HTTPS, rate limiting
- Maintainability: Layered architecture, design patterns

Chương tiếp theo sẽ trình bày thiết kế cơ sở dữ liệu chi tiết, bao gồm schema design, indexing strategy, và data migration plan.

---

**Trang tiếp theo:** Chương 5 - Thiết kế cơ sở dữ liệu
