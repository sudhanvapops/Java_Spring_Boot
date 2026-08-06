import type { CSSProperties, ReactNode } from "react";
import { Icon } from "@/components/core/Icon";
import { Button } from "@/components/forms/Button";

export interface ErrorPanelProps {
  /** What happened, in plain language. */
  headline?: ReactNode;
  /** What to do next. */
  body?: ReactNode;
  actionLabel?: string;
  onAction?: () => void;
  /** Correlation reference in mono / ink-tertiary. Only on the app error page. */
  reference?: string;
  icon?: string;
  style?: CSSProperties;
}

export function ErrorPanel({
  headline = "Can’t reach the server.",
  body = "Check your connection and try again.",
  actionLabel = "Try again",
  onAction,
  reference,
  icon = "server-off",
  style,
}: ErrorPanelProps) {
  return (
    <div
      role="alert"
      style={{
        display: "flex",
        gap: "var(--space-md)",
        padding: "var(--space-lg)",
        background: "var(--surface-1)",
        border: "1px solid var(--hairline-strong)",
        borderRadius: "var(--radius-panel)",
        ...style,
      }}
    >
      <Icon name={icon} size={18} color="var(--danger)" style={{ marginTop: 2 }} />
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ font: "var(--type-body)", fontWeight: "var(--weight-medium)", color: "var(--ink)" }}>{headline}</div>
        {body ? <p style={{ margin: "4px 0 0", font: "var(--type-body-sm)", color: "var(--ink-subtle)", textWrap: "pretty" }}>{body}</p> : null}
        {reference ? (
          <div style={{ marginTop: "var(--space-xs)", font: "var(--type-mono)", fontSize: "var(--size-caption)", color: "var(--ink-tertiary)" }}>
            {reference}
          </div>
        ) : null}
        {actionLabel ? (
          <div style={{ marginTop: "var(--space-md)" }}>
            <Button variant="secondary" size="sm" onClick={onAction}>
              {actionLabel}
            </Button>
          </div>
        ) : null}
      </div>
    </div>
  );
}
