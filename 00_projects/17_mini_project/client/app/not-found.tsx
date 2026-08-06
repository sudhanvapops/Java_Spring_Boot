import Link from "next/link";
import { EmptyState } from "@/components/feedback/EmptyState";

/** Root-level fallback for paths outside both the (app) and (auth) segment
 * trees — the common case (signed in or out, under /dashboard or /login) is
 * handled by app/(app)/[...notfound]/page.tsx instead, inside the shell. */
export default function RootNotFound() {
  return (
    <div style={{ minHeight: "100vh", display: "grid", placeItems: "center", background: "var(--canvas)" }}>
      <EmptyState icon="inbox" headline="Page not found" body="That page doesn’t exist, or it moved." />
      <Link href="/dashboard" style={{ marginTop: "var(--space-md)" }}>
        Back to the console
      </Link>
    </div>
  );
}
