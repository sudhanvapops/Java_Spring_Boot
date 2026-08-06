import { create } from "zustand";
import type { Account } from "@/lib/types/domain";

/**
 * uploads/07-state-management.md "Store 1 — auth". Never persisted (no
 * partial persistence either — a stale "signed in" flag with a dead token is
 * a confusing failure mode). status starts at 'idle', not 'unauthenticated',
 * so the app shell can show a skeleton instead of flashing the login screen
 * on every load while the refresh call is in flight.
 */
export type AuthStatus = "idle" | "loading" | "authenticated" | "unauthenticated";

interface AuthState {
  user: Account | null;
  accessToken: string | null;
  status: AuthStatus;
  error: string | null;
  setUser: (user: Account | null) => void;
  setAccessToken: (token: string | null) => void;
  setStatus: (status: AuthStatus) => void;
  setError: (error: string | null) => void;
  /** Sets user + token + status:'authenticated' in one go. */
  setSession: (user: Account, accessToken: string) => void;
  clearAuth: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  accessToken: null,
  status: "idle",
  error: null,
  setUser: (user) => set({ user }),
  setAccessToken: (accessToken) => set({ accessToken }),
  setStatus: (status) => set({ status }),
  setError: (error) => set({ error }),
  setSession: (user, accessToken) =>
    set({ user, accessToken, status: "authenticated", error: null }),
  clearAuth: () => set({ user: null, accessToken: null, status: "unauthenticated", error: null }),
}));

/** Read outside React (axios interceptors) without subscribing. */
export function getAuthState() {
  return useAuthStore.getState();
}
