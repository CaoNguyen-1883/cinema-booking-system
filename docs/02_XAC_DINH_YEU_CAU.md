# CHƯƠNG 2: XÁC ĐỊNH YÊU CẦU

## 2.1 Yêu cầu nghiệp vụ (Business Requirements)

Yêu cầu nghiệp vụ mô tả các mục tiêu mà hệ thống cần đạt được từ góc độ doanh nghiệp và người dùng cuối. Dựa trên phân tích ở Chương 1, các yêu cầu nghiệp vụ được xác định như sau:

### 2.1.1 Yêu cầu nghiệp vụ từ phía khách hàng

**BR-01: Tìm kiếm và xem thông tin phim**

Khách hàng cần có khả năng tìm kiếm, xem danh sách phim đang chiếu và sắp chiếu. Mỗi phim cần hiển thị đầy đủ thông tin như tên phim, thể loại, thời lượng, rating, diễn viên, đạo diễn, trailer, và mô tả nội dung. Điều này giúp khách hàng đưa ra quyết định xem phim nào.

**BR-02: Xem lịch chiếu theo nhiều tiêu chí**

Khách hàng cần xem lịch chiếu theo rạp gần nhà, theo ngày cụ thể, hoặc theo phim yêu thích. Lịch chiếu phải hiển thị đầy đủ thông tin: giờ chiếu, phòng chiếu, loại phòng (2D/3D), giá vé, và số ghế còn trống.

**BR-03: Chọn ghế trực quan và real-time**

Khách hàng cần xem sơ đồ ghế của phòng chiếu một cách trực quan (visual seat map), với trạng thái ghế được cập nhật real-time (trống/đã đặt/đang được giữ). Khách hàng có thể chọn vị trí ghế ưng ý trước khi thanh toán.

**BR-04: Thanh toán online an toàn và tiện lợi**

Khách hàng cần thanh toán online qua các phương thức phổ biến (ví điện tử, thẻ ngân hàng) một cách nhanh chóng và bảo mật. Sau khi thanh toán thành công, khách hàng nhận vé điện tử ngay lập tức.

**BR-05: Nhận và quản lý vé điện tử**

Khách hàng nhận vé điện tử có mã QR code qua email và có thể lưu trữ trong tài khoản. Vé điện tử có thể được sử dụng để check-in tại rạp mà không cần in vé giấy.

**BR-06: Quản lý lịch sử đặt vé**

Khách hàng cần xem lại các vé đã đặt trước đó, bao gồm cả vé đã sử dụng, vé sắp tới, và vé đã hủy. Điều này giúp khách hàng theo dõi chi tiêu và lên kế hoạch xem phim.

**BR-07: Hủy vé và hoàn tiền**

Khách hàng cần có khả năng hủy vé đã đặt theo chính sách quy định (ví dụ: trước 2 giờ chiếu) và nhận lại tiền. Quy trình hoàn tiền phải minh bạch và tự động.

**BR-08: Tích lũy và sử dụng điểm thưởng**

Khách hàng được tích điểm mỗi khi đặt vé (ví dụ: 1,000 VND = 1 điểm). Điểm có thể được sử dụng để giảm giá cho các lần đặt vé tiếp theo, khuyến khích khách hàng trung thành.

### 2.1.2 Yêu cầu nghiệp vụ từ phía quản trị

**BR-09: Quản lý danh mục phim**

Quản trị viên cần quản lý thông tin phim (thêm mới, cập nhật, xóa), bao gồm: tên phim, mô tả, thời lượng, thể loại, rating, ngày phát hành, poster, trailer. Quản lý phim đang chiếu và phim sắp chiếu.

**BR-10: Quản lý cơ sở vật chất**

Quản trị viên cần quản lý thông tin rạp chiếu phim (tên, địa chỉ, thành phố), phòng chiếu trong mỗi rạp (tên phòng, loại phòng, số ghế), và cấu hình sơ đồ ghế cho mỗi phòng chiếu.

**BR-11: Tạo và quản lý lịch chiếu**

Quản trị viên cần tạo suất chiếu mới bằng cách chọn phim, rạp, phòng chiếu, ngày giờ chiếu, và thiết lập giá vé. Hệ thống phải đảm bảo không có trùng lịch cho cùng một phòng chiếu. Quản trị viên cũng cần có khả năng cập nhật hoặc hủy suất chiếu.

**BR-12: Thiết lập giá vé linh hoạt**

Quản trị viên cần thiết lập giá vé theo nhiều yếu tố: loại ghế (Standard, VIP), ngày trong tuần (ngày thường, cuối tuần), loại phòng chiếu (2D, 3D, 4DX). Điều này cho phép áp dụng chiến lược giá linh hoạt để tối ưu doanh thu.

**BR-13: Theo dõi tình hình bán vé real-time**

Quản trị viên cần xem tình hình bán vé của từng suất chiếu theo thời gian thực (số vé đã bán, số ghế còn trống, tỷ lệ lấp đầy). Điều này giúp đưa ra quyết định điều chỉnh giá hoặc lịch chiếu kịp thời.

**BR-14: Xem báo cáo doanh thu và thống kê**

Quản trị viên cần xem báo cáo doanh thu theo nhiều tiêu chí: theo ngày/tuần/tháng, theo phim, theo rạp, theo loại vé. Báo cáo giúp đánh giá hiệu quả kinh doanh và lập kế hoạch dài hạn.

**BR-15: Quản lý người dùng**

Quản trị viên cần xem danh sách người dùng, xem lịch sử giao dịch của từng user, và có khả năng khóa/mở khóa tài khoản khi phát hiện hành vi vi phạm.

### 2.1.3 Yêu cầu nghiệp vụ từ phía hệ thống

**BR-16: Đảm bảo tính nhất quán dữ liệu**

Hệ thống phải đảm bảo không có tình trạng double booking (hai người cùng đặt một ghế). Khi nhiều người dùng cùng chọn ghế đồng thời, chỉ một người thành công, những người còn lại nhận thông báo ghế đã hết.

**BR-17: Tự động giải phóng ghế đã lock**

Khi người dùng chọn ghế nhưng không hoàn tất thanh toán trong thời gian quy định (ví dụ 5 phút), hệ thống phải tự động giải phóng ghế để người khác có thể đặt.

**BR-18: Gửi thông báo tự động**

Hệ thống tự động gửi email xác nhận sau khi đặt vé thành công, email chứa thông tin vé và mã QR code. Hệ thống cũng gửi email xác nhận khi hủy vé.

**BR-19: Xử lý thanh toán an toàn**

Hệ thống phải tích hợp với các cổng thanh toán uy tín (Momo, VNPay) và xử lý giao dịch theo chuẩn bảo mật. Đảm bảo không lưu trữ thông tin thẻ thanh toán của khách hàng.

---

## 2.2 Yêu cầu chức năng (Functional Requirements)

Yêu cầu chức năng mô tả chi tiết các chức năng mà hệ thống phải cung cấp. Mỗi yêu cầu được đánh số và mô tả rõ ràng.

### 2.2.1 Module Quản lý người dùng (User Management)

**FR-01: Đăng ký tài khoản**

Mô tả: Người dùng mới có thể tạo tài khoản bằng cách cung cấp thông tin cơ bản.

Input:
- Username (duy nhất, 6-50 ký tự, chỉ chứa chữ cái, số, underscore)
- Email (duy nhất, phải hợp lệ theo format email)
- Password (tối thiểu 8 ký tự, phải có chữ hoa, chữ thường, số)
- Full name (2-100 ký tự)
- Phone number (optional, 10-11 chữ số)
- Date of birth (optional, phải từ 10 tuổi trở lên)

Process:
- Validate input data
- Kiểm tra email và username chưa tồn tại trong hệ thống
- Hash password bằng BCrypt
- Lưu thông tin user vào database
- Tự động tạo tài khoản membership với 0 điểm

Output:
- Thông báo đăng ký thành công
- Tự động đăng nhập (optional)
- Gửi email welcome (optional)

Business Rules:
- Email và username phải unique
- Password phải được mã hóa, không lưu plaintext
- User mới mặc định có role = CUSTOMER

**FR-02: Đăng nhập**

Mô tả: Người dùng đã có tài khoản có thể đăng nhập vào hệ thống.

Input:
- Email hoặc Username
- Password

Process:
- Kiểm tra user tồn tại trong database
- Verify password (so sánh hash)
- Kiểm tra tài khoản có bị khóa không (is_active = true)
- Generate JWT access token (expiry: 30 phút, chứa userId, role, tokenVersion)
- Generate JWT refresh token (expiry: 30 ngày, chứa userId, tokenVersion)
- Set refresh token vào HttpOnly Cookie (Secure, SameSite=Strict)

Output:
- Access token (trong response body)
- Refresh token (trong HttpOnly Cookie, không trả về body)
- Thông tin user (id, username, email, fullName, role)

Business Rules:
- Sau 5 lần đăng nhập sai liên tiếp, khóa tài khoản 15 phút
- Access token lưu trong memory (React state), KHÔNG lưu localStorage (tránh XSS)
- Refresh token lưu trong HttpOnly Cookie (browser tự động gửi, không bị XSS đọc)
- Access token gửi trong header Authorization: Bearer {token}
- Sử dụng Token Version để hỗ trợ revoke token (mỗi user có field tokenVersion trong DB)

**FR-03: Quên mật khẩu**

Mô tả: Người dùng quên mật khẩu có thể yêu cầu reset mật khẩu qua email.

Input:
- Email

Process:
- Kiểm tra email tồn tại
- Generate reset token (random, expiry 1 giờ)
- Lưu token vào database với timestamp
- Gửi email chứa link reset password (https://domain.com/reset-password?token=xyz)

Output:
- Email chứa link reset
- Thông báo "Kiểm tra email để reset mật khẩu"

Business Rules:
- Reset token chỉ dùng được 1 lần
- Reset token hết hạn sau 1 giờ
- Giới hạn 3 lần reset/ngày để tránh spam

**FR-04: Đổi mật khẩu**

Mô tả: Người dùng đã đăng nhập có thể đổi mật khẩu.

Input:
- Old password
- New password
- Confirm new password

Process:
- Verify old password
- Validate new password (quy tắc mật khẩu mạnh)
- Kiểm tra new password != old password
- Hash new password
- Update database
- Invalidate tất cả tokens cũ (force logout)

Output:
- Thông báo đổi mật khẩu thành công
- Redirect về trang login

**FR-05: Cập nhật thông tin cá nhân**

Mô tả: Người dùng có thể cập nhật thông tin profile.

Input:
- Full name
- Phone number
- Date of birth
- Avatar image (optional)

Process:
- Validate input
- Upload avatar to file storage nếu có
- Update database

Output:
- Thông tin user đã cập nhật
- Thông báo thành công

Business Rules:
- Không thể thay đổi email và username sau khi đăng ký
- Avatar tối đa 2MB, format: JPG, PNG

**FR-06: Xem thông tin tài khoản**

Mô tả: Người dùng xem thông tin cá nhân và điểm thành viên.

Input: User ID (từ token)

Output:
- Thông tin user: username, email, fullName, phone, dateOfBirth, avatarUrl
- Điểm thành viên hiện tại
- Ngày tham gia (created_at)

**FR-07: Đăng xuất**

Mô tả: Người dùng đăng xuất khỏi hệ thống.

Process:
- Backend tăng tokenVersion trong User entity (invalidate tất cả tokens cũ)
- Clear HttpOnly Cookie chứa refresh token (set maxAge=0)
- Frontend xóa access token trong memory và redirect về trang login

### 2.2.2 Module Quản lý phim (Movie Management)

**FR-08: Xem danh sách phim đang chiếu**

Mô tả: Người dùng xem danh sách tất cả phim đang chiếu tại rạp.

Input:
- Filter by genre (optional): Action, Comedy, Horror, Drama, Animation
- Sort by (optional): releaseDate, title, rating
- Pagination: page, limit (default 20 phim/trang)

Output:
- Danh sách phim với thông tin tóm tắt:
  - Movie ID
  - Title
  - Poster image
  - Genre
  - Duration
  - Rating (P, T13, T16, T18)
  - Release date
  - Is showing (true)

Business Rules:
- Chỉ hiển thị phim có is_showing = true
- Sort mặc định theo release_date DESC (phim mới nhất trước)

**FR-09: Xem danh sách phim sắp chiếu**

Mô tả: Người dùng xem danh sách phim sắp ra mắt.

Input:
- Pagination: page, limit

Output:
- Danh sách phim với is_showing = false và release_date trong tương lai

**FR-10: Xem chi tiết phim**

Mô tả: Người dùng xem thông tin chi tiết của một phim cụ thể.

Input: Movie ID

Output:
- Tất cả thông tin phim:
  - Title
  - Description (mô tả nội dung)
  - Duration (phút)
  - Genre
  - Rating
  - Language
  - Release date
  - Director (đạo diễn)
  - Cast (diễn viên - JSON array)
  - Poster URL
  - Trailer URL (YouTube embed)
- Danh sách rạp đang chiếu phim này
- Danh sách suất chiếu gần nhất (5 suất tiếp theo)

Business Rules:
- Trailer được embed từ YouTube (iframe)
- Cast hiển thị top 5 diễn viên chính

**FR-11: Tìm kiếm phim**

Mô tả: Người dùng tìm kiếm phim theo từ khóa.

Input:
- Keyword (tìm trong title, description, director, cast)
- Filter by genre (optional)
- Pagination

Process:
- Search trong database với LIKE query hoặc full-text search
- Match với title (priority cao nhất), description, director, cast

Output:
- Danh sách phim match với keyword

**FR-12: Admin - Thêm phim mới**

Mô tả: Admin thêm phim mới vào hệ thống.

Input:
- Title (required, max 200 ký tự)
- Description (required, max 2000 ký tự)
- Duration (required, số phút)
- Genre (required, select from predefined list)
- Rating (required: P, T13, T16, T18)
- Language (required, max 50 ký tự)
- Release date (required)
- Director (max 100 ký tự)
- Cast (JSON array of actor names)
- Poster image (upload file)
- Trailer URL (YouTube link)
- Is showing (boolean, default false)

Process:
- Validate all fields
- Upload poster image to file storage, get URL
- Parse trailer URL to get YouTube video ID
- Insert movie to database

Output:
- Movie object với ID
- Thông báo thêm phim thành công

Business Rules:
- Admin role required
- Poster image: max 5MB, JPG/PNG
- Trailer URL phải là YouTube link hợp lệ

**FR-13: Admin - Cập nhật thông tin phim**

Mô tả: Admin sửa thông tin phim đã có.

Input:
- Movie ID
- Các field cần update (tương tự FR-12)

Process:
- Kiểm tra movie tồn tại
- Validate input
- Update database
- Nếu update poster, upload file mới và xóa file cũ

Output:
- Movie object đã update
- Thông báo thành công

**FR-14: Admin - Xóa phim**

Mô tả: Admin xóa phim khỏi hệ thống.

Input: Movie ID

Process:
- Kiểm tra phim có suất chiếu trong tương lai không
- Nếu có: Không cho xóa, thông báo lỗi
- Nếu không: Soft delete (set is_active = false) hoặc hard delete

Output:
- Thông báo xóa thành công

Business Rules:
- Không được xóa phim đang có suất chiếu trong tương lai
- Ưu tiên soft delete để giữ lại lịch sử

### 2.2.3 Module Quản lý rạp và phòng chiếu (Theater & Hall Management)

**FR-15: Xem danh sách rạp**

Mô tả: Người dùng xem danh sách tất cả rạp trong hệ thống.

Input:
- Filter by city (optional)

Output:
- Danh sách rạp:
  - Theater ID
  - Name
  - Location (địa chỉ)
  - City
  - Phone
  - Số lượng phòng chiếu

Business Rules:
- Hiển thị theo city, sau đó theo name

**FR-16: Xem chi tiết rạp**

Mô tả: Người dùng xem thông tin chi tiết của một rạp.

Input: Theater ID

Output:
- Thông tin rạp: name, location, city, phone
- Danh sách phòng chiếu trong rạp:
  - Hall ID
  - Hall name
  - Total seats
  - Screen type (2D, 3D, 4DX)
- Danh sách phim đang chiếu tại rạp này
- Lịch chiếu hôm nay

**FR-17: Admin - Quản lý rạp (CRUD)**

Mô tả: Admin thêm, sửa, xóa rạp.

Input (Create):
- Name (required)
- Location (required)
- City (required)
- Phone (optional)

Process:
- Validate input
- Insert to database

Output:
- Theater object
- Thông báo thành công

Business Rules:
- Admin role required
- Không được xóa rạp đang có suất chiếu trong tương lai

**FR-18: Admin - Quản lý phòng chiếu (CRUD)**

Mô tả: Admin thêm, sửa, xóa phòng chiếu trong rạp.

Input (Create):
- Theater ID (required)
- Hall name (required, e.g., "Screen 1", "Screen 2")
- Total seats (required, số nguyên)
- Screen type (required: 2D, 3D, 4DX, IMAX)

Process:
- Validate input
- Insert hall to database
- Tự động tạo seats cho hall (theo total_seats)

Output:
- Hall object
- Thông báo thành công

Business Rules:
- Tên phòng chiếu phải unique trong cùng một rạp

**FR-19: Admin - Cấu hình sơ đồ ghế**

Mô tả: Admin cấu hình layout ghế cho phòng chiếu.

Input:
- Hall ID
- Số hàng (rows, e.g., 10)
- Số cột (columns, e.g., 10)
- Danh sách ghế VIP (array of seat positions, e.g., ["E5", "E6", "F5", "F6"])

Process:
- Xóa tất cả seats cũ của hall
- Generate seats mới theo rows x columns
- Mỗi seat có: row_label (A, B, C...), column_number (1, 2, 3...)
- Seat_number = row_label + column_number (e.g., "A1", "B5")
- Set seat_type = VIP cho ghế trong danh sách VIP, còn lại = STANDARD
- Insert seats vào database

Output:
- Thông báo cấu hình thành công
- Preview sơ đồ ghế

Business Rules:
- Rows tối đa 26 (A-Z)
- Columns tối đa 20
- Total seats phải match với rows x columns

### 2.2.4 Module Quản lý suất chiếu (Show Management)

**FR-20: Xem lịch chiếu theo rạp**

Mô tả: Người dùng xem lịch chiếu của một rạp trong một ngày cụ thể.

Input:
- Theater ID
- Date (default hôm nay)

Output:
- Danh sách suất chiếu, group by phim:
  - Movie title, poster
  - List of shows:
    - Show ID
    - Show time
    - End time
    - Hall name
    - Screen type
    - Base price
    - Available seats count

Business Rules:
- Chỉ hiển thị suất chiếu có status = SCHEDULED
- Sort theo show_time ASC

**FR-21: Xem lịch chiếu theo phim**

Mô tả: Người dùng xem tất cả suất chiếu của một phim cụ thể.

Input:
- Movie ID
- Date range (optional, default 7 ngày tới)

Output:
- Danh sách suất chiếu, group by rạp và ngày:
  - Theater name, city
  - Date
  - List of shows (tương tự FR-20)

**FR-22: Admin - Tạo suất chiếu mới**

Mô tả: Admin tạo suất chiếu cho một phim tại phòng chiếu cụ thể.

Input:
- Movie ID (required)
- Hall ID (required, chọn từ dropdown rạp -> phòng chiếu)
- Show time (required, datetime)
- Base price (required, VND)

Process:
- Validate movie và hall tồn tại
- Tính end_time = show_time + movie.duration + 20 phút buffer
- Kiểm tra phòng chiếu không bị trùng lịch:
  - Query shows của hall trong khoảng thời gian [show_time, end_time]
  - Nếu có show nào overlap -> return error
- Insert show vào database với status = SCHEDULED
- Tự động tạo show_seats: Copy từ seats của hall
  - Mỗi show_seat có: show_id, seat_id, status = AVAILABLE
  - Tính price cho mỗi ghế:
    - Standard seat: price = base_price
    - VIP seat: price = base_price + 20,000 VND
    - Nếu show vào cuối tuần (Sat, Sun): price += 30,000 VND

Output:
- Show object
- Thông báo tạo suất chiếu thành công

Business Rules:
- Admin role required
- Show time phải trong tương lai (không tạo suất chiếu quá khứ)
- Phòng chiếu không được trùng lịch
- Base price tối thiểu 40,000 VND

**FR-23: Admin - Cập nhật suất chiếu**

Mô tả: Admin sửa thông tin suất chiếu (thời gian, giá vé).

Input:
- Show ID
- Show time (optional)
- Base price (optional)

Process:
- Validate show tồn tại
- Kiểm tra show chưa diễn ra (show_time > NOW)
- Nếu update show_time: Kiểm tra không trùng lịch phòng chiếu
- Update database
- Nếu update base_price: Recalculate giá tất cả show_seats

Output:
- Show object đã update
- Thông báo thành công

Business Rules:
- Chỉ update được show chưa diễn ra
- Nếu đã có booking, không cho update show_time (chỉ update price)

**FR-24: Admin - Hủy suất chiếu**

Mô tả: Admin hủy suất chiếu (vì lý do kỹ thuật, không đủ khán giả, etc.).

Input: Show ID

Process:
- Validate show tồn tại
- Kiểm tra show chưa diễn ra
- Nếu đã có booking:
  - Update tất cả bookings thành CANCELLED
  - Trigger refund process cho tất cả bookings
  - Gửi email thông báo hủy suất chiếu cho khách hàng
- Update show status = CANCELLED
- Update tất cả show_seats status = UNAVAILABLE

Output:
- Thông báo hủy suất chiếu thành công
- Số lượng bookings đã bị hủy và hoàn tiền

Business Rules:
- Admin role required
- Phải gửi email thông báo cho tất cả khách hàng đã đặt vé
- Hoàn tiền 100% cho khách hàng

### 2.2.5 Module Đặt vé (Booking)

**FR-25: Xem sơ đồ ghế cho suất chiếu**

Mô tả: Người dùng xem sơ đồ ghế của một suất chiếu cụ thể với trạng thái real-time.

Input: Show ID

Output:
- Thông tin suất chiếu: movie title, theater, hall, show_time, base_price
- Sơ đồ ghế dạng matrix:
  - Mỗi ghế có: seat_number, row_label, column_number, seat_type, price, status
  - Status: AVAILABLE (trống), LOCKED (đang được giữ), BOOKED (đã đặt)
- Chú thích: màu sắc tương ứng với status
- Tổng số ghế: total, available, locked, booked

Business Rules:
- Status được tính real-time:
  - AVAILABLE: show_seat.status = 'AVAILABLE'
  - LOCKED: show_seat.status = 'LOCKED' AND expires_at > NOW()
  - BOOKED: show_seat.status = 'BOOKED'
- Ghế LOCKED hoặc BOOKED không thể chọn

**FR-26: Chọn ghế**

Mô tả: Người dùng chọn một hoặc nhiều ghế cho suất chiếu.

Input:
- Show ID
- List of Seat IDs (tối đa 10 ghế)
- User ID (từ JWT token)

Process:
- Validate user đã login
- Validate tất cả ghế thuộc show này
- Gọi service lock seats:
  - Với mỗi seat:
    - Try Redis SET key = "seat:lock:{showId}:{seatId}", value = userId, NX, EX 300
    - Nếu SET thành công (return 1):
      - Update database: show_seats.status = 'LOCKED', locked_by = userId, locked_at = NOW(), expires_at = NOW() + 5 minutes
    - Nếu SET thất bại (return 0, key đã tồn tại):
      - Ghế đã bị lock bởi user khác
      - Rollback tất cả ghế đã lock trước đó
      - Return error "Seat {seat_number} đã có người chọn"
- Nếu tất cả ghế lock thành công:
  - Tính tổng tiền (sum of seat prices)
  - Tạo draft booking với status = PENDING

Output:
- Booking object (draft):
  - Booking ID
  - Show info
  - List of seats
  - Total amount
  - Expires at (5 phút từ bây giờ)
- Thông báo "Ghế đã được giữ trong 5 phút"

Business Rules:
- User chỉ được chọn tối đa 10 ghế/lần
- Ghế được lock trong 5 phút (TTL = 300 seconds)
- Sử dụng Redis distributed lock để tránh race condition

**FR-27: Thanh toán**

Mô tả: Người dùng thanh toán cho booking đã tạo.

Input:
- Booking ID
- Payment method (MOMO, VNPAY, CREDIT_CARD)
- Points to use (optional, số điểm muốn sử dụng)

Process:
1. Validate booking tồn tại và status = PENDING
2. Validate booking chưa hết hạn (expires_at > NOW())
3. Validate ghế vẫn còn locked bởi user này
4. Nếu user dùng điểm:
   - Validate user có đủ điểm
   - Tính discount: 100 points = 50,000 VND
   - Update total_amount -= discount
   - Update points_used = số điểm đã dùng
5. Tạo payment record với status = PENDING
6. Gọi Payment Gateway API (Momo/VNPay):
   - Create payment request với amount, booking_id, return_url, callback_url
   - Nhận payment_url từ gateway
7. Return payment_url để redirect user

Output:
- Payment URL (redirect user đến trang thanh toán của Momo/VNPay)

Business Rules:
- Booking timeout: 5 phút
- 100 điểm = 50,000 VND discount
- Payment method: Momo, VNPay (sandbox), Credit Card (simulation)

**FR-28: Xử lý Payment Callback**

Mô tả: Hệ thống nhận callback từ Payment Gateway sau khi user thanh toán.

Input (từ Payment Gateway):
- Transaction ID
- Booking ID
- Status (SUCCESS, FAILED)
- Amount
- Signature (verify tính hợp lệ)

Process:
1. Verify signature từ gateway (đảm bảo request hợp lệ)
2. Validate booking tồn tại
3. Nếu payment SUCCESS:
   - Update payment.status = SUCCESS, paid_at = NOW()
   - Update booking.status = CONFIRMED
   - Update show_seats.status = BOOKED
   - Unlock Redis keys (delete "seat:lock:{showId}:{seatId}")
   - Generate booking_code (unique, 10 ký tự)
   - Generate QR code chứa booking_code
   - Tính points earned: total_amount / 1000 (1,000 VND = 1 điểm)
   - Update user.points += points_earned
   - Trừ points_used nếu có
   - Gửi email confirmation với vé điện tử (PDF + QR code)
4. Nếu payment FAILED:
   - Update payment.status = FAILED
   - Update booking.status = PAYMENT_FAILED
   - Release seats: update show_seats.status = AVAILABLE
   - Unlock Redis keys

Output:
- Return success response to gateway
- Update database accordingly

Business Rules:
- Verify signature để tránh fake callback
- Transaction phải idempotent (handle duplicate callback)
- Email phải được gửi trong vòng 1 phút

**FR-29: Xem chi tiết booking**

Mô tả: Người dùng xem thông tin chi tiết của một booking.

Input:
- Booking ID
- User ID (từ token, để verify ownership)

Output:
- Booking details:
  - Booking code
  - Movie: title, poster, rating, duration
  - Show: theater, hall, show_time
  - Seats: danh sách ghế đã đặt (seat_number, seat_type, price)
  - Total amount
  - Points earned
  - Points used
  - Payment method
  - Status
  - QR code image
  - Created at
- Nút "Download ticket" (PDF)
- Nút "Cancel booking" (nếu còn trong thời hạn hủy)

Business Rules:
- User chỉ xem được booking của chính mình
- Admin xem được tất cả bookings

**FR-30: Xem lịch sử booking**

Mô tả: Người dùng xem danh sách tất cả bookings đã đặt.

Input:
- User ID (từ token)
- Filter by status (optional): ALL, CONFIRMED, CANCELLED
- Pagination

Output:
- Danh sách bookings:
  - Booking code
  - Movie title, poster
  - Show time
  - Theater name
  - Number of seats
  - Total amount
  - Status
  - Created at
- Sort by created_at DESC

**FR-31: Hủy booking**

Mô tả: Người dùng hủy booking đã đặt và yêu cầu hoàn tiền.

Input:
- Booking ID
- User ID (từ token)

Process:
1. Validate booking tồn tại và thuộc về user
2. Validate booking.status = CONFIRMED
3. Kiểm tra thời gian:
   - Tính time_until_show = show.show_time - NOW()
   - Nếu time_until_show < 2 giờ: Return error "Không thể hủy vé trong vòng 2 giờ trước giờ chiếu"
4. Update booking.status = CANCELLED
5. Update show_seats.status = AVAILABLE (release ghế)
6. Create refund request:
   - Refund amount = booking.total_amount - 10,000 VND (phí hủy)
   - Refund method = original payment method
   - Refund status = PENDING
7. Hoàn điểm đã dùng (nếu có): user.points += booking.points_used
8. Trừ lại điểm đã tích lũy từ booking này: user.points -= booking.points_earned
9. Gửi email xác nhận hủy vé

Output:
- Thông báo hủy vé thành công
- Thông tin hoàn tiền (amount, method, thời gian dự kiến)

Business Rules:
- Chỉ hủy được trước 2 giờ chiếu
- Phí hủy: 10,000 VND
- Hoàn tiền trong 3-7 ngày làm việc

### 2.2.6 Module Membership & Points

**FR-32: Xem điểm hiện tại**

Mô tả: Người dùng xem số điểm thành viên hiện tại.

Input: User ID

Output:
- Điểm hiện tại
- Lịch sử tích điểm (10 giao dịch gần nhất):
  - Date
  - Type (EARN, USE, REFUND)
  - Amount (số điểm)
  - Description (e.g., "Đặt vé #ABC123", "Sử dụng 100 điểm")
  - Balance sau giao dịch

**FR-33: Sử dụng điểm để giảm giá**

Mô tả: Người dùng sử dụng điểm khi thanh toán booking (đã implement trong FR-27).

Business Rules:
- 100 điểm = 50,000 VND
- Tối thiểu sử dụng 100 điểm
- Chỉ dùng điểm bội số của 100 (100, 200, 300...)
- Không được dùng quá số điểm hiện có

**FR-34: Tích điểm tự động**

Mô tả: Hệ thống tự động tích điểm sau khi booking thành công (đã implement trong FR-28).

Business Rules:
- 1,000 VND = 1 điểm
- Chỉ tính trên giá vé sau khi trừ điểm (nếu có)
- Points earned được lưu trong booking record

### 2.2.7 Module Admin - Báo cáo (Reporting)

**FR-35: Báo cáo doanh thu theo thời gian**

Mô tả: Admin xem báo cáo doanh thu theo ngày/tuần/tháng.

Input:
- Time range (start_date, end_date)
- Group by (DAY, WEEK, MONTH)

Output:
- Chart: Doanh thu theo thời gian (line chart hoặc bar chart)
- Table:
  - Period (ngày/tuần/tháng)
  - Total revenue (tổng doanh thu)
  - Total bookings (số lượng bookings)
  - Total tickets (số lượng vé)
  - Average ticket price
- Summary:
  - Total revenue trong period
  - % tăng/giảm so với period trước

**FR-36: Báo cáo doanh thu theo phim**

Mô tả: Admin xem doanh thu của từng phim.

Input:
- Time range
- Sort by (revenue, tickets)

Output:
- Table:
  - Movie title
  - Genre
  - Total shows
  - Total tickets sold
  - Total revenue
  - Average ticket price
  - Occupancy rate (% ghế lấp đầy)
- Top 10 phim có doanh thu cao nhất

**FR-37: Báo cáo doanh thu theo rạp**

Mô tả: Admin xem doanh thu của từng rạp.

Input:
- Time range

Output:
- Table:
  - Theater name, city
  - Total shows
  - Total tickets sold
  - Total revenue
  - Occupancy rate
- So sánh doanh thu giữa các rạp (chart)

**FR-38: Báo cáo tỷ lệ lấp đầy ghế (Occupancy Rate)**

Mô tả: Admin xem tỷ lệ lấp đầy ghế theo suất chiếu, phim, rạp.

Input:
- Time range
- Group by (show, movie, theater)

Output:
- Table:
  - Show/Movie/Theater
  - Total seats
  - Seats booked
  - Occupancy rate (%)
- Average occupancy rate trong period

**FR-39: Báo cáo người dùng**

Mô tả: Admin xem thống kê về người dùng.

Output:
- Total users
- New users in period
- Active users (đã đặt ít nhất 1 vé trong 30 ngày)
- Top 10 users có điểm cao nhất
- Top 10 users đặt vé nhiều nhất

---

## 2.3 Yêu cầu phi chức năng (Non-Functional Requirements)

Yêu cầu phi chức năng mô tả các thuộc tính chất lượng của hệ thống.

### 2.3.1 Hiệu năng (Performance)

**NFR-01: Thời gian phản hồi (Response Time)**

- Load danh sách phim: < 2 giây
- Load sơ đồ ghế: < 1 giây
- Xử lý lock ghế: < 1 giây
- Xử lý thanh toán (không tính thời gian gateway): < 3 giây
- API endpoints khác: < 2 giây (P95)

Đo lường: Sử dụng JMeter hoặc Postman để test response time.

**NFR-02: Throughput (Số lượng request xử lý)**

- Hệ thống phải xử lý được ít nhất 100 requests/second trong điều kiện bình thường
- Peak time (premiere phim hot): 200-300 requests/second

Đo lường: Load testing với JMeter, simulate 200-300 concurrent users.

**NFR-03: Concurrent Users**

- Hệ thống phải hỗ trợ 200-300 users đồng thời đặt vé mà không bị crash hoặc slow down đáng kể
- Đảm bảo không có race condition khi nhiều users chọn cùng ghế

Đo lường: Stress testing với multiple users chọn cùng ghế.

**NFR-04: Database Query Performance**

- Các query thường xuyên (select shows, select seats) phải < 50ms
- Các query phức tạp (reports, aggregations) phải < 200ms
- Sử dụng database indexing để tối ưu

Đo lường: Analyze SQL execution plan, measure query time.

### 2.3.2 Tính sẵn sàng (Availability)

**NFR-05: Uptime**

- Hệ thống phải đạt 99% uptime (cho phép downtime tối đa 3.65 ngày/năm)
- Trong thực tế production: target 99.9% uptime

Đo lường: Monitor server uptime, log downtime incidents.

**NFR-06: Error Handling**

- Tất cả errors phải được catch và log
- Error messages phải rõ ràng, user-friendly
- System errors không được expose stack trace cho end user

### 2.3.3 Bảo mật (Security)

**NFR-07: Authentication**

- Sử dụng JWT (JSON Web Token) cho authentication
- Access token expiry: 30 phút (stateless, không lưu DB)
- Refresh token expiry: 30 ngày (lưu trong HttpOnly Cookie)
- Token Version strategy: revoke all tokens bằng cách tăng tokenVersion trong User table
- Tokens được truyền qua HTTPS
- Refresh token: HttpOnly, Secure, SameSite=Strict

**NFR-08: Password Security**

- Password phải được hash bằng BCrypt (cost factor ≥ 12)
- Không lưu trữ plain text password
- Password phải đáp ứng yêu cầu: tối thiểu 8 ký tự, có chữ hoa, chữ thường, số

**NFR-09: HTTPS/SSL**

- Tất cả communication phải qua HTTPS trong production
- SSL certificate hợp lệ
- Force redirect HTTP → HTTPS

**NFR-10: SQL Injection Prevention**

- Sử dụng ORM (JPA) với prepared statements
- Không concatenate SQL strings
- Validate và sanitize user input

**NFR-11: XSS Prevention**

- Escape tất cả user-generated content trước khi render
- Sử dụng Content Security Policy (CSP) headers
- Sanitize input data

**NFR-12: CSRF Protection**

- Implement CSRF tokens cho các POST/PUT/DELETE requests
- Validate CSRF token ở server

**NFR-13: Payment Security**

- Không lưu trữ credit card information
- Sử dụng PCI-DSS compliant payment gateways
- Verify callback signatures từ payment gateways

**NFR-14: Rate Limiting**

- Giới hạn số lượng requests từ một IP/user:
  - Login: 5 requests/minute
  - Register: 3 requests/minute
  - Booking: 10 requests/minute
  - Other endpoints: 100 requests/minute
- Sử dụng Redis để track rate limits

**NFR-15: Data Privacy**

- Tuân thủ GDPR (nếu có EU users) hoặc quy định bảo vệ dữ liệu cá nhân Việt Nam
- User có quyền xóa tài khoản và data
- Log không chứa sensitive information (passwords, credit cards)

### 2.3.4 Khả năng mở rộng (Scalability)

**NFR-16: User Scalability**

- Hệ thống hiện tại hỗ trợ 10,000 registered users
- Kiến trúc cho phép scale lên 100,000 users khi cần (thêm servers, database replication)

**NFR-17: Data Scalability**

- Database schema cho phép lưu trữ:
  - 10,000 users
  - 500 movies
  - 50,000 shows/năm
  - 200,000 bookings/năm
- Có thể thêm database sharding hoặc partitioning khi data lớn hơn

**NFR-18: Horizontal Scaling**

- Backend stateless: có thể deploy multiple instances phía sau load balancer
- Session data lưu trong Redis (shared state)
- Redis distributed locking cho phép scale backend

### 2.3.5 Khả năng bảo trì (Maintainability)

**NFR-19: Code Quality**

- Code tuân thủ coding conventions (Google Java Style Guide)
- Sử dụng Design Patterns phù hợp
- SOLID principles
- DRY (Don't Repeat Yourself)

**NFR-20: Test Coverage**

- Unit test coverage: ≥ 60%
- Integration test cho các API endpoints quan trọng
- Test các tình huống concurrency

**NFR-21: Documentation**

- API documentation đầy đủ (Swagger/OpenAPI)
- Code comments cho logic phức tạp
- README với hướng dẫn setup và run
- Database schema documentation

**NFR-22: Logging**

- Log tất cả important events (login, booking, payment)
- Log errors với stack trace
- Log format structured (JSON) để dễ parse
- Log levels: ERROR, WARN, INFO, DEBUG

**NFR-23: Monitoring**

- Monitor server health (CPU, memory, disk)
- Monitor API response times
- Monitor error rates
- Alerts khi có issues (optional trong scope đồ án)

### 2.3.6 Usability (Khả năng sử dụng)

**NFR-24: User Interface**

- Giao diện trực quan, dễ sử dụng
- Consistent design system (colors, fonts, spacing)
- Clear navigation
- Helpful error messages

**NFR-25: Responsive Design**

- Website responsive trên desktop (≥ 1024px)
- Responsive trên tablet (768px - 1023px)
- Responsive trên mobile (< 768px)
- Touch-friendly trên mobile

**NFR-26: Accessibility**

- Semantic HTML
- Alt text cho images
- Keyboard navigation support (optional)
- ARIA labels (optional)

### 2.3.7 Compatibility (Tính tương thích)

**NFR-27: Browser Compatibility**

- Support Chrome (latest 2 versions)
- Support Firefox (latest 2 versions)
- Support Safari (latest 2 versions)
- Support Edge (latest 2 versions)

**NFR-28: Device Compatibility**

- Desktop (Windows, macOS, Linux)
- Mobile (iOS, Android) - via web browser

### 2.3.8 Reliability (Độ tin cậy)

**NFR-29: Data Integrity**

- Sử dụng database transactions (ACID) cho critical operations
- Foreign key constraints
- Unique constraints
- Check constraints
- Không có double booking

**NFR-30: Backup & Recovery**

- Database backup hàng ngày (automated)
- Backup retention: 30 ngày
- Có thể restore từ backup trong vòng 1 giờ

**NFR-31: Fault Tolerance**

- Graceful degradation: Nếu một service (e.g., email) down, vẫn cho phép đặt vé
- Retry logic cho external API calls (payment gateway)
- Circuit breaker pattern (optional)

---

## 2.4 Ràng buộc hệ thống (System Constraints)

### 2.4.1 Ràng buộc công nghệ

**Công nghệ bắt buộc:**

- **Backend:** Spring Boot 3.3.x (Java 21)
  - Lý do: Yêu cầu của môn học, phù hợp với enterprise application

- **Database:** PostgreSQL 16
  - Lý do: Open-source, ACID compliance, tốt cho relational data

- **Cache:** Redis 7
  - Lý do: In-memory, distributed locking, TTL support

- **Frontend:** React.js 18 (optional, có thể chỉ làm backend + API)
  - Lý do: Popular, component-based, large ecosystem

- **Build Tool:** Maven
  - Lý do: Standard cho Java projects

**Công nghệ không được phép:**

- Không dùng NoSQL làm primary database (MongoDB chỉ dùng cho logs - optional)
- Không dùng real payment gateway (chỉ sandbox/test mode)

### 2.4.2 Ràng buộc về thời gian

- Thời gian thực hiện đồ án: 10 tuần (1 học kỳ)
- Phân bổ:
  - Week 1-2: Requirements & Design
  - Week 3-6: Core Implementation
  - Week 7-8: Advanced Features
  - Week 9-10: Testing & Documentation

### 2.4.3 Ràng buộc về nhân lực

- Số lượng thành viên: 1-3 người (tùy theo quy định của trường)
- Skill level: Sinh viên năm 3-4, có kiến thức về Java, Spring Boot, database

### 2.4.4 Ràng buộc nghiệp vụ

**Quy tắc đặt vé:**

- Mỗi ghế chỉ được đặt bởi 1 booking tại 1 thời điểm
- Ghế được lock trong 5 phút, sau đó tự động release nếu không thanh toán
- Tối đa 10 ghế/booking

**Quy tắc giá vé:**

- Giá vé tối thiểu: 40,000 VND
- VIP seat: +20,000 VND
- Cuối tuần (Sat, Sun): +30,000 VND
- Các discount không được cộng dồn quá mức (tối đa giảm 50%)

**Quy tắc hủy vé:**

- Hủy trước 2 giờ chiếu: Hoàn tiền - phí hủy 10,000 VND
- Hủy trong vòng 2 giờ hoặc sau giờ chiếu: Không hoàn tiền

**Quy tắc điểm thưởng:**

- 1,000 VND = 1 điểm
- 100 điểm = 50,000 VND discount
- Điểm không có expiry (trong scope đồ án, thực tế có thể hết hạn)

### 2.4.5 Ràng buộc về quy mô

**Data volume:**

- 5 rạp (đại diện 5 thành phố)
- 30 phòng chiếu (6 phòng/rạp)
- 15 phim đang chiếu
- 150-200 suất chiếu/ngày
- 10,000 users
- 200,000 bookings/năm

**Concurrent users:**

- Thiết kế cho 200-300 concurrent users
- Có thể test với 500-1000 users để verify scalability

### 2.4.6 Ràng buộc về bảo mật

- Không lưu trữ credit card information
- Tuân thủ best practices về password security
- HTTPS mandatory trong production
- Không expose sensitive data trong logs

### 2.4.7 Ràng buộc về deployment

**Development environment:**

- Docker Compose cho local development
- Tất cả services (PostgreSQL, Redis, Backend) run trong containers

**Production environment (optional):**

- Deploy trên VPS (DigitalOcean, AWS, Azure) hoặc
- Deploy trên PaaS (Heroku, Railway)
- Managed database (RDS, MongoDB Atlas)

---

## 2.5 Kết luận chương

Chương 2 đã xác định chi tiết các yêu cầu của hệ thống đặt vé rạp chiếu phim, bao gồm:

**Yêu cầu nghiệp vụ (19 yêu cầu):** Mô tả các mục tiêu mà hệ thống cần đạt được từ góc độ khách hàng, quản trị viên, và hệ thống tự động.

**Yêu cầu chức năng (39 yêu cầu):** Mô tả chi tiết tất cả các chức năng mà hệ thống phải cung cấp, từ quản lý người dùng, quản lý phim, quản lý rạp, quản lý suất chiếu, đặt vé, thanh toán, membership, đến báo cáo doanh thu. Mỗi yêu cầu được mô tả với input, process, output, và business rules rõ ràng.

**Yêu cầu phi chức năng (31 yêu cầu):** Xác định các thuộc tính chất lượng của hệ thống về hiệu năng (response time < 2s, throughput 100-300 req/s), tính sẵn sàng (99% uptime), bảo mật (JWT, BCrypt, HTTPS, rate limiting), khả năng mở rộng, khả năng bảo trì (60% test coverage, API docs), usability, compatibility, và reliability.

**Ràng buộc hệ thống:** Xác định các giới hạn về công nghệ (Spring Boot, PostgreSQL, Redis), thời gian (10 tuần), quy mô (5 rạp, 10K users), và nghiệp vụ (quy tắc đặt vé, giá vé, hủy vé, điểm thưởng).

Các yêu cầu này sẽ là cơ sở để thực hiện phân tích nhu cầu chi tiết (Chương 3) và thiết kế hệ thống (Chương 4). Việc xác định rõ ràng và đầy đủ yêu cầu từ đầu giúp đảm bảo hệ thống được xây dựng đúng mục tiêu và đáp ứng nhu cầu thực tế.
