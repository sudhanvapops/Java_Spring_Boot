"use client";

import { useEffect, type ReactNode } from "react";
import { usePathname, useRouter } from "next/navigation";
import { useAuthStore } from "@/lib/stores/auth";
import { useHydrateAuth } from "@/lib/hooks/useAuth";
import { AppShell } from "@/components/shell/AppShell";
import { Skeleton } from "@/components/data/Skeleton";

/** Mirrors proxy.ts's flag — see .env.local for why/how to turn it back off. */
const AUTH_DISABLED = process.env.NEXT_PUBLIC_DISABLE_AUTH === "true";

/**
 * Layer 2 of the 3-layer route protection (uploads/05-auth-spec.md "Route
 * protection"): waits for the refresh call, shows a skeleton — never a
 * login-screen flash — and redirects on failure. Layer 1 is proxy.ts
 * (fast cookie-presence check); layer 3, real token verification, only
 * exists once the backend implements the PROPOSED auth endpoints.
 */
export default function AppLayout({ children }: { children: ReactNode }) {
  const status = useAuthStore((s) => s.status);
  const hydrate = useHydrateAuth();
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    if (!AUTH_DISABLED && status === "idle") hydrate();
  }, [status, hydrate]);

  useEffect(() => {
    if (!AUTH_DISABLED && status === "unauthenticated") {
      router.replace(`/login?next=${encodeURIComponent(pathname)}`);
    }
  }, [status, pathname, router]);

  if (!AUTH_DISABLED && status !== "authenticated") {
    return <FullPageSkeleton />;
  }

  return <AppShell>{children}</AppShell>;
}

function FullPageSkeleton() {
  return (
    <div style={{ display: "flex", minHeight: "100vh", background: "var(--canvas)" }}>
      <div style={{ width: "var(--sidebar-width)", flex: "0 0 auto", borderRight: "1px solid var(--hairline)" }} />
      <div style={{ flex: 1, padding: "var(--space-xl)" }}>
        <Skeleton width={240} height={28} />
        <div style={{ marginTop: "var(--space-lg)", display: "grid", gap: "var(--space-md)" }}>
          <Skeleton height={80} />
          <Skeleton height={80} />
          <Skeleton height={80} />
        </div>
      </div>
    </div>
  );
}
