"use client";

import { useMemo } from "react";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { PageHeader } from "@/components/navigation/PageHeader";
import { Panel } from "@/components/panels/Panel";
import { StatusBadge } from "@/components/data/StatusBadge";
import { EmptyState } from "@/components/feedback/EmptyState";
import { Skeleton } from "@/components/data/Skeleton";
import { useTransaction } from "@/lib/hooks/useTransactions";
import { useUnreturnedAll } from "@/lib/hooks/useRecords";
import { daysOverdue as computeDaysOverdue, fmt, isDueToday, isOverdue, parseApiDate } from "@/lib/utils/date";
import type { Status } from "@/components/data/StatusBadge";

/**
 * Ported from ui_kits/console/History.jsx "/transactions/[id]". The API
 * doesn't return a `returned` flag on transaction items, so per-book status
 * is inferred the same way as /records — a book still present in
 * unreturned/all (matched on memberId + bookId + dueDate) is out/due-today/
 * overdue; absent means returned. Absent books have no returnDate in this
 * response shape, so they're labelled "Returned" without a date rather than
 * inventing one.
 */
export default function TransactionDetailPage() {
  const params = useParams<{ id: string }>();
  const id = Number(params.id);
  const router = useRouter();

  const { data: transaction, isLoading, isError } = useTransaction(id);
  const unreturned = useUnreturnedAll();

  // Compare parsed timestamps, not raw ISO strings — the two sides may
  // format the same instant differently (offset, precision), which a
  // string-equality key would silently miss.
  const outKeySet = useMemo(
    () => new Set((unreturned.data ?? []).map((r) => `${r.memberId}:${r.borrowedBookId}:${parseApiDate(r.dueDate).getTime()}`)),
    [unreturned.data],
  );

  if (isLoading) {
    return (
      <div style={{ maxWidth: 760, display: "grid", gap: "var(--space-lg)" }}>
        <Skeleton width={200} height={32} />
        <Skeleton height={160} radius="var(--radius-panel)" />
      </div>
    );
  }

  if (isError || !transaction) {
    return (
      <EmptyState
        icon="history"
        headline="That transaction doesn't exist."
        body="It may have been removed, or the link is wrong."
        actionLabel="Back to transactions"
        onAction={() => router.push("/transactions")}
      />
    );
  }

  return (
    <div style={{ maxWidth: 760 }}>
      <PageHeader
        back={{ label: "Back to transactions", onClick: (e) => { e.preventDefault(); router.push("/transactions"); } }}
        title={<span style={{ font: "var(--type-mono)", fontSize: "var(--size-headline)" }}>TXN {transaction.id}</span>}
        meta={
          <>
            <Link href={`/members/${transaction.memberId}`}>{transaction.memberName}</Link>
            <span style={{ color: "var(--ink-tertiary)" }}>·</span>
            <span>Borrowed {fmt(transaction.borrowDate)}</span>
            <span style={{ color: "var(--ink-tertiary)" }}>·</span>
            <span>
              {transaction.books.length} {transaction.books.length === 1 ? "book" : "books"}
            </span>
          </>
        }
      />
      <Panel title="Books in this transaction" padded={false}>
        {transaction.books.map((b, i) => {
          const key = `${transaction.memberId}:${b.bookId}:${b.dueDate.getTime()}`;
          const stillOut = outKeySet.has(key);
          const overdueDays = stillOut ? computeDaysOverdue(b.dueDate) : 0;
          const status: Status = !stillOut ? "returned" : isOverdue(b.dueDate) ? "overdue" : isDueToday(b.dueDate) ? "due-today" : "out";
          return (
            <div key={`${b.bookId}-${i}`} style={{ display: "flex", alignItems: "center", gap: "var(--space-md)", minHeight: 52, padding: "var(--space-xs) var(--space-md)", borderTop: i ? "1px solid var(--hairline)" : "none" }}>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ font: "var(--type-body-sm)", color: "var(--ink)", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{b.bookName}</div>
                <div style={{ font: "var(--type-caption)", color: "var(--ink-subtle)" }}>{b.author}</div>
              </div>
              <span style={{ font: "var(--type-mono)", color: status === "overdue" ? "var(--danger)" : "var(--ink-subtle)" }}>due {fmt(b.dueDate)}</span>
              <span style={{ minWidth: 120, textAlign: "right" }}>
                <StatusBadge status={status}>{status === "overdue" ? `${overdueDays} days over` : undefined}</StatusBadge>
              </span>
            </div>
          );
        })}
      </Panel>
    </div>
  );
}
