import type { ChangeEvent, CSSProperties, ReactNode } from "react";
import { Icon } from "@/components/core/Icon";
import { Input } from "@/components/forms/Input";
import { Button } from "@/components/forms/Button";

export interface SettingCardProps {
  title: ReactNode;
  /** One line saying what the rule does, in plain librarian language. */
  description: ReactNode;
  value: string | number;
  /** Trailing unit: "books", "days", "per day". */
  unit?: string;
  /** Leading currency symbol for the late fee. */
  prefix?: string;
  /** Caption beneath, e.g. "Changing this doesn't move existing due dates." */
  note?: ReactNode;
  /** False = the setting has never been created: hairline-strong border, amber
   *  icon, and the "Not set yet" line. Save then POSTs instead of PUTs. */
  configured?: boolean;
  saving?: boolean;
  onChange?: (e: ChangeEvent<HTMLInputElement>) => void;
  onSave?: () => void;
  /** Overflow menu holding Delete — never a visible button. */
  overflow?: ReactNode;
  style?: CSSProperties;
}

export function SettingCard({
  title,
  description,
  value,
  unit,
  prefix,
  note,
  configured = true,
  saving = false,
  onChange,
  onSave,
  overflow,
  style,
}: SettingCardProps) {
  return (
    <div
      style={{
        padding: "var(--space-lg)",
        background: "var(--surface-1)",
        border: "1px solid " + (configured ? "var(--hairline)" : "var(--hairline-strong)"),
        borderRadius: "var(--radius-card)",
        ...style,
      }}
    >
      <div style={{ display: "flex", alignItems: "flex-start", gap: "var(--space-xs)" }}>
        <div style={{ flex: 1, minWidth: 0 }}>
          <div style={{ display: "flex", alignItems: "center", gap: "var(--space-xxs)", font: "var(--type-body)", fontWeight: "var(--weight-medium)", color: "var(--ink)" }}>
            {!configured ? <Icon name="triangle-alert" size={14} color="var(--warning)" /> : null}
            {title}
          </div>
          <p style={{ margin: "4px 0 0", font: "var(--type-body-sm)", color: "var(--ink-subtle)" }}>{description}</p>
        </div>
        {overflow}
      </div>

      {!configured ? (
        <p style={{ margin: "var(--space-sm) 0 0", font: "var(--type-body-sm)", color: "var(--warning)" }}>
          Not set yet — lending is blocked until you set this.
        </p>
      ) : null}

      <div style={{ display: "flex", alignItems: "center", gap: "var(--space-sm)", marginTop: "var(--space-md)" }}>
        <Input value={value} onChange={onChange} prefix={prefix} unit={unit} width={168} inputMode="decimal" />
        <Button variant="secondary" loading={saving} loadingLabel="Saving…" onClick={onSave}>
          Save
        </Button>
      </div>

      {note ? <p style={{ margin: "var(--space-sm) 0 0", font: "var(--type-caption)", color: "var(--ink-subtle)" }}>{note}</p> : null}
    </div>
  );
}
