import { Fragment, type ReactNode } from "react";
import { Icon } from "@/components/core/Icon";
import { IconButton } from "@/components/forms/IconButton";

export interface Crumb {
  label: string;
  href?: string;
}

export interface TopbarProps {
  breadcrumb?: Crumb[];
  onNavigate?: (href: string) => void;
  onSearch?: () => void;
  account?: { name: string; role: string };
  /** Extra controls between search and the account menu. */
  right?: ReactNode;
  onAccountClick?: () => void;
}

export function Topbar({ breadcrumb = [], onNavigate, onSearch, account, right, onAccountClick }: TopbarProps) {
  return (
    <header
      style={{
        display: "flex",
        alignItems: "center",
        gap: "var(--space-md)",
        height: "var(--topbar-height)",
        flex: "0 0 auto",
        padding: "0 var(--space-lg)",
        background: "var(--canvas)",
        borderBottom: "1px solid var(--hairline)",
      }}
    >
      <nav aria-label="Breadcrumb" style={{ display: "flex", alignItems: "center", gap: "var(--space-xxs)", flex: 1, minWidth: 0, font: "var(--type-body-sm)" }}>
        {breadcrumb.map((c, i) => (
          <Fragment key={i}>
            {i > 0 ? <Icon name="chevron-right" size={14} color="var(--ink-tertiary)" /> : null}
            {c.href && i < breadcrumb.length - 1 ? (
              <a
                href={c.href}
                onClick={(e) => {
                  e.preventDefault();
                  onNavigate?.(c.href!);
                }}
                style={{ color: "var(--ink-subtle)" }}
              >
                {c.label}
              </a>
            ) : (
              <span style={{ color: "var(--ink)" }}>{c.label}</span>
            )}
          </Fragment>
        ))}
      </nav>
      <button
        onClick={onSearch}
        style={{
          display: "flex",
          alignItems: "center",
          gap: "var(--space-xs)",
          height: 30,
          padding: "0 var(--space-xs) 0 var(--space-sm)",
          background: "var(--surface-1)",
          border: "1px solid var(--hairline)",
          borderRadius: "var(--radius-md)",
          cursor: "pointer",
          font: "var(--type-body-sm)",
          color: "var(--ink-subtle)",
        }}
      >
        <Icon name="search" size={14} />
        Search
        <span style={{ font: "var(--type-mono)", fontSize: 11, color: "var(--ink-tertiary)", border: "1px solid var(--hairline)", borderRadius: "var(--radius-xs)", padding: "1px 4px" }}>
          ⌘K
        </span>
      </button>
      {right}
      {account ? <IconButton icon="circle-user-round" label="Account menu" onClick={onAccountClick} /> : null}
    </header>
  );
}
