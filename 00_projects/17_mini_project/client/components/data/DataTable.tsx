"use client";

import { useState, type ReactNode } from "react";
import { SkeletonRows } from "./Skeleton";
import { EmptyState } from "@/components/feedback/EmptyState";
import { ErrorPanel } from "@/components/feedback/ErrorPanel";
import { Icon } from "@/components/core/Icon";
import { useMediaQuery } from "@/lib/hooks/useMediaQuery";

export interface DataTableColumn<T> {
  key: string;
  header: ReactNode;
  /** CSS grid track. Omit for a flexible column. */
  width?: string;
  align?: "left" | "right";
  sortable?: boolean;
  render?: (row: T) => ReactNode;
}

export interface DataTableSort {
  key: string;
  dir: "asc" | "desc";
}

export interface DataTableProps<T> {
  columns: DataTableColumn<T>[];
  rows: T[];
  /** Which of the four states to render. */
  state?: "loading" | "empty" | "error" | "loaded";
  getRowKey?: (row: T) => string | number;
  onRowClick?: (row: T) => void;
  sort?: DataTableSort;
  onSortChange?: (sort: DataTableSort) => void;
  /** Rendered when state is "empty" — pass a specific EmptyState, never "No results". */
  empty?: ReactNode;
  /** Rendered when state is "error" — defaults to a generic ErrorPanel. */
  error?: ReactNode;
  /** Action column content, revealed on hover but always keyboard-reachable. */
  rowActions?: (row: T) => ReactNode;
  /** Keeps row actions at full opacity instead of fading them out at rest. */
  alwaysShowActions?: boolean;
  /** Per-row emphasis. "danger" for overdue rows, "muted" for inactive records. */
  rowTone?: (row: T) => "danger" | "muted" | undefined;
  /** Viewport width below which each row renders as a card instead of a grid row. Default 768. */
  cardBreakpoint?: number;
}

/** True below `max` px. Below 768 the table becomes a stack of cards. */
function useNarrow(max = 768) {
  return useMediaQuery(`(max-width:${max - 1}px)`);
}

export function DataTable<T>({
  columns,
  rows,
  state = "loaded",
  getRowKey,
  onRowClick,
  sort,
  onSortChange,
  empty,
  error,
  rowActions,
  rowTone,
  alwaysShowActions = false,
  cardBreakpoint = 768,
}: DataTableProps<T>) {
  const narrow = useNarrow(cardBreakpoint);

  if (state === "loading") {
    return (
      <div>
        <HeaderRow columns={columns} />
        <SkeletonRows rows={7} columns={columns.map((c) => c.width || "30%")} />
      </div>
    );
  }
  if (state === "error") {
    return <div style={{ padding: "var(--space-lg) 0" }}>{error || <ErrorPanel />}</div>;
  }
  if (state === "empty") {
    return <div style={{ padding: "var(--space-xxl) 0" }}>{empty || <EmptyState headline="Nothing here yet" />}</div>;
  }

  // Below the breakpoint a grid row can't hold its columns without either
  // clipping or scrolling sideways, so each row becomes a card instead.
  if (narrow) {
    return (
      <div style={{ display: "grid", gap: "var(--space-xs)" }}>
        {rows.map((row, i) => (
          <CardRow
            key={getRowKey ? getRowKey(row) : i}
            row={row}
            columns={columns}
            onRowClick={onRowClick}
            rowActions={rowActions}
            tone={rowTone && rowTone(row)}
          />
        ))}
      </div>
    );
  }

  // min-content keeps every track at least as wide as its content, so a cell can
  // never paint over its neighbour; the page scrolls sideways when space runs out.
  return (
    <div role="table" style={{ minWidth: "min-content" }}>
      <HeaderRow columns={columns} sort={sort} onSortChange={onSortChange} hasActions={!!rowActions} />
      {rows.map((row, i) => (
        <Row
          key={getRowKey ? getRowKey(row) : i}
          row={row}
          columns={columns}
          onRowClick={onRowClick}
          rowActions={rowActions}
          alwaysShowActions={alwaysShowActions}
          tone={rowTone && rowTone(row)}
        />
      ))}
    </div>
  );
}

function HeaderRow<T>({
  columns,
  sort,
  onSortChange,
  hasActions,
}: {
  columns: DataTableColumn<T>[];
  sort?: DataTableSort;
  onSortChange?: (sort: DataTableSort) => void;
  hasActions?: boolean;
}) {
  return (
    <div
      role="row"
      style={{
        position: "sticky",
        top: 0,
        zIndex: 2,
        display: "grid",
        gridTemplateColumns: gridCols(columns, !!hasActions),
        gap: "var(--space-lg)",
        alignItems: "center",
        height: 36,
        background: "var(--canvas)",
        borderBottom: "1px solid var(--hairline)",
      }}
    >
      {columns.map((c) => {
        const active = sort && sort.key === c.key;
        return (
          <div
            key={c.key}
            role="columnheader"
            onClick={
              c.sortable && onSortChange
                ? () => onSortChange({ key: c.key, dir: active && sort!.dir === "asc" ? "desc" : "asc" })
                : undefined
            }
            style={{
              display: "flex",
              alignItems: "center",
              gap: "var(--space-xxs)",
              font: "var(--type-caption)",
              color: active ? "var(--ink-muted)" : "var(--ink-subtle)",
              cursor: c.sortable ? "pointer" : "default",
              userSelect: "none",
              justifyContent: c.align === "right" ? "flex-end" : "flex-start",
            }}
          >
            {c.header}
            {c.sortable ? (
              <Icon
                name={active ? (sort!.dir === "asc" ? "arrow-up" : "arrow-down") : "chevrons-up-down"}
                size={11}
                color={active ? "var(--primary)" : "var(--ink-tertiary)"}
              />
            ) : null}
          </div>
        );
      })}
      {hasActions ? <div /> : null}
    </div>
  );
}

function Row<T>({
  row,
  columns,
  onRowClick,
  rowActions,
  tone,
  alwaysShowActions,
}: {
  row: T;
  columns: DataTableColumn<T>[];
  onRowClick?: (row: T) => void;
  rowActions?: (row: T) => ReactNode;
  tone?: "danger" | "muted";
  alwaysShowActions?: boolean;
}) {
  const [hover, setHover] = useState(false);
  return (
    <div
      role="row"
      tabIndex={onRowClick ? 0 : undefined}
      onClick={onRowClick ? () => onRowClick(row) : undefined}
      onMouseEnter={() => setHover(true)}
      onMouseLeave={() => setHover(false)}
      style={{
        display: "grid",
        gridTemplateColumns: gridCols(columns, !!rowActions),
        gap: "var(--space-lg)",
        alignItems: "center",
        minHeight: 52,
        padding: "var(--space-sm) 0",
        background: hover ? "var(--surface-row-hover)" : "transparent",
        borderBottom: "1px solid var(--hairline)",
        cursor: onRowClick ? "pointer" : "default",
        transition: "var(--transition-hover)",
      }}
    >
      {columns.map((c) => (
        <div
          key={c.key}
          role="cell"
          style={{
            font: "var(--type-body-sm)",
            color: tone === "danger" ? "var(--danger)" : tone === "muted" ? "var(--ink-subtle)" : "var(--ink)",
            textAlign: c.align === "right" ? "right" : "left",
            minWidth: 0,
            overflow: "hidden",
            textOverflow: "ellipsis",
          }}
        >
          {c.render ? c.render(row) : String((row as Record<string, unknown>)[c.key] ?? "")}
        </div>
      ))}
      {rowActions ? (
        <div
          role="cell"
          style={{
            display: "flex",
            gap: "var(--space-xxs)",
            justifyContent: "flex-end",
            opacity: alwaysShowActions || hover ? 1 : 0.35,
            transition: "opacity var(--duration-fast) ease-out",
          }}
        >
          {rowActions(row)}
        </div>
      ) : null}
    </div>
  );
}

function CardRow<T>({
  row,
  columns,
  onRowClick,
  rowActions,
  tone,
}: {
  row: T;
  columns: DataTableColumn<T>[];
  onRowClick?: (row: T) => void;
  rowActions?: (row: T) => ReactNode;
  tone?: "danger" | "muted";
}) {
  const [lead, ...rest] = columns;
  return (
    <div
      role="row"
      tabIndex={onRowClick ? 0 : undefined}
      onClick={onRowClick ? () => onRowClick(row) : undefined}
      style={{
        padding: "var(--space-md)",
        background: "var(--surface-1)",
        border: "1px solid var(--hairline)",
        borderRadius: "var(--radius-panel)",
        cursor: onRowClick ? "pointer" : "default",
      }}
    >
      <div
        role="cell"
        style={{
          font: "var(--type-body-sm)",
          fontWeight: "var(--weight-medium)",
          color: tone === "danger" ? "var(--danger)" : tone === "muted" ? "var(--ink-subtle)" : "var(--ink)",
          textWrap: "pretty",
        }}
      >
        {lead.render ? lead.render(row) : String((row as Record<string, unknown>)[lead.key] ?? "")}
      </div>
      <div style={{ display: "grid", gap: "var(--space-xxs)", marginTop: "var(--space-sm)" }}>
        {rest.map((c) => (
          <div key={c.key} role="cell" style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: "var(--space-md)" }}>
            <span style={{ font: "var(--type-caption)", color: "var(--ink-tertiary)", flex: "0 0 auto" }}>{c.header}</span>
            <span style={{ font: "var(--type-body-sm)", color: "var(--ink-muted)", textAlign: "right", minWidth: 0 }}>
              {c.render ? c.render(row) : String((row as Record<string, unknown>)[c.key] ?? "")}
            </span>
          </div>
        ))}
      </div>
      {rowActions ? (
        <div role="cell" style={{ display: "flex", gap: "var(--space-xs)", marginTop: "var(--space-md)", paddingTop: "var(--space-sm)", borderTop: "1px solid var(--hairline)" }}>
          {rowActions(row)}
        </div>
      ) : null}
    </div>
  );
}

function gridCols<T>(columns: DataTableColumn<T>[], hasActions: boolean) {
  // Flexible tracks get a floor so a long title can never be squeezed to nothing
  // by the fixed tracks beside it; the table scrolls sideways instead.
  return columns.map((c) => c.width || "minmax(160px,1fr)").join(" ") + (hasActions ? " auto" : "");
}
