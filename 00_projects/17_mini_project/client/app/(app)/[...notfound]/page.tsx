"use client";

import { useRouter } from "next/navigation";
import { EmptyState } from "@/components/feedback/EmptyState";

/**
 * Catch-all inside the (app) segment so any unmatched path while signed in
 * renders "page not found" inside the console shell (Sidebar/Topbar stay
 * up), rather than Next's default not-found — matches
 * ui_kits/console/AppShell.jsx's NotFoundScreen composed inside AppShell.
 */
export default function AppNotFound() {
  const router = useRouter();
  return (
    <EmptyState
      icon="inbox"
      headline="Page not found"
      body="That page doesn’t exist, or it moved."
      actionLabel="Back to the console"
      onAction={() => router.push("/dashboard")}
    />
  );
}
