import type { CSSProperties, ReactNode } from "react";

export interface TabItem {
  value: string;
  label: ReactNode;
  count?: number;
}

export interface TabsProps {
  tabs: TabItem[];
  value: string;
  onChange?: (value: string) => void;
  style?: CSSProperties;
}

export function Tabs({ tabs, value, onChange, style }: TabsProps) {
  return (
    <div role="tablist" style={{ display: "flex", gap: "var(--space-xxs)", ...style }}>
      {tabs.map((t) => {
        const active = t.value === value;
        return (
          <button
            key={t.value}
            role="tab"
            aria-selected={active}
            onClick={() => onChange?.(t.value)}
            style={{
              display: "inline-flex",
              alignItems: "center",
              gap: "var(--space-xxs)",
              height: 30,
              padding: "0 var(--space-sm)",
              background: active ? "var(--surface-2)" : "transparent",
              border: "1px solid " + (active ? "var(--hairline)" : "transparent"),
              borderRadius: "var(--radius-badge)",
              cursor: "pointer",
              font: "var(--type-button)",
              color: active ? "var(--ink)" : "var(--ink-subtle)",
              transition: "var(--transition-hover)",
              whiteSpace: "nowrap",
            }}
          >
            {t.label}
            {t.count != null ? <span style={{ font: "var(--type-caption)", color: "var(--ink-tertiary)", fontVariantNumeric: "tabular-nums" }}>{t.count}</span> : null}
          </button>
        );
      })}
    </div>
  );
}
