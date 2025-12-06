import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '@/stores/auth.store';

/**
 * AdminRoute - Route guard for admin-only pages
 *
 * Checks:
 * 1. User is authenticated
 * 2. User has ADMIN role
 *
 * If not authenticated: redirect to /login
 * If authenticated but not ADMIN: redirect to / (home)
 */
export function AdminRoute() {
  const { isAuthenticated, user } = useAuthStore();

  if (!isAuthenticated || !user) {
    return <Navigate to="/login" replace />;
  }

  if (user.role !== 'ADMIN') {
    // User is authenticated but not admin
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
}
