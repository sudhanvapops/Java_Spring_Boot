"use client";

import { useEffect, type ReactNode } from "react";
import { usePathname, useRouter } from "next/navigation";
import { useAuthStore } from "@/lib/stores/auth";
import { useHydrateAuth, useProactiveRefresh } from "@/lib/hooks/useAuth";
import { isAllowedForMember, MEMBER_HOME } from "@/lib/utils/rbac";
import { AppShell } from "@/components/shell/AppShell";
import { Skeleton } from "@/components/data/Skeleton";

/** Mirrors .env.local — see the comment there for why/how to turn it back off. */
const AUTH_DISABLED = process.env.NEXT_PUBLIC_DISABLE_AUTH === "true";

/**
 * Route protection (BACKEND_HANDOFF.md §3.6's three-layer caveat — layer 1,
 * the edge cookie check, was removed in proxy.ts because it structurally
 * can't see the backend's path-scoped cookie): waits for the refresh call,
 * shows a skeleton — never a login-screen flash — and redirects on failure.
 * The server's @PreAuthorize is the only actual security either way.
 *
 * Also enforces the MEMBER-role scope client-side: a MEMBER account can only
 * browse books server-side (§3.2), so anything else redirects to /books
 * rather than rendering a screen full of 403s.
 */
export default function AppLayout({ children }: { children: ReactNode }) {
  const status = useAuthStore((s) => s.status);
  const role = useAuthStore((s) => s.user?.role);
  const hydrate = useHydrateAuth();
  const router = useRouter();
  const pathname = usePathname();

  useProactiveRefresh();

  useEffect(() => {
    if (!AUTH_DISABLED && status === "idle") hydrate();
  }, [status, hydrate]);

  useEffect(() => {
    if (!AUTH_DISABLED && status === "unauthenticated") {
      router.replace(`/login?next=${encodeURIComponent(pathname)}`);
    }
  }, [status, pathname, router]);

  useEffect(() => {
    if (status === "authenticated" && role === "MEMBER" && !isAllowedForMember(pathname)) {
      router.replace(MEMBER_HOME);
    }
  }, [status, role, pathname, router]);

  if (!AUTH_DISABLED && status !== "authenticated") {
    return <FullPageSkeleton />;
  }

  if (status === "authenticated" && role === "MEMBER" && !isAllowedForMember(pathname)) {
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
