"use client";

import { useEffect } from "react";
import { ErrorPanel } from "@/components/feedback/ErrorPanel";

/** Root-level error boundary — catches anything that escapes the (app) and
 * (auth) segment boundaries, including errors in the providers themselves. */
export default function RootError({ error, reset }: { error: Error & { digest?: string }; reset: () => void }) {
  useEffect(() => {
    console.error(error);
  }, [error]);

  return (
    <div style={{ minHeight: "100vh", display: "grid", placeItems: "center", background: "var(--canvas)", padding: "var(--space-lg)" }}>
      <div style={{ width: "var(--form-max-width)", maxWidth: "100%" }}>
        <ErrorPanel
          icon="triangle-alert"
          headline="Something went wrong"
          body="The console hit an unexpected error. Trying again usually works."
          reference={error.digest ? `REF ${error.digest}` : undefined}
          actionLabel="Try again"
          onAction={reset}
        />
      </div>
    </div>
  );
}
