"use client";

import { useEffect, useState, type CSSProperties, type ReactNode } from "react";
import { Icon } from "@/components/core/Icon";
import { usePrefersReducedMotion } from "@/components/core/Motion";

export interface StatCardProps {
  /** Caption above the number: "Out on loan", "Due today". */
  label: ReactNode;
  value: number | string;
  /** "danger" for a non-zero overdue count — turns the number red and adds an icon. */
  tone?: "default" | "danger";
  /** Counts up on mount, under 800ms, ease-out, off under prefers-reduced-motion. */
  animate?: boolean;
  /** Optional caption under the number. */
  hint?: ReactNode;
  style?: CSSProperties;
}

export function StatCard({ label, value, tone = "default", animate = false, hint, style }: StatCardProps) {
  const shown = useCount(value, animate);
  const danger = tone === "danger";
  return (
    <div
      style={{
        padding: "var(--space-lg)",
        background: "var(--surface-1)",
        border: "1px solid var(--hairline)",
        borderRadius: "var(--radius-card)",
        ...style,
      }}
    >
      <div style={{ display: "flex", alignItems: "center", gap: "var(--space-xxs)", font: "var(--type-caption)", color: "var(--ink-subtle)" }}>
        {danger ? <Icon name="triangle-alert" size={12} color="var(--danger)" /> : null}
        {label}
      </div>
      <div
        style={{
          marginTop: "var(--space-xs)",
          font: "var(--type-display-md)",
          letterSpacing: "var(--track-display-md)",
          fontVariantNumeric: "tabular-nums",
          color: danger ? "var(--danger)" : "var(--ink)",
        }}
      >
        {shown}
      </div>
      {hint ? <div style={{ marginTop: "var(--space-xxs)", font: "var(--type-caption)", color: "var(--ink-subtle)" }}>{hint}</div> : null}
    </div>
  );
}

/**
 * Ported from the reference StatCard.jsx's `useCount`, restructured so the
 * "sync to a new target while not animating" case is handled during render
 * (React's documented pattern for adjusting state from props) rather than
 * with a synchronous setState at the top of the effect — the effect itself
 * only ever calls setState from inside the requestAnimationFrame callback,
 * which is legitimate deferred/async work, not a direct effect-body call.
 */
function useCount(target: number | string, animate: boolean): number | string {
  const reduced = usePrefersReducedMotion();
  const shouldAnimate = animate && !reduced && typeof target === "number";

  const [n, setN] = useState<number | string>(shouldAnimate ? 0 : target);
  const [prevTarget, setPrevTarget] = useState(target);
  if (target !== prevTarget) {
    setPrevTarget(target);
    if (!shouldAnimate) setN(target);
  }

  useEffect(() => {
    if (!shouldAnimate) return;
    const start = performance.now();
    const dur = 800;
    let raf: number;
    const tick = (t: number) => {
      const p = Math.min(1, (t - start) / dur);
      setN(Math.round((target as number) * (1 - Math.pow(1 - p, 3))));
      if (p < 1) raf = requestAnimationFrame(tick);
    };
    raf = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(raf);
  }, [target, shouldAnimate]);

  return n;
}
