import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { movieService } from '@/api';
import type {
  CreateMovieRequest,
  UpdateMovieRequest,
} from '@/types';

/**
 * Movie Query Hooks
 */

// Query keys
export const movieKeys = {
  all: ['movies'] as const,
  lists: () => [...movieKeys.all, 'list'] as const,
  list: (filters: Record<string, unknown>) =>
    [...movieKeys.lists(), filters] as const,
  details: () => [...movieKeys.all, 'detail'] as const,
  detail: (id: number) => [...movieKeys.details(), id] as const,
  nowShowing: () => [...movieKeys.all, 'now-showing'] as const,
  comingSoon: () => [...movieKeys.all, 'coming-soon'] as const,
  search: (keyword: string) => [...movieKeys.all, 'search', keyword] as const,
  byGenre: (genreId: number) => [...movieKeys.all, 'genre', genreId] as const,
  byStatus: (status: string) => [...movieKeys.all, 'status', status] as const,
};

/**
 * Get movie by ID
 */
export const useMovie = (id: number, enabled = true) => {
  return useQuery({
    queryKey: movieKeys.detail(id),
    queryFn: () => movieService.getMovieById(id),
    enabled,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
};

/**
 * Get all movies with pagination
 */
export const useMovies = (page = 0, size = 20) => {
  return useQuery({
    queryKey: movieKeys.list({ page, size }),
    queryFn: () => movieService.getAllMovies({ page, size }),
    staleTime: 2 * 60 * 1000, // 2 minutes
  });
};

/**
 * Get now showing movies
 */
export const useNowShowingMovies = () => {
  return useQuery({
    queryKey: movieKeys.nowShowing(),
    queryFn: movieService.getNowShowingMovies,
    staleTime: 5 * 60 * 1000,
  });
};

/**
 * Get coming soon movies
 */
export const useComingSoonMovies = () => {
  return useQuery({
    queryKey: movieKeys.comingSoon(),
    queryFn: movieService.getComingSoonMovies,
    staleTime: 5 * 60 * 1000,
  });
};

/**
 * Search movies
 */
export const useSearchMovies = (keyword: string, page = 0, size = 20) => {
  return useQuery({
    queryKey: movieKeys.search(keyword),
    queryFn: () => movieService.searchMovies({ keyword, page, size }),
    enabled: keyword.length > 0,
    staleTime: 2 * 60 * 1000,
  });
};

/**
 * Get movies by genre
 */
export const useMoviesByGenre = (genreId: number, page = 0, size = 20) => {
  return useQuery({
    queryKey: movieKeys.byGenre(genreId),
    queryFn: () => movieService.getMoviesByGenre(genreId, { page, size }),
    staleTime: 2 * 60 * 1000,
  });
};

/**
 * Get movies by status
 */
export const useMoviesByStatus = (status: string, page = 0, size = 20) => {
  return useQuery({
    queryKey: movieKeys.byStatus(status),
    queryFn: () => movieService.getMoviesByStatus(status, { page, size }),
    staleTime: 2 * 60 * 1000,
  });
};

/**
 * Create movie (admin)
 */
export const useCreateMovie = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateMovieRequest) => movieService.createMovie(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: movieKeys.lists() });
    },
  });
};

/**
 * Update movie (admin)
 */
export const useUpdateMovie = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateMovieRequest }) =>
      movieService.updateMovie(id, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: movieKeys.detail(variables.id) });
      queryClient.invalidateQueries({ queryKey: movieKeys.lists() });
    },
  });
};

/**
 * Delete movie (admin)
 */
export const useDeleteMovie = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => movieService.deleteMovie(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: movieKeys.lists() });
    },
  });
};
