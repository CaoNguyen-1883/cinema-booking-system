# CHƯƠNG 1: TỔNG QUAN

## 1.1 Giới thiệu

### 1.1.1 Bối cảnh

Trong bối cảnh cuộc Cách mạng Công nghiệp 4.0, chuyển đổi số đã và đang diễn ra mạnh mẽ trong mọi lĩnh vực, trong đó có ngành công nghiệp giải trí. Việc ứng dụng công nghệ thông tin vào các hoạt động kinh doanh không chỉ giúp nâng cao hiệu quả vận hành mà còn cải thiện đáng kể trải nghiệm người dùng.

Ngành công nghiệp điện ảnh tại Việt Nam trong những năm gần đây đã có sự phát triển ấn tượng. Theo số liệu từ Cục Điện ảnh, doanh thu phòng vé năm 2023 đạt khoảng 5,000 tỷ đồng với hơn 60 triệu lượt khán giả. Các chuỗi rạp chiếu phim lớn như CGV, Lotte Cinema, Galaxy Cinema đã mở rộng mạng lưới trên toàn quốc với hàng trăm cụm rạp và hàng ngàn phòng chiếu.

Đặc biệt, sau đại dịch COVID-19, xu hướng số hóa trong việc mua sắm dịch vụ giải trí đã trở thành một nhu cầu thiết yếu. Người tiêu dùng ngày càng ưa chuộng các hình thức đặt vé trực tuyến thông qua website hoặc ứng dụng di động, thay vì phương thức truyền thống tại quầy. Theo thống kê, tỷ lệ đặt vé online hiện chiếm 70-80% tổng số vé được bán ra tại các chuỗi rạp lớn.

### 1.1.2 Tình hình thị trường

**Các chuỗi rạp chiếu phim lớn tại Việt Nam:**

**CGV (CJ CGV Vietnam):**
- Chuỗi rạp lớn nhất với 60+ cụm rạp, 450+ phòng chiếu
- Phân bố tại 63 tỉnh thành
- Đa dạng loại hình: Standard 2D, 3D, 4DX, IMAX, ScreenX
- Thị phần: Khoảng 50%

**Lotte Cinema:**
- 40+ cụm rạp trên toàn quốc
- Tập trung tại các thành phố lớn và trung tâm thương mại
- Thị phần: Khoảng 25%

**Galaxy Cinema:**
- 30+ cụm rạp
- Định vị phân khúc trung và cao cấp
- Thị phần: Khoảng 15%

**Các chuỗi rạp khác:**
- Platinum Cineplex, BHD Star Cineplex, Cinestar
- Thị phần: Khoảng 10%

Tất cả các chuỗi rạp lớn hiện nay đều đã triển khai hệ thống đặt vé trực tuyến, chứng tỏ đây là xu hướng tất yếu và là yếu tố cạnh tranh quan trọng trong ngành.

---

## 1.2 Đặt vấn đề

Mặc dù hệ thống đặt vé trực tuyến đã được các chuỗi rạp lớn triển khai, nhưng việc phân tích, thiết kế và xây dựng một hệ thống như vậy từ góc độ kỹ thuật vẫn là một bài toán phức tạp và mang tính thực tiễn cao. Để hiểu rõ hơn về nhu cầu và tầm quan trọng của hệ thống, cần xem xét các vấn đề từ nhiều góc độ.

### 1.2.1 Vấn đề của phương thức mua vé truyền thống

**Về phía khách hàng:**

**Bất tiện và tốn thời gian:**
Khán giả phải di chuyển đến rạp, xếp hàng chờ đợi tại quầy vé. Tình trạng này đặc biệt nghiêm trọng vào các dịp cuối tuần, ngày lễ, hoặc khi có các bộ phim bom tấn ra mắt. Thời gian xếp hàng có thể lên đến 30-60 phút, gây mất thời gian và ảnh hưởng đến trải nghiệm giải trí.

**Rủi ro hết vé:**
Khách hàng có thể gặp tình trạng hết vé hoặc hết ghế đẹp (ghế VIP, ghế trung tâm) khi đến rạp, gây lãng phí thời gian và công sức di chuyển. Đây là vấn đề phổ biến với các suất chiếu prime time (18:00-23:00) vào cuối tuần.

**Khó khăn trong việc lập kế hoạch:**
Khách hàng khó có thể chủ động lập kế hoạch xem phim trước do không biết trước tình trạng ghế trống, giá vé cụ thể, và không thể đặt chỗ trước. Điều này đặc biệt bất tiện cho các nhóm bạn hoặc gia đình muốn ngồi cạnh nhau.

**Không tận dụng được các chương trình khuyến mãi:**
Nhiều chương trình ưu đãi chỉ áp dụng cho kênh online (ví dụ: giảm 20,000 VND khi đặt qua app), khách hàng mua vé tại quầy không được hưởng lợi.

**Về phía rạp chiếu phim:**

**Quản lý suất chiếu phức tạp:**
Việc lập lịch chiếu thủ công cho nhiều phòng chiếu, nhiều phim khác nhau trong ngày dễ xảy ra sai sót như trùng lịch, không tối ưu buffer time giữa các suất.

**Tốn nhân lực và chi phí vận hành:**
Cần nhiều nhân viên quầy vé để phục vụ khách hàng, đặc biệt vào giờ cao điểm. Chi phí nhân công và đào tạo là gánh nặng cho doanh nghiệp.

**Khó dự báo doanh thu:**
Không có dữ liệu real-time về tình hình bán vé, khó đưa ra quyết định điều chỉnh giá vé hoặc lịch chiếu kịp thời.

**Khó thu thập và phân tích dữ liệu khách hàng:**
Dữ liệu về thói quen xem phim, sở thích thể loại của khách hàng không được lưu trữ một cách có hệ thống, khó triển khai các chiến dịch marketing cá nhân hóa.

**Khó kiểm soát tình trạng gian lận:**
Có thể xảy ra tình trạng nhân viên bán vé sai giá, không xuất hóa đơn, hoặc các hành vi gian lận khác.

### 1.2.2 Thách thức kỹ thuật trong xây dựng hệ thống

Xây dựng một hệ thống đặt vé trực tuyến không chỉ đơn giản là tạo giao diện web cho phép khách hàng chọn ghế và thanh toán. Hệ thống cần giải quyết nhiều vấn đề kỹ thuật phức tạp:

**Xử lý đồng thời (Concurrency Control):**
Khi có nhiều người dùng cùng truy cập và chọn ghế cho cùng một suất chiếu, hệ thống phải đảm bảo không xảy ra tình trạng hai người cùng đặt một ghế (double booking). Đây là thách thức về race condition và distributed locking.

**Quản lý trạng thái ghế ngồi:**
Ghế có nhiều trạng thái: Available (trống), Locked (đang được giữ), Booked (đã đặt). Cần có cơ chế timeout để tự động giải phóng ghế nếu người dùng không thanh toán trong thời gian quy định (ví dụ 5 phút).

**Tích hợp thanh toán online:**
Cần tích hợp với các cổng thanh toán (payment gateway) như Momo, VNPay, Visa/Mastercard. Yêu cầu xử lý transaction an toàn, đảm bảo không mất tiền khi có lỗi hệ thống.

**Hiệu năng và khả năng mở rộng:**
Hệ thống phải xử lý được lưu lượng truy cập cao vào các thời điểm ra mắt phim hot (có thể lên đến hàng nghìn request đồng thời). Cần thiết kế kiến trúc có khả năng scale.

**Bảo mật thông tin:**
Bảo vệ thông tin cá nhân của khách hàng, thông tin thẻ thanh toán, ngăn chặn các cuộc tấn công như SQL Injection, XSS, CSRF.

**Tính sẵn sàng cao (High Availability):**
Hệ thống cần hoạt động ổn định 24/7, downtime có thể dẫn đến mất doanh thu và ảnh hưởng uy tín.

---

## 1.3 Mục tiêu đề tài

Với những vấn đề đã phân tích ở trên, đề tài "Phân tích và Thiết kế Hệ thống Đặt vé Trực tuyến cho Chuỗi Rạp chiếu phim" được thực hiện nhằm mục tiêu sau:

### 1.3.1 Mục tiêu tổng quát

Xây dựng một giải pháp phần mềm hoàn chỉnh, cho phép khách hàng đặt vé xem phim một cách nhanh chóng, tiện lợi và an toàn, đồng thời cung cấp cho ban quản lý rạp một công cụ quản trị hiệu quả và tự động hóa các quy trình nghiệp vụ.

### 1.3.2 Mục tiêu cụ thể

**Về phân tích hệ thống:**

1. Nghiên cứu và phân tích chi tiết các quy trình nghiệp vụ thực tế tại chuỗi rạp chiếu phim, bao gồm:
   - Quy trình quản lý phim và lịch chiếu
   - Quy trình đặt vé của khách hàng (online và offline)
   - Quy trình thanh toán và hoàn tiền
   - Quy trình quản lý doanh thu

2. Xác định các yêu cầu chức năng và phi chức năng của hệ thống dựa trên phân tích nghiệp vụ và nhu cầu thực tế.

3. Xác định các đối tượng sử dụng (actors) và mối quan hệ của họ với hệ thống.

**Về thiết kế hệ thống:**

4. Thiết kế kiến trúc tổng thể của hệ thống theo mô hình phân tầng (Layered Architecture), đảm bảo tính module hóa, dễ bảo trì và mở rộng.

5. Thiết kế cơ sở dữ liệu quan hệ (Relational Database) để lưu trữ thông tin người dùng, phim, rạp, suất chiếu, đặt vé, thanh toán, đảm bảo tính toàn vẹn dữ liệu.

6. Thiết kế giao diện người dùng (UI/UX) trực quan, thân thiện, dễ sử dụng cho cả khách hàng và quản trị viên.

7. Thiết kế giải pháp kỹ thuật để xử lý các vấn đề về đồng thời (concurrency), đảm bảo không có double booking.

8. Áp dụng các Design Patterns phù hợp (Repository, Service Layer, Factory, Strategy, Observer) để code dễ đọc, dễ maintain.

**Về xây dựng hệ thống:**

9. Xây dựng module cho khách hàng với các chức năng:
   - Xem danh sách phim đang chiếu và sắp chiếu
   - Xem thông tin chi tiết phim (trailer, thời lượng, thể loại, diễn viên)
   - Xem lịch chiếu theo rạp và theo ngày
   - Chọn ghế trên sơ đồ trực quan với hiển thị real-time ghế còn trống
   - Thanh toán online an toàn qua các cổng thanh toán (Momo, VNPay - sandbox mode)
   - Nhận vé điện tử có mã QR code qua email
   - Quản lý tài khoản cá nhân và lịch sử đặt vé
   - Tích lũy và sử dụng điểm thành viên

10. Xây dựng module quản trị (admin panel) với các chức năng:
    - Quản lý thông tin phim (thêm, sửa, xóa)
    - Quản lý rạp và phòng chiếu
    - Tạo và điều chỉnh lịch chiếu (scheduling)
    - Quản lý giá vé (theo loại ghế, ngày trong tuần)
    - Xem báo cáo doanh thu theo nhiều tiêu chí (ngày, tuần, tháng, phim, rạp)
    - Quản lý người dùng

11. Triển khai cơ chế xử lý đồng thời với Redis distributed locking để giải quyết vấn đề race condition khi nhiều user chọn cùng ghế.

12. Triển khai cơ chế tự động giải phóng ghế đã lock sau timeout (5 phút) nếu không thanh toán.

**Về kiểm thử và đánh giá:**

13. Viết unit test và integration test để đảm bảo chất lượng code.

14. Kiểm thử các tình huống đồng thời (concurrent booking scenarios) để verify giải pháp locking.

15. Đánh giá hiệu năng của hệ thống (response time, throughput) với các công cụ load testing.

---

## 1.4 Phạm vi đề tài

Để đảm bảo tính khả thi và hoàn thành trong thời gian quy định của đồ án môn học, phạm vi của đề tài được xác định rõ ràng như sau:

### 1.4.1 Các chức năng trong phạm vi (In-scope)

**Module khách hàng (Customer):**

1. Quản lý tài khoản:
   - Đăng ký tài khoản mới
   - Đăng nhập bằng email/username và password
   - Cập nhật thông tin cá nhân (họ tên, số điện thoại, ngày sinh)
   - Đổi mật khẩu

2. Tìm kiếm và xem phim:
   - Browse danh sách phim đang chiếu
   - Tìm kiếm phim theo tên, thể loại
   - Xem thông tin chi tiết phim (mô tả, thời lượng, rating, trailer, diễn viên, đạo diễn)
   - Xem danh sách phim sắp chiếu

3. Đặt vé:
   - Xem lịch chiếu theo rạp và ngày
   - Chọn suất chiếu
   - Xem sơ đồ ghế với trạng thái real-time (trống/đã đặt/đang được giữ)
   - Chọn ghế (tối đa 10 ghế/lần)
   - Xem tổng tiền tự động
   - Thanh toán online qua Momo, VNPay (sandbox mode)
   - Nhận vé điện tử (QR code) qua email

4. Quản lý đặt vé:
   - Xem lịch sử đặt vé
   - Xem chi tiết vé đã đặt
   - Hủy vé (theo chính sách: trước 2 giờ chiếu)
   - Tải lại vé điện tử

5. Membership:
   - Tích điểm tự động khi đặt vé (1,000 VND = 1 điểm)
   - Xem số điểm hiện có
   - Sử dụng điểm để giảm giá (100 điểm = 50,000 VND)

**Module quản trị (Admin):**

1. Quản lý phim:
   - Thêm phim mới (thông tin, poster, trailer)
   - Cập nhật thông tin phim
   - Xóa phim (soft delete)
   - Xem danh sách phim

2. Quản lý rạp và phòng chiếu:
   - Thêm/sửa/xóa rạp
   - Thêm/sửa/xóa phòng chiếu (hall/screen)
   - Cấu hình sơ đồ ghế cho phòng chiếu (số hàng, số cột, loại ghế)

3. Quản lý suất chiếu:
   - Tạo suất chiếu mới (chọn phim, rạp, phòng, thời gian, giá vé)
   - Cập nhật thông tin suất chiếu
   - Hủy suất chiếu
   - Xem danh sách suất chiếu theo bộ lọc (ngày, rạp, phim)

4. Quản lý giá vé:
   - Thiết lập giá vé cơ bản
   - Thiết lập giá vé theo loại ghế (Standard, VIP)
   - Thiết lập giá vé theo ngày (ngày thường, cuối tuần)

5. Báo cáo và thống kê:
   - Báo cáo doanh thu theo ngày, tuần, tháng
   - Báo cáo doanh thu theo phim
   - Báo cáo doanh thu theo rạp
   - Thống kê số lượng vé bán ra
   - Thống kê tỷ lệ lấp đầy ghế (occupancy rate)

6. Quản lý người dùng:
   - Xem danh sách người dùng
   - Khóa/mở khóa tài khoản
   - Xem lịch sử đặt vé của user

**Module hệ thống (System):**

1. Xử lý đồng thời:
   - Distributed locking với Redis khi user chọn ghế
   - Đảm bảo không có double booking

2. Tự động hóa:
   - Scheduled job tự động giải phóng ghế lock sau 5 phút nếu không thanh toán
   - Scheduled job cleanup dữ liệu cũ (optional)

3. Thông báo:
   - Gửi email xác nhận sau khi đặt vé thành công
   - Gửi email xác nhận khi hủy vé

### 1.4.2 Các chức năng ngoài phạm vi (Out-of-scope)

Để tập trung vào các chức năng core và đảm bảo hoàn thành đúng thời hạn, các chức năng sau KHÔNG nằm trong phạm vi đề tài:

**Ứng dụng di động (Mobile App):**
- iOS app, Android app
- Lý do: Đồ án tập trung vào backend và web, mobile app có thể phát triển sau

**Tích hợp AI/Machine Learning:**
- Hệ thống gợi ý phim (recommendation engine)
- Chatbot hỗ trợ khách hàng
- Lý do: Phức tạp, yêu cầu data lớn và thời gian training model

**Tính năng mạng xã hội:**
- Review và rating phim
- Share vé lên social media
- Rủ bạn xem phim cùng
- Lý do: Không phải core business, có thể thêm sau

**Quản lý nội bộ chi tiết:**
- Quản lý nhân sự (chấm công, tính lương)
- Quản lý tài chính kế toán sâu (thuế, chi phí vận hành)
- Quản lý kho hàng hóa (nhập/xuất kho bắp, nước, đồ ăn)
- Lý do: Nằm ngoài scope của hệ thống đặt vé, thuộc về ERP

**Tích hợp bên thứ ba:**
- Bán vé qua nền tảng thứ ba (Tiki, Shopee, Grab)
- Tích hợp với hệ thống CRM, ERP của doanh nghiệp
- Lý do: Yêu cầu API và partnership phức tạp

**Tính năng thanh toán nâng cao:**
- Trả góp
- Cryptocurrency
- Ví điện tử quốc tế (PayPal, Stripe)
- Lý do: Chỉ sử dụng payment sandbox cho demo

**Video call support:**
- Live chat với nhân viên support
- Video call tư vấn
- Lý do: Phức tạp về kỹ thuật (WebRTC)

**Multiple languages:**
- Đa ngôn ngữ (English, Korean, etc.)
- Lý do: Tập trung thị trường Việt Nam

### 1.4.3 Giới hạn về quy mô

Để phục vụ mục đích học tập và demo, hệ thống được thiết kế với quy mô:

- Số lượng rạp: 5 rạp (đại diện 5 thành phố lớn)
- Số phòng chiếu/rạp: 6 phòng
- Số ghế/phòng: 50-100 ghế
- Số phim đang chiếu: 15 phim
- Số suất chiếu/ngày: Khoảng 150-200 suất
- Số user: Giả lập 10,000 users
- Concurrent users: Xử lý được 200-300 users đồng thời
- Data retention: Lưu trữ data trong 1 năm

Quy mô này đủ để:
- Demo đầy đủ các tính năng
- Test các tình huống concurrency
- Đánh giá performance
- Nhưng không quá lớn để khó triển khai trong thời gian môn học

---

## 1.5 Mô tả giải pháp đề xuất

### 1.5.1 Kiến trúc tổng thể

Hệ thống được đề xuất xây dựng dưới dạng một ứng dụng Web (Web Application) theo mô hình kiến trúc phân tầng (Layered Architecture) kết hợp với mô hình Client-Server.

**Kiến trúc 3 tầng (Three-tier Architecture):**

```
┌─────────────────────────────────────┐
│   Presentation Layer (Client)       │
│   - Web Browser                     │
│   - React.js UI                     │
│   - HTTP/REST API calls             │
└─────────────────┬───────────────────┘
                  │
                  │ HTTPS
                  │
┌─────────────────▼───────────────────┐
│   Application Layer (Server)        │
│   - Spring Boot Backend             │
│   - Business Logic                  │
│   - API Endpoints                   │
│   - Authentication & Authorization  │
└─────────────────┬───────────────────┘
                  │
                  │ JDBC / Driver
                  │
┌─────────────────▼───────────────────┐
│   Data Layer (Database)             │
│   - PostgreSQL (Relational data)    │
│   - Redis (Cache, Distributed Lock) │
└─────────────────────────────────────┘
```

### 1.5.2 Công nghệ sử dụng

**Phía Client (Frontend):**

Giao diện người dùng được xây dựng bằng các công nghệ web hiện đại:
- **Framework:** React.js 18 - Framework JavaScript phổ biến, component-based, hiệu năng cao
- **State Management:** Redux Toolkit hoặc Zustand - Quản lý state toàn cục
- **Styling:** Tailwind CSS - Utility-first CSS framework, development nhanh
- **HTTP Client:** Axios - Gọi API, xử lý authentication
- **Build Tool:** Vite - Fast build và hot reload

**Phía Server (Backend):**

Backend được phát triển bằng:
- **Framework:** Spring Boot 3.2 (Java 17) - Framework enterprise-level, mature, nhiều thư viện hỗ trợ
- **Security:** Spring Security + JWT - Authentication và Authorization
- **ORM:** Spring Data JPA (Hibernate) - Object-Relational Mapping
- **Validation:** Hibernate Validator - Validate input data
- **Build Tool:** Maven - Dependency management

**Cơ sở dữ liệu:**

- **PostgreSQL 15:**
  - Lưu trữ dữ liệu quan hệ (users, movies, theaters, halls, seats, shows, bookings, payments)
  - ACID compliance đảm bảo tính toàn vẹn dữ liệu
  - Hỗ trợ JSONB cho flexible data

- **Redis 7:**
  - Distributed locking để xử lý concurrency
  - Caching để tăng performance (cache user profile, show availability)
  - Session storage
  - TTL (Time-To-Live) cho seat lock timeout

**Các dịch vụ bên ngoài (External Services):**

- **Payment Gateway:**
  - Momo Sandbox API
  - VNPay Sandbox API
  - Xử lý thanh toán online an toàn

- **Email Service:**
  - SMTP (Gmail SMTP hoặc SendGrid)
  - Gửi email xác nhận đặt vé
  - Gửi vé điện tử với QR code

**DevOps & Tools:**

- **Version Control:** Git, GitHub
- **Containerization:** Docker, Docker Compose
- **API Testing:** Postman
- **Database Tool:** DBeaver (PostgreSQL), RedisInsight (Redis)
- **IDE:** IntelliJ IDEA (Backend), VS Code (Frontend)

### 1.5.3 Các module chính của hệ thống

**Module 1: Authentication & User Management**
- Chức năng: Đăng ký, đăng nhập, quản lý profile
- Công nghệ: Spring Security, JWT, BCrypt password hashing

**Module 2: Movie Management**
- Chức năng: Quản lý thông tin phim, upload poster/trailer
- Công nghệ: Spring Boot REST API, File upload

**Module 3: Theater & Hall Management**
- Chức năng: Quản lý rạp, phòng chiếu, cấu hình ghế
- Công nghệ: Spring Boot, PostgreSQL

**Module 4: Show Scheduling**
- Chức năng: Tạo và quản lý lịch chiếu
- Công nghệ: Spring Boot, Business logic validation

**Module 5: Seat Selection & Booking**
- Chức năng: Xem sơ đồ ghế, chọn ghế, giữ ghế tạm thời
- Công nghệ: Redis distributed locking, WebSocket (optional cho real-time update)

**Module 6: Payment Processing**
- Chức năng: Tích hợp payment gateway, xử lý transaction
- Công nghệ: Momo/VNPay API, Transaction management

**Module 7: Ticket Generation**
- Chức năng: Sinh vé điện tử với QR code
- Công nghệ: QR code library (ZXing), Email service

**Module 8: Reporting & Analytics**
- Chức năng: Báo cáo doanh thu, thống kê
- Công nghệ: SQL aggregation queries, Chart.js (frontend)

**Module 9: Membership & Points**
- Chức năng: Tích điểm, đổi điểm
- Công nghệ: Business logic, Database triggers/stored procedures

### 1.5.4 Luồng xử lý chính (Main Flow)

**Luồng đặt vé của khách hàng:**

1. User đăng nhập vào hệ thống
2. User browse danh sách phim, chọn phim muốn xem
3. User xem thông tin chi tiết phim, trailer
4. User chọn rạp, ngày chiếu, suất chiếu
5. Hệ thống hiển thị sơ đồ ghế với trạng thái real-time
6. User chọn ghế (1-10 ghế)
7. Hệ thống gọi API lock ghế (Redis SET key với TTL 5 phút)
8. Nếu lock thành công, cập nhật database seat status = LOCKED
9. User xác nhận, chuyển sang trang thanh toán
10. User chọn phương thức thanh toán (Momo/VNPay)
11. Hệ thống redirect đến payment gateway
12. User xác nhận thanh toán trên app Momo/VNPay
13. Payment gateway gửi callback về server
14. Server verify callback signature
15. Nếu thanh toán thành công:
    - Update booking status = CONFIRMED
    - Update seat status = BOOKED
    - Generate QR code cho vé
    - Gửi email vé điện tử
    - Tích điểm cho user
16. Redirect user về trang success, hiển thị vé

**Luồng timeout seat lock:**

1. Scheduled job chạy mỗi 1 phút
2. Query tất cả seats có status = LOCKED và expires_at < NOW()
3. Update seat status = AVAILABLE
4. Delete Redis lock key
5. Update booking status = EXPIRED (nếu có)

**Luồng xử lý đồng thời:**

1. User A chọn ghế A5 → Gọi API lock seat
2. Server try SET Redis key "seat:showId:A5" = "userA_id" NX EX 300
3. Nếu success → Lock thành công, update database
4. User B chọn ghế A5 (0.5 giây sau) → Gọi API lock seat
5. Server try SET Redis key "seat:showId:A5" = "userB_id" NX EX 300
6. Redis return NULL (key đã tồn tại) → Lock failed
7. Server return error "Ghế đã có người chọn"
8. User B phải chọn ghế khác

---

## 1.6 Ý nghĩa thực tiễn

### 1.6.1 Đối với khách hàng

**Tiết kiệm thời gian:**
Khách hàng không cần đến rạp, xếp hàng chờ đợi. Có thể đặt vé mọi lúc mọi nơi chỉ với vài thao tác trên điện thoại hoặc laptop.

**Chủ động lựa chọn:**
Khách hàng có thể xem trước sơ đồ ghế, biết rõ ghế nào còn trống, chọn vị trí ưng ý nhất (ghế giữa, ghế đôi, ghế VIP) trước khi đến rạp.

**Minh bạch giá cả:**
Giá vé hiển thị rõ ràng, có thể so sánh giá giữa các suất, các rạp khác nhau. Dễ dàng tận dụng các chương trình khuyến mãi online.

**An toàn và tiện lợi:**
Vé điện tử với QR code, không lo mất vé giấy. Có thể lưu vé trên điện thoại hoặc in ra.

**Tích lũy ưu đãi:**
Tự động tích điểm mỗi lần đặt vé, sử dụng điểm để giảm giá cho các lần sau.

### 1.6.2 Đối với rạp chiếu phim

**Tăng doanh thu:**
- Dễ dàng tiếp cận khách hàng, không giới hạn bởi địa lý
- Tăng tỷ lệ chuyển đổi (conversion rate) do quá trình đặt vé nhanh chóng
- Tận dụng được các slot suất chiếu ít người xem bằng dynamic pricing

**Giảm chi phí vận hành:**
- Giảm số lượng nhân viên quầy vé cần thiết
- Tự động hóa nhiều quy trình (lập lịch, báo cáo)
- Giảm sai sót do nhân viên

**Quản lý tốt hơn:**
- Dữ liệu real-time về tình hình bán vé
- Dễ dàng điều chỉnh giá vé, lịch chiếu dựa trên data
- Báo cáo doanh thu chi tiết, đa chiều

**Dữ liệu khách hàng có giá trị:**
- Thu thập thông tin, thói quen xem phim của khách hàng
- Phân tích để đưa ra chiến lược marketing hiệu quả
- Gửi thông báo phim mới, khuyến mãi cá nhân hóa

**Nâng cao uy tín thương hiệu:**
- Hệ thống hiện đại, chuyên nghiệp
- Trải nghiệm khách hàng tốt hơn
- Cạnh tranh được với các chuỗi rạp lớn

### 1.6.3 Đối với sinh viên

**Áp dụng kiến thức lý thuyết vào thực tế:**
- Phân tích yêu cầu từ nghiệp vụ thực tế
- Thiết kế hệ thống từ kiến trúc đến database
- Áp dụng Design Patterns học từ môn học

**Rèn luyện kỹ năng lập trình:**
- Làm việc với Spring Boot framework
- Học về distributed systems (Redis locking)
- Tích hợp API bên thứ ba (Payment gateway)
- Viết code clean, maintain được

**Học về vấn đề thực tế:**
- Xử lý concurrency - vấn đề phổ biến trong các hệ thống thực tế
- Transaction management
- Security best practices
- Performance optimization

**Xây dựng portfolio:**
- Có một project hoàn chỉnh để demo khi đi phỏng vấn
- Thể hiện khả năng giải quyết vấn đề phức tạp
- Tăng cơ hội tìm được việc làm tốt

**Làm việc nhóm:**
- Phân chia công việc
- Git workflow
- Code review
- Documentation

---

## 1.7 Cấu trúc báo cáo

Báo cáo đồ án được tổ chức thành 7 chương như sau:

**Chương 1: Tổng quan**
Giới thiệu bối cảnh, đặt vấn đề, mục tiêu, phạm vi và giải pháp đề xuất cho đề tài.

**Chương 2: Xác định yêu cầu**
Phân tích chi tiết các yêu cầu nghiệp vụ, yêu cầu chức năng (Functional Requirements) và yêu cầu phi chức năng (Non-Functional Requirements) của hệ thống. Liệt kê đầy đủ các use case và mô tả chi tiết.

**Chương 3: Phân tích nhu cầu**
Xác định các đối tượng sử dụng (actors), vẽ Use Case Diagram, Activity Diagram, Sequence Diagram, State Diagram để mô tả các quy trình nghiệp vụ và luồng xử lý của hệ thống.

**Chương 4: Thiết kế kiến trúc**
Trình bày kiến trúc tổng thể của hệ thống, thiết kế các tầng (Layer Design), Class Diagram chi tiết, áp dụng các Design Patterns, và giải pháp xử lý concurrency.

**Chương 5: Thiết kế cơ sở dữ liệu**
Thiết kế ER Diagram, mô tả chi tiết các bảng trong PostgreSQL, schema Redis, các ràng buộc toàn vẹn, indexing strategy, và normalization.

**Chương 6: Xây dựng mẫu thử**
Trình bày quá trình triển khai hệ thống, môi trường phát triển, cấu trúc project, code mẫu cho các module quan trọng, kết quả testing, và screenshots demo hệ thống.

**Chương 7: Kết luận và hướng phát triển**
Tổng kết những gì đã đạt được, đánh giá kết quả, các hạn chế còn tồn tại, bài học kinh nghiệm, và đề xuất hướng phát triển trong tương lai.

**Phụ lục:**
- Bảng thuật ngữ (Glossary)
- Tài liệu tham khảo (References)
- Sample data
- Source code (optional, có thể đính kèm link GitHub)

---

## 1.8 Kết luận chương

Chương 1 đã trình bày tổng quan về đề tài "Phân tích và Thiết kế Hệ thống Đặt vé Trực tuyến cho Chuỗi Rạp chiếu phim". Qua phân tích bối cảnh, các vấn đề của phương thức đặt vé truyền thống, và nhu cầu thực tế, đề tài có ý nghĩa quan trọng cả về mặt thực tiễn lẫn học thuật.

Mục tiêu của đề tài là xây dựng một hệ thống hoàn chỉnh với đầy đủ các chức năng từ phía khách hàng (đặt vé, thanh toán, quản lý tài khoản) đến phía quản trị (quản lý phim, rạp, suất chiếu, báo cáo), đồng thời giải quyết được các thách thức kỹ thuật như xử lý đồng thời, bảo mật, và hiệu năng.

Phạm vi đề tài đã được xác định rõ ràng với các chức năng core và loại trừ các chức năng phức tạp nằm ngoài khả năng của đồ án môn học. Giải pháp đề xuất sử dụng kiến trúc phân tầng với công nghệ Spring Boot, React.js, PostgreSQL và Redis, phù hợp với yêu cầu của bài toán.

Các chương tiếp theo sẽ đi sâu vào phân tích yêu cầu chi tiết, thiết kế hệ thống, thiết kế cơ sở dữ liệu, và xây dựng mẫu thử để hiện thực hóa các mục tiêu đã đề ra.
