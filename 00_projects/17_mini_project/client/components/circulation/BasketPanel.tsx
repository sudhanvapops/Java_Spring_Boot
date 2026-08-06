import { Panel } from "@/components/panels/Panel";
import { Button } from "@/components/forms/Button";
import { IconButton } from "@/components/forms/IconButton";

export interface BasketBook {
  id: number;
  title: string;
}

export interface BasketPanelProps {
  books: BasketBook[];
  /** Formatted due date: today + MAX_BORROW_DAYS. */
  dueDate?: string;
  onRemove?: (book: BasketBook) => void;
  onConfirm?: () => void;
  disabled?: boolean;
  step?: number;
}

export function BasketPanel({ books = [], dueDate, onRemove, onConfirm, disabled = false, step = 3 }: BasketPanelProps) {
  const n = books.length;
  return (
    <Panel
      step={step}
      title="Confirm"
      padded={false}
      footer={
        <Button fullWidth disabled={disabled || n === 0} onClick={onConfirm}>
          {n === 0 ? "Add a book to lend" : `Lend ${n}${n === 1 ? " book" : " books"}`}
        </Button>
      }
    >
      <div
        style={{
          display: "flex",
          alignItems: "baseline",
          justifyContent: "space-between",
          gap: "var(--space-sm)",
          padding: "var(--space-md)",
          borderBottom: n ? "1px solid var(--hairline)" : "none",
        }}
      >
        <span style={{ font: "var(--type-body-sm)", color: "var(--ink)" }}>
          {n} {n === 1 ? "book" : "books"}
        </span>
        {dueDate ? <span style={{ font: "var(--type-mono)", color: "var(--ink-muted)" }}>due {dueDate}</span> : null}
      </div>
      {n === 0 ? (
        <p style={{ margin: 0, padding: "var(--space-md)", font: "var(--type-body-sm)", color: "var(--ink-subtle)" }}>Nothing in the basket yet.</p>
      ) : (
        books.map((b) => (
          <div key={b.id} style={{ display: "flex", alignItems: "center", gap: "var(--space-xs)", minHeight: 44, padding: "var(--space-xs) var(--space-md)", borderBottom: "1px solid var(--hairline)" }}>
            <span style={{ flex: 1, minWidth: 0, font: "var(--type-body-sm)", color: "var(--ink)", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{b.title}</span>
            <IconButton icon="x" label={"Remove " + b.title} onClick={() => onRemove?.(b)} />
          </div>
        ))
      )}
    </Panel>
  );
}
