import { useMutation } from '@tanstack/react-query';
import { useAuthStore } from '@/stores';
import { axiosInstance } from '@/api';
import type { LoginRequest, LoginResponse, RegisterRequest } from '@/types';

export function useLogin() {
  const setAuth = useAuthStore((state) => state.setAuth);

  return useMutation({
    mutationFn: async (credentials: LoginRequest) => {
      const response = await axiosInstance.post<LoginResponse>('/auth/login', credentials);
      return response.data;
    },
    onSuccess: (data) => {
      setAuth(data.user, data.accessToken);
    },
  });
}

export function useRegister() {
  return useMutation({
    mutationFn: async (data: RegisterRequest) => {
      const response = await axiosInstance.post('/auth/register', data);
      return response.data;
    },
  });
}

export function useLogout() {
  const logout = useAuthStore((state) => state.logout);

  return useMutation({
    mutationFn: async () => {
      await axiosInstance.post('/auth/logout');
    },
    onSettled: () => {
      logout();
    },
  });
}
