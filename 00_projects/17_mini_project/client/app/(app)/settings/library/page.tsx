"use client";

import { useState } from "react";
import { PageHeader } from "@/components/navigation/PageHeader";
import { SettingCard } from "@/components/panels/SettingCard";
import { IconButton } from "@/components/forms/IconButton";
import { DropdownMenu } from "@/components/navigation/DropdownMenu";
import { Dialog } from "@/components/feedback/Dialog";
import { Skeleton } from "@/components/data/Skeleton";
import { useSettingsRaw, useSaveSetting, useDeleteSetting, settingsByKey } from "@/lib/hooks/useSettings";
import { useToast } from "@/lib/hooks/useToast";
import { maxBooksSchema, maxBorrowDaysSchema, finePerDaySchema } from "@/lib/schemas/settings";
import type { SettingKey, SettingValueType } from "@/lib/types/api";

const RULES: {
  key: SettingKey;
  title: string;
  description: string;
  unit?: string;
  prefix?: string;
  note?: string;
  valueType: SettingValueType;
  defaultValue: string;
  schema: typeof maxBooksSchema;
}[] = [
  {
    key: "MAX_BOOKS",
    title: "Books per member",
    description: "How many books one member can have out at a time.",
    unit: "books",
    valueType: "INTEGER",
    defaultValue: "5",
    schema: maxBooksSchema,
  },
  {
    key: "MAX_BORROW_DAYS",
    title: "Loan length",
    description: "How long a member can keep a book.",
    unit: "days",
    note: "Changing this doesn’t move existing due dates.",
    valueType: "INTEGER",
    defaultValue: "14",
    schema: maxBorrowDaysSchema,
  },
  {
    key: "FINE_PER_DAY",
    title: "Late fee",
    description: "Charged for each day a book is overdue.",
    prefix: "₹",
    unit: "per day",
    valueType: "DECIMAL",
    defaultValue: "10.00",
    schema: finePerDaySchema,
  },
];

export default function LibrarySettingsPage() {
  const { data: settings, isLoading } = useSettingsRaw();
  const byKey = settingsByKey(settings);

  if (isLoading) {
    return (
      <div style={{ maxWidth: 640, display: "grid", gap: "var(--space-md)" }}>
        <Skeleton width={200} height={28} />
        <Skeleton height={140} radius="var(--radius-card)" />
        <Skeleton height={140} radius="var(--radius-card)" />
        <Skeleton height={140} radius="var(--radius-card)" />
      </div>
    );
  }

  return (
    <div style={{ maxWidth: 640 }}>
      <PageHeader title="Library rules" subtitle="These control how lending works across the whole library." />
      <div style={{ display: "grid", gap: "var(--space-md)" }}>
        {RULES.map((rule) => (
          <RuleCard key={rule.key} rule={rule} configured={byKey.has(rule.key)} currentValue={byKey.get(rule.key)?.settingValue} />
        ))}
      </div>
    </div>
  );
}

function RuleCard({
  rule,
  configured,
  currentValue,
}: {
  rule: (typeof RULES)[number];
  configured: boolean;
  currentValue?: string;
}) {
  const toast = useToast();
  const saveSetting = useSaveSetting();
  const deleteSetting = useDeleteSetting();
  const [value, setValue] = useState(currentValue ?? rule.defaultValue);
  const [menuOpen, setMenuOpen] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);

  // Sync the field once the fetched value arrives, without clobbering
  // whatever the librarian has already typed on a later render. Adjusting
  // state during render (React's documented pattern) rather than in an
  // effect avoids an extra render pass.
  const [prevCurrentValue, setPrevCurrentValue] = useState(currentValue);
  if (currentValue !== prevCurrentValue) {
    setPrevCurrentValue(currentValue);
    if (currentValue != null) setValue(currentValue);
  }

  const handleSave = async () => {
    const parsed = rule.schema.safeParse(value);
    if (!parsed.success) {
      toast.error(parsed.error.issues[0]?.message ?? "Enter a valid value.");
      return;
    }
    try {
      await saveSetting.mutateAsync({
        key: rule.key,
        value: String(parsed.data),
        valueType: rule.valueType,
        configured,
        description: rule.description,
      });
      toast.success(`${rule.title} saved.`);
    } catch (err) {
      toast.errorFrom(err);
    }
  };

  const handleDelete = async () => {
    setConfirmOpen(false);
    setMenuOpen(false);
    try {
      await deleteSetting.mutateAsync(rule.key);
      toast.success(`${rule.title} deleted.`);
    } catch (err) {
      toast.errorFrom(err);
    }
  };

  return (
    <div style={{ position: "relative" }}>
      <SettingCard
        title={rule.title}
        description={rule.description}
        value={value}
        unit={rule.unit}
        prefix={rule.prefix}
        note={rule.note}
        configured={configured}
        saving={saveSetting.isPending}
        onChange={(e) => setValue(e.target.value)}
        onSave={handleSave}
        overflow={
          configured ? (
            <div style={{ position: "relative" }}>
              <IconButton icon="ellipsis" label="More options" onClick={() => setMenuOpen((o) => !o)} />
              <DropdownMenu
                open={menuOpen}
                items={[{ label: "Delete setting", icon: "trash-2", tone: "danger", onClick: () => { setMenuOpen(false); setConfirmOpen(true); } }]}
              />
            </div>
          ) : undefined
        }
      />
      <Dialog
        open={confirmOpen}
        title={`Delete "${rule.title}"?`}
        confirmLabel="Delete setting"
        destructive
        onCancel={() => setConfirmOpen(false)}
        onConfirm={handleDelete}
      >
        Lending will be blocked until this rule is set again.
      </Dialog>
    </div>
  );
}
