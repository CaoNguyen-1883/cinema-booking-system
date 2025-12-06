import axiosInstance from './axios';
import type { GenreResponse, CreateGenreRequest, ApiResponse } from '@/types';

/**
 * Genre Service
 * Handles all genre-related API calls
 */
export const genreService = {
  /**
   * GET /api/genres
   * Get all genres
   */
  getAllGenres: async (): Promise<GenreResponse[]> => {
    const response = await axiosInstance.get<ApiResponse<GenreResponse[]>>(
      '/genres'
    );
    return response.data.data;
  },

  /**
   * GET /api/genres/{id}
   * Get genre by ID
   */
  getGenreById: async (id: number): Promise<GenreResponse> => {
    const response = await axiosInstance.get<ApiResponse<GenreResponse>>(
      `/genres/${id}`
    );
    return response.data.data;
  },

  /**
   * POST /api/admin/movies/genres
   * Create a new genre (Admin only)
   */
  createGenre: async (data: CreateGenreRequest): Promise<GenreResponse> => {
    const response = await axiosInstance.post<ApiResponse<GenreResponse>>(
      '/admin/movies/genres',
      data
    );
    return response.data.data;
  },

  /**
   * DELETE /api/admin/movies/genres/{id}
   * Delete a genre (Admin only)
   */
  deleteGenre: async (id: number): Promise<void> => {
    await axiosInstance.delete(`/admin/movies/genres/${id}`);
  },
};
