"use client";

import { useEffect, useMemo, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { PageHeader } from "@/components/navigation/PageHeader";
import { Panel } from "@/components/panels/Panel";
import { MemberSearchCombobox, MemberSummary, type MemberOption } from "@/components/circulation/MemberSearchCombobox";
import { BookSearchCombobox, type BookOption } from "@/components/circulation/BookSearchCombobox";
import { BasketPanel } from "@/components/circulation/BasketPanel";
import { Button } from "@/components/forms/Button";
import { Banner } from "@/components/feedback/Banner";
import { EmptyState } from "@/components/feedback/EmptyState";
import { Skeleton } from "@/components/data/Skeleton";
import { MemberRow } from "@/components/circulation/MemberRow";
import { BookRow } from "@/components/circulation/BookRow";
import { useMembers } from "@/lib/hooks/useMembers";
import { useBooks } from "@/lib/hooks/useBooks";
import { useUnreturnedForMember } from "@/lib/hooks/useRecords";
import { useSettings } from "@/lib/hooks/useSettings";
import { useBorrowBooks } from "@/lib/hooks/useTransactions";
import { useLendBasketStore } from "@/lib/stores/lendBasket";
import { useToast } from "@/lib/hooks/useToast";
import { useMediaQuery } from "@/lib/hooks/useMediaQuery";
import { dueDateFor, fmt } from "@/lib/utils/date";
import type { Member, Transaction } from "@/lib/types/domain";

/**
 * Ported from ui_kits/console/Lend.jsx. Not a form — a 3-panel workspace
 * with every client-side guard rail from uploads/06-validation-rules.md
 * "Workflow validation" expressed as a per-row `reason` in the book picker.
 */
export function LendWorkspace() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const toast = useToast();

  const { member, items, selectMember, clearMember, addBook, removeBook, clear } = useLendBasketStore();
  const [mq, setMq] = useState("");
  const [bq, setBq] = useState("");
  const [receipt, setReceipt] = useState<{ member: Member; transaction: Transaction } | null>(null);
  // Layout rules: /lend's two panels stack below 768px instead of staying
  // side-by-side and getting squeezed.
  const isNarrow = useMediaQuery("(max-width:767px)");
  const workspaceColumns = isNarrow ? "1fr" : "1fr 1fr";

  const { data: members, isLoading: membersLoading } = useMembers();
  const { data: books, isLoading: booksLoading } = useBooks();
  const settings = useSettings();
  const unreturnedForMember = useUnreturnedForMember(member?.id ?? NaN);
  const borrowBooks = useBorrowBooks();

  const presetMemberId = searchParams.get("member");
  useEffect(() => {
    if (presetMemberId && !member && members) {
      const preset = members.find((m) => m.id === Number(presetMemberId));
      if (preset) selectMember(preset);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [presetMemberId, members]);

  const first = member ? member.name.split(" ")[0] : "This member";
  const maxBooks = settings.data?.MAX_BOOKS ?? null;
  const maxBorrowDays = settings.data?.MAX_BORROW_DAYS ?? null;
  const settingsMissing = !settings.isLoading && (maxBooks == null || maxBorrowDays == null);

  const memberResults: MemberOption[] = useMemo(
    () =>
      (members ?? [])
        .filter((m) => !mq || `${m.name} ${m.email}`.toLowerCase().includes(mq.toLowerCase()))
        .map((m) => ({ id: m.id, name: m.name, email: m.email, booksOut: m.booksOut, allowance: m.remainingAllowance, disabled: !m.isActive })),
    [members, mq],
  );

  const alreadyOutIds = useMemo(() => new Set((unreturnedForMember.data ?? []).map((r) => r.borrowedBookId)), [unreturnedForMember.data]);

  const bookResults: BookOption[] = useMemo(
    () =>
      (books ?? [])
        .filter((b) => !bq || `${b.title} ${b.author}`.toLowerCase().includes(bq.toLowerCase()))
        .map((b) => {
          let reason: string | null = null;
          if (!b.isActive) reason = "Inactive";
          else if (b.availableCopies === 0) reason = "None available";
          else if (alreadyOutIds.has(b.id)) reason = `${first} already has this book`;
          else if (items.some((x) => x.id === b.id)) reason = "Already in this basket";
          else if (member && maxBooks != null && items.length >= member.remainingAllowance) {
            reason = `That's over ${first}'s limit of ${maxBooks} books. Remove one first.`;
          }
          return {
            id: b.id,
            title: b.title,
            author: b.author,
            available: b.availableCopies,
            total: b.totalCopies,
            disabled: !member || !!reason,
            reason: member ? (reason ?? undefined) : undefined,
          };
        }),
    [books, bq, alreadyOutIds, items, member, maxBooks, first],
  );

  const handleAdd = (option: BookOption) => {
    const book = (books ?? []).find((b) => b.id === option.id);
    if (book) addBook(book);
  };

  const handleConfirm = async () => {
    if (!member || items.length === 0) return;
    try {
      const transaction = await borrowBooks.mutateAsync({
        memberId: member.id,
        books: items.map((b) => ({ bookId: b.id })),
      });
      toast.success(`${items.length} ${items.length === 1 ? "book" : "books"} lent to ${member.name}.`);
      setReceipt({ member, transaction });
    } catch (err) {
      toast.errorFrom(err);
    }
  };

  const handleLendAgain = () => {
    setReceipt(null);
    clear();
  };

  if (receipt) {
    return <Receipt member={receipt.member} transaction={receipt.transaction} onAgain={handleLendAgain} />;
  }

  if (membersLoading || booksLoading || settings.isLoading) {
    return (
      <div style={{ display: "grid", gap: "var(--space-lg)" }}>
        <Skeleton width={220} height={28} />
        <div style={{ display: "grid", gridTemplateColumns: workspaceColumns, gap: "var(--space-lg)" }}>
          <Skeleton height={200} radius="var(--radius-panel)" />
          <Skeleton height={200} radius="var(--radius-panel)" />
        </div>
      </div>
    );
  }

  if (settingsMissing) {
    return (
      <EmptyState
        icon="settings"
        headline="Library rules aren't set up yet."
        body="Set the borrowing limits before lending any books."
        actionLabel="Go to settings"
        onAction={() => router.push("/settings/library")}
      />
    );
  }

  const dueDatePreview = items.length && maxBorrowDays != null ? fmt(dueDateFor(new Date(), maxBorrowDays)) : undefined;

  return (
    <div>
      <PageHeader title="Lend books" subtitle="Pick a member, add their books, then confirm." />
      <div style={{ display: "grid", gridTemplateColumns: workspaceColumns, gap: "var(--space-lg)", alignItems: "start" }}>
        <div style={{ display: "grid", gap: "var(--space-lg)" }}>
          <Panel step={1} title="Member">
            {member ? (
              <MemberSummary
                member={{ id: member.id, name: member.name, email: member.email, booksOut: member.booksOut, allowance: member.remainingAllowance }}
                onChange={() => clearMember()}
              />
            ) : (
              <MemberSearchCombobox members={memberResults} query={mq} onQueryChange={setMq} onSelect={(opt) => {
                const full = (members ?? []).find((m) => m.id === opt.id);
                if (full) selectMember(full);
              }} />
            )}
          </Panel>
          {member && !member.remainingAllowance ? (
            <Banner tone="warning" action={<Button variant="secondary" size="sm" onClick={() => router.push(`/returns?member=${member.id}`)}>Take books back</Button>}>
              {first} already has {member.booksOut} books out — the limit is {maxBooks}.
            </Banner>
          ) : null}
          <BasketPanel
            books={items}
            dueDate={dueDatePreview}
            disabled={!member}
            onRemove={(b) => removeBook(b.id)}
            onConfirm={handleConfirm}
          />
        </div>
        <Panel step={2} title="Books">
          {!member ? (
            <div style={{ marginBottom: "var(--space-md)" }}>
              <Banner tone="info">Pick a member first — what they can borrow depends on who they are.</Banner>
            </div>
          ) : null}
          <BookSearchCombobox books={bookResults} query={bq} onQueryChange={setBq} onAdd={handleAdd} />
        </Panel>
      </div>
    </div>
  );
}

function Receipt({ member, transaction, onAgain }: { member: Member; transaction: Transaction; onAgain: () => void }) {
  const router = useRouter();
  const dueDate = transaction.books[0] ? fmt(transaction.books[0].dueDate) : "";
  return (
    <div style={{ maxWidth: "var(--form-max-width)" }}>
      <PageHeader
        title="Lent"
        subtitle={`${transaction.books.length} ${transaction.books.length === 1 ? "book is" : "books are"} now with ${member.name}.`}
      />
      <Panel
        title="Receipt"
        padded={false}
        footer={
          <div style={{ display: "flex", gap: "var(--space-xs)" }}>
            <Button variant="secondary" iconLeft="printer" onClick={() => window.print()}>
              Print
            </Button>
            <Button onClick={onAgain}>Lend more books</Button>
            <Button variant="tertiary" onClick={() => router.push(`/members/${member.id}`)}>
              View member
            </Button>
          </div>
        }
      >
        <MemberRow member={{ id: member.id, name: member.name, email: member.email }} />
        <div style={{ borderTop: "1px solid var(--hairline)" }} />
        {transaction.books.map((b) => (
          <BookRow key={b.bookId} book={{ id: b.bookId, title: b.bookName, author: b.author }} showAvailability={false} meta={<span style={{ font: "var(--type-mono)" }}>due {dueDate}</span>} />
        ))}
        <div style={{ display: "flex", justifyContent: "space-between", padding: "var(--space-md)", borderTop: "1px solid var(--hairline)", font: "var(--type-caption)", color: "var(--ink-subtle)" }}>
          <span>Transaction</span>
          <span style={{ font: "var(--type-mono)", color: "var(--ink-muted)" }}>TXN {transaction.id}</span>
        </div>
      </Panel>
    </div>
  );
}
