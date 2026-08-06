"use client";

import { useMemo, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { PageHeader } from "@/components/navigation/PageHeader";
import { Button } from "@/components/forms/Button";
import { Panel } from "@/components/panels/Panel";
import { Banner } from "@/components/feedback/Banner";
import { EmptyState } from "@/components/feedback/EmptyState";
import { StatusBadge } from "@/components/data/StatusBadge";
import { FineDisplay } from "@/components/data/FineDisplay";
import { Skeleton } from "@/components/data/Skeleton";
import { Tabs } from "@/components/navigation/Tabs";
import { UnreturnedList } from "@/components/circulation/UnreturnedList";
import { useMember, useActivateMember } from "@/lib/hooks/useMembers";
import { useUnreturnedForMemberDerived, useRecordsForMember } from "@/lib/hooks/useRecords";
import { useTransactionsForMember } from "@/lib/hooks/useTransactions";
import { useSettings } from "@/lib/hooks/useSettings";
import { useToast } from "@/lib/hooks/useToast";
import { fmt } from "@/lib/utils/date";

type HistoryTab = "transactions" | "records";

export default function MemberDetailPage() {
  const params = useParams<{ id: string }>();
  const id = Number(params.id);
  const router = useRouter();
  const toast = useToast();

  const { data: member, isLoading, isError } = useMember(id);
  const out = useUnreturnedForMemberDerived(id);
  const txns = useTransactionsForMember(id);
  const records = useRecordsForMember(id);
  const settings = useSettings();
  const activateMember = useActivateMember();

  const [tab, setTab] = useState<HistoryTab>("transactions");

  const finePerDay = settings.data?.FINE_PER_DAY ?? 0;
  const outRows = useMemo(
    () =>
      (out.data ?? []).map((r) => ({
        id: r.bookId,
        title: r.bookName,
        author: r.author,
        dueDate: fmt(r.dueDate),
        daysOverdue: r.daysOverdue,
        fine: r.daysOverdue ? r.daysOverdue * finePerDay : 0,
        estimate: !!r.daysOverdue,
      })),
    [out.data, finePerDay],
  );
  const totalFineEstimate = outRows.reduce((s, r) => s + r.fine, 0);

  const handleActivate = async () => {
    if (!member) return;
    try {
      await activateMember.mutateAsync(member.id);
      toast.success(`${member.name} reactivated.`);
    } catch (err) {
      toast.errorFrom(err);
    }
  };

  if (isLoading) {
    return (
      <div style={{ display: "grid", gap: "var(--space-lg)" }}>
        <Skeleton width={320} height={32} />
        <Skeleton height={160} radius="var(--radius-card)" />
      </div>
    );
  }

  if (isError || !member) {
    return (
      <EmptyState
        icon="users"
        headline="That member doesn't exist."
        body="They may have been removed, or the link is wrong."
        actionLabel="Back to members"
        onAction={() => router.push("/members")}
      />
    );
  }

  const first = member.name.split(" ")[0];

  return (
    <div>
      <PageHeader
        back={{ label: "Back to members", onClick: (e) => { e.preventDefault(); router.push("/members"); } }}
        title={<span style={{ color: member.isActive ? "var(--ink)" : "var(--ink-subtle)" }}>{member.name}</span>}
        meta={
          <>
            <span style={{ font: "var(--type-mono)" }}>{member.email}</span>
            <span style={{ color: "var(--ink-tertiary)" }}>·</span>
            <span>{member.age}</span>
            <StatusBadge status={member.isActive ? "active" : "inactive"} />
            <span style={{ font: "var(--type-mono)" }}>ID {member.id}</span>
          </>
        }
        action={
          <>
            <Button disabled={!member.isActive || !member.remainingAllowance} onClick={() => router.push(`/lend?member=${member.id}`)}>
              Lend books
            </Button>
            <Button variant="secondary" iconLeft="pencil" onClick={() => router.push(`/members/${member.id}/edit`)}>
              Edit
            </Button>
          </>
        }
      />

      {!member.isActive ? (
        <div style={{ marginBottom: "var(--space-lg)" }}>
          <Banner
            tone="warning"
            action={
              <Button variant="secondary" size="sm" loading={activateMember.isPending} onClick={handleActivate}>
                Reactivate
              </Button>
            }
          >
            This member is deactivated and can’t borrow books.
          </Banner>
        </div>
      ) : null}

      <div style={{ display: "grid", gap: "var(--space-lg)" }}>
        <Panel
          title={`Out now${outRows.length ? ` (${outRows.length})` : ""}`}
          padded={!outRows.length}
          footer={
            outRows.length ? (
              <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: "var(--space-md)" }}>
                <span style={{ font: "var(--type-body-sm)", color: "var(--ink-subtle)" }}>
                  {member.remainingAllowance} more allowed · fines so far <FineDisplay amount={totalFineEstimate} estimate />
                </span>
                <Button variant="secondary" onClick={() => router.push(`/returns?member=${member.id}`)}>
                  Take books back
                </Button>
              </div>
            ) : null
          }
        >
          {outRows.length ? (
            <UnreturnedList records={outRows} selectable={false} />
          ) : (
            <EmptyState icon="circle-check" headline={`${first} has nothing out.`} body="Everything they borrowed is back on the shelf." pattern={false} />
          )}
        </Panel>

        <Panel
          title="History"
          padded={false}
          action={
            <Tabs
              value={tab}
              onChange={(v) => setTab(v as HistoryTab)}
              tabs={[
                { value: "transactions", label: "Transactions", count: txns.data?.length ?? 0 },
                { value: "records", label: "All records", count: records.data?.length ?? 0 },
              ]}
            />
          }
        >
          {tab === "transactions" ? (
            txns.data?.length ? (
              txns.data.map((t, i) => (
                <div
                  key={t.id}
                  onClick={() => router.push(`/transactions/${t.id}`)}
                  style={{
                    display: "flex",
                    alignItems: "center",
                    gap: "var(--space-md)",
                    minHeight: 52,
                    padding: "var(--space-xs) var(--space-md)",
                    borderTop: i ? "1px solid var(--hairline)" : "none",
                    cursor: "pointer",
                  }}
                >
                  <span style={{ font: "var(--type-mono)", color: "var(--ink-muted)", width: 84 }}>TXN {t.id}</span>
                  <span style={{ flex: 1, minWidth: 0, font: "var(--type-body-sm)", color: "var(--ink)", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                    {t.books[0]?.bookName}
                    {t.books.length > 1 ? <span style={{ color: "var(--ink-subtle)" }}> +{t.books.length - 1} more</span> : null}
                  </span>
                  <span style={{ font: "var(--type-mono)", color: "var(--ink-subtle)" }}>{fmt(t.borrowDate)}</span>
                </div>
              ))
            ) : (
              <EmptyState
                icon="history"
                headline={`${first} hasn’t borrowed anything yet.`}
                body="Their history will fill in as they take books out."
                actionLabel="Lend books"
                onAction={() => router.push(`/lend?member=${member.id}`)}
              />
            )
          ) : records.data?.length ? (
            records.data.map((r, i) => (
              <div
                key={`${r.bookId}-${i}`}
                style={{ display: "flex", alignItems: "center", gap: "var(--space-md)", minHeight: 52, padding: "var(--space-xs) var(--space-md)", borderTop: i ? "1px solid var(--hairline)" : "none" }}
              >
                <span style={{ flex: 1, minWidth: 0, font: "var(--type-body-sm)", color: "var(--ink)", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>{r.bookName}</span>
                <span style={{ font: "var(--type-mono)", color: "var(--ink-subtle)" }}>due {fmt(r.dueDate)}</span>
                <StatusBadge status={r.status}>{r.status === "overdue" ? `${r.daysOverdue} days over` : undefined}</StatusBadge>
              </div>
            ))
          ) : (
            <EmptyState icon="list" headline="No records yet" body="Every book lent to this member will appear here." />
          )}
        </Panel>
      </div>
    </div>
  );
}
