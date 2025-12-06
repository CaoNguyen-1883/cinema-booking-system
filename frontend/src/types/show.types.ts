// Show Module Types

export interface ShowResponse {
  id: number;
  movieId: number;
  movieTitle: string;
  hallId: number;
  hallName: string;
  cinemaId: number;
  cinemaName: string;
  showDate: string;
  startTime: string;
  endTime: string;
  basePrice: number;
  status: 'SCHEDULED' | 'SHOWING' | 'COMPLETED' | 'CANCELLED';
  availableSeats: number;
  totalSeats: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateShowRequest {
  movieId: number;
  hallId: number;
  showDate: string;
  startTime: string;
  basePrice: number;
}

export interface UpdateShowRequest {
  showDate?: string;
  startTime?: string;
  basePrice?: number;
  status?: 'SCHEDULED' | 'SHOWING' | 'COMPLETED' | 'CANCELLED';
}

export interface ShowSeatResponse {
  id: number;
  showId: number;
  seatId: number;
  rowName: string;
  seatNumber: number;
  seatType: 'NORMAL' | 'VIP' | 'COUPLE';
  price: number;
  status: 'AVAILABLE' | 'LOCKED' | 'SOLD';
  lockedUntil?: string;
  lockedByUserId?: number;
}
