"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { PageHeader } from "@/components/navigation/PageHeader";
import { Button } from "@/components/forms/Button";
import { DataTable, type DataTableColumn } from "@/components/data/DataTable";
import { EmptyState } from "@/components/feedback/EmptyState";
import { useDueToday } from "@/lib/hooks/useRecords";
import { useToast } from "@/lib/hooks/useToast";
import type { DueTodayRecord } from "@/lib/types/domain";

/** Ported from ui_kits/console/DueToday.jsx "/records/due-today". */
export default function DueTodayPage() {
  const router = useRouter();
  const toast = useToast();
  const { data: rows, isLoading, isError, refetch } = useDueToday();

  const list = rows ?? [];

  const copyAll = async () => {
    const emails = list.map((r) => r.memberEmail).join(", ");
    try {
      await navigator.clipboard.writeText(emails);
    } catch {
      // clipboard permission denied — the toast below still confirms count
    }
    toast.success(`${list.length} email addresses copied.`);
  };

  const columns: DataTableColumn<DueTodayRecord>[] = [
    { key: "bookName", header: "Book" },
    { key: "memberName", header: "Member", width: "160px", render: (r) => <Link href={`/members/${r.memberId}`}>{r.memberName}</Link> },
    { key: "memberEmail", header: "Email", width: "210px", render: (r) => <span style={{ font: "var(--type-mono)", color: "var(--ink-subtle)" }}>{r.memberEmail}</span> },
  ];

  const state = isError ? "error" : isLoading ? "loading" : list.length ? "loaded" : "empty";

  return (
    <div>
      <PageHeader
        title="Due today"
        subtitle={list.length ? `${list.length} ${list.length === 1 ? "book needs" : "books need"} to come back today.` : "Nothing needs to come back today."}
        action={list.length ? <Button variant="secondary" iconLeft="copy" onClick={copyAll}>Copy all emails</Button> : undefined}
      />
      <DataTable
        state={state}
        rows={list}
        getRowKey={(r) => `${r.memberId}-${r.bookId}`}
        columns={columns}
        rowActions={(r) => (
          <Button size="sm" variant="secondary" onClick={() => router.push(`/returns?member=${r.memberId}`)}>
            Take it back
          </Button>
        )}
        alwaysShowActions
        error={<EmptyState icon="server-off" headline="Can't load today's list." body="Check that the API is running and try again." actionLabel="Try again" onAction={() => refetch()} />}
        empty={<EmptyState icon="circle-check" headline="Nothing due today" body="No books need to come back today." />}
      />
    </div>
  );
}
