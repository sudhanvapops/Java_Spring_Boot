"use client";

import type { ChangeEvent, CSSProperties, ReactNode } from "react";
import { Icon } from "@/components/core/Icon";

export interface CheckboxProps {
  id?: string;
  checked?: boolean;
  /** Select-all header state when only some rows are ticked. */
  indeterminate?: boolean;
  disabled?: boolean;
  label?: ReactNode;
  /** Second line under the label, in caption / ink-subtle. */
  description?: ReactNode;
  onChange?: (e: ChangeEvent<HTMLInputElement>) => void;
  style?: CSSProperties;
}

export function Checkbox({ id, checked, indeterminate = false, disabled, label, description, onChange, style }: CheckboxProps) {
  const on = checked || indeterminate;
  return (
    <label
      htmlFor={id}
      style={{
        display: "flex",
        alignItems: description ? "flex-start" : "center",
        gap: "var(--space-sm)",
        minHeight: "var(--touch-min)",
        cursor: disabled ? "not-allowed" : "pointer",
        opacity: disabled ? 0.5 : 1,
        ...style,
      }}
    >
      <span
        style={{
          display: "grid",
          placeItems: "center",
          flex: "0 0 auto",
          width: 18,
          height: 18,
          marginTop: description ? 2 : 0,
          background: on ? "var(--primary)" : "var(--surface-2)",
          border: "1px solid " + (on ? "var(--primary)" : "var(--hairline-strong)"),
          borderRadius: "var(--radius-xs)",
          transition: "var(--transition-hover)",
        }}
      >
        {indeterminate ? (
          <span style={{ width: 8, height: 2, background: "#fff" }} />
        ) : checked ? (
          <Icon name="check" size={12} color="#fff" />
        ) : null}
      </span>
      <input
        id={id}
        type="checkbox"
        checked={!!checked}
        disabled={disabled}
        onChange={onChange}
        style={{ position: "absolute", opacity: 0, width: 1, height: 1 }}
      />
      {label ? (
        <span>
          <span style={{ font: "var(--type-body-sm)", color: "var(--ink)" }}>{label}</span>
          {description ? (
            <span style={{ display: "block", font: "var(--type-caption)", color: "var(--ink-subtle)" }}>
              {description}
            </span>
          ) : null}
        </span>
      ) : null}
    </label>
  );
}
