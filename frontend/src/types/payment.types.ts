// Payment Module Types

export interface PaymentResponse {
  id: number;
  bookingId: number;
  bookingCode: string;
  paymentMethod: 'VNPAY' | 'MOMO' | 'ZALOPAY';
  amount: number;
  status: 'PENDING' | 'COMPLETED' | 'FAILED' | 'CANCELLED' | 'REFUNDED';
  transactionId?: string;
  paymentUrl?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreatePaymentRequest {
  bookingCode: string;
  paymentMethod: 'VNPAY' | 'MOMO' | 'ZALOPAY';
}

export interface PaymentUrlResponse {
  paymentUrl: string;
  bookingCode: string;
  amount: number;
  expiresAt: string;
}

export interface PaymentResultResponse {
  success: boolean;
  bookingCode: string;
  transactionId?: string;
  amount: number;
  message: string;
}

export interface PaymentStatusResponse {
  bookingCode: string;
  paymentStatus: 'PENDING' | 'COMPLETED' | 'FAILED' | 'CANCELLED' | 'REFUNDED' | 'NO_PAYMENT';
  amount?: number;
  transactionId?: string;
  paidAt?: string;
}
