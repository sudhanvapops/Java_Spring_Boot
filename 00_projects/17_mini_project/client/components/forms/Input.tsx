"use client";

import { useState, type InputHTMLAttributes, type ReactNode } from "react";
import { Icon } from "@/components/core/Icon";

export interface InputProps extends Omit<InputHTMLAttributes<HTMLInputElement>, "size"> {
  size?: "sm" | "md" | "lg";
  /** Red border plus aria-invalid. Pair with `error`. */
  invalid?: boolean;
  /** Lucide icon name rendered inside the field, left of the text. */
  iconLeft?: string;
  /** Static leading text in mono — a currency symbol, e.g. "₹". */
  prefix?: string;
  /** Static trailing text — "books", "days", "per day". */
  unit?: string;
  /** Error sentence rendered below the field with a warning icon. Overrides helper. */
  error?: ReactNode;
  /** Caption below the field explaining the field. */
  helper?: ReactNode;
  width?: number | string;
}

export function Input({
  id,
  size = "md",
  invalid = false,
  disabled = false,
  iconLeft,
  prefix,
  unit,
  error,
  helper,
  width,
  style,
  onFocus,
  onBlur,
  ...rest
}: InputProps) {
  const [focus, setFocus] = useState(false);
  const height = size === "sm" ? 32 : size === "lg" ? 44 : 38;
  const borderColor = invalid ? "var(--danger)" : focus ? "var(--primary-focus)" : "var(--hairline)";
  return (
    <div style={{ width: width || "100%" }}>
      <div
        style={{
          display: "flex",
          alignItems: "center",
          gap: "var(--space-xs)",
          height,
          padding: "0 var(--space-sm)",
          background: "var(--surface-2)",
          border: "1px solid " + borderColor,
          borderRadius: "var(--radius-control)",
          boxShadow: focus ? "var(--focus-ring)" : "none",
          opacity: disabled ? 0.5 : 1,
          transition: "var(--transition-hover)",
          ...style,
        }}
      >
        {iconLeft ? <Icon name={iconLeft} size={16} color="var(--ink-subtle)" /> : null}
        {prefix ? <span style={{ font: "var(--type-mono)", color: "var(--ink-subtle)" }}>{prefix}</span> : null}
        <input
          id={id}
          disabled={disabled}
          aria-invalid={invalid || undefined}
          onFocus={(e) => {
            setFocus(true);
            onFocus?.(e);
          }}
          onBlur={(e) => {
            setFocus(false);
            onBlur?.(e);
          }}
          {...rest}
          style={{
            flex: 1,
            minWidth: 0,
            background: "transparent",
            border: "none",
            outline: "none",
            font: "var(--type-body-sm)",
            color: "var(--ink)",
            padding: 0,
          }}
        />
        {unit ? <span style={{ font: "var(--type-caption)", color: "var(--ink-subtle)", whiteSpace: "nowrap" }}>{unit}</span> : null}
      </div>
      {error ? (
        <div
          style={{
            display: "flex",
            alignItems: "center",
            gap: "var(--space-xxs)",
            marginTop: "var(--space-xxs)",
            font: "var(--type-caption)",
            color: "var(--danger)",
          }}
        >
          <Icon name="triangle-alert" size={12} />
          {error}
        </div>
      ) : helper ? (
        <div style={{ marginTop: "var(--space-xxs)", font: "var(--type-caption)", color: "var(--ink-subtle)" }}>{helper}</div>
      ) : null}
    </div>
  );
}
