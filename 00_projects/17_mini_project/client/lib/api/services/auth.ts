import { apiClient, bareClient } from "@/lib/api/client";
import type {
  LoginRequestDto,
  LoginResponseDto,
  LogoutResponseDto,
  RefreshResponseDto,
  RegisterRequestDto,
  RegisterResponseDto,
  StaffRegisterRequestDto,
  StaffRegisterResponseDto,
} from "@/lib/types/api";

/**
 * The real, implemented contract — BACKEND_HANDOFF.md §3.3/§3.4. login/
 * refresh use the bare client (no interceptors) to avoid a recursive
 * refresh loop; everything else uses the normal client.
 */

/** Public — registers the caller as a Member (library patron), not a User
 * login account. No password; the response has no login/session outcome. */
export async function register(payload: RegisterRequestDto): Promise<RegisterResponseDto> {
  const res = await apiClient.post<RegisterResponseDto>("/api/auth/register", payload);
  return res.data;
}

/** @AdminOnly — requires an existing admin session's bearer token. */
export async function registerStaff(payload: StaffRegisterRequestDto): Promise<StaffRegisterResponseDto> {
  const res = await apiClient.post<StaffRegisterResponseDto>("/api/auth/register-staff", payload);
  return res.data;
}

export async function login(payload: LoginRequestDto): Promise<LoginResponseDto> {
  const res = await bareClient.post<LoginResponseDto>("/api/auth/login", payload);
  return res.data;
}

/** No body — reads the httpOnly `refreshToken` cookie. */
export async function refresh(): Promise<RefreshResponseDto> {
  const res = await bareClient.post<RefreshResponseDto>("/api/auth/refresh");
  return res.data;
}

export async function logout(): Promise<LogoutResponseDto> {
  const res = await apiClient.post<LogoutResponseDto>("/api/auth/logout");
  return res.data;
}
