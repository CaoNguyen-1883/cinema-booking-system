import { createBrowserRouter, Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '@/stores';

// Protected Route wrapper
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

// Placeholder components - replace with actual pages
const HomePage = () => <div className="p-8">Home Page</div>;
const LoginPage = () => <div className="p-8">Login Page</div>;
const RegisterPage = () => <div className="p-8">Register Page</div>;
const NotFoundPage = () => <div className="p-8">404 - Not Found</div>;

export const router = createBrowserRouter([
  {
    path: '/',
    element: <HomePage />,
  },
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
  {
    element: <ProtectedRoute />,
    children: [
      // Add protected routes here
    ],
  },
  {
    path: '*',
    element: <NotFoundPage />,
  },
]);
