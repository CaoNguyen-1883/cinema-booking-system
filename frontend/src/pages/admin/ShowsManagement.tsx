import { useState } from 'react';
import { showService } from '@/api/show.service';
import { movieService } from '@/api/movie.service';
import { cinemaService } from '@/api/cinema.service';
import type {
  ShowResponse,
  CreateShowRequest,
  UpdateShowRequest,
  MovieResponse,
  CinemaResponse,
  HallResponse,
} from '@/types';

export function ShowsManagement() {
  const [shows, setShows] = useState<ShowResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);
  const [statusFilter, setStatusFilter] = useState('');

  // For creating shows
  const [showForm, setShowForm] = useState(false);
  const [editingShow, setEditingShow] = useState<ShowResponse | null>(null);
  const [formData, setFormData] = useState<CreateShowRequest>({
    movieId: 0,
    hallId: 0,
    showDate: '',
    startTime: '',
    basePrice: 50000,
  });

  // For dropdowns
  const [movies, setMovies] = useState<MovieResponse[]>([]);
  const [cinemas, setCinemas] = useState<CinemaResponse[]>([]);
  const [halls, setHalls] = useState<HallResponse[]>([]);
  const [selectedCinemaId, setSelectedCinemaId] = useState(0);

  // Load shows
  const loadShows = async () => {
    setLoading(true);
    try {
      const response = await showService.getAllShows({
        status: statusFilter,
        page,
        size: 20,
      });
      setShows(response.content);
      setTotal(response.totalElements);
    } catch (error: any) {
      alert('Failed to load shows: ' + (error?.response?.data?.message || error.message));
    } finally {
      setLoading(false);
    }
  };

  // Load movies for dropdown
  const loadMovies = async () => {
    try {
      const response = await movieService.getAllMovies({ page: 0, size: 100 });
      setMovies(response.content);
    } catch (error: any) {
      alert('Failed to load movies: ' + (error?.response?.data?.message || error.message));
    }
  };

  // Load cinemas for dropdown
  const loadCinemas = async () => {
    try {
      const response = await cinemaService.getAllCinemas({ page: 0, size: 100 });
      setCinemas(response.content);
    } catch (error: any) {
      alert('Failed to load cinemas: ' + (error?.response?.data?.message || error.message));
    }
  };

  // Load halls when cinema is selected
  const loadHalls = async (cinemaId: number) => {
    try {
      const hallsData = await cinemaService.getHallsByCinema(cinemaId);
      setHalls(hallsData);
    } catch (error: any) {
      alert('Failed to load halls: ' + (error?.response?.data?.message || error.message));
    }
  };

  // Create show
  const handleCreateShow = async () => {
    try {
      await showService.createShow(formData);
      alert('Show created successfully!');
      setShowForm(false);
      resetForm();
      loadShows();
    } catch (error: any) {
      alert('Failed to create show: ' + (error?.response?.data?.message || error.message));
    }
  };

  // Update show
  const handleUpdateShow = async () => {
    if (!editingShow) return;
    try {
      await showService.updateShow(editingShow.id, formData as UpdateShowRequest);
      alert('Show updated successfully!');
      setShowForm(false);
      setEditingShow(null);
      resetForm();
      loadShows();
    } catch (error: any) {
      alert('Failed to update show: ' + (error?.response?.data?.message || error.message));
    }
  };

  // Delete show
  const handleDeleteShow = async (id: number) => {
    if (!confirm('Are you sure you want to delete this show?')) return;
    try {
      await showService.deleteShow(id);
      alert('Show deleted successfully!');
      loadShows();
    } catch (error: any) {
      alert('Failed to delete show: ' + (error?.response?.data?.message || error.message));
    }
  };

  // Cancel show
  const handleCancelShow = async (id: number) => {
    if (!confirm('Are you sure you want to cancel this show?')) return;
    try {
      await showService.cancelShow(id);
      alert('Show cancelled successfully!');
      loadShows();
    } catch (error: any) {
      alert('Failed to cancel show: ' + (error?.response?.data?.message || error.message));
    }
  };

  const resetForm = () => {
    setFormData({
      movieId: 0,
      hallId: 0,
      showDate: '',
      startTime: '',
      basePrice: 50000,
    });
    setSelectedCinemaId(0);
    setHalls([]);
  };

  const openCreateForm = () => {
    setShowForm(true);
    setEditingShow(null);
    resetForm();
    loadMovies();
    loadCinemas();
  };

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Shows Management</h1>
        <div className="space-x-2">
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-3 py-2 border rounded"
          >
            <option value="">All Status</option>
            <option value="SCHEDULED">SCHEDULED</option>
            <option value="SHOWING">SHOWING</option>
            <option value="COMPLETED">COMPLETED</option>
            <option value="CANCELLED">CANCELLED</option>
          </select>
          <button
            onClick={loadShows}
            className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Load Shows
          </button>
          <button
            onClick={openCreateForm}
            className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
          >
            + New Show
          </button>
        </div>
      </div>

      {loading && <div>Loading...</div>}

      {/* Shows Table */}
      <table className="w-full border-collapse border">
        <thead>
          <tr className="bg-gray-100">
            <th className="border p-2">ID</th>
            <th className="border p-2">Movie</th>
            <th className="border p-2">Cinema</th>
            <th className="border p-2">Hall</th>
            <th className="border p-2">Date</th>
            <th className="border p-2">Time</th>
            <th className="border p-2">Price</th>
            <th className="border p-2">Seats</th>
            <th className="border p-2">Status</th>
            <th className="border p-2">Actions</th>
          </tr>
        </thead>
        <tbody>
          {shows.map((show) => (
            <tr key={show.id}>
              <td className="border p-2">{show.id}</td>
              <td className="border p-2">{show.movieTitle}</td>
              <td className="border p-2">{show.cinemaName}</td>
              <td className="border p-2">{show.hallName}</td>
              <td className="border p-2">{show.showDate}</td>
              <td className="border p-2">{show.startTime} - {show.endTime}</td>
              <td className="border p-2">{show.basePrice.toLocaleString()}</td>
              <td className="border p-2">{show.availableSeats}/{show.totalSeats}</td>
              <td className="border p-2">
                <span className={`px-2 py-1 rounded text-xs ${
                  show.status === 'SCHEDULED' ? 'bg-blue-100 text-blue-800' :
                  show.status === 'SHOWING' ? 'bg-green-100 text-green-800' :
                  show.status === 'COMPLETED' ? 'bg-gray-100 text-gray-800' :
                  'bg-red-100 text-red-800'
                }`}>
                  {show.status}
                </span>
              </td>
              <td className="border p-2">
                <button
                  onClick={() => {
                    setEditingShow(show);
                    setFormData({
                      movieId: show.movieId,
                      hallId: show.hallId,
                      showDate: show.showDate,
                      startTime: show.startTime,
                      basePrice: show.basePrice,
                    });
                    setShowForm(true);
                    loadMovies();
                    loadCinemas();
                  }}
                  className="px-2 py-1 bg-yellow-500 text-white rounded mr-2 text-sm"
                >
                  Edit
                </button>
                <button
                  onClick={() => handleCancelShow(show.id)}
                  className="px-2 py-1 bg-orange-500 text-white rounded mr-2 text-sm"
                  disabled={show.status === 'CANCELLED'}
                >
                  Cancel
                </button>
                <button
                  onClick={() => handleDeleteShow(show.id)}
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
        <div>Total: {total} shows</div>
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

      {/* Show Form Modal */}
      {showForm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="bg-white p-6 rounded-lg w-[600px] max-h-[80vh] overflow-y-auto">
            <h2 className="text-xl font-bold mb-4">
              {editingShow ? 'Edit Show' : 'Create Show'}
            </h2>
            <div className="space-y-3">
              <div>
                <label className="block text-sm font-medium mb-1">Movie *</label>
                <select
                  value={formData.movieId}
                  onChange={(e) => setFormData({ ...formData, movieId: parseInt(e.target.value) })}
                  className="w-full border rounded px-3 py-2"
                  disabled={editingShow !== null}
                >
                  <option value={0}>Select Movie</option>
                  {movies.map((movie) => (
                    <option key={movie.id} value={movie.id}>
                      {movie.title} ({movie.duration} mins)
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">Cinema *</label>
                <select
                  value={selectedCinemaId}
                  onChange={(e) => {
                    const cinemaId = parseInt(e.target.value);
                    setSelectedCinemaId(cinemaId);
                    if (cinemaId > 0) {
                      loadHalls(cinemaId);
                    } else {
                      setHalls([]);
                    }
                    setFormData({ ...formData, hallId: 0 });
                  }}
                  className="w-full border rounded px-3 py-2"
                  disabled={editingShow !== null}
                >
                  <option value={0}>Select Cinema</option>
                  {cinemas.map((cinema) => (
                    <option key={cinema.id} value={cinema.id}>
                      {cinema.name} - {cinema.city}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">Hall *</label>
                <select
                  value={formData.hallId}
                  onChange={(e) => setFormData({ ...formData, hallId: parseInt(e.target.value) })}
                  className="w-full border rounded px-3 py-2"
                  disabled={editingShow !== null || halls.length === 0}
                >
                  <option value={0}>Select Hall</option>
                  {halls.map((hall) => (
                    <option key={hall.id} value={hall.id}>
                      {hall.name} ({hall.totalSeats} seats)
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">Show Date *</label>
                <input
                  type="date"
                  value={formData.showDate}
                  onChange={(e) => setFormData({ ...formData, showDate: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">Start Time *</label>
                <input
                  type="time"
                  value={formData.startTime}
                  onChange={(e) => setFormData({ ...formData, startTime: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">Base Price *</label>
                <input
                  type="number"
                  value={formData.basePrice}
                  onChange={(e) => setFormData({ ...formData, basePrice: parseFloat(e.target.value) })}
                  className="w-full border rounded px-3 py-2"
                  step="1000"
                />
              </div>

              {editingShow && (
                <div>
                  <label className="block text-sm font-medium mb-1">Status</label>
                  <select
                    value={(formData as UpdateShowRequest).status || editingShow.status}
                    onChange={(e) => setFormData({
                      ...(formData as any),
                      status: e.target.value as any
                    })}
                    className="w-full border rounded px-3 py-2"
                  >
                    <option value="SCHEDULED">SCHEDULED</option>
                    <option value="SHOWING">SHOWING</option>
                    <option value="COMPLETED">COMPLETED</option>
                    <option value="CANCELLED">CANCELLED</option>
                  </select>
                </div>
              )}
            </div>

            <div className="mt-6 flex justify-end space-x-2">
              <button
                onClick={() => {
                  setShowForm(false);
                  setEditingShow(null);
                  resetForm();
                }}
                className="px-4 py-2 border rounded"
              >
                Cancel
              </button>
              <button
                onClick={editingShow ? handleUpdateShow : handleCreateShow}
                className="px-4 py-2 bg-blue-500 text-white rounded"
              >
                {editingShow ? 'Update' : 'Create'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
