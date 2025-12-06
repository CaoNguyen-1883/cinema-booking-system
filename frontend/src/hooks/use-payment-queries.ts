import { useQuery, useMutation } from '@tanstack/react-query';
import { paymentService } from '@/api';
import type { CreatePaymentRequest } from '@/types';

/**
 * Payment Query Hooks
 */

// Query keys
export const paymentKeys = {
  all: ['payments'] as const,
  status: (bookingCode: string) =>
    [...paymentKeys.all, 'status', bookingCode] as const,
};

/**
 * Get payment status
 */
export const usePaymentStatus = (bookingCode: string, enabled = true) => {
  return useQuery({
    queryKey: paymentKeys.status(bookingCode),
    queryFn: () => paymentService.getPaymentStatus(bookingCode),
    enabled: enabled && bookingCode.length > 0,
    staleTime: 10 * 1000, // 10 seconds
    refetchInterval: 5 * 1000, // Refetch every 5 seconds while pending
  });
};

/**
 * Create payment URL
 */
export const useCreatePayment = () => {
  return useMutation({
    mutationFn: (data: CreatePaymentRequest) =>
      paymentService.createPayment(data),
    onSuccess: (data) => {
      // Redirect to payment URL
      window.location.href = data.paymentUrl;
    },
  });
};
