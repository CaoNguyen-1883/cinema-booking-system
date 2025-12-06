import axiosInstance from './axios';
import type {
  LoginRequest,
  RegisterRequest,
  RefreshTokenRequest,
  AuthResponse,
  ApiResponse,
} from '@/types';

/**
 * Authentication Service
 * Handles all auth-related API calls
 */
export const authService = {
  /**
   * POST /api/auth/login
   * User login
   */
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const response = await axiosInstance.post<ApiResponse<AuthResponse>>(
      '/auth/login',
      data
    );
    return response.data.data;
  },

  /**
   * POST /api/auth/register
   * User registration
   */
  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await axiosInstance.post<ApiResponse<AuthResponse>>(
      '/auth/register',
      data
    );
    return response.data.data;
  },

  /**
   * POST /api/auth/refresh
   * Refresh access token
   */
  refreshToken: async (data: RefreshTokenRequest): Promise<AuthResponse> => {
    const response = await axiosInstance.post<ApiResponse<AuthResponse>>(
      '/auth/refresh',
      data
    );
    return response.data.data;
  },

  /**
   * POST /api/auth/logout
   * User logout
   */
  logout: async (): Promise<void> => {
    await axiosInstance.post('/auth/logout');
  },

  /**
   * GET /api/auth/me
   * Get current user info
   */
  getCurrentUser: async (): Promise<AuthResponse['user']> => {
    const response = await axiosInstance.get<ApiResponse<AuthResponse['user']>>(
      '/auth/me'
    );
    return response.data.data;
  },
};
