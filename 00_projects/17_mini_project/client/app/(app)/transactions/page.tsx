"use client";

import { useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import { PageHeader } from "@/components/navigation/PageHeader";
import { Input } from "@/components/forms/Input";
import { DataTable, type DataTableColumn, type DataTableSort } from "@/components/data/DataTable";
import { EmptyState } from "@/components/feedback/EmptyState";
import { useTransactions } from "@/lib/hooks/useTransactions";
import { fmt } from "@/lib/utils/date";
import type { Transaction } from "@/lib/types/domain";

/** Ported from ui_kits/console/History.jsx "/transactions". */
export default function TransactionsPage() {
  const router = useRouter();
  const { data: transactions, isLoading, isError, refetch } = useTransactions();

  const [q, setQ] = useState("");
  const [sort, setSort] = useState<DataTableSort>({ key: "borrowDate", dir: "desc" });

  const rows = useMemo(() => {
    let r = transactions ?? [];
    if (q) {
      const t = q.toLowerCase();
      r = r.filter((x) => x.memberName.toLowerCase().includes(t) || String(x.id).includes(t) || x.books.some((b) => b.bookName.toLowerCase().includes(t)));
    }
    const dir = sort.dir === "asc" ? 1 : -1;
    return [...r].sort((a, b) => {
      if (sort.key === "borrowDate") return (a.borrowDate.getTime() - b.borrowDate.getTime()) * dir;
      if (sort.key === "id") return (a.id - b.id) * dir;
      return String(a.memberName).localeCompare(String(b.memberName)) * dir;
    });
  }, [transactions, q, sort]);

  const columns: DataTableColumn<Transaction>[] = [
    { key: "id", header: "ID", width: "92px", sortable: true, render: (t) => <span style={{ font: "var(--type-mono)", color: "var(--ink-muted)" }}>TXN {t.id}</span> },
    { key: "memberName", header: "Member", width: "160px", sortable: true },
    {
      key: "books",
      header: "Books",
      render: (t) => (
        <span>
          {t.books[0]?.bookName}
          {t.books.length > 1 ? <span style={{ color: "var(--ink-subtle)" }}> +{t.books.length - 1} more</span> : null}
        </span>
      ),
    },
    { key: "borrowDate", header: "Borrowed on", width: "132px", sortable: true, render: (t) => <span style={{ font: "var(--type-mono)", color: "var(--ink-subtle)" }}>{fmt(t.borrowDate)}</span> },
  ];

  const state = isError ? "error" : isLoading ? "loading" : rows.length ? "loaded" : "empty";

  return (
    <div>
      <PageHeader title="Transactions" subtitle="Every lending event, newest first." />
      <div style={{ display: "flex", flexWrap: "wrap", alignItems: "center", gap: "var(--space-sm) var(--space-md)", marginBottom: "var(--space-md)" }}>
        <Input iconLeft="search" placeholder="Search by member, title or ID…" value={q} onChange={(e) => setQ(e.target.value)} width={300} />
        <span style={{ marginLeft: "auto", font: "var(--type-caption)", color: "var(--ink-subtle)", flex: "0 0 auto" }}>
          {rows.length} {rows.length === 1 ? "transaction" : "transactions"}
        </span>
      </div>
      <DataTable
        state={state}
        rows={rows}
        getRowKey={(t) => t.id}
        sort={sort}
        onSortChange={setSort}
        onRowClick={(t) => router.push(`/transactions/${t.id}`)}
        columns={columns}
        error={<EmptyState icon="server-off" headline="Can't load transactions." body="Check that the API is running and try again." actionLabel="Try again" onAction={() => refetch()} />}
        empty={
          q ? (
            <EmptyState icon="search" headline={`Nothing matches "${q}"`} body="Try a member name, a title, or a transaction ID." actionLabel="Clear search" onAction={() => setQ("")} />
          ) : (
            <EmptyState icon="history" headline="No transactions yet" body="Lending a book will create the first one." actionLabel="Lend books" onAction={() => router.push("/lend")} />
          )
        }
      />
    </div>
  );
}
