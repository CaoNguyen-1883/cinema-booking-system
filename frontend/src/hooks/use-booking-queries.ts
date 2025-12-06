import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { bookingService } from '@/api';
import type {
  CreateBookingRequest,
  CheckoutRequest,
  ConfirmPaymentRequest,
} from '@/types';

/**
 * Booking Query Hooks
 */

// Query keys
export const bookingKeys = {
  all: ['bookings'] as const,
  lists: () => [...bookingKeys.all, 'list'] as const,
  list: (filters: Record<string, unknown>) =>
    [...bookingKeys.lists(), filters] as const,
  details: () => [...bookingKeys.all, 'detail'] as const,
  detail: (code: string) => [...bookingKeys.details(), code] as const,
  myBookings: (page?: number, size?: number) =>
    [...bookingKeys.all, 'my-bookings', page, size] as const,
};

/**
 * Get booking by code
 */
export const useBooking = (bookingCode: string, enabled = true) => {
  return useQuery({
    queryKey: bookingKeys.detail(bookingCode),
    queryFn: () => bookingService.getBookingByCode(bookingCode),
    enabled: enabled && bookingCode.length > 0,
    staleTime: 30 * 1000, // 30 seconds
    refetchInterval: 60 * 1000, // Refetch every minute for status updates
  });
};

/**
 * Get user's bookings
 */
export const useMyBookings = (page = 0, size = 10) => {
  return useQuery({
    queryKey: bookingKeys.myBookings(page, size),
    queryFn: () => bookingService.getMyBookings({ page, size }),
    staleTime: 1 * 60 * 1000,
  });
};

/**
 * Get all bookings (admin)
 */
export const useAllBookings = (status?: string, page = 0, size = 20) => {
  return useQuery({
    queryKey: bookingKeys.list({ status, page, size }),
    queryFn: () => bookingService.getAllBookings({ status, page, size }),
    staleTime: 1 * 60 * 1000,
  });
};

/**
 * Create booking (lock seats)
 */
export const useCreateBooking = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateBookingRequest) =>
      bookingService.createBooking(data),
    onSuccess: (data) => {
      queryClient.setQueryData(bookingKeys.detail(data.bookingCode), data);
      queryClient.invalidateQueries({ queryKey: bookingKeys.myBookings() });
    },
  });
};

/**
 * Checkout booking
 */
export const useCheckout = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CheckoutRequest) => bookingService.checkout(data),
    onSuccess: (data) => {
      queryClient.setQueryData(bookingKeys.detail(data.bookingCode), data);
    },
  });
};

/**
 * Confirm payment
 */
export const useConfirmPayment = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: ConfirmPaymentRequest) =>
      bookingService.confirmPayment(data),
    onSuccess: (data) => {
      queryClient.setQueryData(bookingKeys.detail(data.bookingCode), data);
      queryClient.invalidateQueries({ queryKey: bookingKeys.myBookings() });
    },
  });
};

/**
 * Cancel booking
 */
export const useCancelBooking = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (bookingCode: string) =>
      bookingService.cancelBooking(bookingCode),
    onSuccess: (_, bookingCode) => {
      queryClient.invalidateQueries({
        queryKey: bookingKeys.detail(bookingCode),
      });
      queryClient.invalidateQueries({ queryKey: bookingKeys.myBookings() });
    },
  });
};

/**
 * Admin cancel booking
 */
export const useAdminCancelBooking = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (bookingCode: string) =>
      bookingService.adminCancelBooking(bookingCode),
    onSuccess: (_, bookingCode) => {
      queryClient.invalidateQueries({
        queryKey: bookingKeys.detail(bookingCode),
      });
      queryClient.invalidateQueries({ queryKey: bookingKeys.lists() });
    },
  });
};
