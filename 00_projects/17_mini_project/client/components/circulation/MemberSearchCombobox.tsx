import { SearchCombobox } from "./SearchCombobox";
import { StatusBadge } from "@/components/data/StatusBadge";

export interface MemberOption {
  id: number;
  name: string;
  email: string;
  /** Count from unreturned/{memberId}. */
  booksOut: number;
  /** MAX_BOOKS minus booksOut. Showing it up front prevents a failed submit. */
  allowance: number;
  /** Inactive members stay listed but aren't selectable. */
  disabled?: boolean;
}

export interface MemberSearchComboboxProps {
  members: MemberOption[];
  query?: string;
  onQueryChange?: (q: string) => void;
  onSelect?: (member: MemberOption) => void;
  placeholder?: string;
}

export function MemberSearchCombobox({ members = [], query, onQueryChange, onSelect, placeholder = "Search members…" }: MemberSearchComboboxProps) {
  return (
    <SearchCombobox
      placeholder={placeholder}
      query={query}
      onQueryChange={onQueryChange}
      options={members}
      onSelect={onSelect}
      emptyMessage={query ? `No members match "${query}".` : "Start typing a name or email."}
      renderOption={(m) => (
        <>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ font: "var(--type-body-sm)", color: m.disabled ? "var(--ink-subtle)" : "var(--ink)" }}>{m.name}</div>
            <div style={{ font: "var(--type-caption)", color: "var(--ink-subtle)", overflow: "hidden", textOverflow: "ellipsis" }}>{m.email}</div>
          </div>
          <div style={{ textAlign: "right", flex: "0 0 auto" }}>
            {m.disabled ? (
              <StatusBadge status="inactive" />
            ) : (
              <>
                <div style={{ font: "var(--type-mono)", color: "var(--ink-muted)" }}>{m.booksOut} out</div>
                <div style={{ font: "var(--type-caption)", color: "var(--ink-subtle)" }}>{m.allowance} more allowed</div>
              </>
            )}
          </div>
        </>
      )}
    />
  );
}

/** The collapsed state once a member is chosen, with a Change action. */
export function MemberSummary({ member, onChange }: { member: MemberOption; onChange?: () => void }) {
  return (
    <div style={{ display: "flex", alignItems: "center", gap: "var(--space-md)" }}>
      <span
        style={{
          display: "grid",
          placeItems: "center",
          width: 36,
          height: 36,
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
        <div style={{ font: "var(--type-body-sm)", color: "var(--ink)" }}>{member.name}</div>
        <div style={{ font: "var(--type-caption)", color: "var(--ink-subtle)" }}>{member.email}</div>
        <div style={{ marginTop: 2, font: "var(--type-mono)", color: "var(--ink-muted)" }}>
          {member.booksOut} out · {member.allowance} more allowed
        </div>
      </div>
      {onChange ? (
        <button onClick={onChange} style={{ background: "none", border: "none", padding: 0, cursor: "pointer", font: "var(--type-button)", color: "var(--primary)" }}>
          Change
        </button>
      ) : null}
    </div>
  );
}
