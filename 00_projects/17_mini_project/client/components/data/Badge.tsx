import type { CSSProperties, ReactNode } from "react";
import { Icon } from "@/components/core/Icon";

export type BadgeTone = "neutral" | "primary" | "warning" | "danger" | "success";

export interface BadgeProps {
  tone?: BadgeTone;
  /** Lucide icon name rendered before the count. */
  icon?: string;
  children: ReactNode;
  style?: CSSProperties;
}

const TONES: Record<BadgeTone, { bg: string; fg: string }> = {
  neutral: { bg: "var(--surface-3)", fg: "var(--ink-muted)" },
  primary: { bg: "var(--primary-tint)", fg: "var(--primary)" },
  warning: { bg: "var(--warning-tint)", fg: "var(--warning)" },
  danger: { bg: "var(--danger-tint)", fg: "var(--danger)" },
  success: { bg: "var(--success-tint)", fg: "var(--success)" },
};

export function Badge({ tone = "neutral", icon, children, style }: BadgeProps) {
  const t = TONES[tone];
  return (
    <span
      style={{
        display: "inline-flex",
        alignItems: "center",
        gap: 3,
        minWidth: 20,
        height: 18,
        padding: "0 6px",
        background: t.bg,
        color: t.fg,
        borderRadius: "var(--radius-badge)",
        font: "var(--type-caption)",
        fontVariantNumeric: "tabular-nums",
        justifyContent: "center",
        ...style,
      }}
    >
      {icon ? <Icon name={icon} size={11} /> : null}
      {children}
    </span>
  );
}
