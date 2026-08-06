import type { CSSProperties, MouseEvent, ReactNode } from "react";
import { Icon } from "@/components/core/Icon";

export interface PageHeaderProps {
  title: ReactNode;
  /** One line of context in body-sm / ink-subtle. */
  subtitle?: ReactNode;
  /** Back affordance on detail and form pages: "Back to books". */
  back?: { label: string; href?: string; onClick?: (e: MouseEvent) => void };
  /** Primary (and at most one secondary) action. */
  action?: ReactNode;
  /** Inline metadata row — badges, a mono ID, an email. */
  meta?: ReactNode;
  style?: CSSProperties;
}

export function PageHeader({ title, subtitle, back, action, meta, style }: PageHeaderProps) {
  return (
    <header style={{ marginBottom: "var(--space-lg)", ...style }}>
      {back ? (
        <a
          href={back.href || "#"}
          onClick={back.onClick}
          style={{
            display: "inline-flex",
            alignItems: "center",
            gap: "var(--space-xxs)",
            font: "var(--type-body-sm)",
            color: "var(--ink-subtle)",
            marginBottom: "var(--space-sm)",
          }}
        >
          <Icon name="arrow-left" size={14} />
          {back.label}
        </a>
      ) : null}
      <div style={{ display: "flex", alignItems: "flex-start", justifyContent: "space-between", gap: "var(--space-lg)" }}>
        <div style={{ minWidth: 0 }}>
          <h1 style={{ margin: 0, font: "var(--type-headline)", letterSpacing: "var(--track-headline)", color: "var(--ink)" }}>{title}</h1>
          {subtitle ? <p style={{ margin: "var(--space-xxs) 0 0", font: "var(--type-body-sm)", color: "var(--ink-subtle)" }}>{subtitle}</p> : null}
          {meta ? (
            <div style={{ display: "flex", alignItems: "center", gap: "var(--space-xs)", marginTop: "var(--space-xs)", font: "var(--type-body-sm)", color: "var(--ink-subtle)" }}>
              {meta}
            </div>
          ) : null}
        </div>
        {action ? <div style={{ display: "flex", gap: "var(--space-xs)", flex: "0 0 auto" }}>{action}</div> : null}
      </div>
    </header>
  );
}
