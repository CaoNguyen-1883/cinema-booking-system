import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { showService } from '@/api';
import type {
  CreateShowRequest,
  UpdateShowRequest,
} from '@/types';

/**
 * Show Query Hooks
 */

// Query keys
export const showKeys = {
  all: ['shows'] as const,
  lists: () => [...showKeys.all, 'list'] as const,
  list: (filters: Record<string, unknown>) =>
    [...showKeys.lists(), filters] as const,
  details: () => [...showKeys.all, 'detail'] as const,
  detail: (id: number) => [...showKeys.details(), id] as const,
  seats: (showId: number) => [...showKeys.all, 'seats', showId] as const,
  byMovie: (movieId: number, filters?: Record<string, unknown>) =>
    [...showKeys.all, 'movie', movieId, filters] as const,
  byCinema: (cinemaId: number, filters?: Record<string, unknown>) =>
    [...showKeys.all, 'cinema', cinemaId, filters] as const,
};

/**
 * Get show by ID
 */
export const useShow = (id: number, enabled = true) => {
  return useQuery({
    queryKey: showKeys.detail(id),
    queryFn: () => showService.getShowById(id),
    enabled,
    staleTime: 1 * 60 * 1000, // 1 minute
  });
};

/**
 * Get show seats
 */
export const useShowSeats = (showId: number, enabled = true) => {
  return useQuery({
    queryKey: showKeys.seats(showId),
    queryFn: () => showService.getShowSeats(showId),
    enabled,
    staleTime: 30 * 1000, // 30 seconds - frequent updates for seat availability
    refetchInterval: 30 * 1000, // Auto-refetch every 30 seconds
  });
};

/**
 * Get shows by movie
 */
export const useShowsByMovie = (
  movieId: number,
  params?: { startDate?: string; endDate?: string }
) => {
  return useQuery({
    queryKey: showKeys.byMovie(movieId, params),
    queryFn: () => showService.getShowsByMovie(movieId, params),
    staleTime: 2 * 60 * 1000,
  });
};

/**
 * Get shows by cinema
 */
export const useShowsByCinema = (
  cinemaId: number,
  params?: { date?: string }
) => {
  return useQuery({
    queryKey: showKeys.byCinema(cinemaId, params),
    queryFn: () => showService.getShowsByCinema(cinemaId, params),
    staleTime: 2 * 60 * 1000,
  });
};

/**
 * Get all shows with pagination
 */
export const useShows = (status?: string, page = 0, size = 20) => {
  return useQuery({
    queryKey: showKeys.list({ status, page, size }),
    queryFn: () => showService.getAllShows({ status, page, size }),
    staleTime: 2 * 60 * 1000,
  });
};

/**
 * Create show (admin)
 */
export const useCreateShow = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateShowRequest) => showService.createShow(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: showKeys.lists() });
    },
  });
};

/**
 * Update show (admin)
 */
export const useUpdateShow = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateShowRequest }) =>
      showService.updateShow(id, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: showKeys.detail(variables.id) });
      queryClient.invalidateQueries({ queryKey: showKeys.lists() });
    },
  });
};

/**
 * Delete show (admin)
 */
export const useDeleteShow = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => showService.deleteShow(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: showKeys.lists() });
    },
  });
};

/**
 * Cancel show (admin)
 */
export const useCancelShow = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => showService.cancelShow(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: showKeys.detail(id) });
      queryClient.invalidateQueries({ queryKey: showKeys.lists() });
    },
  });
};
