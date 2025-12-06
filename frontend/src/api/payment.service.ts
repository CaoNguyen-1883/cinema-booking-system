import axiosInstance from './axios';
import type {
  CreatePaymentRequest,
  PaymentUrlResponse,
  PaymentStatusResponse,
  ApiResponse,
} from '@/types';

/**
 * Payment Service
 * Handles all payment-related API calls
 */
export const paymentService = {
  /**
   * POST /api/payments/create
   * Create payment URL
   */
  createPayment: async (
    data: CreatePaymentRequest
  ): Promise<PaymentUrlResponse> => {
    const response = await axiosInstance.post<ApiResponse<PaymentUrlResponse>>(
      '/payments/create',
      data
    );
    return response.data.data;
  },

  /**
   * GET /api/payments/status/{bookingCode}
   * Get payment status
   */
  getPaymentStatus: async (
    bookingCode: string
  ): Promise<PaymentStatusResponse> => {
    const response = await axiosInstance.get<
      ApiResponse<PaymentStatusResponse>
    >(`/payments/status/${bookingCode}`);
    return response.data.data;
  },
};
