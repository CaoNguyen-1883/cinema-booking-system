import axiosInstance from './axios';
import type {
  CinemaResponse,
  CreateCinemaRequest,
  UpdateCinemaRequest,
  HallResponse,
  CreateHallRequest,
  UpdateHallRequest,
  SeatResponse,
  UpdateSeatRequest,
  ApiResponse,
  PaginatedResponse,
} from '@/types';

/**
 * Cinema Service
 * Handles all cinema-related API calls
 */
export const cinemaService = {
  // Cinema endpoints
  /**
   * GET /api/cinemas/{id}
   * Get cinema by ID
   */
  getCinemaById: async (id: number): Promise<CinemaResponse> => {
    const response = await axiosInstance.get<ApiResponse<CinemaResponse>>(
      `/cinemas/${id}`
    );
    return response.data.data;
  },

  /**
   * GET /api/cinemas/{id}/with-halls
   * Get cinema with halls
   */
  getCinemaWithHalls: async (id: number): Promise<CinemaResponse> => {
    const response = await axiosInstance.get<ApiResponse<CinemaResponse>>(
      `/cinemas/${id}/with-halls`
    );
    return response.data.data;
  },

  /**
   * GET /api/cinemas
   * Get all cinemas with pagination
   */
  getAllCinemas: async (params: {
    page?: number;
    size?: number;
  }): Promise<PaginatedResponse<CinemaResponse>> => {
    const response = await axiosInstance.get<
      ApiResponse<PaginatedResponse<CinemaResponse>>
    >('/cinemas', { params });
    return response.data.data;
  },

  /**
   * GET /api/cinemas/active
   * Get active cinemas
   */
  getActiveCinemas: async (): Promise<CinemaResponse[]> => {
    const response = await axiosInstance.get<ApiResponse<CinemaResponse[]>>(
      '/cinemas/active'
    );
    return response.data.data;
  },

  /**
   * GET /api/cinemas/city/{city}
   * Get cinemas by city
   */
  getCinemasByCity: async (
    city: string,
    params: { page?: number; size?: number }
  ): Promise<PaginatedResponse<CinemaResponse>> => {
    const response = await axiosInstance.get<
      ApiResponse<PaginatedResponse<CinemaResponse>>
    >(`/cinemas/city/${city}`, { params });
    return response.data.data;
  },

  /**
   * GET /api/cinemas/cities
   * Get distinct cities
   */
  getDistinctCities: async (): Promise<string[]> => {
    const response = await axiosInstance.get<ApiResponse<string[]>>(
      '/cinemas/cities'
    );
    return response.data.data;
  },

  /**
   * GET /api/cinemas/search
   * Search cinemas by keyword
   */
  searchCinemas: async (params: {
    keyword: string;
    page?: number;
    size?: number;
  }): Promise<PaginatedResponse<CinemaResponse>> => {
    const response = await axiosInstance.get<
      ApiResponse<PaginatedResponse<CinemaResponse>>
    >('/cinemas/search', { params });
    return response.data.data;
  },

  /**
   * POST /api/admin/cinemas
   * Create new cinema (admin)
   */
  createCinema: async (data: CreateCinemaRequest): Promise<CinemaResponse> => {
    const response = await axiosInstance.post<ApiResponse<CinemaResponse>>(
      '/admin/cinemas',
      data
    );
    return response.data.data;
  },

  /**
   * PUT /api/admin/cinemas/{id}
   * Update cinema (admin)
   */
  updateCinema: async (
    id: number,
    data: UpdateCinemaRequest
  ): Promise<CinemaResponse> => {
    const response = await axiosInstance.put<ApiResponse<CinemaResponse>>(
      `/admin/cinemas/${id}`,
      data
    );
    return response.data.data;
  },

  /**
   * DELETE /api/admin/cinemas/{id}
   * Delete cinema (admin)
   */
  deleteCinema: async (id: number): Promise<void> => {
    await axiosInstance.delete(`/admin/cinemas/${id}`);
  },

  // Hall endpoints
  /**
   * GET /api/halls/{id}
   * Get hall by ID
   */
  getHallById: async (id: number): Promise<HallResponse> => {
    const response = await axiosInstance.get<ApiResponse<HallResponse>>(
      `/halls/${id}`
    );
    return response.data.data;
  },

  /**
   * GET /api/halls/{id}/with-seats
   * Get hall with seats
   */
  getHallWithSeats: async (id: number): Promise<HallResponse> => {
    const response = await axiosInstance.get<ApiResponse<HallResponse>>(
      `/halls/${id}/with-seats`
    );
    return response.data.data;
  },

  /**
   * GET /api/halls/cinema/{cinemaId}
   * Get halls by cinema
   */
  getHallsByCinema: async (cinemaId: number): Promise<HallResponse[]> => {
    const response = await axiosInstance.get<ApiResponse<HallResponse[]>>(
      `/halls/cinema/${cinemaId}`
    );
    return response.data.data;
  },

  /**
   * POST /api/admin/halls
   * Create new hall (admin)
   */
  createHall: async (data: CreateHallRequest): Promise<HallResponse> => {
    const response = await axiosInstance.post<ApiResponse<HallResponse>>(
      '/admin/halls',
      data
    );
    return response.data.data;
  },

  /**
   * PUT /api/admin/halls/{id}
   * Update hall (admin)
   */
  updateHall: async (
    id: number,
    data: UpdateHallRequest
  ): Promise<HallResponse> => {
    const response = await axiosInstance.put<ApiResponse<HallResponse>>(
      `/admin/halls/${id}`,
      data
    );
    return response.data.data;
  },

  /**
   * DELETE /api/admin/halls/{id}
   * Delete hall (admin)
   */
  deleteHall: async (id: number): Promise<void> => {
    await axiosInstance.delete(`/admin/halls/${id}`);
  },

  // Seat endpoints
  /**
   * PUT /api/admin/seats/{id}
   * Update seat (admin)
   */
  updateSeat: async (
    id: number,
    data: UpdateSeatRequest
  ): Promise<SeatResponse> => {
    const response = await axiosInstance.put<ApiResponse<SeatResponse>>(
      `/admin/seats/${id}`,
      data
    );
    return response.data.data;
  },
};
