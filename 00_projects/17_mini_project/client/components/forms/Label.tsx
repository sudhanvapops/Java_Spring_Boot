import type { HTMLAttributes, ReactNode } from "react";

export interface LabelProps extends Omit<HTMLAttributes<HTMLDivElement>, "children"> {
  htmlFor?: string;
  children: ReactNode;
  /** Right-aligned slot on the label row — e.g. a "Forgot password?" link. */
  hint?: ReactNode;
  required?: boolean;
}

export function Label({ htmlFor, children, hint, required, style, ...rest }: LabelProps) {
  return (
    <div
      style={{
        display: "flex",
        alignItems: "baseline",
        justifyContent: "space-between",
        gap: "var(--space-sm)",
        marginBottom: "var(--space-xs)",
        ...style,
      }}
      {...rest}
    >
      <label
        htmlFor={htmlFor}
        style={{ font: "var(--type-body-sm)", fontWeight: "var(--weight-medium)", color: "var(--text-label)" }}
      >
        {children}
        {required ? <span style={{ color: "var(--ink-tertiary)" }}> *</span> : null}
      </label>
      {hint ? <span style={{ font: "var(--type-caption)" }}>{hint}</span> : null}
    </div>
  );
}
