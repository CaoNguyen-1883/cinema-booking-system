import { createBrowserRouter, Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '@/stores';
import { AdminRoute } from '@/components/route-guards';
import { PublicLayout, AdminLayout } from '@/layouts';
import {
  HomePage,
  MoviesPage,
  MovieDetailPage,
  LoginPage,
  RegisterPage,
  ProfilePage,
  AdminDashboard,
  MoviesManagementPage,
  CinemasManagement,
  ShowsManagement,
  UsersManagement,
} from '@/pages';

// Protected Route wrapper (requires authentication)
function ProtectedRoute() {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}

// Public Route wrapper (redirect to home if authenticated)
function PublicRoute() {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);

  if (isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
}

// 404 Page
const NotFoundPage = () => (
  <div className="min-h-screen flex items-center justify-center">
    <div className="text-center">
      <h1 className="text-6xl font-bold text-gray-800 mb-4">404</h1>
      <p className="text-xl text-gray-600 mb-6">Page Not Found</p>
      <a href="/" className="text-blue-600 hover:underline">
        Go back to home
      </a>
    </div>
  </div>
);

export const router = createBrowserRouter([
  // Public routes with PublicLayout
  {
    element: <PublicLayout />,
    children: [
      {
        path: '/',
        element: <HomePage />,
      },
      {
        path: '/movies',
        element: <MoviesPage />,
      },
      {
        path: '/movies/:id',
        element: <MovieDetailPage />,
      },
      // Auth routes (redirect if already logged in)
      {
        element: <PublicRoute />,
        children: [
          {
            path: '/login',
            element: <LoginPage />,
          },
          {
            path: '/register',
            element: <RegisterPage />,
          },
        ],
      },
      // Protected customer routes
      {
        element: <ProtectedRoute />,
        children: [
          {
            path: '/profile',
            element: <ProfilePage />,
          },
          // Add more customer routes here: bookings, etc.
        ],
      },
    ],
  },

  // Admin routes with AdminLayout
  {
    element: <AdminRoute />,
    children: [
      {
        element: <AdminLayout />,
        children: [
          {
            path: '/admin',
            element: <AdminDashboard />,
          },
          {
            path: '/admin/movies',
            element: <MoviesManagementPage />,
          },
          {
            path: '/admin/cinemas',
            element: <CinemasManagement />,
          },
          {
            path: '/admin/shows',
            element: <ShowsManagement />,
          },
          {
            path: '/admin/users',
            element: <UsersManagement />,
          },
          // Add more admin routes here: bookings, etc.
        ],
      },
    ],
  },

  // 404 catch-all
  {
    path: '*',
    element: <NotFoundPage />,
  },
]);
