import { useCallback } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import * as authApi from "@/lib/api/services/auth";
import { refreshAccessToken } from "@/lib/api/client";
import { useAuthStore } from "@/lib/stores/auth";
import type {
  ChangePasswordFormValues,
  LoginFormValues,
  ResetPasswordFormValues,
  SignupFormValues,
} from "@/lib/schemas/auth";

function toAccount(user: { id: number; name: string; email: string; role: "LIBRARIAN" | "MEMBER"; memberId: number | null }) {
  return { id: user.id, name: user.name, email: user.email, role: user.role, memberId: user.memberId };
}

export function useLogin() {
  const setSession = useAuthStore((s) => s.setSession);
  return useMutation({
    mutationFn: (values: Pick<LoginFormValues, "email" | "password">) => authApi.login(values),
    onSuccess: (data) => setSession(toAccount(data.user), data.accessToken),
  });
}

export function useSignup() {
  const setSession = useAuthStore((s) => s.setSession);
  return useMutation({
    mutationFn: (values: Pick<SignupFormValues, "name" | "email" | "password">) => authApi.signup(values),
    onSuccess: (data) => setSession(toAccount(data.user), data.accessToken),
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

export function useForgotPassword() {
  return useMutation({ mutationFn: (values: { email: string }) => authApi.forgotPassword(values) });
}

/** Gates the /reset-password form on mount per uploads/05-auth-spec.md —
 * a missing token never even calls the network. */
export function useValidateResetToken(token: string | null) {
  return useQuery({
    queryKey: ["auth", "reset-password", "validate", token],
    queryFn: () => authApi.validateResetToken(token as string),
    enabled: !!token,
    retry: false,
    staleTime: 0,
  });
}

export function useResetPassword() {
  return useMutation({
    mutationFn: (payload: Pick<ResetPasswordFormValues, "token" | "password">) => authApi.resetPassword(payload),
  });
}

export function useChangePassword() {
  return useMutation({
    mutationFn: (payload: Pick<ChangePasswordFormValues, "currentPassword" | "newPassword">) =>
      authApi.changePassword({ currentPassword: payload.currentPassword, newPassword: payload.newPassword }),
  });
}

export function useResendVerification() {
  return useMutation({ mutationFn: (email: string) => authApi.resendVerification(email) });
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
