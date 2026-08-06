"use client";

import { useState, type ReactNode } from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

/**
 * Retries per uploads/08-api-client.md: network/timeout retry twice with
 * backoff, 5xx once, everything else (4xx, mutations) never — a timed-out
 * lend may have already succeeded server-side, so mutations must not
 * auto-retry (that would risk double-lending).
 */
function shouldRetryQuery(failureCount: number, error: unknown): boolean {
  const err = error as { status?: number | null; errorCode?: string | null } | undefined;
  if (!err || err.status == null) {
    // network error / timeout
    return failureCount < 2;
  }
  if (err.status >= 500) return failureCount < 1;
  return false;
}

export function AppProviders({ children }: { children: ReactNode }) {
  const [client] = useState(
    () =>
      new QueryClient({
        defaultOptions: {
          queries: {
            retry: shouldRetryQuery,
            refetchOnWindowFocus: false,
          },
          mutations: {
            retry: false,
          },
        },
      }),
  );

  return <QueryClientProvider client={client}>{children}</QueryClientProvider>;
}
