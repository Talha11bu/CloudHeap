import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type  { User, SessionState } from '../types/index.ts';

interface AuthStore {
  token: string | null;
  user: User | null;
  session: SessionState | null;
  setAuth: (data: { token: string; user: User; session: SessionState }) => void;
  clearAuth: () => void;
}

export const useAuthStore = create<AuthStore>()(
  persist(
    (set) => ({
      token: null,
      user: null,
      session: null,
      setAuth: (data) => set({ ...data }),
      clearAuth: () => set({ token: null, user: null, session: null }),
    }),
    { name: 'Silkroad-auth' }
  )
);
