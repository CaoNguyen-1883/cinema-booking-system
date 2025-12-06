import { Outlet, Link } from 'react-router-dom';
import { useAuthStore } from '@/stores/auth.store';
import { useLogout } from '@/hooks';

export function PublicLayout() {
  const { isAuthenticated, user } = useAuthStore();
  const { mutate: logout } = useLogout();

  return (
    <div className="min-h-screen flex flex-col">
      {/* Header */}
      <header className="bg-gray-900 text-white shadow-lg">
        <div className="container mx-auto px-4 py-4 flex justify-between items-center">
          <Link to="/" className="text-2xl font-bold">
            Cinema Booking
          </Link>
          <nav className="flex items-center gap-6">
            <Link to="/" className="hover:text-gray-300">
              Home
            </Link>
            <Link to="/movies" className="hover:text-gray-300">
              Movies
            </Link>
            {isAuthenticated ? (
              <>
                <Link to="/profile" className="hover:text-gray-300">
                  Profile
                </Link>
                {user?.role === 'ADMIN' && (
                  <Link to="/admin" className="hover:text-gray-300">
                    Admin
                  </Link>
                )}
                <button
                  onClick={() => logout()}
                  className="bg-red-600 px-4 py-2 rounded hover:bg-red-700"
                >
                  Logout
                </button>
              </>
            ) : (
              <>
                <Link
                  to="/login"
                  className="bg-blue-600 px-4 py-2 rounded hover:bg-blue-700"
                >
                  Login
                </Link>
                <Link
                  to="/register"
                  className="bg-green-600 px-4 py-2 rounded hover:bg-green-700"
                >
                  Register
                </Link>
              </>
            )}
          </nav>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1">
        <Outlet />
      </main>

      {/* Footer */}
      <footer className="bg-gray-800 text-white py-6 mt-auto">
        <div className="container mx-auto px-4 text-center">
          <p>&copy; 2024 Cinema Booking System. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
}
