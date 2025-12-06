import { useAuthStore } from '@/stores/auth.store';

export function AdminDashboard() {
  const { user } = useAuthStore();

  return (
    <div className="p-8">
      <h1 className="text-3xl font-bold mb-6">Admin Dashboard</h1>
      <p className="text-gray-600 mb-4">
        Welcome back, {user?.fullName || user?.username}!
      </p>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="font-semibold text-gray-600 mb-2">Movies</h3>
          <p className="text-3xl font-bold">--</p>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="font-semibold text-gray-600 mb-2">Cinemas</h3>
          <p className="text-3xl font-bold">--</p>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="font-semibold text-gray-600 mb-2">Shows</h3>
          <p className="text-3xl font-bold">--</p>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="font-semibold text-gray-600 mb-2">Users</h3>
          <p className="text-3xl font-bold">--</p>
        </div>
      </div>
    </div>
  );
}
