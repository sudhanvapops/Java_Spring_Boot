import type { CSSProperties, ReactNode } from "react";
import { StatusBadge } from "@/components/data/StatusBadge";

export interface MemberRowMember {
  id: number;
  name: string;
  email: string;
  /** Derived from unreturned/all grouped by memberId. */
  booksOut?: number;
  isActive?: boolean;
}

export interface MemberRowProps {
  member: MemberRowMember;
  action?: ReactNode;
  style?: CSSProperties;
}

export function MemberRow({ member, action, style }: MemberRowProps) {
  return (
    <div style={{ display: "flex", alignItems: "center", gap: "var(--space-md)", minHeight: 52, padding: "var(--space-xs) var(--space-md)", ...style }}>
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
        {member.name
          .split(" ")
          .map((n) => n[0])
          .slice(0, 2)
          .join("")}
      </span>
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ font: "var(--type-body-sm)", color: member.isActive === false ? "var(--ink-subtle)" : "var(--ink)" }}>{member.name}</div>
        <div style={{ font: "var(--type-caption)", color: "var(--ink-subtle)", overflow: "hidden", textOverflow: "ellipsis" }}>{member.email}</div>
      </div>
      {member.isActive === false ? <StatusBadge status="inactive" /> : null}
      {member.booksOut != null ? <span style={{ font: "var(--type-mono)", color: "var(--ink-muted)", flex: "0 0 auto" }}>{member.booksOut} out</span> : null}
      {action}
    </div>
  );
}
