// User Module Types

export interface UserResponse {
  id: number;
  username: string;
  email: string;
  fullName: string;
  phoneNumber?: string;
  avatarUrl?: string;
  points: number;
  role: string; // "ADMIN" or "CUSTOMER"
  status: string; // "ACTIVE", "INACTIVE", or "LOCKED"
  lastLoginAt?: string;
  createdAt: string;
}

export interface UpdateProfileRequest {
  fullName?: string;
  phoneNumber?: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface AdminUpdateUserRequest {
  fullName?: string;
  phoneNumber?: string;
  role?: 'ADMIN' | 'CUSTOMER';
  status?: 'ACTIVE' | 'INACTIVE' | 'LOCKED';
  points?: number;
}
