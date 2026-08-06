import type { CSSProperties, ReactNode } from "react";
import { MotionDiv, EASE, RISE } from "@/components/core/Motion";

export interface AuthCardProps {
  /** Rendered as plain type — no logo mark was supplied with the brand. */
  wordmark?: string;
  /** display-md title: "Welcome back", "Create your account". */
  title: ReactNode;
  subtitle?: ReactNode;
  /** Form-level message above the fields. Credential failures go here, never on a field. */
  error?: ReactNode;
  children?: ReactNode;
  /** Below-card line, e.g. "New here? Create an account". */
  footer?: ReactNode;
  style?: CSSProperties;
}

export function AuthCard({ wordmark = "Stacks", title, subtitle, error, children, footer, style }: AuthCardProps) {
  return (
    <MotionDiv initial={RISE.hidden} animate={RISE.visible} transition={EASE.entrance} style={{ width: "var(--auth-card-width)", maxWidth: "100%", ...style }}>
      <div
        style={{
          padding: "var(--space-xl)",
          background: "var(--surface-1)",
          border: "1px solid var(--hairline)",
          borderRadius: "var(--radius-card)",
        }}
      >
        <div style={{ font: "var(--type-card-title)", letterSpacing: "var(--track-card-title)", color: "var(--ink)" }}>{wordmark}</div>
        <h1 style={{ margin: "var(--space-lg) 0 0", font: "var(--type-display-md)", letterSpacing: "var(--track-display-md)", color: "var(--ink)" }}>{title}</h1>
        {subtitle ? <p style={{ margin: "var(--space-xs) 0 0", font: "var(--type-body)", color: "var(--ink-subtle)" }}>{subtitle}</p> : null}
        {error ? (
          <div
            role="alert"
            style={{
              display: "flex",
              gap: "var(--space-xs)",
              marginTop: "var(--space-lg)",
              padding: "var(--space-sm) var(--space-md)",
              background: "var(--danger-tint)",
              border: "1px solid var(--danger)",
              borderRadius: "var(--radius-md)",
              font: "var(--type-body-sm)",
              color: "var(--ink)",
            }}
          >
            {error}
          </div>
        ) : null}
        <div style={{ marginTop: "var(--space-lg)", display: "grid", gap: "var(--space-md)" }}>{children}</div>
      </div>
      {footer ? <div style={{ marginTop: "var(--space-lg)", textAlign: "center", font: "var(--type-body-sm)", color: "var(--ink-subtle)" }}>{footer}</div> : null}
    </MotionDiv>
  );
}

export function BackgroundBeams({ style }: { style?: CSSProperties }) {
  const beams = [12, 26, 41, 58, 74, 88];
  return (
    <div aria-hidden style={{ position: "absolute", inset: 0, overflow: "hidden", pointerEvents: "none", ...style }}>
      {beams.map((left, i) => (
        <span
          key={i}
          style={{
            position: "absolute",
            left: left + "%",
            top: "-20%",
            width: 1,
            height: "140%",
            background: "linear-gradient(180deg,transparent,rgba(79,124,255,.55),transparent)",
            opacity: 0.28,
            transform: "rotate(" + (i % 2 ? 6 : -6) + "deg)",
          }}
        />
      ))}
      <div
        style={{
          position: "absolute",
          inset: 0,
          background: "radial-gradient(60% 50% at 50% 40%,rgba(79,124,255,.10),transparent 70%)",
        }}
      />
    </div>
  );
}
