import { useCallback, useEffect } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import * as authApi from "@/lib/api/services/auth";
import { refreshAccessToken } from "@/lib/api/client";
import { useAuthStore } from "@/lib/stores/auth";
import type { LoginFormValues, StaffRegisterFormValues } from "@/lib/schemas/auth";
import type { MemberFormValues } from "@/lib/schemas/member";

/** The access token lives 30s (BACKEND_HANDOFF.md §3.5) — refresh well
 * before it expires rather than only reactively on 401, or normal usage
 * spends a lot of time retrying. */
const PROACTIVE_REFRESH_MS = 15_000;

export function useLogin() {
  const setSession = useAuthStore((s) => s.setSession);
  return useMutation({
    mutationFn: (values: Pick<LoginFormValues, "email" | "password">) => authApi.login(values),
    onSuccess: (data) => setSession({ id: data.userId, email: data.email, role: data.role }, data.accessToken),
  });
}

/** Public self-signup — registers the caller as a Member (library patron),
 * not a login account (§3.2/§3.3; user accounts are staff-only). No
 * onSuccess session handling: there's nothing to log in to. */
export function useRegister() {
  return useMutation({
    mutationFn: (values: MemberFormValues) => authApi.register(values),
  });
}

/** @AdminOnly — only usable by an already-signed-in admin. Does not touch
 * the caller's own session. */
export function useRegisterStaff() {
  return useMutation({
    mutationFn: (
      values: Pick<StaffRegisterFormValues, "username" | "email" | "password" | "confirmPassword" | "role">,
    ) => authApi.registerStaff(values),
  });
}

export function useLogout() {
  const clearAuth = useAuthStore((s) => s.clearAuth);
  const qc = useQueryClient();
  return useMutation({
    mutationFn: () => authApi.logout(),
    onSettled: () => {
      clearAuth();
      qc.clear();
    },
  });
}

/** Called once on (app) layout mount: attempt a refresh to silently
 * rehydrate the session from the httpOnly cookie. Resolves to
 * 'authenticated' or 'unauthenticated' — callers gate rendering on status,
 * never assume this resolves quickly. */
export function useHydrateAuth() {
  const setStatus = useAuthStore((s) => s.setStatus);
  return useCallback(async () => {
    setStatus("loading");
    const token = await refreshAccessToken();
    setStatus(token ? "authenticated" : "unauthenticated");
  }, [setStatus]);
}

/** Keeps the 30s access token alive proactively while a session is active —
 * see PROACTIVE_REFRESH_MS. Mount once, near the top of the authenticated
 * shell. */
export function useProactiveRefresh() {
  const status = useAuthStore((s) => s.status);
  useEffect(() => {
    if (status !== "authenticated") return;
    const id = setInterval(() => {
      refreshAccessToken();
    }, PROACTIVE_REFRESH_MS);
    return () => clearInterval(id);
  }, [status]);
}
