import type { CSSProperties, ReactNode } from "react";

export interface PanelProps {
  title?: ReactNode;
  /** Step number badge, for sequences where the order carries information (/lend). */
  step?: number;
  /** Right-aligned header slot — a "View all →" link, a select-all checkbox. */
  action?: ReactNode;
  /** surface-2 footer strip, for totals and the panel's primary action. */
  footer?: ReactNode;
  children?: ReactNode;
  /** Set false when children are full-width rows that supply their own padding. */
  padded?: boolean;
  style?: CSSProperties;
}

export function Panel({ title, step, action, footer, children, padded = true, style }: PanelProps) {
  return (
    <section
      style={{
        background: "var(--surface-1)",
        border: "1px solid var(--hairline)",
        borderRadius: "var(--radius-panel)",
        overflow: "hidden",
        ...style,
      }}
    >
      {title ? (
        <header
          style={{
            display: "flex",
            alignItems: "center",
            gap: "var(--space-xs)",
            minHeight: 44,
            padding: "0 var(--space-md)",
            borderBottom: "1px solid var(--hairline)",
          }}
        >
          {step != null ? (
            <span
              style={{
                display: "grid",
                placeItems: "center",
                width: 18,
                height: 18,
                flex: "0 0 auto",
                borderRadius: "var(--radius-pill)",
                background: "var(--surface-3)",
                font: "var(--type-mono)",
                fontSize: 11,
                color: "var(--ink-muted)",
              }}
            >
              {step}
            </span>
          ) : null}
          <h2 style={{ margin: 0, flex: 1, minWidth: 0, font: "var(--type-body-sm)", fontWeight: "var(--weight-medium)", color: "var(--ink)" }}>{title}</h2>
          {action}
        </header>
      ) : null}
      <div style={{ padding: padded ? "var(--space-md)" : 0 }}>{children}</div>
      {footer ? <footer style={{ padding: "var(--space-md)", borderTop: "1px solid var(--hairline)", background: "var(--surface-2)" }}>{footer}</footer> : null}
    </section>
  );
}
