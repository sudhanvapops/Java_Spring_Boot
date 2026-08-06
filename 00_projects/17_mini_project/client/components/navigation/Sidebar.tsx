"use client";

import { useState } from "react";
import { Icon } from "@/components/core/Icon";
import { Badge, type BadgeTone } from "@/components/data/Badge";

export interface SidebarItemDef {
  label: string;
  href: string;
  /** Lucide icon name. */
  icon: string;
  /** Count badge — "Due today" carries one. */
  count?: number;
  countTone?: "neutral" | "warning" | "danger";
}

export interface SidebarGroup {
  /** Eyebrow label: CATALOGUE, CIRCULATION, HISTORY. Omit for ungrouped items. */
  label?: string;
  items: SidebarItemDef[];
}

export interface SidebarProps {
  groups: SidebarGroup[];
  /** href of the current route. */
  active?: string;
  onNavigate?: (href: string) => void;
  account?: { name: string; role: string };
  /** Icon-only rail, used below 1024px. */
  collapsed?: boolean;
  /** Rendered as plain type — the brand has no supplied logo mark. */
  wordmark?: string;
}

export function Sidebar({ groups = [], active, onNavigate, account, collapsed = false, wordmark = "Stacks" }: SidebarProps) {
  const width = collapsed ? "var(--sidebar-collapsed)" : "var(--sidebar-width)";
  return (
    <nav
      style={{
        display: "flex",
        flexDirection: "column",
        width,
        flex: "0 0 auto",
        background: "var(--canvas)",
        borderRight: "1px solid var(--hairline)",
        padding: "var(--space-lg) var(--space-sm) var(--space-sm)",
        transition: "width var(--duration-medium) var(--ease-out)",
        overflow: "hidden",
      }}
    >
      <div
        style={{
          font: "var(--type-card-title)",
          letterSpacing: "var(--track-card-title)",
          color: "var(--ink)",
          padding: "0 var(--space-xs)",
          marginBottom: "var(--space-lg)",
          whiteSpace: "nowrap",
        }}
      >
        {collapsed ? wordmark.slice(0, 1) : wordmark}
      </div>

      <div style={{ flex: 1, display: "grid", gap: "var(--space-md)", alignContent: "start" }}>
        {groups.map((g, gi) => (
          <div key={gi} style={{ display: "grid", gap: 2 }}>
            {g.label && !collapsed ? (
              <div
                style={{
                  font: "var(--type-eyebrow)",
                  letterSpacing: "var(--track-eyebrow)",
                  textTransform: "uppercase",
                  color: "var(--text-eyebrow)",
                  padding: "0 var(--space-xs)",
                  marginBottom: "var(--space-xxs)",
                }}
              >
                {g.label}
              </div>
            ) : null}
            {g.items.map((item) => (
              <SidebarItem key={item.href} item={item} active={item.href === active} collapsed={collapsed} onNavigate={onNavigate} />
            ))}
          </div>
        ))}
      </div>

      {account ? (
        <div
          style={{
            display: "flex",
            alignItems: "center",
            gap: "var(--space-xs)",
            marginTop: "var(--space-md)",
            padding: "var(--space-xs)",
            background: "var(--surface-1)",
            border: "1px solid var(--hairline)",
            borderRadius: "var(--radius-md)",
            minHeight: 44,
          }}
        >
          <span
            style={{
              display: "grid",
              placeItems: "center",
              width: 28,
              height: 28,
              flex: "0 0 auto",
              borderRadius: "var(--radius-pill)",
              background: "var(--surface-3)",
              font: "var(--type-caption)",
              color: "var(--ink-muted)",
            }}
          >
            {account.name
              .split(" ")
              .map((n) => n[0])
              .slice(0, 2)
              .join("")}
          </span>
          {!collapsed ? (
            <span style={{ minWidth: 0 }}>
              <span style={{ display: "block", font: "var(--type-body-sm)", color: "var(--ink)", whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
                {account.name}
              </span>
              <span style={{ display: "block", font: "var(--type-caption)", color: "var(--ink-subtle)" }}>{account.role}</span>
            </span>
          ) : null}
        </div>
      ) : null}
    </nav>
  );
}

function SidebarItem({
  item,
  active,
  collapsed,
  onNavigate,
}: {
  item: SidebarItemDef;
  active: boolean;
  collapsed: boolean;
  onNavigate?: (href: string) => void;
}) {
  const [hover, setHover] = useState(false);
  return (
    <a
      href={item.href}
      onClick={(e) => {
        e.preventDefault();
        onNavigate?.(item.href);
      }}
      onMouseEnter={() => setHover(true)}
      onMouseLeave={() => setHover(false)}
      title={collapsed ? item.label : undefined}
      style={{
        position: "relative",
        display: "flex",
        alignItems: "center",
        gap: "var(--space-xs)",
        minHeight: 34,
        padding: "0 var(--space-xs)",
        background: active ? "var(--surface-2)" : hover ? "var(--surface-1)" : "transparent",
        borderRadius: "var(--radius-md)",
        textDecoration: "none",
        font: "var(--type-body-sm)",
        color: active ? "var(--ink)" : "var(--ink-subtle)",
        transition: "var(--transition-hover)",
        whiteSpace: "nowrap",
      }}
    >
      {active ? (
        <span style={{ position: "absolute", left: 0, top: 7, bottom: 7, width: 2, borderRadius: "var(--radius-pill)", background: "var(--primary)" }} />
      ) : null}
      <Icon name={item.icon} size={16} color={active ? "var(--ink)" : "var(--ink-subtle)"} />
      {!collapsed ? <span style={{ flex: 1, overflow: "hidden", textOverflow: "ellipsis" }}>{item.label}</span> : null}
      {!collapsed && item.count != null ? (
        <Badge tone={(item.countTone as BadgeTone) || "warning"} icon={item.countTone === "neutral" ? undefined : "triangle-alert"}>
          {item.count}
        </Badge>
      ) : null}
    </a>
  );
}
