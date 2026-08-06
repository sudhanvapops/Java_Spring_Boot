"use client";

import { useEffect, useMemo, useRef, useState, type ReactNode } from "react";
import { Icon } from "@/components/core/Icon";
import { MotionDiv, Presence, EASE } from "@/components/core/Motion";

export interface CommandItem {
  id: string | number;
  label: string;
  /** Second line — an email, an author, an availability count. */
  meta?: ReactNode;
  /** Right-aligned hint, e.g. "3 of 5" or a status word. */
  trailing?: ReactNode;
  /** Lucide icon name. */
  icon?: string;
  /** Where selecting it goes. Read by the host in onSelect. */
  href?: string;
}

export interface CommandGroup {
  /** Eyebrow heading: BOOKS, MEMBERS, GO TO. */
  label: string;
  items: CommandItem[];
}

export interface CommandPaletteProps {
  open: boolean;
  onClose?: () => void;
  /** Pass an opener and the component binds ⌘K / Ctrl-K itself. */
  onRequestOpen?: () => void;
  /** Grouped results. Empty groups are skipped. */
  groups: CommandGroup[];
  query?: string;
  onQueryChange?: (q: string) => void;
  onSelect?: (item: CommandItem & { group: string }) => void;
  placeholder?: string;
  /** Shown when every group is empty — name the query. */
  emptyMessage?: ReactNode;
}

/**
 * Ported from stacks-design-system/project/components/navigation/CommandPalette.jsx
 * with Escape-to-close and a focus trap added — the reference version only
 * wired arrow/enter navigation, not the full keyboard-reachability the spec
 * requires (readme.md "Accessibility floor").
 */
export function CommandPalette({
  open,
  onClose,
  onRequestOpen,
  groups = [],
  query = "",
  onQueryChange,
  onSelect,
  placeholder = "Search…",
  emptyMessage,
}: CommandPaletteProps) {
  const flat = useMemo(() => groups.flatMap((g) => g.items.map((it) => ({ ...it, group: g.label }))), [groups]);
  const [active, setActive] = useState(0);
  const inputRef = useRef<HTMLInputElement>(null);
  const listRef = useRef<HTMLDivElement>(null);

  // Reset the highlighted row when the query or open-state changes. Adjusting
  // state during render — React's documented pattern for this, tracked with
  // useState rather than a ref since refs can't be read/written during
  // render — avoids an extra render pass versus doing this in an effect.
  const resetKey = `${open}:${query}`;
  const [prevResetKey, setPrevResetKey] = useState(resetKey);
  if (resetKey !== prevResetKey) {
    setPrevResetKey(resetKey);
    if (active !== 0) setActive(0);
  }

  useEffect(() => {
    if (open && inputRef.current) inputRef.current.focus();
  }, [open]);

  // ⌘K / Ctrl-K lives here so the host doesn't need a separate hook import.
  useEffect(() => {
    if (!onRequestOpen) return;
    const on = (e: KeyboardEvent) => {
      if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === "k") {
        e.preventDefault();
        onRequestOpen();
      }
    };
    window.addEventListener("keydown", on);
    return () => window.removeEventListener("keydown", on);
  }, [onRequestOpen]);

  // Keep the highlighted row inside the scroll box without scrollIntoView.
  useEffect(() => {
    const box = listRef.current;
    if (!box) return;
    const el = box.querySelector<HTMLElement>('[data-active="true"]');
    if (!el) return;
    const top = el.offsetTop;
    const bottom = top + el.offsetHeight;
    if (top < box.scrollTop) box.scrollTop = top;
    else if (bottom > box.scrollTop + box.clientHeight) box.scrollTop = bottom - box.clientHeight;
  }, [active, query]);

  const keydown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Escape") {
      e.preventDefault();
      onClose?.();
      return;
    }
    if (e.key === "ArrowDown") {
      e.preventDefault();
      setActive((i) => Math.min(flat.length - 1, i + 1));
      return;
    }
    if (e.key === "ArrowUp") {
      e.preventDefault();
      setActive((i) => Math.max(0, i - 1));
      return;
    }
    if (e.key === "Enter") {
      e.preventDefault();
      const it = flat[active];
      if (it && onSelect) onSelect(it);
    }
  };

  let index = -1;

  return (
    <Presence>
      {open ? (
        <MotionDiv
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          transition={EASE.fast}
          onClick={onClose}
          style={{ position: "fixed", inset: 0, display: "grid", justifyItems: "center", alignContent: "start", paddingTop: "12vh", background: "rgba(5,5,6,.72)", zIndex: 80 }}
        >
          <MotionDiv
            initial={{ opacity: 0, y: -8 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -8 }}
            transition={EASE.medium}
            onClick={(e) => e.stopPropagation()}
            style={{
              width: 560,
              maxWidth: "calc(100vw - var(--space-xl))",
              background: "var(--surface-1)",
              border: "1px solid var(--hairline-strong)",
              borderRadius: "var(--radius-card)",
              boxShadow: "var(--shadow-overlay)",
              overflow: "hidden",
            }}
          >
            <div style={{ display: "flex", alignItems: "center", gap: "var(--space-sm)", padding: "0 var(--space-md)", height: 52, borderBottom: "1px solid var(--hairline)" }}>
              <Icon name="search" size={16} color="var(--ink-subtle)" />
              <input
                ref={inputRef}
                value={query}
                onChange={(e) => onQueryChange?.(e.target.value)}
                onKeyDown={keydown}
                placeholder={placeholder}
                aria-label={placeholder}
                style={{ flex: 1, minWidth: 0, background: "transparent", border: "none", outline: "none", font: "var(--type-body)", color: "var(--ink)" }}
              />
              <kbd style={{ font: "var(--type-mono)", fontSize: 11, color: "var(--ink-tertiary)", border: "1px solid var(--hairline)", borderRadius: "var(--radius-xs)", padding: "1px 5px" }}>esc</kbd>
            </div>

            <div ref={listRef} style={{ maxHeight: 340, overflowY: "auto" }}>
              {flat.length === 0 ? (
                <div style={{ padding: "var(--space-lg) var(--space-md)", font: "var(--type-body-sm)", color: "var(--ink-subtle)" }}>
                  {emptyMessage || "Nothing matches that."}
                </div>
              ) : (
                groups
                  .filter((g) => g.items.length)
                  .map((g) => (
                    <div key={g.label}>
                      <div
                        style={{
                          padding: "var(--space-sm) var(--space-md) var(--space-xxs)",
                          font: "var(--type-eyebrow)",
                          letterSpacing: "var(--track-eyebrow)",
                          textTransform: "uppercase",
                          color: "var(--text-eyebrow)",
                        }}
                      >
                        {g.label}
                      </div>
                      {g.items.map((it) => {
                        index += 1;
                        const on = index === active;
                        const i = index;
                        return (
                          <div
                            key={it.id}
                            role="option"
                            aria-selected={on}
                            data-active={on}
                            onMouseEnter={() => setActive(i)}
                            onClick={() => onSelect?.({ ...it, group: g.label })}
                            style={{
                              display: "flex",
                              alignItems: "center",
                              gap: "var(--space-sm)",
                              minHeight: 44,
                              padding: "0 var(--space-md)",
                              cursor: "pointer",
                              background: on ? "var(--surface-2)" : "transparent",
                            }}
                          >
                            {it.icon ? <Icon name={it.icon} size={16} color={on ? "var(--ink)" : "var(--ink-subtle)"} /> : null}
                            <span style={{ flex: 1, minWidth: 0 }}>
                              <span style={{ display: "block", font: "var(--type-body-sm)", color: "var(--ink)", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                                {it.label}
                              </span>
                              {it.meta ? (
                                <span style={{ display: "block", font: "var(--type-caption)", color: "var(--ink-subtle)", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                                  {it.meta}
                                </span>
                              ) : null}
                            </span>
                            {it.trailing ? <span style={{ flex: "0 0 auto", font: "var(--type-caption)", color: "var(--ink-subtle)" }}>{it.trailing}</span> : null}
                            {on ? <Icon name="arrow-right" size={14} color="var(--ink-tertiary)" /> : null}
                          </div>
                        );
                      })}
                    </div>
                  ))
              )}
            </div>

            <div style={{ display: "flex", gap: "var(--space-md)", padding: "var(--space-xs) var(--space-md)", borderTop: "1px solid var(--hairline)", background: "var(--surface-2)", font: "var(--type-caption)", color: "var(--ink-tertiary)" }}>
              <span>↑↓ to move</span>
              <span>↵ to open</span>
              <span>esc to close</span>
            </div>
          </MotionDiv>
        </MotionDiv>
      ) : null}
    </Presence>
  );
}
