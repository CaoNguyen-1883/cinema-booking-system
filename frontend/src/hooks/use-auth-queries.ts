import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { authService } from '@/api';
import { useAuthStore } from '@/stores/auth.store';
import type {
  LoginRequest,
  RegisterRequest,
  AuthResponse,
} from '@/types';

/**
 * Auth Query Hooks
 */

// Query keys
export const authKeys = {
  all: ['auth'] as const,
  currentUser: () => [...authKeys.all, 'current-user'] as const,
};

/**
 * Get current user
 */
export const useCurrentUser = () => {
  const { isAuthenticated } = useAuthStore();

  return useQuery({
    queryKey: authKeys.currentUser(),
    queryFn: authService.getCurrentUser,
    enabled: isAuthenticated,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
};

/**
 * Login mutation
 */
export const useLogin = () => {
  const { setAuth } = useAuthStore();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: LoginRequest) => authService.login(data),
    onSuccess: (data: AuthResponse) => {
      // Store accessToken in memory only (Zustand state)
      // refreshToken is stored in httpOnly cookie by backend
      setAuth(data.user, data.accessToken);
      queryClient.setQueryData(authKeys.currentUser(), data.user);
    },
  });
};

/**
 * Register mutation
 */
export const useRegister = () => {
  const { setAuth } = useAuthStore();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: RegisterRequest) => authService.register(data),
    onSuccess: (data: AuthResponse) => {
      // Store accessToken in memory only (Zustand state)
      // refreshToken is stored in httpOnly cookie by backend
      setAuth(data.user, data.accessToken);
      queryClient.setQueryData(authKeys.currentUser(), data.user);
    },
  });
};

/**
 * Logout mutation
 */
export const useLogout = () => {
  const { logout } = useAuthStore();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: authService.logout,
    onSuccess: () => {
      // Clear memory state
      // Backend will clear httpOnly cookie
      logout();
      queryClient.clear();
      window.location.href = '/';
    },
  });
};
