import { Outlet, Link, useLocation } from 'react-router-dom';
import { useAuthStore } from '@/stores/auth.store';
import { useLogout } from '@/hooks';

export function AdminLayout() {
  const { user } = useAuthStore();
  const { mutate: logout } = useLogout();
  const location = useLocation();

  const isActive = (path: string) => location.pathname === path;

  const menuItems = [
    { path: '/admin', label: 'Dashboard', icon: '📊' },
    { path: '/admin/movies', label: 'Movies', icon: '🎬' },
    { path: '/admin/cinemas', label: 'Cinemas', icon: '🏢' },
    { path: '/admin/shows', label: 'Shows', icon: '🎭' },
    { path: '/admin/users', label: 'Users', icon: '👥' },
    { path: '/admin/bookings', label: 'Bookings', icon: '🎫' },
  ];

  return (
    <div className="min-h-screen flex bg-gray-100">
      {/* Sidebar */}
      <aside className="w-64 bg-gray-900 text-white flex-shrink-0">
        <div className="p-6">
          <h1 className="text-2xl font-bold mb-8">Admin Panel</h1>
          <nav className="space-y-2">
            {menuItems.map((item) => (
              <Link
                key={item.path}
                to={item.path}
                className={`flex items-center gap-3 px-4 py-3 rounded-lg transition ${
                  isActive(item.path)
                    ? 'bg-blue-600'
                    : 'hover:bg-gray-800'
                }`}
              >
                <span className="text-xl">{item.icon}</span>
                <span>{item.label}</span>
              </Link>
            ))}
          </nav>
        </div>
        <div className="absolute bottom-0 w-64 p-6 border-t border-gray-700">
          <Link
            to="/"
            className="block text-center py-2 bg-gray-800 rounded hover:bg-gray-700 mb-2"
          >
            ← Back to Site
          </Link>
        </div>
      </aside>

      {/* Main Content */}
      <div className="flex-1 flex flex-col">
        {/* Top Header */}
        <header className="bg-white shadow-sm px-8 py-4 flex justify-between items-center">
          <h2 className="text-xl font-semibold text-gray-800">
            {menuItems.find((item) => isActive(item.path))?.label || 'Admin'}
          </h2>
          <div className="flex items-center gap-4">
            <span className="text-gray-600">
              {user?.fullName || user?.username}
            </span>
            <button
              onClick={() => logout()}
              className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700"
            >
              Logout
            </button>
          </div>
        </header>

        {/* Page Content */}
        <main className="flex-1 overflow-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
