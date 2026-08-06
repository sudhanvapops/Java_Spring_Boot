"use client";

import { useMemo, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { PageHeader } from "@/components/navigation/PageHeader";
import { Input } from "@/components/forms/Input";
import { Button } from "@/components/forms/Button";
import { DataTable, type DataTableColumn, type DataTableSort } from "@/components/data/DataTable";
import { StatusBadge } from "@/components/data/StatusBadge";
import { EmptyState } from "@/components/feedback/EmptyState";
import { useUnreturnedRecords } from "@/lib/hooks/useRecords";
import { fmt } from "@/lib/utils/date";
import type { LoanRecord } from "@/lib/types/domain";

/** Ported from ui_kits/console/History.jsx "/records" (scope='unreturned'). */
export default function UnreturnedRecordsPage() {
  const router = useRouter();
  const { data: records, isLoading, isError, refetch } = useUnreturnedRecords();

  const [q, setQ] = useState("");
  const [sort, setSort] = useState<DataTableSort>({ key: "dueDate", dir: "asc" });

  const rows = useMemo(() => {
    let r = records ?? [];
    if (q) {
      const t = q.toLowerCase();
      r = r.filter((x) => `${x.bookName} ${x.author} ${x.memberName}`.toLowerCase().includes(t));
    }
    const dir = sort.dir === "asc" ? 1 : -1;
    return [...r].sort((a, b) => {
      if (sort.key === "dueDate") return (a.dueDate.getTime() - b.dueDate.getTime()) * dir;
      if (sort.key === "bookName") return a.bookName.localeCompare(b.bookName) * dir;
      return a.memberName.localeCompare(b.memberName) * dir;
    });
  }, [records, q, sort]);

  const columns: DataTableColumn<LoanRecord>[] = [
    { key: "bookName", header: "Book", sortable: true },
    { key: "author", header: "Author", width: "170px" },
    {
      key: "memberName",
      header: "Member",
      width: "140px",
      sortable: true,
      render: (r) => <Link href={`/members/${r.memberId}`}>{r.memberName}</Link>,
    },
    {
      key: "dueDate",
      header: "Due date",
      width: "104px",
      sortable: true,
      render: (r) => <span style={{ font: "var(--type-mono)", color: r.status === "overdue" ? "var(--danger)" : "var(--ink-subtle)" }}>{fmt(r.dueDate)}</span>,
    },
    {
      key: "status",
      header: "Status",
      width: "132px",
      render: (r) => <StatusBadge status={r.status}>{r.status === "overdue" ? `${r.daysOverdue} days over` : undefined}</StatusBadge>,
    },
  ];

  const state = isError ? "error" : isLoading ? "loading" : rows.length ? "loaded" : "empty";

  return (
    <div>
      <PageHeader title="Currently out" subtitle="Every book off the shelf right now, most urgent first." />
      <div style={{ display: "flex", flexWrap: "wrap", alignItems: "center", gap: "var(--space-sm) var(--space-md)", marginBottom: "var(--space-md)" }}>
        <Input iconLeft="search" placeholder="Search by book or member…" value={q} onChange={(e) => setQ(e.target.value)} width={280} />
        <span style={{ marginLeft: "auto", font: "var(--type-caption)", color: "var(--ink-subtle)", flex: "0 0 auto" }}>
          {rows.length} {rows.length === 1 ? "record" : "records"}
        </span>
      </div>
      <DataTable
        state={state}
        rows={rows}
        getRowKey={(r) => `${r.memberId}-${r.bookId}`}
        sort={sort}
        onSortChange={setSort}
        rowTone={(r) => (r.status === "overdue" ? "danger" : undefined)}
        columns={columns}
        rowActions={(r) => (
          <Button size="sm" variant="secondary" onClick={() => router.push(`/returns?member=${r.memberId}`)}>
            Take it back
          </Button>
        )}
        alwaysShowActions
        error={<EmptyState icon="server-off" headline="Can't load records." body="Check that the API is running and try again." actionLabel="Try again" onAction={() => refetch()} />}
        empty={
          q ? (
            <EmptyState icon="search" headline={`Nothing matches "${q}"`} body="Try a different book or member." actionLabel="Clear search" onAction={() => setQ("")} />
          ) : (
            <EmptyState icon="circle-check" headline="Nothing is out" body="Every book is on the shelf." />
          )
        }
      />
    </div>
  );
}
