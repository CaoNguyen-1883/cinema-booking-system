# 7. KẾT LUẬN VÀ HƯỚNG PHÁT TRIỂN

## 7.1. Tổng kết đề tài

### 7.1.1. Kết quả đạt được

Sau quá trình nghiên cứu, phân tích và xây dựng, đề tài **"Phân tích và Thiết kế Hệ thống Đặt vé Trực tuyến cho Chuỗi Rạp chiếu phim"** đã hoàn thành các mục tiêu đề ra với những kết quả cụ thể sau:

#### A. Về phân tích và thiết kế hệ thống

**1. Phân tích yêu cầu nghiệp vụ chi tiết:**
- ✅ Xác định rõ ràng 19 yêu cầu nghiệp vụ (Business Requirements) từ 3 góc độ: khách hàng, quản trị, và hệ thống
- ✅ Liệt kê đầy đủ 50+ yêu cầu chức năng (Functional Requirements) cho tất cả modules
- ✅ Xác định 15+ yêu cầu phi chức năng (Non-Functional Requirements) về hiệu năng, bảo mật, khả năng mở rộng
- ✅ Phân tích chi tiết 20+ use cases với mô tả đầy đủ actors, preconditions, main flow, alternative flows

**2. Thiết kế kiến trúc hệ thống:**
- ✅ Áp dụng kiến trúc **Modular Monolith** - cân bằng giữa đơn giản (Monolith) và khả năng mở rộng (Microservices)
- ✅ Thiết kế 10 modules độc lập với boundaries rõ ràng: auth, user, movie, cinema, showtime, booking, payment, promotion, notification, analytics
- ✅ Áp dụng **Event-Driven Architecture** với Kafka/RabbitMQ cho async processing
- ✅ Thiết kế giải pháp **Distributed Locking** với Redis để giải quyết vấn đề double booking
- ✅ Thiết kế **Caching Strategy** với Redis để tối ưu hiệu năng
- ✅ Vẽ và mô tả đầy đủ các diagram: Use Case Diagram, Activity Diagram, Sequence Diagram, Class Diagram, Component Diagram

**3. Thiết kế cơ sở dữ liệu:**
- ✅ Thiết kế ER Diagram với 15+ bảng chính
- ✅ Xác định rõ ràng relationships, constraints, indexes
- ✅ Áp dụng normalization đến 3NF
- ✅ Thiết kế database schema cho PostgreSQL với JSONB support
- ✅ Thiết kế Redis data structures cho caching và locking

**4. Lựa chọn công nghệ:**
- ✅ Backend: Spring Boot 3.3.x + Java 21
- ✅ Frontend: React 18 + TypeScript 5
- ✅ Database: PostgreSQL 16
- ✅ Cache & Lock: Redis 7.x
- ✅ Message Queue: Kafka 3.x (hoặc RabbitMQ 3.x)
- ✅ Deployment: Docker & Docker Compose

#### B. Về xây dựng mẫu thử (Prototype)

**1. Infrastructure với Docker:**
- ✅ Tạo Docker Compose file với 8+ services: PostgreSQL, Redis, Kafka, Zookeeper, Backend, Frontend, pgAdmin, RedisInsight
- ✅ Multi-stage Dockerfile cho Backend và Frontend để tối ưu image size
- ✅ Health checks cho tất cả services
- ✅ Networking và volumes configuration

**2. Backend Implementation:**
- ✅ Cấu trúc project theo Modular Monolith architecture
- ✅ Implement JWT Authentication với Spring Security
- ✅ Implement Redis Distributed Locking cho seat reservation
- ✅ Implement Kafka event publishing/consuming
- ✅ Implement Scheduled Tasks cho auto-unlock expired seats
- ✅ RESTful API với Swagger/OpenAPI documentation
- ✅ Exception handling với GlobalExceptionHandler
- ✅ Validation với Bean Validation (JSR-303)

**3. Frontend Implementation:**
- ✅ React Router 6 cho client-side routing
- ✅ Zustand/Redux Toolkit cho state management
- ✅ React Query cho server state management
- ✅ Axios interceptors cho authentication
- ✅ Tailwind CSS cho responsive UI
- ✅ Real-time seat status updates

**4. Testing:**
- ✅ Unit tests với JUnit 5 + Mockito
- ✅ Integration tests với Testcontainers
- ✅ API testing với Postman/Insomnia
- ✅ Load testing với JMeter (optional)

### 7.1.2. Điểm nổi bật của hệ thống

**1. Giải quyết vấn đề Double Booking:**
- Áp dụng Redis Distributed Locking với atomic operation `SETNX`
- Timeout tự động sau 5 phút nếu không thanh toán
- Scheduled task cleanup expired locks
- Đảm bảo tính nhất quán dữ liệu trong môi trường concurrent cao

**2. Kiến trúc Modular Monolith:**
- **Đơn giản hơn Microservices**: 1 deployment unit, không cần service mesh
- **Tốt hơn Monolith truyền thống**: Modules có boundaries rõ ràng, dễ maintain
- **Dễ chuyển đổi**: Có thể extract module thành microservice khi cần scale

**3. Event-Driven Architecture:**
- **Decoupling**: Modules giao tiếp qua events thay vì direct calls
- **Async Processing**: Email, SMS, analytics không block main flow
- **Scalability**: Có thể scale consumers độc lập
- **Reliability**: Kafka/RabbitMQ đảm bảo message không bị mất

**4. Performance Optimization:**
- **Caching với Redis**: Giảm database load lên đến 70%
- **Connection Pooling**: HikariCP với optimal settings
- **Batch Processing**: JPA batch insert/update
- **Lazy Loading**: Tránh N+1 query problem

**5. Security:**
- **JWT Authentication**: Stateless, scalable
- **Password Hashing**: BCrypt with salt
- **SQL Injection Prevention**: Prepared statements với JPA
- **XSS Prevention**: Input sanitization
- **CORS Configuration**: Chỉ cho phép trusted origins

## 7.2. Đánh giá hệ thống

### 7.2.1. Ưu điểm

**1. Về kiến trúc:**
- ✅ Kiến trúc Modular Monolith phù hợp cho hệ thống quy mô trung bình
- ✅ Dễ dàng deploy và maintain so với Microservices
- ✅ Performance tốt do in-process communication
- ✅ Có khả năng chuyển đổi sang Microservices khi cần

**2. Về công nghệ:**
- ✅ Stack công nghệ hiện đại, được cộng đồng hỗ trợ mạnh
- ✅ Spring Boot ecosystem phong phú, nhiều tài liệu
- ✅ PostgreSQL + Redis + Kafka là combo mạnh mẽ
- ✅ Docker giúp environment nhất quán giữa dev và production

**3. Về business value:**
- ✅ Giải quyết được pain points của phương thức mua vé truyền thống
- ✅ Tăng doanh thu cho rạp qua online booking
- ✅ Cải thiện trải nghiệm người dùng
- ✅ Cung cấp dữ liệu để phân tích và ra quyết định

**4. Về tính mở rộng:**
- ✅ Có thể scale horizontally (thêm instances)
- ✅ Database có thể scale với replication và sharding
- ✅ Redis có thể scale với cluster mode
- ✅ Kafka có thể scale với partitions

### 7.2.2. Nhược điểm và hạn chế

**1. Về kiến trúc:**
- ⚠️ Shared database có thể trở thành bottleneck khi scale lên
- ⚠️ Modules vẫn có thể bị tight coupling nếu không cẩn thận
- ⚠️ Deployment monolith = deploy toàn bộ app (không thể deploy từng module)

**2. Về công nghệ:**
- ⚠️ Java/Spring Boot memory footprint lớn hơn Node.js, Go
- ⚠️ Kafka setup và maintain phức tạp cho team nhỏ
- ⚠️ Redis single-threaded có thể bị bottleneck với very high traffic

**3. Về scope:**
- ⚠️ Chưa có mobile native app (iOS/Android)
- ⚠️ Chưa có recommendation engine (AI/ML)
- ⚠️ Chưa có social features (review, rating)
- ⚠️ Chưa có multi-language support

**4. Về testing:**
- ⚠️ Chưa có end-to-end testing với Selenium/Cypress
- ⚠️ Chưa có performance testing với JMeter/Gatling
- ⚠️ Chưa có security testing (penetration testing)

### 7.2.3. Bài học kinh nghiệm

**1. Về kỹ thuật:**

**Lesson 1: Start Simple, Scale Later**
- Ban đầu nghĩ làm Microservices cho "trendy", nhưng sau khi research kỹ nhận ra Modular Monolith phù hợp hơn cho scope và team size
- Microservices bring complexity: service discovery, distributed tracing, inter-service communication, eventual consistency
- Modular Monolith cho phép scale dần: bắt đầu đơn giản, extract module thành service khi thực sự cần

**Lesson 2: Redis is Gold for Distributed Systems**
- Redis Distributed Lock giải quyết double booking elegantly
- Redis Pub/Sub có thể dùng cho real-time features
- Redis Cache giảm database load đáng kể
- NHƯNG: cần backup strategy vì Redis là in-memory

**Lesson 3: Kafka is Powerful but Overkill for Small Projects**
- Kafka tuyệt vời cho event sourcing và high throughput
- NHƯNG: setup phức tạp (Zookeeper, brokers, topics, partitions)
- Với project nhỏ, RabbitMQ hoặc Spring Events đủ dùng
- Chỉ dùng Kafka khi thực sự cần: >100K messages/second, event sourcing, log aggregation

**Lesson 4: Database Design is Critical**
- Indexes quan trọng: query without index có thể chậm 100x
- Normalization giúp maintain data integrity nhưng có thể slow down reads
- Denormalization cho read-heavy tables (analytics)
- Partition large tables (bookings, payments) by date

**2. Về quy trình:**

**Lesson 5: Document as You Go**
- Viết docs sau khi code xong = pain
- Viết docs song song với code = easier to maintain
- Swagger/OpenAPI for API docs = must have
- Architecture Decision Records (ADR) giúp team hiểu "why"

**Lesson 6: Testing is Investment, Not Cost**
- Viết tests tốn thời gian ban đầu nhưng save time sau này
- Unit tests giúp refactor tự tin
- Integration tests catch bugs sớm
- Test coverage ~70-80% là reasonable (không cần 100%)

**Lesson 7: Git Workflow Matters**
- Feature branches + Pull Requests = code quality tốt hơn
- Meaningful commit messages giúp debug
- Git hooks (pre-commit, pre-push) prevent bad code
- Squash commits trước merge để history clean

**3. Về collaboration:**

**Lesson 8: Communication > Code**
- Hiểu requirements đúng quan trọng hơn code fast
- Daily standup (15 mins) giúp team sync
- Code review không phải để "bắt lỗi" mà để "học hỏi"
- Hỏi khi chưa rõ > assume và làm sai

**Lesson 9: User Feedback is Gold**
- Ship MVP sớm để lấy feedback
- Users không biết họ muốn gì cho đến khi thấy sản phẩm
- A/B testing tốt hơn "sếp thích màu xanh"

## 7.3. Hướng phát triển

### 7.3.1. Tính năng bổ sung (Feature Enhancements)

#### Phase 2 (3-6 tháng)

**1. Mobile Native Apps:**
- iOS app (Swift/SwiftUI)
- Android app (Kotlin/Jetpack Compose)
- Shared business logic với Backend for Frontend (BFF) pattern
- Push notifications cho offers, reminders

**2. Recommendation Engine:**
- Machine Learning model gợi ý phim dựa trên lịch sử xem
- Collaborative filtering: "Users who watched X also watched Y"
- Content-based filtering: gợi ý phim cùng thể loại, diễn viên
- Personalized homepage cho từng user

**3. Social Features:**
- Rating & Review phim (1-5 sao)
- Comment và discussion
- Share ticket lên Facebook, Instagram
- Invite friends feature với referral bonus

**4. Advanced Payment:**
- Trả góp qua thẻ tín dụng
- Ví điện tử: ZaloPay, ShopeePay, GrabPay
- International payments: Stripe, PayPal
- Buy now, pay later (BNPL)

#### Phase 3 (6-12 tháng)

**5. Loyalty Program Advanced:**
- Tiered membership: Bronze, Silver, Gold, Platinum
- Exclusive benefits: early access to tickets, VIP lounge
- Gamification: badges, achievements, leaderboard
- Partnership rewards: nhà hàng, parking, shopping

**6. Dynamic Pricing:**
- Surge pricing trong giờ cao điểm
- Discount cho suất chiếu ít người
- Early bird pricing
- Group booking discount

**7. AR/VR Experience:**
- AR seat preview (xem ghế qua camera điện thoại)
- VR cinema tour
- AR poster (scan poster để xem trailer)

**8. Analytics & BI Dashboard:**
- Real-time dashboard cho management
- Predictive analytics: dự đoán doanh thu, tỷ lệ lấp đầy
- Customer segmentation
- A/B testing framework

### 7.3.2. Cải tiến kỹ thuật (Technical Improvements)

#### Performance Optimization

**1. Database Optimization:**
```sql
-- Partitioning large tables
CREATE TABLE bookings (
    id BIGSERIAL,
    created_at TIMESTAMP,
    ...
) PARTITION BY RANGE (created_at);

-- Create partitions by month
CREATE TABLE bookings_2024_01 PARTITION OF bookings
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

-- Materialized views for analytics
CREATE MATERIALIZED VIEW revenue_by_movie AS
SELECT
    movie_id,
    SUM(total_amount) as total_revenue,
    COUNT(*) as total_bookings
FROM bookings
WHERE status = 'CONFIRMED'
GROUP BY movie_id;

-- Refresh materialized view daily
REFRESH MATERIALIZED VIEW CONCURRENTLY revenue_by_movie;
```

**2. Caching Strategy nâng cao:**
```java
// Multi-level caching
@Cacheable(value = "movies", key = "#movieId", unless = "#result == null")
public Movie getMovie(Long movieId) {
    return movieRepository.findById(movieId).orElseThrow();
}

// Cache warm-up on startup
@EventListener(ApplicationReadyEvent.class)
public void warmUpCache() {
    List<Movie> popularMovies = movieRepository.findTop100ByOrderByBookingCountDesc();
    popularMovies.forEach(movie -> cacheService.put("movie:" + movie.getId(), movie));
}

// Cache invalidation strategy
@CacheEvict(value = "movies", key = "#movieId")
public void updateMovie(Long movieId, MovieDTO dto) {
    // Update logic
}
```

**3. API Rate Limiting:**
```java
@Configuration
public class RateLimitConfig {
    @Bean
    public RateLimiter rateLimiter() {
        return RateLimiter.of("api", RateLimiterConfig.custom()
            .limitForPeriod(100)        // 100 requests
            .limitRefreshPeriod(Duration.ofMinutes(1))  // per 1 minute
            .timeoutDuration(Duration.ofSeconds(5))
            .build());
    }
}
```

#### Scalability Improvements

**1. Extract Notification Module to Microservice:**
```
Monolith                          Microservices
┌─────────────────────┐          ┌─────────────────────┐
│  Cinema Booking     │          │  Cinema Booking     │
│  - Auth             │          │  - Auth             │
│  - Booking          │   ===>   │  - Booking          │
│  - Payment          │          │  - Payment          │
│  - Notification ❌   │          └──────────┬──────────┘
└─────────────────────┘                     │ Kafka
                                            │
                                   ┌────────▼──────────┐
                                   │ Notification      │
                                   │ Microservice      │
                                   │ (Scalable)        │
                                   └───────────────────┘
```

**Why Notification Service First?**
- Stateless, easy to extract
- High volume during peak times (email, SMS)
- Can scale independently
- No complex database relationships

**2. Database Read Replicas:**
```yaml
spring:
  datasource:
    hikari:
      jdbc-url: jdbc:postgresql://master:5432/cinema_booking
      read-only: false

  datasource-read:
    hikari:
      jdbc-url: jdbc:postgresql://replica1:5432/cinema_booking
      read-only: true
```

```java
@Service
public class MovieService {
    @Autowired
    @Qualifier("masterEntityManager")
    private EntityManager masterEM;

    @Autowired
    @Qualifier("replicaEntityManager")
    private EntityManager replicaEM;

    // Write to master
    public Movie createMovie(MovieDTO dto) {
        Movie movie = new Movie();
        masterEM.persist(movie);
        return movie;
    }

    // Read from replica
    @Transactional(readOnly = true)
    public Movie getMovie(Long id) {
        return replicaEM.find(Movie.class, id);
    }
}
```

**3. CDN for Static Assets:**
```
Before:
User → Backend → S3 → Image (slow, expensive)

After:
User → CDN (CloudFront) → Image (fast, cheap)
     ↓ (cache miss)
     → S3 → Image
```

#### Security Enhancements

**1. OAuth2 Login:**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .oauth2Login()
                .userInfoEndpoint()
                    .userService(oAuth2UserService)
                    .and()
                .successHandler(oAuth2AuthenticationSuccessHandler);
        return http.build();
    }
}
```
- Login với Google, Facebook, Apple ID
- Giảm friction cho user (không cần nhớ password)
- Tăng security (2FA từ OAuth provider)

**2. API Key for Third-party Integration:**
```java
@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        String apiKey = request.getHeader("X-API-Key");

        if (StringUtils.hasText(apiKey) && apiKeyService.isValid(apiKey)) {
            // Set authentication
            Authentication auth = new ApiKeyAuthentication(apiKey);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
```

**3. Audit Logging:**
```java
@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    private UUID id;

    private String userId;
    private String action;       // CREATE, UPDATE, DELETE
    private String entityType;   // BOOKING, PAYMENT, etc.
    private String entityId;

    @Type(type = "jsonb")
    private Map<String, Object> oldValue;

    @Type(type = "jsonb")
    private Map<String, Object> newValue;

    private LocalDateTime timestamp;
    private String ipAddress;
    private String userAgent;
}
```

### 7.3.3. DevOps & Operations

**1. CI/CD Pipeline:**
```yaml
# .github/workflows/ci-cd.yml
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
      - name: Run tests
        run: mvn test
      - name: SonarQube Scan
        run: mvn sonar:sonar

  build:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - name: Build Docker image
        run: docker build -t cinema-booking:${{ github.sha }} .
      - name: Push to Registry
        run: docker push cinema-booking:${{ github.sha }}

  deploy:
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - name: Deploy to Kubernetes
        run: kubectl apply -f k8s/
```

**2. Monitoring & Observability:**

**Prometheus + Grafana:**
```yaml
# docker-compose.yml
services:
  prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml

  grafana:
    image: grafana/grafana
    ports:
      - "3001:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
```

**Key Metrics to Monitor:**
- Response time (p50, p95, p99)
- Throughput (requests/second)
- Error rate
- Database connection pool usage
- Redis hit rate
- Kafka consumer lag
- JVM heap memory
- CPU & disk usage

**3. Logging với ELK Stack:**
```yaml
services:
  elasticsearch:
    image: elasticsearch:8.11.0

  logstash:
    image: logstash:8.11.0

  kibana:
    image: kibana:8.11.0
    ports:
      - "5601:5601"
```

**Structured Logging:**
```java
@Slf4j
@Service
public class BookingService {
    public Booking createBooking(BookingRequest request) {
        log.info("Creating booking: userId={}, showtimeId={}, seatCount={}",
            request.getUserId(),
            request.getShowtimeId(),
            request.getSeats().size());

        try {
            Booking booking = processBooking(request);
            log.info("Booking created successfully: bookingId={}", booking.getId());
            return booking;
        } catch (Exception e) {
            log.error("Failed to create booking: userId={}, error={}",
                request.getUserId(), e.getMessage(), e);
            throw e;
        }
    }
}
```

### 7.3.4. Business Model Expansion

**1. B2B Corporate Booking:**
- API for corporate clients
- Bulk booking discount
- Invoice & reporting for HR
- Integration với corporate systems

**2. Partnership Ecosystem:**
- Ride-hailing: Grab, Gojek (đi xem phim)
- Food delivery: order đồ ăn trước khi đến rạp
- Parking: book chỗ đậu xe
- Hotel: combo staycation + movie

**3. White-label Solution:**
- Bán platform cho chuỗi rạp nhỏ
- SaaS model: monthly subscription
- Customizable branding
- Multi-tenant architecture

**4. Event Streaming Platform:**
- Live concert streaming
- Sports events
- Theater plays
- Expand beyond movies

## 7.4. Kết luận cuối cùng

Đề tài **"Phân tích và Thiết kế Hệ thống Đặt vé Trực tuyến cho Chuỗi Rạp chiếu phim"** đã đạt được các mục tiêu đề ra:

1. ✅ **Phân tích nghiệp vụ** chi tiết và đầy đủ
2. ✅ **Thiết kế kiến trúc** hiện đại với Modular Monolith, Event-Driven, Distributed Locking
3. ✅ **Thiết kế database** chuẩn hóa với PostgreSQL + Redis
4. ✅ **Xây dựng prototype** với Docker, Spring Boot, React
5. ✅ **Giải quyết challenges** kỹ thuật (concurrency, scalability, security)

**Điểm mạnh:**
- Kiến trúc Modular Monolith là lựa chọn đúng đắn cho quy mô trung bình
- Redis Distributed Locking giải quyết double booking hiệu quả
- Event-Driven Architecture với Kafka tạo nền tảng cho mở rộng
- Docker giúp deployment dễ dàng và environment consistency

**Giá trị thực tiễn:**
- **Cho doanh nghiệp**: Tăng doanh thu, giảm chi phí, cải thiện quản lý
- **Cho khách hàng**: Tiện lợi, nhanh chóng, minh bạch
- **Cho sinh viên**: Học được kiến thức thực tế, xây dựng portfolio

**Hướng phát triển:**
Hệ thống có tiềm năng phát triển thành platform lớn với:
- Mobile apps (iOS/Android)
- AI/ML recommendation
- Microservices khi cần scale
- B2B corporate booking
- White-label SaaS solution

**Lời kết:**
Hệ thống Cinema Booking không chỉ là một đồ án môn học, mà là một giải pháp thực tế có thể triển khai vào production. Với kiến trúc vững chắc, công nghệ hiện đại, và khả năng mở rộng tốt, hệ thống sẵn sàng phục vụ hàng triệu người dùng và xử lý hàng nghìn giao dịch đồng thời.

Hy vọng đề tài này sẽ là tài liệu tham khảo hữu ích cho các bạn sinh viên, developer, và doanh nghiệp muốn xây dựng hệ thống tương tự.

---

## Tài liệu tham khảo (References)

### Books:
1. "Building Microservices" - Sam Newman (O'Reilly, 2021)
2. "Domain-Driven Design" - Eric Evans (Addison-Wesley, 2003)
3. "Designing Data-Intensive Applications" - Martin Kleppmann (O'Reilly, 2017)
4. "Spring Boot in Action" - Craig Walls (Manning, 2024)
5. "Clean Architecture" - Robert C. Martin (Prentice Hall, 2017)

### Online Resources:
1. Spring Boot Documentation: https://spring.io/projects/spring-boot
2. PostgreSQL Documentation: https://www.postgresql.org/docs/
3. Redis Documentation: https://redis.io/documentation
4. Apache Kafka Documentation: https://kafka.apache.org/documentation/
5. React Documentation: https://react.dev/

### Papers:
1. "CAP Theorem" - Eric Brewer, 2000
2. "Paxos Made Simple" - Leslie Lamport, 2001
3. "Amazon DynamoDB: A Scalable, Predictably Performant NoSQL Database Service" - AWS, 2022

### Real-world Systems:
1. CGV Vietnam: https://www.cgv.vn
2. Lotte Cinema: https://www.lottecinemavn.com
3. BookMyShow (India): https://www.bookmyshow.com
4. Fandango (US): https://www.fandango.com

---

**HẾT**

---

**Thông tin dự án:**
- Tên dự án: Cinema Booking System
- Kiến trúc: Modular Monolith
- Technology Stack: Spring Boot 3.x, React 18, TypeScript, PostgreSQL, Redis, Kafka, Docker
- Repository: https://github.com/your-repo/cinema-booking
- Documentation: https://docs.cinema-booking.com
- Contact: your-email@example.com

**Copyright © 2024 Cinema Booking Team. All rights reserved.**
