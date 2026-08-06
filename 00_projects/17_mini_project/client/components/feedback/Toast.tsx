import type { CSSProperties, ReactNode } from "react";
import { Icon } from "@/components/core/Icon";
import { IconButton } from "@/components/forms/IconButton";
import { MotionDiv, EASE } from "@/components/core/Motion";

export type ToastTone = "success" | "danger" | "info";

export interface ToastProps {
  tone?: ToastTone;
  /** One sentence naming what happened: '"The Pragmatic Programmer" added.' */
  message: ReactNode;
  /** Recovery action, e.g. a Try again button. */
  action?: ReactNode;
  onDismiss?: () => void;
  style?: CSSProperties;
}

const TONES: Record<ToastTone, { icon: string; color: string }> = {
  success: { icon: "circle-check", color: "var(--success)" },
  danger: { icon: "circle-x", color: "var(--danger)" },
  info: { icon: "info", color: "var(--primary)" },
};

export function Toast({ tone = "success", message, action, onDismiss, style }: ToastProps) {
  const t = TONES[tone];
  return (
    <MotionDiv
      role="status"
      aria-live="polite"
      initial={{ opacity: 0, y: 12 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: 12 }}
      transition={EASE.medium}
      style={{
        display: "flex",
        alignItems: "center",
        gap: "var(--space-sm)",
        minWidth: 300,
        maxWidth: 420,
        padding: "var(--space-sm) var(--space-sm) var(--space-sm) var(--space-md)",
        background: "var(--surface-4)",
        border: "1px solid var(--hairline-strong)",
        borderRadius: "var(--radius-panel)",
        boxShadow: "var(--shadow-popover)",
        font: "var(--type-body-sm)",
        color: "var(--ink)",
        ...style,
      }}
    >
      <Icon name={t.icon} size={16} color={t.color} />
      <span style={{ flex: 1, minWidth: 0 }}>{message}</span>
      {action}
      {onDismiss ? <IconButton icon="x" label="Dismiss" onClick={onDismiss} /> : null}
    </MotionDiv>
  );
}
