"use client";

import { useState, type ButtonHTMLAttributes } from "react";
import { Icon } from "@/components/core/Icon";

export interface IconButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  /** Lucide icon name. */
  icon: string;
  /** Required — this is the button's accessible name and its tooltip. */
  label: string;
  tone?: "default" | "danger";
  /** Box size in px. Default 32. */
  size?: number;
}

export function IconButton({ icon, label, tone = "default", size = 32, type = "button", style, ...rest }: IconButtonProps) {
  const [hover, setHover] = useState(false);
  const colors: Record<string, string> = {
    default: "var(--ink-subtle)",
    danger: "var(--danger)",
  };
  return (
    <button
      type={type}
      aria-label={label}
      title={label}
      onMouseEnter={() => setHover(true)}
      onMouseLeave={() => setHover(false)}
      style={{
        display: "inline-grid",
        placeItems: "center",
        width: size,
        height: size,
        minWidth: 32,
        minHeight: 32,
        background: hover ? "var(--surface-3)" : "transparent",
        border: "1px solid " + (hover ? "var(--hairline)" : "transparent"),
        borderRadius: "var(--radius-sm)",
        cursor: "pointer",
        color: hover ? (tone === "danger" ? "var(--danger)" : "var(--ink)") : colors[tone],
        transition: "var(--transition-hover)",
        ...style,
      }}
      {...rest}
    >
      <Icon name={icon} size={16} />
    </button>
  );
}
