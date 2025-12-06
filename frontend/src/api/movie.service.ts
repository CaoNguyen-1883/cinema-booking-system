import axiosInstance from './axios';
import type {
  MovieResponse,
  CreateMovieRequest,
  UpdateMovieRequest,
  ApiResponse,
  PaginatedResponse,
} from '@/types';

/**
 * Movie Service
 * Handles all movie-related API calls
 */
export const movieService = {
  /**
   * GET /api/movies/{id}
   * Get movie by ID
   */
  getMovieById: async (id: number): Promise<MovieResponse> => {
    const response = await axiosInstance.get<ApiResponse<MovieResponse>>(
      `/movies/${id}`
    );
    return response.data.data;
  },

  /**
   * GET /api/movies
   * Get all movies with pagination
   */
  getAllMovies: async (params: {
    page?: number;
    size?: number;
  }): Promise<PaginatedResponse<MovieResponse>> => {
    const response = await axiosInstance.get<
      ApiResponse<PaginatedResponse<MovieResponse>>
    >('/movies', { params });
    return response.data.data;
  },

  /**
   * GET /api/movies/now-showing
   * Get now showing movies
   */
  getNowShowingMovies: async (): Promise<MovieResponse[]> => {
    const response = await axiosInstance.get<ApiResponse<MovieResponse[]>>(
      '/movies/now-showing'
    );
    return response.data.data;
  },

  /**
   * GET /api/movies/coming-soon
   * Get coming soon movies
   */
  getComingSoonMovies: async (): Promise<MovieResponse[]> => {
    const response = await axiosInstance.get<ApiResponse<MovieResponse[]>>(
      '/movies/coming-soon'
    );
    return response.data.data;
  },

  /**
   * GET /api/movies/search
   * Search movies by keyword
   */
  searchMovies: async (params: {
    keyword: string;
    page?: number;
    size?: number;
  }): Promise<PaginatedResponse<MovieResponse>> => {
    const response = await axiosInstance.get<
      ApiResponse<PaginatedResponse<MovieResponse>>
    >('/movies/search', { params });
    return response.data.data;
  },

  /**
   * GET /api/movies/genre/{genreId}
   * Get movies by genre
   */
  getMoviesByGenre: async (
    genreId: number,
    params: { page?: number; size?: number }
  ): Promise<PaginatedResponse<MovieResponse>> => {
    const response = await axiosInstance.get<
      ApiResponse<PaginatedResponse<MovieResponse>>
    >(`/movies/genre/${genreId}`, { params });
    return response.data.data;
  },

  /**
   * GET /api/movies/status/{status}
   * Get movies by status
   */
  getMoviesByStatus: async (
    status: string,
    params: { page?: number; size?: number }
  ): Promise<PaginatedResponse<MovieResponse>> => {
    const response = await axiosInstance.get<
      ApiResponse<PaginatedResponse<MovieResponse>>
    >(`/movies/status/${status}`, { params });
    return response.data.data;
  },

  /**
   * POST /api/admin/movies
   * Create new movie (admin)
   */
  createMovie: async (data: CreateMovieRequest): Promise<MovieResponse> => {
    const response = await axiosInstance.post<ApiResponse<MovieResponse>>(
      '/admin/movies',
      data
    );
    return response.data.data;
  },

  /**
   * PUT /api/admin/movies/{id}
   * Update movie (admin)
   */
  updateMovie: async (
    id: number,
    data: UpdateMovieRequest
  ): Promise<MovieResponse> => {
    const response = await axiosInstance.put<ApiResponse<MovieResponse>>(
      `/admin/movies/${id}`,
      data
    );
    return response.data.data;
  },

  /**
   * DELETE /api/admin/movies/{id}
   * Delete movie (admin)
   */
  deleteMovie: async (id: number): Promise<void> => {
    await axiosInstance.delete(`/admin/movies/${id}`);
  },

  /**
   * PUT /api/admin/movies/{id}/status
   * Update movie status (admin)
   */
  updateMovieStatus: async (id: number, status: string): Promise<void> => {
    await axiosInstance.put(`/admin/movies/${id}/status`, { status });
  },
};
