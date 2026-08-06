"use client";

import { useEffect, useMemo, useRef, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { PageHeader } from "@/components/navigation/PageHeader";
import { Panel } from "@/components/panels/Panel";
import { MemberSearchCombobox, MemberSummary } from "@/components/circulation/MemberSearchCombobox";
import { UnreturnedList, type UnreturnedRecord } from "@/components/circulation/UnreturnedList";
import { Checkbox } from "@/components/forms/Checkbox";
import { Button } from "@/components/forms/Button";
import { FineDisplay } from "@/components/data/FineDisplay";
import { EmptyState } from "@/components/feedback/EmptyState";
import { Skeleton } from "@/components/data/Skeleton";
import { useMembers } from "@/lib/hooks/useMembers";
import { useReturnBooks, useUnreturnedForMember } from "@/lib/hooks/useRecords";
import { useSettings } from "@/lib/hooks/useSettings";
import { useReturnSelectionStore } from "@/lib/stores/returnSelection";
import { useToast } from "@/lib/hooks/useToast";
import { daysOverdue as computeDaysOverdue, fmt, parseApiDate } from "@/lib/utils/date";
import { formatAmount } from "@/lib/utils/currency";

/** Ported from ui_kits/console/Returns.jsx. */
export function ReturnsWorkspace() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const toast = useToast();

  const { member, selected, selectMember, toggle, selectAll, clearSelection, reset } = useReturnSelectionStore();
  const [mq, setMq] = useState("");
  const [receipt, setReceipt] = useState<{ books: UnreturnedRecord[]; total: number } | null>(null);

  const { data: members, isLoading: membersLoading } = useMembers();
  const unreturned = useUnreturnedForMember(member?.id ?? NaN);
  const settings = useSettings();
  const returnBooks = useReturnBooks();

  const finePerDay = settings.data?.FINE_PER_DAY ?? 0;

  const records: UnreturnedRecord[] = useMemo(
    () =>
      (unreturned.data ?? []).map((r) => {
        const due = parseApiDate(r.dueDate);
        const overdue = computeDaysOverdue(due);
        return {
          id: r.borrowedBookId,
          title: r.bookName,
          author: r.author,
          dueDate: fmt(due),
          daysOverdue: overdue || undefined,
          fine: overdue ? overdue * finePerDay : 0,
          estimate: overdue > 0,
        };
      }),
    [unreturned.data, finePerDay],
  );

  const presetMemberId = searchParams.get("member");
  const autoSelectedFor = useRef<number | null>(null);
  useEffect(() => {
    if (presetMemberId && !member && members) {
      const preset = members.find((m) => m.id === Number(presetMemberId));
      if (preset) selectMember(preset);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [presetMemberId, members]);

  // Pre-tick every book the member has out, once per member selection.
  useEffect(() => {
    if (member && unreturned.data && autoSelectedFor.current !== member.id) {
      selectAll(unreturned.data.map((r) => r.borrowedBookId));
      autoSelectedFor.current = member.id;
    }
  }, [member, unreturned.data, selectAll]);

  const chosen = records.filter((r) => selected.has(r.id as number));
  const totalFine = chosen.reduce((s, r) => s + (r.fine || 0), 0);
  const allOn = records.length > 0 && selected.size === records.length;

  const handleChangeMember = () => {
    autoSelectedFor.current = null;
    reset();
  };

  const handleTakeBack = async () => {
    if (!member || chosen.length === 0) return;
    try {
      const result = await returnBooks.mutateAsync({
        memberId: member.id,
        books: chosen.map((r) => ({ bookId: r.id as number })),
      });
      toast.success(`${result.books.length} ${result.books.length === 1 ? "book" : "books"} back from ${member.name}.`);
      setReceipt({
        books: result.books.map((b) => ({ id: b.bookId, title: b.bookName, dueDate: fmt(b.returnDate), fine: b.fine, estimate: false })),
        total: result.totalFine,
      });
    } catch (err) {
      toast.errorFrom(err);
    }
  };

  const handleAgain = () => {
    setReceipt(null);
    autoSelectedFor.current = null;
    reset();
  };

  if (receipt) {
    return (
      <div style={{ maxWidth: "var(--form-max-width)" }}>
        <PageHeader title="Taken back" subtitle={`${receipt.books.length} ${receipt.books.length === 1 ? "book is" : "books are"} back on the shelf.`} />
        <Panel
          title="Receipt"
          padded={false}
          footer={
            <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: "var(--space-md)" }}>
              <span style={{ font: "var(--type-body-sm)", color: "var(--ink)" }}>
                {receipt.total ? <>{formatAmount(receipt.total)} to collect.</> : "Nothing to collect."}
              </span>
              <div style={{ display: "flex", gap: "var(--space-xs)" }}>
                <Button variant="secondary" onClick={handleAgain}>
                  Take more books back
                </Button>
                <Button variant="tertiary" onClick={() => router.push("/dashboard")}>
                  Done
                </Button>
              </div>
            </div>
          }
        >
          <UnreturnedList records={receipt.books} selectable={false} />
        </Panel>
      </div>
    );
  }

  if (membersLoading) {
    return (
      <div style={{ maxWidth: 800, display: "grid", gap: "var(--space-lg)" }}>
        <Skeleton width={220} height={28} />
        <Skeleton height={120} radius="var(--radius-panel)" />
      </div>
    );
  }

  return (
    <div style={{ maxWidth: 800 }}>
      <PageHeader title="Take books back" subtitle="Find the member, tick what they’ve brought in." />
      <div style={{ display: "grid", gap: "var(--space-lg)" }}>
        <Panel title="Member">
          {member ? (
            <MemberSummary
              member={{ id: member.id, name: member.name, email: member.email, booksOut: member.booksOut, allowance: member.remainingAllowance }}
              onChange={handleChangeMember}
            />
          ) : (
            <MemberSearchCombobox
              members={(members ?? []).map((m) => ({ id: m.id, name: m.name, email: m.email, booksOut: m.booksOut, allowance: m.remainingAllowance, disabled: false }))}
              query={mq}
              onQueryChange={setMq}
              onSelect={(opt) => {
                const full = (members ?? []).find((m) => m.id === opt.id);
                if (full) selectMember(full);
              }}
            />
          )}
        </Panel>

        {member ? (
          unreturned.isLoading ? (
            <Skeleton height={120} radius="var(--radius-panel)" />
          ) : records.length ? (
            <>
              <Panel
                title="Out now"
                padded={false}
                action={
                  <Checkbox
                    id="all"
                    checked={allOn}
                    indeterminate={!allOn && selected.size > 0}
                    onChange={() => (allOn ? clearSelection() : selectAll(records.map((r) => r.id as number)))}
                    label="Select all"
                    style={{ minHeight: 0 }}
                  />
                }
              >
                <UnreturnedList records={records} selected={Array.from(selected)} onToggle={(r) => toggle(r.id as number)} />
              </Panel>

              <Panel
                title="Total"
                footer={
                  <Button fullWidth disabled={!chosen.length} onClick={handleTakeBack} loading={returnBooks.isPending} loadingLabel="Taking back…">
                    {chosen.length ? `Take back ${chosen.length}${chosen.length === 1 ? " book" : " books"}` : "Tick a book to take back"}
                  </Button>
                }
              >
                <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: "var(--space-md)" }}>
                  <span style={{ display: "flex", alignItems: "center", gap: "var(--space-xs)", font: "var(--type-body-sm)", color: "var(--ink)" }}>
                    {chosen.length} {chosen.length === 1 ? "book" : "books"} · fine due <FineDisplay amount={totalFine} estimate={totalFine > 0} />
                  </span>
                  <span style={{ font: "var(--type-body-sm)", color: "var(--ink-subtle)" }}>Returning today, {fmt(new Date())}</span>
                </div>
              </Panel>
            </>
          ) : (
            <Panel padded={false}>
              <EmptyState
                icon="circle-check"
                headline={`${member.name.split(" ")[0]} has no books out.`}
                body="Nothing to take back."
                actionLabel="Lend books"
                onAction={() => router.push(`/lend?member=${member.id}`)}
                pattern={false}
              />
            </Panel>
          )
        ) : null}
      </div>
    </div>
  );
}
