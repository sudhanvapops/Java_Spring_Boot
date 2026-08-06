"use client";

import { AnimatePresence } from "framer-motion";
import { Toast } from "./Toast";
import { useUiStore } from "@/lib/stores/ui";

/** Bottom-right toast stack driven by the ui store's pushToast/dismissToast
 * (lib/stores/ui.ts) — replaces the reference kit's notify() callback
 * threaded through every screen's props. */
export function ToastHost() {
  const toasts = useUiStore((s) => s.toasts);
  const dismissToast = useUiStore((s) => s.dismissToast);

  return (
    <div
      style={{
        position: "fixed",
        bottom: "var(--space-lg)",
        right: "var(--space-lg)",
        zIndex: 100,
        display: "flex",
        flexDirection: "column",
        gap: "var(--space-xs)",
        alignItems: "flex-end",
      }}
    >
      <AnimatePresence>
        {toasts.map((t) => (
          <Toast key={t.id} tone={t.tone} message={t.message} action={t.action ? <ToastAction {...t.action} /> : undefined} onDismiss={() => dismissToast(t.id)} />
        ))}
      </AnimatePresence>
    </div>
  );
}

function ToastAction({ label, onClick }: { label: string; onClick: () => void }) {
  return (
    <button
      type="button"
      onClick={onClick}
      style={{
        background: "none",
        border: "none",
        padding: 0,
        font: "var(--type-body-sm)",
        fontWeight: "var(--weight-medium)",
        color: "var(--primary)",
        cursor: "pointer",
        whiteSpace: "nowrap",
      }}
    >
      {label}
    </button>
  );
}
