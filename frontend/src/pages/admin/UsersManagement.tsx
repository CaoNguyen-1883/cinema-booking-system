import { useState } from 'react';
import { userService } from '@/api/user.service';
import type { UserResponse } from '@/types';

export function UsersManagement() {
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);

  // Points modal
  const [showPointsModal, setShowPointsModal] = useState(false);
  const [selectedUser, setSelectedUser] = useState<UserResponse | null>(null);
  const [pointsAmount, setPointsAmount] = useState(0);
  const [pointsAction, setPointsAction] = useState<'add' | 'deduct'>('add');

  // Load users
  const loadUsers = async () => {
    setLoading(true);
    try {
      const response = await userService.getAllUsers({ page, size: 20 });
      setUsers(response.content);
      setTotal(response.totalElements);
    } catch (error: any) {
      alert('Failed to load users: ' + (error?.response?.data?.message || error.message));
    } finally {
      setLoading(false);
    }
  };

  // Update user role
  const handleUpdateRole = async (userId: number, newRole: string) => {
    if (!confirm(`Are you sure you want to change user role to ${newRole}?`)) return;
    try {
      await userService.updateUserRole(userId, newRole);
      alert('User role updated successfully!');
      loadUsers();
    } catch (error: any) {
      alert('Failed to update role: ' + (error?.response?.data?.message || error.message));
    }
  };

  // Update user status
  const handleUpdateStatus = async (userId: number, newStatus: string) => {
    if (!confirm(`Are you sure you want to change user status to ${newStatus}?`)) return;
    try {
      await userService.updateUserStatus(userId, newStatus);
      alert('User status updated successfully!');
      loadUsers();
    } catch (error: any) {
      alert('Failed to update status: ' + (error?.response?.data?.message || error.message));
    }
  };

  // Lock user
  const handleLockUser = async (userId: number) => {
    if (!confirm('Are you sure you want to lock this user?')) return;
    try {
      await userService.lockUser(userId);
      alert('User locked successfully!');
      loadUsers();
    } catch (error: any) {
      alert('Failed to lock user: ' + (error?.response?.data?.message || error.message));
    }
  };

  // Unlock user
  const handleUnlockUser = async (userId: number) => {
    if (!confirm('Are you sure you want to unlock this user?')) return;
    try {
      await userService.unlockUser(userId);
      alert('User unlocked successfully!');
      loadUsers();
    } catch (error: any) {
      alert('Failed to unlock user: ' + (error?.response?.data?.message || error.message));
    }
  };

  // Handle points operation
  const handlePointsOperation = async () => {
    if (!selectedUser || pointsAmount <= 0) {
      alert('Please enter a valid amount');
      return;
    }
    try {
      if (pointsAction === 'add') {
        await userService.addPoints(selectedUser.id, pointsAmount);
        alert(`Added ${pointsAmount} points to user!`);
      } else {
        await userService.deductPoints(selectedUser.id, pointsAmount);
        alert(`Deducted ${pointsAmount} points from user!`);
      }
      setShowPointsModal(false);
      setPointsAmount(0);
      loadUsers();
    } catch (error: any) {
      alert('Failed to update points: ' + (error?.response?.data?.message || error.message));
    }
  };

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Users Management</h1>
        <button
          onClick={loadUsers}
          className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
        >
          Load Users
        </button>
      </div>

      {loading && <div>Loading...</div>}

      {/* Users Table */}
      <div className="overflow-x-auto">
        <table className="w-full border-collapse border">
          <thead>
            <tr className="bg-gray-100">
              <th className="border p-2">ID</th>
              <th className="border p-2">Username</th>
              <th className="border p-2">Email</th>
              <th className="border p-2">Full Name</th>
              <th className="border p-2">Phone</th>
              <th className="border p-2">Points</th>
              <th className="border p-2">Role</th>
              <th className="border p-2">Status</th>
              <th className="border p-2">Last Login</th>
              <th className="border p-2">Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id}>
                <td className="border p-2">{user.id}</td>
                <td className="border p-2">{user.username}</td>
                <td className="border p-2">{user.email}</td>
                <td className="border p-2">{user.fullName}</td>
                <td className="border p-2">{user.phoneNumber || '-'}</td>
                <td className="border p-2">
                  <div className="flex items-center gap-2">
                    <span>{user.points}</span>
                    <button
                      onClick={() => {
                        setSelectedUser(user);
                        setPointsAction('add');
                        setShowPointsModal(true);
                      }}
                      className="text-xs px-2 py-1 bg-green-500 text-white rounded"
                      title="Add points"
                    >
                      +
                    </button>
                    <button
                      onClick={() => {
                        setSelectedUser(user);
                        setPointsAction('deduct');
                        setShowPointsModal(true);
                      }}
                      className="text-xs px-2 py-1 bg-red-500 text-white rounded"
                      title="Deduct points"
                    >
                      -
                    </button>
                  </div>
                </td>
                <td className="border p-2">
                  <select
                    value={user.role}
                    onChange={(e) => handleUpdateRole(user.id, e.target.value)}
                    className="border rounded px-2 py-1 text-sm"
                  >
                    <option value="CUSTOMER">CUSTOMER</option>
                    <option value="STAFF">STAFF</option>
                    <option value="ADMIN">ADMIN</option>
                  </select>
                </td>
                <td className="border p-2">
                  <select
                    value={user.status}
                    onChange={(e) => handleUpdateStatus(user.id, e.target.value)}
                    className="border rounded px-2 py-1 text-sm"
                  >
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="INACTIVE">INACTIVE</option>
                    <option value="BANNED">BANNED</option>
                  </select>
                </td>
                <td className="border p-2 text-sm">
                  {user.lastLoginAt ? new Date(user.lastLoginAt).toLocaleString() : '-'}
                </td>
                <td className="border p-2">
                  <div className="flex gap-2">
                    {user.status !== 'BANNED' ? (
                      <button
                        onClick={() => handleLockUser(user.id)}
                        className="px-2 py-1 bg-orange-500 text-white rounded text-sm"
                      >
                        Lock
                      </button>
                    ) : (
                      <button
                        onClick={() => handleUnlockUser(user.id)}
                        className="px-2 py-1 bg-green-500 text-white rounded text-sm"
                      >
                        Unlock
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      <div className="mt-4 flex justify-between items-center">
        <div>Total: {total} users</div>
        <div className="space-x-2">
          <button
            onClick={() => setPage(Math.max(0, page - 1))}
            disabled={page === 0}
            className="px-3 py-1 border rounded disabled:opacity-50"
          >
            Previous
          </button>
          <span>Page {page + 1}</span>
          <button
            onClick={() => setPage(page + 1)}
            disabled={(page + 1) * 20 >= total}
            className="px-3 py-1 border rounded disabled:opacity-50"
          >
            Next
          </button>
        </div>
      </div>

      {/* Points Modal */}
      {showPointsModal && selectedUser && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="bg-white p-6 rounded-lg w-[400px]">
            <h2 className="text-xl font-bold mb-4">
              {pointsAction === 'add' ? 'Add Points' : 'Deduct Points'}
            </h2>
            <div className="mb-4">
              <p className="text-sm text-gray-600 mb-2">
                User: <strong>{selectedUser.username}</strong>
              </p>
              <p className="text-sm text-gray-600 mb-4">
                Current points: <strong>{selectedUser.points}</strong>
              </p>
              <label className="block text-sm font-medium mb-1">Amount</label>
              <input
                type="number"
                value={pointsAmount}
                onChange={(e) => setPointsAmount(parseInt(e.target.value) || 0)}
                className="w-full border rounded px-3 py-2"
                min="0"
                step="100"
              />
            </div>
            <div className="flex justify-end space-x-2">
              <button
                onClick={() => {
                  setShowPointsModal(false);
                  setPointsAmount(0);
                }}
                className="px-4 py-2 border rounded"
              >
                Cancel
              </button>
              <button
                onClick={handlePointsOperation}
                className={`px-4 py-2 text-white rounded ${
                  pointsAction === 'add' ? 'bg-green-500' : 'bg-red-500'
                }`}
              >
                {pointsAction === 'add' ? 'Add' : 'Deduct'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
