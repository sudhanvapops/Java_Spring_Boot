import axios, { AxiosError, AxiosHeaders, type AxiosInstance, type AxiosResponse, type InternalAxiosRequestConfig } from "axios";
import { getAuthState, useAuthStore } from "@/lib/stores/auth";
import type { ApiErrorEnvelope, NormalizedApiError, RefreshResponseDto } from "@/lib/types/api";
import { formatErrorMessage, isEmptyStateCode } from "@/lib/utils/errors";

const baseURL = process.env.NEXT_PUBLIC_API_BASE_URL;

/**
 * uploads/08-api-client.md. Two instances: `apiClient` carries auth +
 * envelope-unwrap + error-normalize + 401-refresh interceptors; `bareClient`
 * gets envelope-unwrap + error-normalize too (login needs a proper
 * userMessage same as everything else) but never the 401-refresh-retry
 * behaviour, and is used exclusively for /auth/login and /auth/refresh so a
 * failing refresh can never trigger another refresh (recursive loop).
 */
export const bareClient: AxiosInstance = axios.create({
  baseURL,
  timeout: 15000,
  withCredentials: true,
  headers: { "Content-Type": "application/json" },
});

export const apiClient: AxiosInstance = axios.create({
  baseURL,
  timeout: 15000,
  withCredentials: true,
  headers: { "Content-Type": "application/json" },
});

apiClient.interceptors.request.use((config) => {
  const { accessToken } = getAuthState();
  if (accessToken) {
    config.headers.set("Authorization", `Bearer ${accessToken}`);
  }
  if (process.env.NODE_ENV !== "production") {
    const id = crypto.randomUUID().slice(0, 8);
    config.headers.set("X-Correlation-Id", id);
    console.debug(`[api] -> ${config.method?.toUpperCase()} ${config.url} (${id})`);
  }
  return config;
});

function normalize(
  status: number | null,
  errorCode: string | null,
  message: string | null,
  raw: unknown,
): NormalizedApiError {
  return {
    status,
    errorCode,
    message,
    userMessage: formatErrorMessage(errorCode ?? undefined),
    isEmptyState: status === 404 && isEmptyStateCode(errorCode),
    raw,
  };
}

/** Unwrap { success, message, data } -> data; pass through anything else. */
function unwrapEnvelope(response: AxiosResponse) {
  const body = response.data as unknown;
  if (body && typeof body === "object" && "success" in body) {
    return { ...response, data: (body as unknown as { data: unknown }).data };
  }
  return response;
}

/** Turn any axios rejection into a NormalizedApiError. Shared by both
 * clients; only apiClient additionally attempts a 401 refresh-and-retry
 * before falling through to this. */
function normalizeAxiosError(error: AxiosError<ApiErrorEnvelope>): Promise<never> {
  if (!error.response) {
    const code = error.code === "ECONNABORTED" ? "TIMEOUT" : axios.isCancel(error) ? "CANCELLED" : "NETWORK_ERROR";
    return Promise.reject(normalize(null, code, error.message, error));
  }
  const { status, data: body } = error.response;
  return Promise.reject(normalize(status, body?.errorCode ?? null, body?.message ?? null, error));
}

bareClient.interceptors.response.use(unwrapEnvelope, normalizeAxiosError);

/**
 * Single-flight refresh: the shared promise IS the queue — concurrent 401s
 * all await the same in-flight call instead of each starting their own
 * (rotating refresh tokens would otherwise revoke each other in a race).
 * Only clears the store on failure; the actual redirect-to-login happens
 * reactively in app/(app)/layout.tsx watching auth status, which is the
 * layer that actually has router access.
 */
let refreshPromise: Promise<string | null> | null = null;

/** Exported so app/(app)/layout.tsx can call the same single-flight refresh
 * on mount to hydrate the session (layer 2 of the 3-layer route protection —
 * see uploads/05-auth-spec.md "Route protection"). */
export async function refreshAccessToken(): Promise<string | null> {
  if (!refreshPromise) {
    refreshPromise = (async () => {
      try {
        const res = await bareClient.post<RefreshResponseDto>("/api/auth/refresh");
        const data = res.data;
        useAuthStore.getState().setSession(
          { id: data.userId, email: data.email, role: data.role },
          data.accessToken,
        );
        return data.accessToken;
      } catch {
        useAuthStore.getState().clearAuth();
        return null;
      } finally {
        refreshPromise = null;
      }
    })();
  }
  return refreshPromise;
}

apiClient.interceptors.response.use(unwrapEnvelope, async (error: AxiosError<ApiErrorEnvelope>) => {
  const config = error.config as (InternalAxiosRequestConfig & { _retried?: boolean }) | undefined;
  const status = error.response?.status;

  if (status === 401 && config && !config._retried) {
    config._retried = true;
    const token = await refreshAccessToken();
    if (token) {
      config.headers = config.headers ?? new AxiosHeaders();
      (config.headers as AxiosHeaders).set("Authorization", `Bearer ${token}`);
      return apiClient(config);
    }
  }

  return normalizeAxiosError(error);
});

export function isNormalizedApiError(error: unknown): error is NormalizedApiError {
  return typeof error === "object" && error !== null && "userMessage" in error;
}
