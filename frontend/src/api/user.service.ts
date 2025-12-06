import axiosInstance from './axios';
import type {
  UserResponse,
  UpdateProfileRequest,
  ChangePasswordRequest,
  AdminUpdateUserRequest,
  ApiResponse,
  PaginatedResponse,
} from '@/types';

/**
 * User Service
 * Handles all user-related API calls
 */
export const userService = {
  /**
   * GET /api/users/profile
   * Get current user profile
   */
  getProfile: async (): Promise<UserResponse> => {
    const response = await axiosInstance.get<ApiResponse<UserResponse>>(
      '/users/profile'
    );
    return response.data.data;
  },

  /**
   * PUT /api/users/profile
   * Update user profile
   */
  updateProfile: async (data: UpdateProfileRequest): Promise<UserResponse> => {
    const response = await axiosInstance.put<ApiResponse<UserResponse>>(
      '/users/profile',
      data
    );
    return response.data.data;
  },

  /**
   * PUT /api/users/change-password
   * Change password
   */
  changePassword: async (data: ChangePasswordRequest): Promise<void> => {
    await axiosInstance.put('/users/change-password', data);
  },

  /**
   * GET /api/users/{id}
   * Get user by ID (admin)
   */
  getUserById: async (id: number): Promise<UserResponse> => {
    const response = await axiosInstance.get<ApiResponse<UserResponse>>(
      `/users/${id}`
    );
    return response.data.data;
  },

  /**
   * GET /api/users/username/{username}
   * Get user by username (admin)
   */
  getUserByUsername: async (username: string): Promise<UserResponse> => {
    const response = await axiosInstance.get<ApiResponse<UserResponse>>(
      `/users/username/${username}`
    );
    return response.data.data;
  },

  /**
   * GET /api/users
   * Get all users with pagination (admin)
   */
  getAllUsers: async (params: {
    page?: number;
    size?: number;
  }): Promise<PaginatedResponse<UserResponse>> => {
    const response = await axiosInstance.get<
      ApiResponse<PaginatedResponse<UserResponse>>
    >('/users', { params });
    return response.data.data;
  },

  /**
   * PUT /api/users/{id}
   * Admin update user
   */
  adminUpdateUser: async (
    id: number,
    data: AdminUpdateUserRequest
  ): Promise<UserResponse> => {
    const response = await axiosInstance.put<ApiResponse<UserResponse>>(
      `/users/${id}`,
      data
    );
    return response.data.data;
  },

  /**
   * POST /api/users/{id}/add-points
   * Add points to user (admin)
   */
  addPoints: async (id: number, points: number): Promise<void> => {
    await axiosInstance.post(`/users/${id}/add-points`, { points });
  },

  /**
   * POST /api/users/{id}/deduct-points
   * Deduct points from user (admin)
   */
  deductPoints: async (id: number, points: number): Promise<void> => {
    await axiosInstance.post(`/users/${id}/deduct-points`, { points });
  },

  /**
   * POST /api/users/{id}/lock
   * Lock user account (admin)
   */
  lockUser: async (id: number): Promise<void> => {
    await axiosInstance.post(`/users/${id}/lock`);
  },

  /**
   * POST /api/users/{id}/unlock
   * Unlock user account (admin)
   */
  unlockUser: async (id: number): Promise<void> => {
    await axiosInstance.post(`/users/${id}/unlock`);
  },

  /**
   * PUT /api/admin/users/{id}/status?status=ACTIVE
   * Update user status (ACTIVE, INACTIVE, BANNED) (admin)
   */
  updateUserStatus: async (
    id: number,
    status: string
  ): Promise<UserResponse> => {
    const response = await axiosInstance.put<ApiResponse<UserResponse>>(
      `/admin/users/${id}/status`,
      null,
      { params: { status } }
    );
    return response.data.data;
  },

  /**
   * PUT /api/admin/users/{id}/role?role=ADMIN
   * Update user role (CUSTOMER, STAFF, ADMIN) (admin)
   */
  updateUserRole: async (id: number, role: string): Promise<UserResponse> => {
    const response = await axiosInstance.put<ApiResponse<UserResponse>>(
      `/admin/users/${id}/role`,
      null,
      { params: { role } }
    );
    return response.data.data;
  },
};
