# Token Storage Strategy

## Tổng quan

Hệ thống sử dụng chiến lược lưu trữ token an toàn với 2 loại token:

### 1. Refresh Token
- **Lưu trữ**: httpOnly cookie (do backend set)
- **Mục đích**: Tạo access token mới
- **Thời gian sống**: Dài (7-30 ngày)
- **Bảo mật**: Không thể truy cập từ JavaScript → An toàn với XSS attacks

### 2. Access Token
- **Lưu trữ**: Memory only (Zustand state)
- **Mục đích**: Xác thực các API requests
- **Thời gian sống**: Ngắn (15-60 phút)
- **Bảo mật**: Chỉ tồn tại trong RAM, không persist vào localStorage → An toàn với XSS attacks

## Tại sao không dùng localStorage cho accessToken?

### ⚠️ Vấn đề với localStorage:
```javascript
// CỰC KỲ NGUY HIỂM nếu có XSS attack
const token = localStorage.getItem('accessToken');
// Attacker có thể inject script và đánh cắp token
```

### ✅ Giải pháp: Memory-only storage
```javascript
// Token chỉ tồn tại trong Zustand state (memory)
const { accessToken } = useAuthStore();
// Attacker KHÔNG THỂ đọc được từ memory
```

## Luồng hoạt động

### 1. Login/Register
```
User → Login → Backend
                   ↓
            Set httpOnly cookie (refreshToken)
            Return accessToken + user info
                   ↓
            Frontend lưu vào Zustand state (memory)
```

### 2. API Request
```
Frontend → Get token from Zustand
        → Add to Authorization header
        → Send request to Backend
```

### 3. Token Expired (401)
```
Backend → Return 401
Frontend → Auto call /auth/refresh (với httpOnly cookie)
Backend → Return new accessToken
Frontend → Update Zustand state
        → Retry original request
```

### 4. Page Refresh
```
Browser refresh → App load
                → useInitAuth() runs
                → Call /auth/refresh
                → Get new accessToken
                → Continue as logged in
```

### 5. Logout
```
Frontend → Call /auth/logout
Backend → Clear httpOnly cookie
Frontend → Clear Zustand state
        → Redirect to home
```

## Cách sử dụng

### 1. Setup trong App.tsx (hoặc root component)
```typescript
import { useInitAuth } from '@/hooks';

function App() {
  const { isInitializing } = useInitAuth();

  if (isInitializing) {
    return <LoadingSpinner />;
  }

  return <RouterProvider router={router} />;
}
```

### 2. Login trong component
```typescript
import { useLogin } from '@/hooks';

function LoginPage() {
  const { mutate: login, isPending } = useLogin();

  const handleSubmit = (data: LoginRequest) => {
    login(data, {
      onSuccess: () => {
        // accessToken đã được lưu vào Zustand (memory)
        // refreshToken đã được lưu vào httpOnly cookie
        navigate('/dashboard');
      },
      onError: (error) => {
        toast.error('Login failed');
      },
    });
  };

  return <LoginForm onSubmit={handleSubmit} />;
}
```

### 3. Check authentication
```typescript
import { useAuthStore } from '@/stores/auth.store';

function ProtectedRoute() {
  const { isAuthenticated, user } = useAuthStore();

  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }

  return <Dashboard user={user} />;
}
```

### 4. API calls tự động có token
```typescript
import { useMovies } from '@/hooks';

function MoviesPage() {
  // Token tự động được thêm vào header bởi axios interceptor
  const { data, isLoading } = useMovies();

  return <MovieList movies={data} />;
}
```

## Yêu cầu Backend

Backend cần implement:

### 1. Login/Register Response
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGc...",
    "refreshToken": "eyJhbGc...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "username": "john",
      "email": "john@example.com",
      "fullName": "John Doe",
      "role": "CUSTOMER",
      "points": 100
    }
  }
}
```

**QUAN TRỌNG**: Backend phải set httpOnly cookie:
```java
ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
    .httpOnly(true)
    .secure(true) // HTTPS only in production
    .sameSite("Strict")
    .maxAge(7 * 24 * 60 * 60) // 7 days
    .path("/api/auth")
    .build();

response.addHeader("Set-Cookie", cookie.toString());
```

### 2. Refresh Token Endpoint
```
POST /api/auth/refresh
Cookie: refreshToken=eyJhbGc...

Response:
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGc...",
    "user": { ... }
  }
}
```

### 3. Logout Endpoint
```
POST /api/auth/logout
Cookie: refreshToken=eyJhbGc...

Backend clears the httpOnly cookie:
ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
    .maxAge(0)
    .build();
```

### 4. CORS Configuration
```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:5173")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true); // QUAN TRỌNG!
            }
        };
    }
}
```

## Bảo mật

### ✅ Đã được bảo vệ:
1. **XSS (Cross-Site Scripting)**:
   - refreshToken: httpOnly cookie → JavaScript không đọc được
   - accessToken: Memory only → JavaScript của attacker không đọc được

2. **Token Expiration**:
   - accessToken: Thời gian ngắn → Giảm thiểu rủi ro nếu bị lộ
   - refreshToken: Chỉ dùng để refresh → Có thể revoke dễ dàng

3. **Automatic Refresh**:
   - Token tự động refresh khi hết hạn
   - User không bị logout đột ngột

### ⚠️ Cần chú ý:
1. **CSRF (Cross-Site Request Forgery)**:
   - Sử dụng `SameSite=Strict` cho cookie
   - Có thể thêm CSRF token nếu cần

2. **HTTPS Only**:
   - Production PHẢI dùng HTTPS
   - Set `secure: true` cho cookie

3. **Token Revocation**:
   - Backend nên có blacklist cho refreshToken
   - Implement logout trên tất cả devices

## Testing

### 1. Test Login Flow
```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}' \
  -c cookies.txt

# Check cookie được set
cat cookies.txt
```

### 2. Test Refresh Flow
```bash
# Refresh token
curl -X POST http://localhost:8080/api/auth/refresh \
  -b cookies.txt

# Should return new accessToken
```

### 3. Test API with Token
```bash
# Get movies (should work with cookie)
curl -X GET http://localhost:8080/api/movies/now-showing \
  -H "Authorization: Bearer <accessToken>" \
  -b cookies.txt
```

## Migration từ localStorage

Nếu đang dùng localStorage, cần:

1. **Xóa token cũ**:
```typescript
localStorage.removeItem('accessToken');
localStorage.removeItem('refreshToken');
```

2. **Yêu cầu user login lại**:
   - Hiển thị thông báo: "Vui lòng đăng nhập lại để tăng cường bảo mật"

3. **Backend update**:
   - Implement httpOnly cookie cho refreshToken
   - Update CORS config để allow credentials

## FAQ

### Q: accessToken có bị mất khi refresh trang không?
**A**: Có, nhưng sẽ tự động được refresh từ httpOnly cookie. Hook `useInitAuth` sẽ xử lý việc này.

### Q: Có thể dùng localStorage cho accessToken được không?
**A**: Không nên. localStorage dễ bị XSS attack. Nếu attacker inject script, họ có thể đánh cắp token.

### Q: Tại sao không lưu cả 2 token vào httpOnly cookie?
**A**: Có thể làm vậy, nhưng:
- Phức tạp hơn cho mobile app
- Cần xử lý CSRF
- Không linh hoạt cho multi-domain

### Q: Nếu backend chưa có httpOnly cookie thì sao?
**A**: Tạm thời có thể dùng localStorage, nhưng CẦN ưu tiên implement httpOnly cookie sớm nhất có thể.

## Tài liệu tham khảo

- [OWASP Token Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/JSON_Web_Token_for_Java_Cheat_Sheet.html)
- [HttpOnly Cookie Security](https://developer.mozilla.org/en-US/docs/Web/HTTP/Cookies#security)
- [SameSite Cookie Attribute](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Set-Cookie/SameSite)
