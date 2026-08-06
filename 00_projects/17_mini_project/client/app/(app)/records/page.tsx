"use client";

import { useMemo, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { PageHeader } from "@/components/navigation/PageHeader";
import { Tabs } from "@/components/navigation/Tabs";
import { Input } from "@/components/forms/Input";
import { DataTable, type DataTableColumn, type DataTableSort } from "@/components/data/DataTable";
import { StatusBadge } from "@/components/data/StatusBadge";
import { EmptyState } from "@/components/feedback/EmptyState";
import { useAllRecords } from "@/lib/hooks/useRecords";
import { fmt } from "@/lib/utils/date";
import type { LoanRecord } from "@/lib/types/domain";

type Filter = "all" | "out" | "returned" | "overdue";

/** Ported from ui_kits/console/History.jsx "/records" (scope='all'). */
export default function RecordsPage() {
  const router = useRouter();
  const { data: records, isLoading, isError, refetch } = useAllRecords();

  const [filter, setFilter] = useState<Filter>("all");
  const [q, setQ] = useState("");
  const [sort, setSort] = useState<DataTableSort>({ key: "dueDate", dir: "desc" });

  const rows = useMemo(() => {
    let r = records ?? [];
    r = r.filter((x) => (filter === "all" ? true : filter === "out" ? x.status !== "returned" : filter === "returned" ? x.status === "returned" : x.status === "overdue"));
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
  }, [records, filter, q, sort]);

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
      <PageHeader title="Borrow records" subtitle="Every individual loan, across all members and transactions." />
      <div style={{ display: "flex", flexWrap: "wrap", alignItems: "center", gap: "var(--space-sm) var(--space-md)", marginBottom: "var(--space-md)" }}>
        <Input iconLeft="search" placeholder="Search by book or member…" value={q} onChange={(e) => setQ(e.target.value)} width={280} />
        <Tabs
          value={filter}
          onChange={(v) => setFilter(v as Filter)}
          tabs={[
            { value: "all", label: "All" },
            { value: "out", label: "Out" },
            { value: "returned", label: "Returned" },
            { value: "overdue", label: "Overdue" },
          ]}
        />
        <span style={{ marginLeft: "auto", font: "var(--type-caption)", color: "var(--ink-subtle)", flex: "0 0 auto" }}>
          {rows.length} {rows.length === 1 ? "record" : "records"}
        </span>
      </div>
      <DataTable
        state={state}
        rows={rows}
        getRowKey={(r) => `${r.memberId}-${r.bookId}-${r.dueDate.getTime()}`}
        sort={sort}
        onSortChange={setSort}
        rowTone={(r) => (r.status === "overdue" ? "danger" : r.status === "returned" ? "muted" : undefined)}
        columns={columns}
        error={<EmptyState icon="server-off" headline="Can't load records." body="Check that the API is running and try again." actionLabel="Try again" onAction={() => refetch()} />}
        empty={
          q ? (
            <EmptyState icon="search" headline={`Nothing matches "${q}"`} body="Try a different book or member." actionLabel="Clear search" onAction={() => setQ("")} />
          ) : (
            <EmptyState icon="list" headline="No records yet" body="Lending a book creates the first record." actionLabel="Lend books" onAction={() => router.push("/lend")} />
          )
        }
      />
    </div>
  );
}
