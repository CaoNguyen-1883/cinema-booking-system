// Movie Module Types

export interface MovieResponse {
  id: number;
  title: string;
  originalTitle?: string;
  description?: string;
  duration: number;
  releaseDate: string;
  endDate?: string;
  director?: string;
  castMembers?: string;
  language?: string;
  country?: string;
  rating: 'P' | 'C13' | 'C16' | 'C18';
  trailerUrl?: string;
  posterUrl?: string;
  backdropUrl?: string;
  status: 'COMING_SOON' | 'NOW_SHOWING' | 'ENDED';
  genres: GenreResponse[];
  averageRating?: number;
  totalReviews?: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateMovieRequest {
  title: string;
  originalTitle?: string;
  description?: string;
  duration: number;
  releaseDate: string;
  endDate?: string;
  director?: string;
  castMembers?: string;
  language?: string;
  country?: string;
  rating: string;
  trailerUrl?: string;
  posterUrl?: string;
  backdropUrl?: string;
  status?: string;
  genreIds?: number[];
}

export interface UpdateMovieRequest {
  title?: string;
  originalTitle?: string;
  description?: string;
  duration?: number;
  releaseDate?: string;
  endDate?: string;
  director?: string;
  castMembers?: string;
  language?: string;
  country?: string;
  rating?: string;
  trailerUrl?: string;
  posterUrl?: string;
  backdropUrl?: string;
  status?: string;
  genreIds?: number[];
}

export interface GenreResponse {
  id: number;
  name: string;
  description?: string;
}

export interface CreateGenreRequest {
  name: string;
  description?: string;
}
