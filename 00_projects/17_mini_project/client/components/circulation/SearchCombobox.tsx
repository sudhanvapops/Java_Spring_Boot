"use client";

import { useState, type ReactNode } from "react";
import { Input } from "@/components/forms/Input";

export interface ComboOption {
  id?: string | number;
  disabled?: boolean;
}

export interface SearchComboboxProps<T extends ComboOption> {
  placeholder?: string;
  query?: string;
  onQueryChange?: (q: string) => void;
  options: T[];
  /** Renders one row. Show the facts needed to choose, not just a name. */
  renderOption: (option: T) => ReactNode;
  onSelect?: (option: T) => void;
  /** Shown when nothing matches — be specific, name the query. */
  emptyMessage?: ReactNode;
  maxHeight?: number;
}

export function SearchCombobox<T extends ComboOption>({
  placeholder,
  query,
  onQueryChange,
  options = [],
  renderOption,
  onSelect,
  emptyMessage = "Nothing matches that.",
  maxHeight = 260,
}: SearchComboboxProps<T>) {
  return (
    <div>
      <Input iconLeft="search" placeholder={placeholder} value={query} onChange={(e) => onQueryChange?.(e.target.value)} />
      <div
        role="listbox"
        style={{
          marginTop: "var(--space-xs)",
          maxHeight,
          overflowY: "auto",
          border: "1px solid var(--hairline)",
          borderRadius: "var(--radius-md)",
          background: "var(--surface-2)",
        }}
      >
        {options.length === 0 ? (
          <div style={{ padding: "var(--space-md)", font: "var(--type-body-sm)", color: "var(--ink-subtle)" }}>{emptyMessage}</div>
        ) : (
          options.map((o, i) => (
            <Option key={o.id != null ? o.id : i} option={o} last={i === options.length - 1} onSelect={onSelect} render={renderOption} />
          ))
        )}
      </div>
    </div>
  );
}

function Option<T extends ComboOption>({
  option,
  last,
  onSelect,
  render,
}: {
  option: T;
  last: boolean;
  onSelect?: (option: T) => void;
  render: (option: T) => ReactNode;
}) {
  const [hover, setHover] = useState(false);
  const selectable = !option.disabled;
  return (
    <div
      role="option"
      aria-disabled={!selectable}
      aria-selected={false}
      tabIndex={0}
      onClick={selectable && onSelect ? () => onSelect(option) : undefined}
      onMouseEnter={() => setHover(true)}
      onMouseLeave={() => setHover(false)}
      style={{
        display: "flex",
        alignItems: "center",
        gap: "var(--space-md)",
        padding: "var(--space-sm) var(--space-md)",
        minHeight: 44,
        borderBottom: last ? "none" : "1px solid var(--hairline)",
        background: hover && selectable ? "var(--surface-3)" : "transparent",
        cursor: selectable ? "pointer" : "default",
        transition: "var(--transition-hover)",
      }}
    >
      {render(option)}
    </div>
  );
}
