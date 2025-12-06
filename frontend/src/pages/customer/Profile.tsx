import { useProfile } from '@/hooks';

export function ProfilePage() {
  const { data: profile, isLoading } = useProfile();

  if (isLoading) {
    return <div className="container mx-auto px-4 py-8">Loading...</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6">My Profile</h1>
      <div className="bg-white rounded-lg shadow p-6 max-w-2xl">
        <div className="space-y-4">
          <div>
            <label className="font-semibold">Username:</label>
            <p className="text-gray-700">{profile?.username}</p>
          </div>
          <div>
            <label className="font-semibold">Email:</label>
            <p className="text-gray-700">{profile?.email}</p>
          </div>
          <div>
            <label className="font-semibold">Full Name:</label>
            <p className="text-gray-700">{profile?.fullName}</p>
          </div>
          <div>
            <label className="font-semibold">Phone:</label>
            <p className="text-gray-700">{profile?.phoneNumber || 'N/A'}</p>
          </div>
          <div>
            <label className="font-semibold">Points:</label>
            <p className="text-gray-700">{profile?.points} pts</p>
          </div>
        </div>
      </div>
    </div>
  );
}
