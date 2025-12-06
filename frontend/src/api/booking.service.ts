import axiosInstance from './axios';
import type {
  BookingResponse,
  CreateBookingRequest,
  CheckoutRequest,
  ConfirmPaymentRequest,
  ApiResponse,
  PaginatedResponse,
} from '@/types';

/**
 * Booking Service
 * Handles all booking-related API calls
 */
export const bookingService = {
  /**
   * POST /api/bookings
   * Create booking (lock seats)
   */
  createBooking: async (
    data: CreateBookingRequest
  ): Promise<BookingResponse> => {
    const response = await axiosInstance.post<ApiResponse<BookingResponse>>(
      '/bookings',
      data
    );
    return response.data.data;
  },

  /**
   * GET /api/bookings/{bookingCode}
   * Get booking by code
   */
  getBookingByCode: async (bookingCode: string): Promise<BookingResponse> => {
    const response = await axiosInstance.get<ApiResponse<BookingResponse>>(
      `/bookings/${bookingCode}`
    );
    return response.data.data;
  },

  /**
   * POST /api/bookings/checkout
   * Checkout booking (apply points/discounts)
   */
  checkout: async (data: CheckoutRequest): Promise<BookingResponse> => {
    const response = await axiosInstance.post<ApiResponse<BookingResponse>>(
      '/bookings/checkout',
      data
    );
    return response.data.data;
  },

  /**
   * POST /api/bookings/confirm-payment
   * Confirm payment
   */
  confirmPayment: async (
    data: ConfirmPaymentRequest
  ): Promise<BookingResponse> => {
    const response = await axiosInstance.post<ApiResponse<BookingResponse>>(
      '/bookings/confirm-payment',
      data
    );
    return response.data.data;
  },

  /**
   * PUT /api/bookings/{bookingCode}/cancel
   * Cancel booking
   */
  cancelBooking: async (bookingCode: string): Promise<void> => {
    await axiosInstance.put(`/bookings/${bookingCode}/cancel`);
  },

  /**
   * GET /api/bookings/my-bookings
   * Get user's bookings
   */
  getMyBookings: async (params: {
    page?: number;
    size?: number;
  }): Promise<PaginatedResponse<BookingResponse>> => {
    const response = await axiosInstance.get<
      ApiResponse<PaginatedResponse<BookingResponse>>
    >('/bookings/my-bookings', { params });
    return response.data.data;
  },

  /**
   * GET /api/bookings/admin/all
   * Get all bookings (admin)
   */
  getAllBookings: async (params: {
    status?: string;
    page?: number;
    size?: number;
  }): Promise<PaginatedResponse<BookingResponse>> => {
    const response = await axiosInstance.get<
      ApiResponse<PaginatedResponse<BookingResponse>>
    >('/bookings/admin/all', { params });
    return response.data.data;
  },

  /**
   * PUT /api/bookings/admin/{bookingCode}/cancel
   * Cancel booking (admin)
   */
  adminCancelBooking: async (bookingCode: string): Promise<void> => {
    await axiosInstance.put(`/bookings/admin/${bookingCode}/cancel`);
  },
};
