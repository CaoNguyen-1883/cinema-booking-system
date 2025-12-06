import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { userService } from '@/api';
import type {
  UpdateProfileRequest,
  ChangePasswordRequest,
  AdminUpdateUserRequest,
} from '@/types';

/**
 * User Query Hooks
 */

// Query keys
export const userKeys = {
  all: ['users'] as const,
  lists: () => [...userKeys.all, 'list'] as const,
  list: (filters: Record<string, unknown>) =>
    [...userKeys.lists(), filters] as const,
  details: () => [...userKeys.all, 'detail'] as const,
  detail: (id: number) => [...userKeys.details(), id] as const,
  profile: () => [...userKeys.all, 'profile'] as const,
  byUsername: (username: string) =>
    [...userKeys.all, 'username', username] as const,
};

/**
 * Get current user profile
 */
export const useProfile = () => {
  return useQuery({
    queryKey: userKeys.profile(),
    queryFn: userService.getProfile,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
};

/**
 * Update profile
 */
export const useUpdateProfile = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: UpdateProfileRequest) => userService.updateProfile(data),
    onSuccess: (data) => {
      queryClient.setQueryData(userKeys.profile(), data);
    },
  });
};

/**
 * Change password
 */
export const useChangePassword = () => {
  return useMutation({
    mutationFn: (data: ChangePasswordRequest) =>
      userService.changePassword(data),
  });
};

/**
 * Get user by ID (admin)
 */
export const useUser = (id: number, enabled = true) => {
  return useQuery({
    queryKey: userKeys.detail(id),
    queryFn: () => userService.getUserById(id),
    enabled,
    staleTime: 5 * 60 * 1000,
  });
};

/**
 * Get user by username (admin)
 */
export const useUserByUsername = (username: string, enabled = true) => {
  return useQuery({
    queryKey: userKeys.byUsername(username),
    queryFn: () => userService.getUserByUsername(username),
    enabled: enabled && username.length > 0,
    staleTime: 5 * 60 * 1000,
  });
};

/**
 * Get all users (admin)
 */
export const useUsers = (page = 0, size = 20) => {
  return useQuery({
    queryKey: userKeys.list({ page, size }),
    queryFn: () => userService.getAllUsers({ page, size }),
    staleTime: 2 * 60 * 1000,
  });
};

/**
 * Admin update user
 */
export const useAdminUpdateUser = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: AdminUpdateUserRequest }) =>
      userService.adminUpdateUser(id, data),
    onSuccess: (data, variables) => {
      queryClient.setQueryData(userKeys.detail(variables.id), data);
      queryClient.invalidateQueries({ queryKey: userKeys.lists() });
    },
  });
};

/**
 * Add points (admin)
 */
export const useAddPoints = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, points }: { id: number; points: number }) =>
      userService.addPoints(id, points),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: userKeys.detail(variables.id) });
    },
  });
};

/**
 * Deduct points (admin)
 */
export const useDeductPoints = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, points }: { id: number; points: number }) =>
      userService.deductPoints(id, points),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: userKeys.detail(variables.id) });
    },
  });
};

/**
 * Lock user (admin)
 */
export const useLockUser = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => userService.lockUser(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: userKeys.detail(id) });
      queryClient.invalidateQueries({ queryKey: userKeys.lists() });
    },
  });
};

/**
 * Unlock user (admin)
 */
export const useUnlockUser = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => userService.unlockUser(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: userKeys.detail(id) });
      queryClient.invalidateQueries({ queryKey: userKeys.lists() });
    },
  });
};
