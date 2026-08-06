import type { CSSProperties, ReactNode } from "react";
import { Icon } from "@/components/core/Icon";

export type Status = "active" | "inactive" | "out" | "returned" | "due-today" | "overdue" | "librarian";

export interface StatusBadgeProps {
  status: Status;
  /** Override the default word. The word itself is never removed. */
  children?: ReactNode;
  style?: CSSProperties;
}

const STATUSES: Record<Status, { label: string; color: string; dot?: string; icon?: string }> = {
  active: { label: "Active", color: "var(--ink-muted)", dot: "var(--success)" },
  inactive: { label: "Inactive", color: "var(--ink-tertiary)", dot: "var(--ink-tertiary)" },
  out: { label: "Out", color: "var(--ink-muted)", dot: "var(--primary)" },
  returned: { label: "Returned", color: "var(--ink-muted)", dot: "var(--success)" },
  "due-today": { label: "Due today", color: "var(--warning)", icon: "clock" },
  overdue: { label: "Overdue", color: "var(--danger)", icon: "triangle-alert" },
  librarian: { label: "Librarian", color: "var(--ink-muted)", dot: "var(--primary)" },
};

export function StatusBadge({ status, children, style }: StatusBadgeProps) {
  const s = STATUSES[status] || STATUSES.active;
  return (
    <span
      style={{
        display: "inline-flex",
        alignItems: "center",
        gap: "var(--space-xxs)",
        padding: "2px 8px",
        background: "var(--surface-2)",
        borderRadius: "var(--radius-badge)",
        font: "var(--type-caption)",
        color: s.color,
        whiteSpace: "nowrap",
        ...style,
      }}
    >
      {s.icon ? (
        <Icon name={s.icon} size={11} />
      ) : (
        <span style={{ width: 6, height: 6, borderRadius: "var(--radius-pill)", background: s.dot }} />
      )}
      {children || s.label}
    </span>
  );
}
