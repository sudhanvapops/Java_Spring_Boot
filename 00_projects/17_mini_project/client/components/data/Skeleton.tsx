import type { CSSProperties } from "react";

export interface SkeletonProps {
  width?: number | string;
  height?: number | string;
  radius?: string;
  style?: CSSProperties;
}

export function Skeleton({ width = "100%", height = 12, radius = "var(--radius-xs)", style }: SkeletonProps) {
  return (
    <span
      style={{
        display: "block",
        width,
        height,
        borderRadius: radius,
        background: "linear-gradient(90deg,var(--surface-2) 25%,var(--surface-3) 50%,var(--surface-2) 75%)",
        backgroundSize: "200% 100%",
        animation: "stacks-shimmer 1.4s linear infinite",
        ...style,
      }}
    />
  );
}

export interface SkeletonRowsProps {
  rows?: number;
  /** Column widths, matching the real table's columns. */
  columns?: (number | string)[];
  style?: CSSProperties;
}

export function SkeletonRows({ rows = 6, columns = ["40%", "24%", "84px", "72px"], style }: SkeletonRowsProps) {
  return (
    <div style={style}>
      {Array.from({ length: rows }).map((_, r) => (
        <div
          key={r}
          style={{ display: "flex", alignItems: "center", gap: "var(--space-lg)", height: 52, borderBottom: "1px solid var(--hairline)" }}
        >
          {columns.map((c, i) => (
            <Skeleton key={i} width={c} height={10} />
          ))}
        </div>
      ))}
    </div>
  );
}
