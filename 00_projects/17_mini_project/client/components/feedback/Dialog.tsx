"use client";

import { useEffect, useRef, type ReactNode } from "react";
import { Button } from "@/components/forms/Button";
import { MotionDiv, Presence, EASE } from "@/components/core/Motion";

export interface DialogProps {
  open?: boolean;
  title: ReactNode;
  /** Body copy. Say so when the action is reversible; people stop hesitating. */
  children?: ReactNode;
  /** Names the action: "Deactivate book". */
  confirmLabel: string;
  onConfirm?: () => void;
  cancelLabel?: string;
  onCancel?: () => void;
  /** Danger primary button, for deactivations and deletions. */
  destructive?: boolean;
  width?: number;
}

const FOCUSABLE = 'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])';

/**
 * Ported from stacks-design-system/project/components/feedback/Dialog.jsx
 * with keyboard handling added: the reference version has role="dialog"
 * aria-modal but no Escape-to-close or focus trap, which the spec requires
 * (readme.md "Accessibility floor" — full keyboard reachability + focus trap
 * in dialogs + Escape closes).
 */
export function Dialog({
  open = true,
  title,
  children,
  confirmLabel,
  onConfirm,
  cancelLabel = "Cancel",
  onCancel,
  destructive = false,
  width = 440,
}: DialogProps) {
  const panelRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!open) return;
    const panel = panelRef.current;
    const focusables = panel?.querySelectorAll<HTMLElement>(FOCUSABLE);
    focusables?.[0]?.focus();

    const onKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        e.preventDefault();
        onCancel?.();
        return;
      }
      if (e.key === "Tab" && focusables && focusables.length > 0) {
        const first = focusables[0];
        const last = focusables[focusables.length - 1];
        if (e.shiftKey && document.activeElement === first) {
          e.preventDefault();
          last.focus();
        } else if (!e.shiftKey && document.activeElement === last) {
          e.preventDefault();
          first.focus();
        }
      }
    };
    document.addEventListener("keydown", onKeyDown);
    return () => document.removeEventListener("keydown", onKeyDown);
  }, [open, onCancel]);

  return (
    <Presence>
      {open ? (
        <MotionDiv
          key="scrim"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          transition={EASE.fast}
          style={{ position: "absolute", inset: 0, display: "grid", placeItems: "center", background: "rgba(5,5,6,.72)", zIndex: 50 }}
        >
          <MotionDiv
            ref={panelRef}
            role="dialog"
            aria-modal="true"
            aria-label={typeof title === "string" ? title : undefined}
            initial={{ opacity: 0, y: 8 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: 8 }}
            transition={EASE.medium}
            style={{
              width,
              maxWidth: "100%",
              padding: "var(--space-lg)",
              background: "var(--surface-1)",
              border: "1px solid var(--hairline-strong)",
              borderRadius: "var(--radius-card)",
              boxShadow: "var(--shadow-overlay)",
            }}
          >
            <div style={{ font: "var(--type-card-title)", letterSpacing: "var(--track-card-title)", color: "var(--ink)" }}>{title}</div>
            <div style={{ marginTop: "var(--space-sm)", font: "var(--type-body-sm)", color: "var(--ink-subtle)", textWrap: "pretty" }}>
              {children}
            </div>
            <div style={{ display: "flex", gap: "var(--space-xs)", justifyContent: "flex-end", marginTop: "var(--space-lg)" }}>
              <Button variant="tertiary" onClick={onCancel}>
                {cancelLabel}
              </Button>
              <Button variant={destructive ? "danger" : "primary"} onClick={onConfirm}>
                {confirmLabel}
              </Button>
            </div>
          </MotionDiv>
        </MotionDiv>
      ) : null}
    </Presence>
  );
}
