import type { CSSProperties, ReactNode } from "react";
import { Icon } from "@/components/core/Icon";

export type BannerTone = "info" | "warning" | "danger" | "success";

export interface BannerProps {
  tone?: BannerTone;
  children: ReactNode;
  /** Recovery action, right-aligned. */
  action?: ReactNode;
  style?: CSSProperties;
}

const TONES: Record<BannerTone, { icon: string; color: string }> = {
  info: { icon: "info", color: "var(--primary)" },
  warning: { icon: "triangle-alert", color: "var(--warning)" },
  danger: { icon: "circle-x", color: "var(--danger)" },
  success: { icon: "circle-check", color: "var(--success)" },
};

export function Banner({ tone = "warning", children, action, style }: BannerProps) {
  const t = TONES[tone];
  return (
    <div
      role="status"
      style={{
        display: "flex",
        alignItems: "center",
        gap: "var(--space-sm)",
        padding: "var(--space-sm) var(--space-md)",
        background: "var(--surface-1)",
        border: "1px solid var(--hairline-strong)",
        borderRadius: "var(--radius-panel)",
        font: "var(--type-body-sm)",
        color: "var(--ink-muted)",
        ...style,
      }}
    >
      <Icon name={t.icon} size={16} color={t.color} />
      <span style={{ flex: 1, minWidth: 0 }}>{children}</span>
      {action}
    </div>
  );
}
