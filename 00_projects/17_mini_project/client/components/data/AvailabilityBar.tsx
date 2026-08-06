import type { CSSProperties } from "react";

export interface AvailabilityBarProps {
  available: number;
  total: number;
  /** Column width in px. Default 84. */
  width?: number;
  style?: CSSProperties;
}

export function AvailabilityBar({ available, total, width = 84, style }: AvailabilityBarProps) {
  const none = available <= 0;
  const pct = total > 0 ? Math.max(0, Math.min(1, available / total)) * 100 : 0;
  return (
    <div style={{ width, ...style }}>
      <div
        style={{
          font: "var(--type-mono)",
          fontVariantNumeric: "tabular-nums",
          color: none ? "var(--danger)" : "var(--ink-muted)",
          whiteSpace: "nowrap",
        }}
      >
        {none ? "None available" : `${available} of ${total}`}
      </div>
      <div style={{ marginTop: 5, height: 3, borderRadius: "var(--radius-pill)", background: "var(--hairline)", overflow: "hidden" }}>
        <div
          style={{
            width: pct + "%",
            height: "100%",
            borderRadius: "var(--radius-pill)",
            background: none ? "var(--danger)" : "var(--primary)",
          }}
        />
      </div>
    </div>
  );
}
