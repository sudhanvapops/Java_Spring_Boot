import { SearchCombobox } from "./SearchCombobox";
import { AvailabilityBar } from "@/components/data/AvailabilityBar";
import { Button } from "@/components/forms/Button";

export interface BookOption {
  id: number;
  title: string;
  author: string;
  available: number;
  total: number;
  /** Blocks Add. */
  disabled?: boolean;
  /** Why it can't be added: "None available", "Inactive", "Priya already has this book",
   *  "Already in this basket", "That's over Priya's limit of 5 books. Remove one first." */
  reason?: string;
}

export interface BookSearchComboboxProps {
  books: BookOption[];
  query?: string;
  onQueryChange?: (q: string) => void;
  onAdd?: (book: BookOption) => void;
  placeholder?: string;
}

export function BookSearchCombobox({ books = [], query, onQueryChange, onAdd, placeholder = "Search the catalogue…" }: BookSearchComboboxProps) {
  return (
    <SearchCombobox
      placeholder={placeholder}
      query={query}
      onQueryChange={onQueryChange}
      options={books}
      emptyMessage={query ? `No books match "${query}".` : "Search by title or author."}
      renderOption={(b) => (
        <>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div
              style={{
                font: "var(--type-body-sm)",
                color: b.disabled ? "var(--ink-subtle)" : "var(--ink)",
                overflow: "hidden",
                textOverflow: "ellipsis",
                whiteSpace: "nowrap",
              }}
            >
              {b.title}
            </div>
            <div style={{ font: "var(--type-caption)", color: "var(--ink-subtle)" }}>{b.author}</div>
            {b.reason ? <div style={{ marginTop: 2, font: "var(--type-caption)", color: "var(--danger)" }}>{b.reason}</div> : null}
          </div>
          <AvailabilityBar available={b.available} total={b.total} />
          <Button size="sm" variant="secondary" disabled={b.disabled} onClick={() => onAdd?.(b)}>
            Add
          </Button>
        </>
      )}
    />
  );
}
