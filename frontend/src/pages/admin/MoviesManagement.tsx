import { useState, useEffect } from 'react';
import { movieService } from '@/api/movie.service';
import { genreService } from '@/api/genre.service';
import { fileService } from '@/api/file.service';
import type {
  MovieResponse,
  CreateMovieRequest,
  UpdateMovieRequest,
  GenreResponse,
} from '@/types';

export function MoviesManagementPage() {
  const [movies, setMovies] = useState<MovieResponse[]>([]);
  const [genres, setGenres] = useState<GenreResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);
  const [statusFilter, setStatusFilter] = useState('');

  // Form state
  const [showForm, setShowForm] = useState(false);
  const [editingMovie, setEditingMovie] = useState<MovieResponse | null>(null);
  const [formData, setFormData] = useState<CreateMovieRequest>({
    title: '',
    originalTitle: '',
    description: '',
    duration: 0,
    releaseDate: '',
    endDate: '',
    director: '',
    castMembers: '',
    language: '',
    country: '',
    rating: 'P',
    trailerUrl: '',
    posterUrl: '',
    backdropUrl: '',
    genreIds: [],
  });

  // Image upload state
  const [posterUploadMode, setPosterUploadMode] = useState<'url' | 'upload'>('url');
  const [backdropUploadMode, setBackdropUploadMode] = useState<'url' | 'upload'>('url');
  const [uploadingPoster, setUploadingPoster] = useState(false);
  const [uploadingBackdrop, setUploadingBackdrop] = useState(false);

  // Load movies
  const loadMovies = async () => {
    setLoading(true);
    try {
      let response;
      if (statusFilter) {
        response = await movieService.getMoviesByStatus(statusFilter, { page, size: 20 });
      } else {
        response = await movieService.getAllMovies({ page, size: 20 });
      }
      setMovies(response.content);
      setTotal(response.totalElements);
    } catch (error: any) {
      alert('Failed to load movies: ' + (error?.response?.data?.message || error.message));
    } finally {
      setLoading(false);
    }
  };

  // Load genres
  const loadGenres = async () => {
    try {
      const data = await genreService.getAllGenres();
      setGenres(data);
    } catch (error: any) {
      alert('Failed to load genres: ' + (error?.response?.data?.message || error.message));
    }
  };

  useEffect(() => {
    loadGenres();
  }, []);

  // Create movie
  const handleCreateMovie = async () => {
    try {
      await movieService.createMovie(formData);
      alert('Movie created successfully!');
      setShowForm(false);
      resetForm();
      loadMovies();
    } catch (error: any) {
      alert('Failed to create movie: ' + (error?.response?.data?.message || error.message));
    }
  };

  // Update movie
  const handleUpdateMovie = async () => {
    if (!editingMovie) return;
    try {
      await movieService.updateMovie(editingMovie.id, formData as UpdateMovieRequest);
      alert('Movie updated successfully!');
      setShowForm(false);
      setEditingMovie(null);
      resetForm();
      loadMovies();
    } catch (error: any) {
      alert('Failed to update movie: ' + (error?.response?.data?.message || error.message));
    }
  };

  // Delete movie
  const handleDeleteMovie = async (id: number) => {
    if (!confirm('Are you sure you want to delete this movie?')) return;
    try {
      await movieService.deleteMovie(id);
      alert('Movie deleted successfully!');
      loadMovies();
    } catch (error: any) {
      alert('Failed to delete movie: ' + (error?.response?.data?.message || error.message));
    }
  };

  // Update status
  const handleUpdateStatus = async (id: number, status: string) => {
    try {
      await movieService.updateMovieStatus(id, status);
      alert('Movie status updated!');
      loadMovies();
    } catch (error: any) {
      alert('Failed to update status: ' + (error?.response?.data?.message || error.message));
    }
  };

  // Handle poster upload
  const handlePosterUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      alert('Please select an image file');
      return;
    }

    setUploadingPoster(true);
    try {
      const response = await fileService.uploadMoviePoster(file);
      setFormData({ ...formData, posterUrl: response.url });
      alert('Poster uploaded successfully!');
    } catch (error: any) {
      alert('Failed to upload poster: ' + (error?.response?.data?.message || error.message));
    } finally {
      setUploadingPoster(false);
    }
  };

  // Handle backdrop upload
  const handleBackdropUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      alert('Please select an image file');
      return;
    }

    setUploadingBackdrop(true);
    try {
      const response = await fileService.uploadMovieBackdrop(file);
      setFormData({ ...formData, backdropUrl: response.url });
      alert('Backdrop uploaded successfully!');
    } catch (error: any) {
      alert('Failed to upload backdrop: ' + (error?.response?.data?.message || error.message));
    } finally {
      setUploadingBackdrop(false);
    }
  };

  const resetForm = () => {
    setFormData({
      title: '',
      originalTitle: '',
      description: '',
      duration: 0,
      releaseDate: '',
      endDate: '',
      director: '',
      castMembers: '',
      language: '',
      country: '',
      rating: 'P',
      trailerUrl: '',
      posterUrl: '',
      backdropUrl: '',
      genreIds: [],
    });
    setPosterUploadMode('url');
    setBackdropUploadMode('url');
  };

  const openCreateForm = () => {
    setShowForm(true);
    setEditingMovie(null);
    resetForm();
  };

  const openEditForm = (movie: MovieResponse) => {
    setEditingMovie(movie);
    setFormData({
      title: movie.title,
      originalTitle: movie.originalTitle || '',
      description: movie.description || '',
      duration: movie.duration,
      releaseDate: movie.releaseDate,
      endDate: movie.endDate || '',
      director: movie.director || '',
      castMembers: movie.castMembers || '',
      language: movie.language || '',
      country: movie.country || '',
      rating: movie.rating,
      trailerUrl: movie.trailerUrl || '',
      posterUrl: movie.posterUrl || '',
      backdropUrl: movie.backdropUrl || '',
      genreIds: movie.genres.map((g) => g.id),
    });
    setShowForm(true);
  };

  const toggleGenre = (genreId: number) => {
    setFormData((prev) => ({
      ...prev,
      genreIds: prev.genreIds?.includes(genreId)
        ? prev.genreIds.filter((id) => id !== genreId)
        : [...(prev.genreIds || []), genreId],
    }));
  };

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Movies Management</h1>
        <div className="space-x-2">
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-3 py-2 border rounded"
          >
            <option value="">All Status</option>
            <option value="COMING_SOON">COMING SOON</option>
            <option value="NOW_SHOWING">NOW SHOWING</option>
            <option value="ENDED">ENDED</option>
          </select>
          <button
            onClick={loadMovies}
            className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Load Movies
          </button>
          <button
            onClick={openCreateForm}
            className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
          >
            + New Movie
          </button>
        </div>
      </div>

      {loading && <div>Loading...</div>}

      {/* Movies Table */}
      <table className="w-full border-collapse border">
        <thead>
          <tr className="bg-gray-100">
            <th className="border p-2">ID</th>
            <th className="border p-2">Title</th>
            <th className="border p-2">Duration</th>
            <th className="border p-2">Release Date</th>
            <th className="border p-2">Rating</th>
            <th className="border p-2">Status</th>
            <th className="border p-2">Genres</th>
            <th className="border p-2">Actions</th>
          </tr>
        </thead>
        <tbody>
          {movies.map((movie) => (
            <tr key={movie.id}>
              <td className="border p-2">{movie.id}</td>
              <td className="border p-2">
                <div className="font-medium">{movie.title}</div>
                {movie.originalTitle && (
                  <div className="text-sm text-gray-500">{movie.originalTitle}</div>
                )}
              </td>
              <td className="border p-2">{movie.duration} mins</td>
              <td className="border p-2">{movie.releaseDate}</td>
              <td className="border p-2">{movie.rating}</td>
              <td className="border p-2">
                <select
                  value={movie.status}
                  onChange={(e) => handleUpdateStatus(movie.id, e.target.value)}
                  className="border rounded px-2 py-1 text-sm"
                >
                  <option value="COMING_SOON">COMING SOON</option>
                  <option value="NOW_SHOWING">NOW SHOWING</option>
                  <option value="ENDED">ENDED</option>
                </select>
              </td>
              <td className="border p-2">
                <div className="flex flex-wrap gap-1">
                  {movie.genres.map((genre) => (
                    <span
                      key={genre.id}
                      className="px-2 py-1 bg-blue-100 text-blue-800 rounded text-xs"
                    >
                      {genre.name}
                    </span>
                  ))}
                </div>
              </td>
              <td className="border p-2">
                <button
                  onClick={() => openEditForm(movie)}
                  className="px-2 py-1 bg-yellow-500 text-white rounded mr-2 text-sm"
                >
                  Edit
                </button>
                <button
                  onClick={() => handleDeleteMovie(movie.id)}
                  className="px-2 py-1 bg-red-500 text-white rounded text-sm"
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Pagination */}
      <div className="mt-4 flex justify-between items-center">
        <div>Total: {total} movies</div>
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

      {/* Movie Form Modal */}
      {showForm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg w-[800px] max-h-[90vh] overflow-y-auto">
            <h2 className="text-xl font-bold mb-4">
              {editingMovie ? 'Edit Movie' : 'Create Movie'}
            </h2>
            <div className="grid grid-cols-2 gap-4">
              {/* Column 1 */}
              <div className="space-y-3">
                <div>
                  <label className="block text-sm font-medium mb-1">Title *</label>
                  <input
                    type="text"
                    value={formData.title}
                    onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                    className="w-full border rounded px-3 py-2"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Original Title</label>
                  <input
                    type="text"
                    value={formData.originalTitle}
                    onChange={(e) =>
                      setFormData({ ...formData, originalTitle: e.target.value })
                    }
                    className="w-full border rounded px-3 py-2"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Duration (mins) *</label>
                  <input
                    type="number"
                    value={formData.duration}
                    onChange={(e) =>
                      setFormData({ ...formData, duration: parseInt(e.target.value) || 0 })
                    }
                    className="w-full border rounded px-3 py-2"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Release Date *</label>
                  <input
                    type="date"
                    value={formData.releaseDate}
                    onChange={(e) => setFormData({ ...formData, releaseDate: e.target.value })}
                    className="w-full border rounded px-3 py-2"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">End Date</label>
                  <input
                    type="date"
                    value={formData.endDate}
                    onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                    className="w-full border rounded px-3 py-2"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Rating *</label>
                  <select
                    value={formData.rating}
                    onChange={(e) => setFormData({ ...formData, rating: e.target.value })}
                    className="w-full border rounded px-3 py-2"
                  >
                    <option value="P">P - Phổ thông</option>
                    <option value="C13">C13 - 13+</option>
                    <option value="C16">C16 - 16+</option>
                    <option value="C18">C18 - 18+</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Director</label>
                  <input
                    type="text"
                    value={formData.director}
                    onChange={(e) => setFormData({ ...formData, director: e.target.value })}
                    className="w-full border rounded px-3 py-2"
                  />
                </div>
              </div>

              {/* Column 2 */}
              <div className="space-y-3">
                <div>
                  <label className="block text-sm font-medium mb-1">Cast Members</label>
                  <input
                    type="text"
                    value={formData.castMembers}
                    onChange={(e) => setFormData({ ...formData, castMembers: e.target.value })}
                    className="w-full border rounded px-3 py-2"
                    placeholder="Actor 1, Actor 2, ..."
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Language</label>
                  <input
                    type="text"
                    value={formData.language}
                    onChange={(e) => setFormData({ ...formData, language: e.target.value })}
                    className="w-full border rounded px-3 py-2"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Country</label>
                  <input
                    type="text"
                    value={formData.country}
                    onChange={(e) => setFormData({ ...formData, country: e.target.value })}
                    className="w-full border rounded px-3 py-2"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Trailer URL</label>
                  <input
                    type="url"
                    value={formData.trailerUrl}
                    onChange={(e) => setFormData({ ...formData, trailerUrl: e.target.value })}
                    className="w-full border rounded px-3 py-2"
                  />
                </div>
                <div>
                  <div className="flex justify-between items-center mb-1">
                    <label className="block text-sm font-medium">Poster Image</label>
                    <div className="flex gap-2">
                      <button
                        type="button"
                        onClick={() => setPosterUploadMode('url')}
                        className={`px-2 py-1 text-xs rounded ${
                          posterUploadMode === 'url'
                            ? 'bg-blue-500 text-white'
                            : 'bg-gray-200 text-gray-700'
                        }`}
                      >
                        URL
                      </button>
                      <button
                        type="button"
                        onClick={() => setPosterUploadMode('upload')}
                        className={`px-2 py-1 text-xs rounded ${
                          posterUploadMode === 'upload'
                            ? 'bg-blue-500 text-white'
                            : 'bg-gray-200 text-gray-700'
                        }`}
                      >
                        Upload
                      </button>
                    </div>
                  </div>
                  {posterUploadMode === 'url' ? (
                    <input
                      type="url"
                      value={formData.posterUrl}
                      onChange={(e) => setFormData({ ...formData, posterUrl: e.target.value })}
                      className="w-full border rounded px-3 py-2"
                      placeholder="https://example.com/poster.jpg"
                    />
                  ) : (
                    <div className="space-y-2">
                      <input
                        type="file"
                        accept="image/*"
                        onChange={handlePosterUpload}
                        disabled={uploadingPoster}
                        className="w-full border rounded px-3 py-2 text-sm"
                      />
                      {uploadingPoster && (
                        <p className="text-sm text-blue-600">Uploading...</p>
                      )}
                      {formData.posterUrl && (
                        <input
                          type="text"
                          value={formData.posterUrl}
                          readOnly
                          className="w-full border rounded px-3 py-2 text-sm bg-gray-50"
                          placeholder="Uploaded URL will appear here"
                        />
                      )}
                    </div>
                  )}
                  {formData.posterUrl && (
                    <img
                      src={formData.posterUrl}
                      alt="Poster preview"
                      className="mt-2 w-32 h-48 object-cover rounded border"
                      onError={(e) => {
                        e.currentTarget.style.display = 'none';
                      }}
                    />
                  )}
                </div>
                <div>
                  <div className="flex justify-between items-center mb-1">
                    <label className="block text-sm font-medium">Backdrop Image</label>
                    <div className="flex gap-2">
                      <button
                        type="button"
                        onClick={() => setBackdropUploadMode('url')}
                        className={`px-2 py-1 text-xs rounded ${
                          backdropUploadMode === 'url'
                            ? 'bg-blue-500 text-white'
                            : 'bg-gray-200 text-gray-700'
                        }`}
                      >
                        URL
                      </button>
                      <button
                        type="button"
                        onClick={() => setBackdropUploadMode('upload')}
                        className={`px-2 py-1 text-xs rounded ${
                          backdropUploadMode === 'upload'
                            ? 'bg-blue-500 text-white'
                            : 'bg-gray-200 text-gray-700'
                        }`}
                      >
                        Upload
                      </button>
                    </div>
                  </div>
                  {backdropUploadMode === 'url' ? (
                    <input
                      type="url"
                      value={formData.backdropUrl}
                      onChange={(e) => setFormData({ ...formData, backdropUrl: e.target.value })}
                      className="w-full border rounded px-3 py-2"
                      placeholder="https://example.com/backdrop.jpg"
                    />
                  ) : (
                    <div className="space-y-2">
                      <input
                        type="file"
                        accept="image/*"
                        onChange={handleBackdropUpload}
                        disabled={uploadingBackdrop}
                        className="w-full border rounded px-3 py-2 text-sm"
                      />
                      {uploadingBackdrop && (
                        <p className="text-sm text-blue-600">Uploading...</p>
                      )}
                      {formData.backdropUrl && (
                        <input
                          type="text"
                          value={formData.backdropUrl}
                          readOnly
                          className="w-full border rounded px-3 py-2 text-sm bg-gray-50"
                          placeholder="Uploaded URL will appear here"
                        />
                      )}
                    </div>
                  )}
                  {formData.backdropUrl && (
                    <img
                      src={formData.backdropUrl}
                      alt="Backdrop preview"
                      className="mt-2 w-full h-24 object-cover rounded border"
                      onError={(e) => {
                        e.currentTarget.style.display = 'none';
                      }}
                    />
                  )}
                </div>
              </div>

              {/* Full width fields */}
              <div className="col-span-2">
                <label className="block text-sm font-medium mb-1">Description</label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                  rows={3}
                />
              </div>

              <div className="col-span-2">
                <label className="block text-sm font-medium mb-2">Genres</label>
                <div className="flex flex-wrap gap-2">
                  {genres.map((genre) => (
                    <label key={genre.id} className="flex items-center space-x-2">
                      <input
                        type="checkbox"
                        checked={formData.genreIds?.includes(genre.id)}
                        onChange={() => toggleGenre(genre.id)}
                        className="rounded"
                      />
                      <span>{genre.name}</span>
                    </label>
                  ))}
                </div>
              </div>
            </div>

            <div className="mt-6 flex justify-end space-x-2">
              <button
                onClick={() => {
                  setShowForm(false);
                  setEditingMovie(null);
                  resetForm();
                }}
                className="px-4 py-2 border rounded"
              >
                Cancel
              </button>
              <button
                onClick={editingMovie ? handleUpdateMovie : handleCreateMovie}
                className="px-4 py-2 bg-blue-500 text-white rounded"
              >
                {editingMovie ? 'Update' : 'Create'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
