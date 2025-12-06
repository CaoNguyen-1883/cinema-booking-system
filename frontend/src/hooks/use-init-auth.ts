import { useEffect, useState } from 'react';
import { useAuthStore } from '@/stores/auth.store';
import { authService } from '@/api';

/**
 * Hook to initialize authentication on app load
 *
 * This hook should be called once in the root component (App.tsx)
 * It checks if user was previously authenticated and attempts to
 * refresh the access token using the httpOnly refresh token cookie.
 *
 * Flow:
 * 1. Check if user was authenticated (from persisted Zustand state)
 * 2. If yes, call /auth/refresh to get new accessToken
 * 3. Backend reads refreshToken from httpOnly cookie
 * 4. Update Zustand store with new accessToken
 *
 * This ensures user stays logged in after page refresh without
 * storing sensitive tokens in localStorage.
 */
export const useInitAuth = () => {
  const [isInitializing, setIsInitializing] = useState(true);
  const { isAuthenticated, setAuth, logout } = useAuthStore();

  useEffect(() => {
    const initAuth = async () => {
      // Only attempt refresh if user was previously authenticated
      if (!isAuthenticated) {
        setIsInitializing(false);
        return;
      }

      try {
        // Call refresh endpoint
        // Backend will read refreshToken from httpOnly cookie
        const response = await authService.refreshToken({ refreshToken: '' });

        // Update store with new accessToken (memory only)
        setAuth(response.user, response.accessToken);
      } catch (error) {
        // Refresh failed, user needs to login again
        console.error('Failed to refresh token:', error);
        logout();
      } finally {
        setIsInitializing(false);
      }
    };

    initAuth();
  }, []); // Run only once on mount

  return { isInitializing };
};
