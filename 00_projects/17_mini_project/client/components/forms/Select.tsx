"use client";

import { useState, type SelectHTMLAttributes } from "react";
import { Icon } from "@/components/core/Icon";

export interface SelectOption {
  value: string;
  label: string;
}

export interface SelectProps extends Omit<SelectHTMLAttributes<HTMLSelectElement>, "size"> {
  options: SelectOption[];
  size?: "sm" | "md" | "lg";
  width?: number | string;
}

export function Select({ id, options = [], value, onChange, size = "md", disabled, width, style, ...rest }: SelectProps) {
  const [focus, setFocus] = useState(false);
  const height = size === "sm" ? 32 : size === "lg" ? 44 : 38;
  return (
    <div
      style={{
        position: "relative",
        display: "inline-flex",
        alignItems: "center",
        height,
        width: width || "100%",
        background: "var(--surface-2)",
        border: "1px solid " + (focus ? "var(--primary-focus)" : "var(--hairline)"),
        borderRadius: "var(--radius-control)",
        boxShadow: focus ? "var(--focus-ring)" : "none",
        opacity: disabled ? 0.5 : 1,
        ...style,
      }}
    >
      <select
        id={id}
        value={value}
        onChange={onChange}
        disabled={disabled}
        onFocus={() => setFocus(true)}
        onBlur={() => setFocus(false)}
        style={{
          appearance: "none",
          width: "100%",
          height: "100%",
          background: "transparent",
          border: "none",
          outline: "none",
          font: "var(--type-body-sm)",
          color: "var(--ink)",
          padding: "0 var(--space-xl) 0 var(--space-sm)",
          cursor: disabled ? "not-allowed" : "pointer",
        }}
        {...rest}
      >
        {options.map((o) => (
          <option key={o.value} value={o.value} style={{ background: "var(--surface-3)" }}>
            {o.label}
          </option>
        ))}
      </select>
      <Icon
        name="chevron-down"
        size={16}
        color="var(--ink-subtle)"
        style={{ position: "absolute", right: "var(--space-sm)", pointerEvents: "none" }}
      />
    </div>
  );
}
