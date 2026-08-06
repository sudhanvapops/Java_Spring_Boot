import { apiClient, bareClient } from "@/lib/api/client";
import type {
  AuthResponseDto,
  ChangePasswordRequestDto,
  ForgotPasswordRequestDto,
  LoginRequestDto,
  ResetPasswordRequestDto,
  SignupRequestDto,
} from "@/lib/types/api";

/**
 * PROPOSED — uploads/05-auth-spec.md. None of these endpoints exist in the
 * Spring Boot backend yet; this is written so the frontend is ready the
 * moment they're added. login/refresh use the bare client (no interceptors)
 * to avoid a recursive refresh loop; everything else uses the normal client.
 */

export async function signup(payload: SignupRequestDto): Promise<AuthResponseDto> {
  const res = await apiClient.post<AuthResponseDto>("/api/auth/signup", payload);
  return res.data;
}

export async function login(payload: LoginRequestDto): Promise<AuthResponseDto> {
  const res = await bareClient.post<AuthResponseDto>("/api/auth/login", payload);
  return res.data;
}

export async function logout(): Promise<void> {
  await apiClient.post("/api/auth/logout");
}

export async function me(): Promise<AuthResponseDto["user"]> {
  const res = await apiClient.get<AuthResponseDto["user"]>("/api/auth/me");
  return res.data;
}

export async function forgotPassword(payload: ForgotPasswordRequestDto): Promise<void> {
  await apiClient.post("/api/auth/forgot-password", payload);
}

export async function validateResetToken(token: string): Promise<boolean> {
  try {
    await apiClient.get("/api/auth/reset-password/validate", { params: { token } });
    return true;
  } catch {
    return false;
  }
}

export async function resetPassword(payload: ResetPasswordRequestDto): Promise<void> {
  await apiClient.post("/api/auth/reset-password", payload);
}

export async function changePassword(payload: ChangePasswordRequestDto): Promise<void> {
  await apiClient.post("/api/auth/change-password", payload);
}

export async function verifyEmail(token: string): Promise<void> {
  await apiClient.post("/api/auth/verify-email", { token });
}

export async function resendVerification(email: string): Promise<void> {
  await apiClient.post("/api/auth/resend-verification", { email });
}
