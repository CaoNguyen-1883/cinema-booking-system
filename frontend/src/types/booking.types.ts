// Booking Module Types

export interface BookingResponse {
  id: number;
  bookingCode: string;
  userId: number;
  userName: string;
  userEmail: string;
  showId: number;
  showDate: string;
  showStartTime: string;
  movieTitle: string;
  cinemaName: string;
  hallName: string;
  totalAmount: number;
  discountAmount: number;
  pointsUsed: number;
  finalAmount: number;
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'EXPIRED';
  seats: BookingSeatResponse[];
  expiresAt?: string;
  confirmedAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateBookingRequest {
  showId: number;
  seatIds: number[];
}

export interface CheckoutRequest {
  bookingCode: string;
  pointsToUse?: number;
}

export interface ConfirmPaymentRequest {
  bookingCode: string;
  transactionId: string;
}

export interface BookingSeatResponse {
  id: number;
  seatId: number;
  rowName: string;
  seatNumber: number;
  seatType: 'NORMAL' | 'VIP' | 'COUPLE';
  price: number;
}
