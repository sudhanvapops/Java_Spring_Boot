import type { CSSProperties, ReactNode } from "react";
import { Icon } from "@/components/core/Icon";
import { Button } from "@/components/forms/Button";
import { MotionDiv, EASE, RISE } from "@/components/core/Motion";

const DOTS = "radial-gradient(var(--hairline) 1px,transparent 1px)";

export interface EmptyStateProps {
  /** Lucide icon name. Default "inbox". */
  icon?: string;
  /** Specific headline, e.g. 'No books match "gibbon"'. */
  headline: ReactNode;
  /** One line of explanation. */
  body?: ReactNode;
  /** Names the action: "Add a book", "Clear search". */
  actionLabel?: string;
  onAction?: () => void;
  /** Optional second action beside the primary. */
  secondary?: ReactNode;
  /** Static dot texture behind the copy. Set false inside tight panels. */
  pattern?: boolean;
  style?: CSSProperties;
}

export function EmptyState({
  icon = "inbox",
  headline,
  body,
  actionLabel,
  onAction,
  secondary,
  pattern = true,
  style,
}: EmptyStateProps) {
  return (
    <div style={{ position: "relative", display: "grid", placeItems: "center", padding: "var(--space-xxl) var(--space-lg)", ...style }}>
      {pattern ? (
        <div
          aria-hidden
          style={{
            position: "absolute",
            inset: 0,
            backgroundImage: DOTS,
            backgroundSize: "16px 16px",
            opacity: 0.5,
            maskImage: "radial-gradient(closest-side,#000 20%,transparent 80%)",
            WebkitMaskImage: "radial-gradient(closest-side,#000 20%,transparent 80%)",
            pointerEvents: "none",
          }}
        />
      ) : null}
      <MotionDiv
        initial={RISE.hidden}
        animate={RISE.visible}
        transition={EASE.entrance}
        style={{ position: "relative", maxWidth: "var(--empty-state-width)", textAlign: "center" }}
      >
        <div
          style={{
            display: "grid",
            placeItems: "center",
            width: 40,
            height: 40,
            margin: "0 auto var(--space-md)",
            background: "var(--surface-2)",
            border: "1px solid var(--hairline)",
            borderRadius: "var(--radius-lg)",
          }}
        >
          <Icon name={icon} size={18} color="var(--ink-subtle)" />
        </div>
        <div style={{ font: "var(--type-subhead)", letterSpacing: "var(--track-subhead)", color: "var(--ink)" }}>{headline}</div>
        {body ? (
          <p style={{ margin: "var(--space-xs) 0 0", font: "var(--type-body-sm)", color: "var(--ink-subtle)", textWrap: "pretty" }}>
            {body}
          </p>
        ) : null}
        {actionLabel ? (
          <div style={{ display: "flex", gap: "var(--space-xs)", justifyContent: "center", marginTop: "var(--space-lg)" }}>
            <Button variant="primary" onClick={onAction}>
              {actionLabel}
            </Button>
            {secondary}
          </div>
        ) : null}
      </MotionDiv>
    </div>
  );
}
