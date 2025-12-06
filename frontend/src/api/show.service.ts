import axiosInstance from './axios';
import type {
  ShowResponse,
  CreateShowRequest,
  UpdateShowRequest,
  ShowSeatResponse,
  ApiResponse,
  PaginatedResponse,
} from '@/types';

/**
 * Show Service
 * Handles all show-related API calls
 */
export const showService = {
  /**
   * GET /api/shows/{id}
   * Get show by ID
   */
  getShowById: async (id: number): Promise<ShowResponse> => {
    const response = await axiosInstance.get<ApiResponse<ShowResponse>>(
      `/shows/${id}`
    );
    return response.data.data;
  },

  /**
   * GET /api/shows/{id}/seats
   * Get show seats
   */
  getShowSeats: async (id: number): Promise<ShowSeatResponse[]> => {
    const response = await axiosInstance.get<ApiResponse<ShowSeatResponse[]>>(
      `/shows/${id}/seats`
    );
    return response.data.data;
  },

  /**
   * GET /api/shows/movie/{movieId}
   * Get shows by movie
   */
  getShowsByMovie: async (
    movieId: number,
    params?: { startDate?: string; endDate?: string }
  ): Promise<ShowResponse[]> => {
    const response = await axiosInstance.get<ApiResponse<ShowResponse[]>>(
      `/shows/movie/${movieId}`,
      { params }
    );
    return response.data.data;
  },

  /**
   * GET /api/shows/cinema/{cinemaId}
   * Get shows by cinema
   */
  getShowsByCinema: async (
    cinemaId: number,
    params?: { date?: string }
  ): Promise<ShowResponse[]> => {
    const response = await axiosInstance.get<ApiResponse<ShowResponse[]>>(
      `/shows/cinema/${cinemaId}`,
      { params }
    );
    return response.data.data;
  },

  /**
   * GET /api/shows
   * Get all shows with pagination
   */
  getAllShows: async (params: {
    status?: string;
    page?: number;
    size?: number;
  }): Promise<PaginatedResponse<ShowResponse>> => {
    const response = await axiosInstance.get<
      ApiResponse<PaginatedResponse<ShowResponse>>
    >('/shows', { params });
    return response.data.data;
  },

  /**
   * GET /api/admin/shows/by-hall/{hallId}?date=
   * Get shows by hall and date for schedule view (admin)
   */
  getShowsByHallAndDate: async (
    hallId: number,
    date: string
  ): Promise<ShowResponse[]> => {
    const response = await axiosInstance.get<ApiResponse<ShowResponse[]>>(
      `/admin/shows/by-hall/${hallId}`,
      { params: { date } }
    );
    return response.data.data;
  },

  /**
   * POST /api/admin/shows
   * Create new show (admin)
   */
  createShow: async (data: CreateShowRequest): Promise<ShowResponse> => {
    const response = await axiosInstance.post<ApiResponse<ShowResponse>>(
      '/admin/shows',
      data
    );
    return response.data.data;
  },

  /**
   * PUT /api/admin/shows/{id}
   * Update show (admin)
   */
  updateShow: async (
    id: number,
    data: UpdateShowRequest
  ): Promise<ShowResponse> => {
    const response = await axiosInstance.put<ApiResponse<ShowResponse>>(
      `/admin/shows/${id}`,
      data
    );
    return response.data.data;
  },

  /**
   * DELETE /api/admin/shows/{id}
   * Delete show (admin)
   */
  deleteShow: async (id: number): Promise<void> => {
    await axiosInstance.delete(`/admin/shows/${id}`);
  },

  /**
   * PUT /api/admin/shows/{id}/cancel
   * Cancel show (admin)
   */
  cancelShow: async (id: number): Promise<void> => {
    await axiosInstance.put(`/admin/shows/${id}/cancel`);
  },
};
