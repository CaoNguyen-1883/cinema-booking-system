// Cinema Module Types

export interface CinemaResponse {
  id: number;
  name: string;
  address: string;
  city: string;
  district?: string;
  phoneNumber: string;
  email?: string;
  openingHours?: string;
  facilities?: string;
  latitude?: number;
  longitude?: number;
  mapUrl?: string;
  imageUrl?: string;
  status: 'ACTIVE' | 'INACTIVE' | 'MAINTENANCE';
  halls?: HallResponse[];
  createdAt: string;
  updatedAt: string;
}

export interface CreateCinemaRequest {
  name: string;
  address: string;
  city: string;
  district?: string;
  phoneNumber: string;
  email?: string;
  openingHours?: string;
  facilities?: string;
  latitude?: number;
  longitude?: number;
  mapUrl?: string;
  imageUrl?: string;
}

export interface UpdateCinemaRequest {
  name?: string;
  address?: string;
  city?: string;
  district?: string;
  phoneNumber?: string;
  email?: string;
  openingHours?: string;
  facilities?: string;
  latitude?: number;
  longitude?: number;
  mapUrl?: string;
  imageUrl?: string;
  status?: 'ACTIVE' | 'INACTIVE' | 'MAINTENANCE';
}

export interface HallResponse {
  id: number;
  name: string;
  cinemaId: number;
  cinemaName?: string;
  totalSeats: number;
  totalRows: number;
  seatsPerRow: number;
  screenType?: string;
  soundSystem?: string;
  status: 'ACTIVE' | 'INACTIVE' | 'MAINTENANCE';
  seats?: SeatResponse[];
  createdAt: string;
  updatedAt: string;
}

export interface CreateHallRequest {
  name: string;
  cinemaId: number;
  totalRows: number;
  seatsPerRow: number;
  screenType?: string;
  soundSystem?: string;
}

export interface UpdateHallRequest {
  name?: string;
  screenType?: string;
  soundSystem?: string;
  status?: 'ACTIVE' | 'INACTIVE' | 'MAINTENANCE';
}

export interface SeatResponse {
  id: number;
  hallId: number;
  rowName: string;
  seatNumber: number;
  seatType: 'NORMAL' | 'VIP' | 'COUPLE';
  status: 'ACTIVE' | 'INACTIVE' | 'MAINTENANCE';
}

export interface UpdateSeatRequest {
  seatType?: 'NORMAL' | 'VIP' | 'COUPLE';
  status?: 'ACTIVE' | 'INACTIVE' | 'MAINTENANCE';
}
