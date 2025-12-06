import { useState } from 'react';
import { cinemaService } from '@/api/cinema.service';
import type {
  CinemaResponse,
  CreateCinemaRequest,
  UpdateCinemaRequest,
  HallResponse,
  CreateHallRequest,
  UpdateHallRequest
} from '@/types';

export function CinemasManagement() {
  const [cinemas, setCinemas] = useState<CinemaResponse[]>([]);
  const [selectedCinema, setSelectedCinema] = useState<CinemaResponse | null>(null);
  const [halls, setHalls] = useState<HallResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);

  // Cinema form state
  const [showCinemaForm, setShowCinemaForm] = useState(false);
  const [editingCinema, setEditingCinema] = useState<CinemaResponse | null>(null);
  const [cinemaForm, setCinemaForm] = useState<CreateCinemaRequest>({
    name: '',
    address: '',
    city: '',
    phoneNumber: '',
  });

  // Hall form state
  const [showHallForm, setShowHallForm] = useState(false);
  const [editingHall, setEditingHall] = useState<HallResponse | null>(null);
  const [hallForm, setHallForm] = useState<CreateHallRequest>({
    name: '',
    cinemaId: 0,
    totalRows: 10,
    seatsPerRow: 12,
  });

  // Load cinemas
  const loadCinemas = async () => {
    setLoading(true);
    try {
      const response = await cinemaService.getAllCinemas({ page, size: 10 });
      setCinemas(response.content);
      setTotal(response.totalElements);
    } catch (error: any) {
      alert('Failed to load cinemas: ' + (error?.response?.data?.message || error.message));
    } finally {
      setLoading(false);
    }
  };

  // Load halls for a cinema
  const loadHalls = async (cinemaId: number) => {
    try {
      const cinema = await cinemaService.getCinemaWithHalls(cinemaId);
      setHalls(cinema.halls || []);
      setSelectedCinema(cinema);
    } catch (error: any) {
      alert('Failed to load halls: ' + (error?.response?.data?.message || error.message));
    }
  };

  // Create cinema
  const handleCreateCinema = async () => {
    try {
      await cinemaService.createCinema(cinemaForm);
      alert('Cinema created successfully!');
      setShowCinemaForm(false);
      setCinemaForm({ name: '', address: '', city: '', phoneNumber: '' });
      loadCinemas();
    } catch (error: any) {
      alert('Failed to create cinema: ' + (error?.response?.data?.message || error.message));
    }
  };

  // Update cinema
  const handleUpdateCinema = async () => {
    if (!editingCinema) return;
    try {
      await cinemaService.updateCinema(editingCinema.id, cinemaForm as UpdateCinemaRequest);
      alert('Cinema updated successfully!');
      setShowCinemaForm(false);
      setEditingCinema(null);
      setCinemaForm({ name: '', address: '', city: '', phoneNumber: '' });
      loadCinemas();
    } catch (error: any) {
      alert('Failed to update cinema: ' + (error?.response?.data?.message || error.message));
    }
  };

  // Delete cinema
  const handleDeleteCinema = async (id: number) => {
    if (!confirm('Are you sure you want to delete this cinema?')) return;
    try {
      await cinemaService.deleteCinema(id);
      alert('Cinema deleted successfully!');
      loadCinemas();
    } catch (error: any) {
      alert('Failed to delete cinema: ' + (error?.response?.data?.message || error.message));
    }
  };

  // Create hall
  const handleCreateHall = async () => {
    try {
      await cinemaService.createHall(hallForm);
      alert('Hall created successfully!');
      setShowHallForm(false);
      setHallForm({ name: '', cinemaId: 0, totalRows: 10, seatsPerRow: 12 });
      if (selectedCinema) loadHalls(selectedCinema.id);
    } catch (error: any) {
      alert('Failed to create hall: ' + (error?.response?.data?.message || error.message));
    }
  };

  // Update hall
  const handleUpdateHall = async () => {
    if (!editingHall) return;
    try {
      await cinemaService.updateHall(editingHall.id, hallForm as UpdateHallRequest);
      alert('Hall updated successfully!');
      setShowHallForm(false);
      setEditingHall(null);
      setHallForm({ name: '', cinemaId: 0, totalRows: 10, seatsPerRow: 12 });
      if (selectedCinema) loadHalls(selectedCinema.id);
    } catch (error: any) {
      alert('Failed to update hall: ' + (error?.response?.data?.message || error.message));
    }
  };

  // Delete hall
  const handleDeleteHall = async (id: number) => {
    if (!confirm('Are you sure you want to delete this hall?')) return;
    try {
      await cinemaService.deleteHall(id);
      alert('Hall deleted successfully!');
      if (selectedCinema) loadHalls(selectedCinema.id);
    } catch (error: any) {
      alert('Failed to delete hall: ' + (error?.response?.data?.message || error.message));
    }
  };

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Cinemas Management</h1>
        <div className="space-x-2">
          <button
            onClick={loadCinemas}
            className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Load Cinemas
          </button>
          <button
            onClick={() => {
              setShowCinemaForm(true);
              setEditingCinema(null);
              setCinemaForm({ name: '', address: '', city: '', phoneNumber: '' });
            }}
            className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
          >
            + New Cinema
          </button>
        </div>
      </div>

      {loading && <div>Loading...</div>}

      {/* Cinemas Table */}
      <div className="mb-8">
        <table className="w-full border-collapse border">
          <thead>
            <tr className="bg-gray-100">
              <th className="border p-2">ID</th>
              <th className="border p-2">Name</th>
              <th className="border p-2">City</th>
              <th className="border p-2">Address</th>
              <th className="border p-2">Phone</th>
              <th className="border p-2">Status</th>
              <th className="border p-2">Actions</th>
            </tr>
          </thead>
          <tbody>
            {cinemas.map((cinema) => (
              <tr key={cinema.id}>
                <td className="border p-2">{cinema.id}</td>
                <td className="border p-2">{cinema.name}</td>
                <td className="border p-2">{cinema.city}</td>
                <td className="border p-2">{cinema.address}</td>
                <td className="border p-2">{cinema.phoneNumber}</td>
                <td className="border p-2">{cinema.status}</td>
                <td className="border p-2">
                  <button
                    onClick={() => loadHalls(cinema.id)}
                    className="px-2 py-1 bg-blue-500 text-white rounded mr-2 text-sm"
                  >
                    Halls
                  </button>
                  <button
                    onClick={() => {
                      setEditingCinema(cinema);
                      setCinemaForm({
                        name: cinema.name,
                        address: cinema.address,
                        city: cinema.city,
                        phoneNumber: cinema.phoneNumber,
                        district: cinema.district,
                        email: cinema.email,
                      });
                      setShowCinemaForm(true);
                    }}
                    className="px-2 py-1 bg-yellow-500 text-white rounded mr-2 text-sm"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => handleDeleteCinema(cinema.id)}
                    className="px-2 py-1 bg-red-500 text-white rounded text-sm"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <div className="mt-4 flex justify-between items-center">
          <div>Total: {total} cinemas</div>
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
              disabled={(page + 1) * 10 >= total}
              className="px-3 py-1 border rounded disabled:opacity-50"
            >
              Next
            </button>
          </div>
        </div>
      </div>

      {/* Cinema Form Modal */}
      {showCinemaForm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="bg-white p-6 rounded-lg w-[600px] max-h-[80vh] overflow-y-auto">
            <h2 className="text-xl font-bold mb-4">
              {editingCinema ? 'Edit Cinema' : 'Create Cinema'}
            </h2>
            <div className="space-y-3">
              <div>
                <label className="block text-sm font-medium mb-1">Name *</label>
                <input
                  type="text"
                  value={cinemaForm.name}
                  onChange={(e) => setCinemaForm({ ...cinemaForm, name: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">City *</label>
                <input
                  type="text"
                  value={cinemaForm.city}
                  onChange={(e) => setCinemaForm({ ...cinemaForm, city: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Address *</label>
                <input
                  type="text"
                  value={cinemaForm.address}
                  onChange={(e) => setCinemaForm({ ...cinemaForm, address: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Phone *</label>
                <input
                  type="text"
                  value={cinemaForm.phoneNumber}
                  onChange={(e) => setCinemaForm({ ...cinemaForm, phoneNumber: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">District</label>
                <input
                  type="text"
                  value={cinemaForm.district || ''}
                  onChange={(e) => setCinemaForm({ ...cinemaForm, district: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Email</label>
                <input
                  type="email"
                  value={cinemaForm.email || ''}
                  onChange={(e) => setCinemaForm({ ...cinemaForm, email: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                />
              </div>
            </div>
            <div className="mt-6 flex justify-end space-x-2">
              <button
                onClick={() => {
                  setShowCinemaForm(false);
                  setEditingCinema(null);
                }}
                className="px-4 py-2 border rounded"
              >
                Cancel
              </button>
              <button
                onClick={editingCinema ? handleUpdateCinema : handleCreateCinema}
                className="px-4 py-2 bg-blue-500 text-white rounded"
              >
                {editingCinema ? 'Update' : 'Create'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Halls Section */}
      {selectedCinema && (
        <div className="mt-8 border-t pt-6">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-bold">
              Halls for {selectedCinema.name}
            </h2>
            <button
              onClick={() => {
                setShowHallForm(true);
                setEditingHall(null);
                setHallForm({
                  name: '',
                  cinemaId: selectedCinema.id,
                  totalRows: 10,
                  seatsPerRow: 12
                });
              }}
              className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
            >
              + New Hall
            </button>
          </div>

          <table className="w-full border-collapse border">
            <thead>
              <tr className="bg-gray-100">
                <th className="border p-2">ID</th>
                <th className="border p-2">Name</th>
                <th className="border p-2">Rows</th>
                <th className="border p-2">Seats/Row</th>
                <th className="border p-2">Total Seats</th>
                <th className="border p-2">Status</th>
                <th className="border p-2">Actions</th>
              </tr>
            </thead>
            <tbody>
              {halls.map((hall) => (
                <tr key={hall.id}>
                  <td className="border p-2">{hall.id}</td>
                  <td className="border p-2">{hall.name}</td>
                  <td className="border p-2">{hall.totalRows}</td>
                  <td className="border p-2">{hall.seatsPerRow}</td>
                  <td className="border p-2">{hall.totalSeats}</td>
                  <td className="border p-2">{hall.status}</td>
                  <td className="border p-2">
                    <button
                      onClick={() => {
                        setEditingHall(hall);
                        setHallForm({
                          name: hall.name,
                          cinemaId: selectedCinema.id,
                          totalRows: hall.totalRows,
                          seatsPerRow: hall.seatsPerRow,
                        });
                        setShowHallForm(true);
                      }}
                      className="px-2 py-1 bg-yellow-500 text-white rounded mr-2 text-sm"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => handleDeleteHall(hall.id)}
                      className="px-2 py-1 bg-red-500 text-white rounded text-sm"
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Hall Form Modal */}
      {showHallForm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="bg-white p-6 rounded-lg w-[500px]">
            <h2 className="text-xl font-bold mb-4">
              {editingHall ? 'Edit Hall' : 'Create Hall'}
            </h2>
            <div className="space-y-3">
              <div>
                <label className="block text-sm font-medium mb-1">Name *</label>
                <input
                  type="text"
                  value={hallForm.name}
                  onChange={(e) => setHallForm({ ...hallForm, name: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Total Rows *</label>
                <input
                  type="number"
                  value={hallForm.totalRows}
                  onChange={(e) => setHallForm({ ...hallForm, totalRows: parseInt(e.target.value) })}
                  className="w-full border rounded px-3 py-2"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Seats Per Row *</label>
                <input
                  type="number"
                  value={hallForm.seatsPerRow}
                  onChange={(e) => setHallForm({ ...hallForm, seatsPerRow: parseInt(e.target.value) })}
                  className="w-full border rounded px-3 py-2"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Screen Type</label>
                <input
                  type="text"
                  value={(hallForm as any).screenType || ''}
                  onChange={(e) => setHallForm({ ...hallForm, screenType: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                />
              </div>
            </div>
            <div className="mt-6 flex justify-end space-x-2">
              <button
                onClick={() => {
                  setShowHallForm(false);
                  setEditingHall(null);
                }}
                className="px-4 py-2 border rounded"
              >
                Cancel
              </button>
              <button
                onClick={editingHall ? handleUpdateHall : handleCreateHall}
                className="px-4 py-2 bg-blue-500 text-white rounded"
              >
                {editingHall ? 'Update' : 'Create'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
