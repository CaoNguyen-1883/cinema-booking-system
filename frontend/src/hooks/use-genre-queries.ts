import { useQuery } from '@tanstack/react-query';
import { genreService } from '@/api';

/**
 * Genre Query Hooks
 */

// Query keys
export const genreKeys = {
  all: ['genres'] as const,
  list: () => [...genreKeys.all, 'list'] as const,
  details: () => [...genreKeys.all, 'detail'] as const,
  detail: (id: number) => [...genreKeys.details(), id] as const,
};

/**
 * Get all genres
 */
export const useGenres = () => {
  return useQuery({
    queryKey: genreKeys.list(),
    queryFn: genreService.getAllGenres,
    staleTime: 10 * 60 * 1000, // 10 minutes - genres rarely change
  });
};

/**
 * Get genre by ID
 */
export const useGenre = (id: number, enabled = true) => {
  return useQuery({
    queryKey: genreKeys.detail(id),
    queryFn: () => genreService.getGenreById(id),
    enabled,
    staleTime: 10 * 60 * 1000,
  });
};
