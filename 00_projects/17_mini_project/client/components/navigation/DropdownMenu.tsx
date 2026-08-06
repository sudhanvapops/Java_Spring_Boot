import type { CSSProperties, ReactNode } from "react";
import { Icon } from "@/components/core/Icon";
import { MotionDiv, Presence, EASE } from "@/components/core/Motion";

export interface DropdownMenuItem {
  label?: ReactNode;
  icon?: string;
  shortcut?: string;
  tone?: "default" | "danger";
  onClick?: () => void;
  separator?: boolean;
}

export interface DropdownMenuProps {
  open?: boolean;
  items: DropdownMenuItem[];
  /** Which edge the menu anchors to. */
  align?: "left" | "right";
  style?: CSSProperties;
}

export function DropdownMenu({ open = true, items = [], align = "right", style }: DropdownMenuProps) {
  return (
    <Presence>
      {open ? (
        <MotionDiv
          role="menu"
          initial={{ opacity: 0, y: -4 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: -4 }}
          transition={EASE.fast}
          style={{
            position: "absolute",
            [align]: 0,
            marginTop: "var(--space-xxs)",
            minWidth: 190,
            padding: "var(--space-xxs)",
            background: "var(--surface-4)",
            border: "1px solid var(--hairline-strong)",
            borderRadius: "var(--radius-md)",
            boxShadow: "var(--shadow-popover)",
            zIndex: 40,
            ...style,
          }}
        >
          {items.map((it, i) =>
            it.separator ? (
              <div key={i} style={{ height: 1, background: "var(--hairline)", margin: "var(--space-xxs) 0" }} />
            ) : (
              <button
                key={i}
                role="menuitem"
                onClick={it.onClick}
                style={{
                  display: "flex",
                  alignItems: "center",
                  gap: "var(--space-xs)",
                  width: "100%",
                  height: 32,
                  padding: "0 var(--space-xs)",
                  background: "transparent",
                  border: "none",
                  borderRadius: "var(--radius-xs)",
                  cursor: "pointer",
                  font: "var(--type-body-sm)",
                  color: it.tone === "danger" ? "var(--danger)" : "var(--ink-muted)",
                  textAlign: "left",
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.background = "var(--surface-3)";
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.background = "transparent";
                }}
              >
                {it.icon ? <Icon name={it.icon} size={14} /> : null}
                <span style={{ flex: 1 }}>{it.label}</span>
                {it.shortcut ? <span style={{ font: "var(--type-mono)", fontSize: 11, color: "var(--ink-tertiary)" }}>{it.shortcut}</span> : null}
              </button>
            ),
          )}
        </MotionDiv>
      ) : null}
    </Presence>
  );
}
