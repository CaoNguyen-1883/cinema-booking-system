import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { cinemaService } from '@/api';
import type {
  CreateCinemaRequest,
  UpdateCinemaRequest,
  CreateHallRequest,
  UpdateHallRequest,
} from '@/types';

/**
 * Cinema Query Hooks
 */

// Query keys
export const cinemaKeys = {
  all: ['cinemas'] as const,
  lists: () => [...cinemaKeys.all, 'list'] as const,
  list: (filters: Record<string, unknown>) =>
    [...cinemaKeys.lists(), filters] as const,
  details: () => [...cinemaKeys.all, 'detail'] as const,
  detail: (id: number) => [...cinemaKeys.details(), id] as const,
  withHalls: (id: number) => [...cinemaKeys.details(), id, 'halls'] as const,
  active: () => [...cinemaKeys.all, 'active'] as const,
  cities: () => [...cinemaKeys.all, 'cities'] as const,
  byCity: (city: string) => [...cinemaKeys.all, 'city', city] as const,
};

export const hallKeys = {
  all: ['halls'] as const,
  details: () => [...hallKeys.all, 'detail'] as const,
  detail: (id: number) => [...hallKeys.details(), id] as const,
  withSeats: (id: number) => [...hallKeys.details(), id, 'seats'] as const,
  byCinema: (cinemaId: number) => [...hallKeys.all, 'cinema', cinemaId] as const,
};

/**
 * Get cinema by ID
 */
export const useCinema = (id: number, enabled = true) => {
  return useQuery({
    queryKey: cinemaKeys.detail(id),
    queryFn: () => cinemaService.getCinemaById(id),
    enabled,
    staleTime: 5 * 60 * 1000,
  });
};

/**
 * Get cinema with halls
 */
export const useCinemaWithHalls = (id: number, enabled = true) => {
  return useQuery({
    queryKey: cinemaKeys.withHalls(id),
    queryFn: () => cinemaService.getCinemaWithHalls(id),
    enabled,
    staleTime: 5 * 60 * 1000,
  });
};

/**
 * Get all cinemas
 */
export const useCinemas = (page = 0, size = 20) => {
  return useQuery({
    queryKey: cinemaKeys.list({ page, size }),
    queryFn: () => cinemaService.getAllCinemas({ page, size }),
    staleTime: 5 * 60 * 1000,
  });
};

/**
 * Get active cinemas
 */
export const useActiveCinemas = () => {
  return useQuery({
    queryKey: cinemaKeys.active(),
    queryFn: cinemaService.getActiveCinemas,
    staleTime: 5 * 60 * 1000,
  });
};

/**
 * Get cinemas by city
 */
export const useCinemasByCity = (city: string, page = 0, size = 20) => {
  return useQuery({
    queryKey: cinemaKeys.byCity(city),
    queryFn: () => cinemaService.getCinemasByCity(city, { page, size }),
    enabled: city.length > 0,
    staleTime: 5 * 60 * 1000,
  });
};

/**
 * Get distinct cities
 */
export const useCities = () => {
  return useQuery({
    queryKey: cinemaKeys.cities(),
    queryFn: cinemaService.getDistinctCities,
    staleTime: 10 * 60 * 1000, // 10 minutes
  });
};

/**
 * Search cinemas
 */
export const useSearchCinemas = (keyword: string, page = 0, size = 20) => {
  return useQuery({
    queryKey: cinemaKeys.list({ keyword, page, size }),
    queryFn: () => cinemaService.searchCinemas({ keyword, page, size }),
    enabled: keyword.length > 0,
    staleTime: 2 * 60 * 1000,
  });
};

/**
 * Get hall by ID
 */
export const useHall = (id: number, enabled = true) => {
  return useQuery({
    queryKey: hallKeys.detail(id),
    queryFn: () => cinemaService.getHallById(id),
    enabled,
    staleTime: 5 * 60 * 1000,
  });
};

/**
 * Get hall with seats
 */
export const useHallWithSeats = (id: number, enabled = true) => {
  return useQuery({
    queryKey: hallKeys.withSeats(id),
    queryFn: () => cinemaService.getHallWithSeats(id),
    enabled,
    staleTime: 5 * 60 * 1000,
  });
};

/**
 * Get halls by cinema
 */
export const useHallsByCinema = (cinemaId: number) => {
  return useQuery({
    queryKey: hallKeys.byCinema(cinemaId),
    queryFn: () => cinemaService.getHallsByCinema(cinemaId),
    staleTime: 5 * 60 * 1000,
  });
};

/**
 * Create cinema (admin)
 */
export const useCreateCinema = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateCinemaRequest) => cinemaService.createCinema(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: cinemaKeys.lists() });
      queryClient.invalidateQueries({ queryKey: cinemaKeys.cities() });
    },
  });
};

/**
 * Update cinema (admin)
 */
export const useUpdateCinema = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateCinemaRequest }) =>
      cinemaService.updateCinema(id, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: cinemaKeys.detail(variables.id) });
      queryClient.invalidateQueries({ queryKey: cinemaKeys.lists() });
    },
  });
};

/**
 * Delete cinema (admin)
 */
export const useDeleteCinema = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => cinemaService.deleteCinema(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: cinemaKeys.lists() });
    },
  });
};

/**
 * Create hall (admin)
 */
export const useCreateHall = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateHallRequest) => cinemaService.createHall(data),
    onSuccess: (data) => {
      queryClient.invalidateQueries({
        queryKey: hallKeys.byCinema(data.cinemaId),
      });
      queryClient.invalidateQueries({
        queryKey: cinemaKeys.withHalls(data.cinemaId),
      });
    },
  });
};

/**
 * Update hall (admin)
 */
export const useUpdateHall = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateHallRequest }) =>
      cinemaService.updateHall(id, data),
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({ queryKey: hallKeys.detail(variables.id) });
      queryClient.invalidateQueries({
        queryKey: hallKeys.byCinema(data.cinemaId),
      });
    },
  });
};

/**
 * Delete hall (admin)
 */
export const useDeleteHall = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => cinemaService.deleteHall(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: hallKeys.all });
    },
  });
};
