import type { CSSProperties, ReactNode } from "react";
import { AvailabilityBar } from "@/components/data/AvailabilityBar";
import { StatusBadge } from "@/components/data/StatusBadge";

export interface BookRowBook {
  id: number;
  title: string;
  author: string;
  /** Only read when showAvailability is true. */
  available?: number;
  total?: number;
  isActive?: boolean;
}

export interface BookRowProps {
  book: BookRowBook;
  /** Trailing action: an Add button, a "Take it back" button, an IconButton. */
  action?: ReactNode;
  /** Right-aligned facts before the action — a member name, a due date. */
  meta?: ReactNode;
  /** Set false where the row is about a specific loan rather than the title's
   *  shelf stock — a due-back list, a receipt. Never pass invented counts to
   *  keep the bar on screen. */
  showAvailability?: boolean;
  style?: CSSProperties;
}

export function BookRow({ book, action, meta, showAvailability = true, style }: BookRowProps) {
  return (
    <div style={{ display: "flex", alignItems: "center", gap: "var(--space-md)", minHeight: 52, padding: "var(--space-xs) var(--space-md)", ...style }}>
      <div style={{ flex: 1, minWidth: 0 }}>
        <div
          style={{
            font: "var(--type-body-sm)",
            color: book.isActive === false ? "var(--ink-subtle)" : "var(--ink)",
            overflow: "hidden",
            textOverflow: "ellipsis",
            whiteSpace: "nowrap",
          }}
        >
          {book.title}
        </div>
        <div style={{ font: "var(--type-caption)", color: "var(--ink-subtle)" }}>{book.author}</div>
      </div>
      {book.isActive === false ? <StatusBadge status="inactive" /> : null}
      {meta ? <div style={{ flex: "0 0 auto", font: "var(--type-body-sm)", color: "var(--ink-subtle)", textAlign: "right", whiteSpace: "nowrap" }}>{meta}</div> : null}
      {showAvailability ? <AvailabilityBar available={book.available ?? 0} total={book.total ?? 0} /> : null}
      {action}
    </div>
  );
}
