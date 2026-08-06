import type { CSSProperties } from "react";
import { Icon } from "@/components/core/Icon";
import { CURRENCY } from "@/lib/utils/currency";

export interface FineDisplayProps {
  /** Fine in currency units. 0 renders as "no fine". */
  amount?: number;
  /** Days overdue, prefixed to the amount: "3 days over · ₹30". */
  daysOverdue?: number;
  /** Marks a client-side estimate before the server confirms. */
  estimate?: boolean;
  /** Symbol. Defaults to NEXT_PUBLIC_CURRENCY_SYMBOL — the API has no currency field. */
  currency?: string;
  style?: CSSProperties;
}

export function FineDisplay({ amount = 0, daysOverdue, estimate = false, currency = CURRENCY, style }: FineDisplayProps) {
  if (!amount) {
    return (
      <span style={{ font: "var(--type-caption)", color: "var(--ink-subtle)", ...style }}>no fine</span>
    );
  }
  return (
    <span style={{ display: "inline-flex", alignItems: "center", gap: "var(--space-xxs)", color: "var(--danger)", ...style }}>
      <Icon name="triangle-alert" size={12} />
      {daysOverdue ? (
        <span style={{ font: "var(--type-caption)" }}>
          {daysOverdue} {daysOverdue === 1 ? "day" : "days"} over ·
        </span>
      ) : null}
      <span style={{ font: "var(--type-mono)", fontVariantNumeric: "tabular-nums" }}>
        {currency}
        {amount.toFixed(2).replace(/\.00$/, "")}
      </span>
      {estimate ? <span style={{ font: "var(--type-caption)", color: "var(--ink-subtle)" }}>est.</span> : null}
    </span>
  );
}
