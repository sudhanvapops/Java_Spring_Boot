import { Checkbox } from "@/components/forms/Checkbox";
import { FineDisplay } from "@/components/data/FineDisplay";

export interface UnreturnedRecord {
  id: string | number;
  title: string;
  author?: string;
  /** Formatted date, e.g. "11 Aug". */
  dueDate: string;
  /** Days past due. Drives the red treatment alongside the icon. */
  daysOverdue?: number;
  /** daysOverdue × FINE_PER_DAY. 0 renders as "no fine". */
  fine?: number;
  /** Marks the fine as a client estimate until the server confirms. */
  estimate?: boolean;
}

export interface UnreturnedListProps {
  records: UnreturnedRecord[];
  /** ids of ticked rows. */
  selected?: (string | number)[];
  onToggle?: (record: UnreturnedRecord) => void;
  /** False for the read-only listing on member and book detail. */
  selectable?: boolean;
}

export function UnreturnedList({ records = [], selected = [], onToggle, selectable = true }: UnreturnedListProps) {
  return (
    <div>
      {records.map((r, i) => {
        const on = selected.includes(r.id);
        return (
          <div
            key={r.id}
            style={{
              display: "flex",
              alignItems: "center",
              gap: "var(--space-md)",
              minHeight: 52,
              padding: "var(--space-xs) var(--space-md)",
              borderBottom: i === records.length - 1 ? "none" : "1px solid var(--hairline)",
              background: on ? "var(--surface-2)" : "transparent",
              transition: "var(--transition-hover)",
            }}
          >
            {selectable ? <Checkbox id={"ret-" + r.id} checked={on} onChange={() => onToggle?.(r)} style={{ minHeight: 0 }} /> : null}
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ font: "var(--type-body-sm)", color: "var(--ink)", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{r.title}</div>
              {r.author ? <div style={{ font: "var(--type-caption)", color: "var(--ink-subtle)" }}>{r.author}</div> : null}
            </div>
            <div style={{ font: "var(--type-mono)", color: r.daysOverdue ? "var(--danger)" : "var(--ink-subtle)", flex: "0 0 auto" }}>due {r.dueDate}</div>
            <div style={{ minWidth: 132, textAlign: "right", flex: "0 0 auto" }}>
              <FineDisplay amount={r.fine} daysOverdue={r.daysOverdue} estimate={r.estimate} />
            </div>
          </div>
        );
      })}
    </div>
  );
}
