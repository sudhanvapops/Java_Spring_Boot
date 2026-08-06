"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { ErrorPanel } from "@/components/feedback/ErrorPanel";

/**
 * Error boundary for the (app) segment — the layout above it (Sidebar/
 * Topbar) keeps rendering; only the content area shows this. Mirrors
 * ui_kits/console/AppShell.jsx's ErrorScreen.
 */
export default function AppError({ error, reset }: { error: Error & { digest?: string }; reset: () => void }) {
  const router = useRouter();

  useEffect(() => {
    console.error(error);
  }, [error]);

  return (
    <div style={{ maxWidth: "var(--form-max-width)" }}>
      <ErrorPanel
        icon="triangle-alert"
        headline="Something went wrong"
        body="The console hit an unexpected error. Trying again usually works."
        reference={error.digest ? `REF ${error.digest}` : undefined}
        actionLabel="Try again"
        onAction={reset}
      />
      <div style={{ marginTop: "var(--space-md)" }}>
        <a
          href="#back"
          onClick={(e) => {
            e.preventDefault();
            router.push("/dashboard");
          }}
        >
          Back to overview
        </a>
      </div>
    </div>
  );
}
