# Admin API Audit Report

## Backend Admin APIs - Tổng cộng: 25 endpoints

### 1. AdminMovieController (`/admin/movies`) - 5 endpoints

| Method | Endpoint | Function | Frontend Service |
|--------|----------|----------|-----------------|
| POST | `/admin/movies` | createMovie | ✅ movieService.createMovie |
| PUT | `/admin/movies/{id}` | updateMovie | ✅ movieService.updateMovie |
| DELETE | `/admin/movies/{id}` | deleteMovie | ✅ movieService.deleteMovie |
| POST | `/admin/movies/genres` | createGenre | ❌ **THIẾU** |
| DELETE | `/admin/movies/genres/{id}` | deleteGenre | ❌ **THIẾU** |

### 2. AdminCinemaController (`/admin/cinemas`) - 7 endpoints

| Method | Endpoint | Function | Frontend Service |
|--------|----------|----------|-----------------|
| POST | `/admin/cinemas` | createCinema | ✅ cinemaService.createCinema |
| PUT | `/admin/cinemas/{id}` | updateCinema | ✅ cinemaService.updateCinema |
| DELETE | `/admin/cinemas/{id}` | deleteCinema | ✅ cinemaService.deleteCinema |
| POST | `/admin/cinemas/{cinemaId}/halls` | createHall | ✅ cinemaService.createHall |
| PUT | `/admin/cinemas/halls/{hallId}` | updateHall | ✅ cinemaService.updateHall |
| DELETE | `/admin/cinemas/halls/{hallId}` | deleteHall | ✅ cinemaService.deleteHall |
| POST | `/admin/cinemas/halls/{hallId}/regenerate-seats` | regenerateSeats | ✅ cinemaService.regenerateSeats |

### 3. AdminShowController (`/admin/shows`) - 6 endpoints

| Method | Endpoint | Function | Frontend Service |
|--------|----------|----------|-----------------|
| GET | `/admin/shows?status=&page=&size=` | getAllShows | ⚠️ showService.getAllShows (không có admin prefix) |
| GET | `/admin/shows/by-hall/{hallId}?date=` | getShowsByHallAndDate | ❌ **THIẾU** |
| POST | `/admin/shows` | createShow | ✅ showService.createShow |
| PUT | `/admin/shows/{id}` | updateShow | ✅ showService.updateShow |
| POST | `/admin/shows/{id}/cancel` | cancelShow | ✅ showService.cancelShow |
| DELETE | `/admin/shows/{id}` | deleteShow | ✅ showService.deleteShow |

### 4. AdminUserController (`/admin/users`) - 7 endpoints

| Method | Endpoint | Function | Frontend Service |
|--------|----------|----------|-----------------|
| GET | `/admin/users?page=&size=` | getAllUsers | ✅ userService.getAllUsers |
| GET | `/admin/users/{id}` | getUserById | ✅ userService.getUserById |
| PUT | `/admin/users/{id}` | updateUser | ✅ userService.adminUpdateUser |
| POST | `/admin/users/{id}/add-points` | addPoints | ✅ userService.addPoints |
| POST | `/admin/users/{id}/deduct-points` | deductPoints | ✅ userService.deductPoints |
| PUT | `/admin/users/{id}/status` | updateUserStatus | ❌ **THIẾU** |
| PUT | `/admin/users/{id}/role` | updateUserRole | ❌ **THIẾU** |

## Tổng kết

### ✅ Frontend ĐÃ CÓ: 20/25 endpoints (80%)

### ❌ Frontend THIẾU: 5 endpoints (20%)

1. **Genre Management (2 endpoints):**
   - POST `/admin/movies/genres` - createGenre
   - DELETE `/admin/movies/genres/{id}` - deleteGenre

2. **Show Management (1 endpoint):**
   - GET `/admin/shows/by-hall/{hallId}?date=` - getShowsByHallAndDate

3. **User Management (2 endpoints):**
   - PUT `/admin/users/{id}/status` - updateUserStatus
   - PUT `/admin/users/{id}/role` - updateUserRole

### ⚠️ CẦN KIỂM TRA:

- `showService.getAllShows()` đang call `/shows` thay vì `/admin/shows`
  → Cần verify xem có cần sửa không

## Kế hoạch thực hiện

### Bước 1: Thêm 5 endpoints còn thiếu vào frontend services
- ✅ Thêm genre management vào movieService hoặc genreService
- ✅ Thêm getShowsByHallAndDate vào showService
- ✅ Thêm updateUserStatus và updateUserRole vào userService

### Bước 2: Tạo admin pages còn thiếu
- ✅ Cinemas Management Page (với Halls)
- ✅ Shows Management Page
- ✅ Users Management Page
- ✅ Genres Management (có thể tích hợp vào Movies Management)

### Bước 3: Update AdminRoute để support STAFF role
- ✅ Cho phép STAFF truy cập admin panel (hoặc tạo staff panel riêng)

### Bước 4: Testing
- ✅ Test tất cả CRUD operations
- ✅ Test phân quyền ADMIN vs STAFF vs CUSTOMER
- ✅ Test error handling

## Notes

- Backend có 3 roles: CUSTOMER, STAFF, ADMIN
- Hiện tại chỉ ADMIN được sử dụng trong @PreAuthorize
- STAFF role chưa có logic phân quyền riêng
- Frontend cần quyết định: STAFF = ADMIN hay STAFF có panel riêng?
