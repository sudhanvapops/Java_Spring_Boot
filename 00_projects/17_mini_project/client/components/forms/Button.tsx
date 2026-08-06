"use client";

import { useState, type ButtonHTMLAttributes, type CSSProperties, type ReactNode } from "react";
import { Icon } from "@/components/core/Icon";

const BASE: CSSProperties = {
  display: "inline-flex",
  alignItems: "center",
  justifyContent: "center",
  gap: "var(--space-xs)",
  font: "var(--type-button)",
  borderRadius: "var(--radius-control)",
  border: "1px solid transparent",
  cursor: "pointer",
  whiteSpace: "nowrap",
  textDecoration: "none",
  transition: "var(--transition-hover)",
};

const SIZES: Record<string, CSSProperties> = {
  sm: { height: 32, padding: "0 var(--space-sm)" },
  md: { height: 36, padding: "0 var(--space-md)" },
  lg: { height: 44, padding: "0 var(--space-lg)" },
};

type Variant = "primary" | "secondary" | "tertiary" | "danger";

const VARIANTS: Record<Variant, CSSProperties> = {
  primary: { background: "var(--primary)", color: "var(--text-on-primary)", borderColor: "var(--primary)" },
  secondary: { background: "var(--surface-2)", color: "var(--ink)", borderColor: "var(--hairline)" },
  tertiary: { background: "transparent", color: "var(--ink-muted)", borderColor: "transparent" },
  danger: { background: "var(--danger)", color: "#fff", borderColor: "var(--danger)" },
};

const HOVER: Record<Variant, CSSProperties> = {
  primary: { background: "var(--primary-hover)", borderColor: "var(--primary-hover)" },
  secondary: { background: "var(--surface-3)", borderColor: "var(--hairline-strong)" },
  tertiary: { background: "var(--surface-1)", color: "var(--ink)" },
  danger: { background: "#EC5F63", borderColor: "#EC5F63" },
};

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  /** primary = one per view. tertiary = Cancel. danger = destructive, and it still names the action. */
  variant?: Variant;
  /** md is the default. lg (44px) for auth and any touch-first surface. */
  size?: "sm" | "md" | "lg";
  fullWidth?: boolean;
  /** Keeps the button width, swaps a spinner in and shows loadingLabel. */
  loading?: boolean;
  /** Present-tense label shown while loading, e.g. "Signing in…". */
  loadingLabel?: ReactNode;
  /** Lucide icon name rendered before the label. */
  iconLeft?: string;
  /** Lucide icon name rendered after the label. */
  iconRight?: string;
  children?: ReactNode;
}

export function Button({
  variant = "primary",
  size = "md",
  fullWidth = false,
  disabled = false,
  loading = false,
  loadingLabel,
  iconLeft,
  iconRight,
  type = "button",
  children,
  style,
  ...rest
}: ButtonProps) {
  const [hover, setHover] = useState(false);
  const off = disabled || loading;
  return (
    <button
      type={type}
      disabled={off}
      onMouseEnter={() => setHover(true)}
      onMouseLeave={() => setHover(false)}
      style={{
        ...BASE,
        ...SIZES[size],
        ...VARIANTS[variant],
        ...(hover && !off ? HOVER[variant] : null),
        width: fullWidth ? "100%" : undefined,
        opacity: disabled ? 0.5 : 1,
        color: disabled ? "var(--ink-tertiary)" : (hover && !off && HOVER[variant].color) || VARIANTS[variant].color,
        cursor: off ? "not-allowed" : "pointer",
        ...style,
      }}
      {...rest}
    >
      {loading ? <Spinner /> : iconLeft ? <Icon name={iconLeft} size={16} /> : null}
      {loading ? loadingLabel || children : children}
      {!loading && iconRight ? <Icon name={iconRight} size={16} /> : null}
    </button>
  );
}

function Spinner() {
  return (
    <span
      style={{
        width: 14,
        height: 14,
        borderRadius: "var(--radius-pill)",
        border: "2px solid currentColor",
        borderTopColor: "transparent",
        animation: "stacks-spin 700ms linear infinite",
        opacity: 0.9,
      }}
    />
  );
}
