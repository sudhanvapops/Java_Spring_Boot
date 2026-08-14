"use client";

import { useMemo, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { PageHeader } from "@/components/navigation/PageHeader";
import { Button } from "@/components/forms/Button";
import { Panel } from "@/components/panels/Panel";
import { Banner } from "@/components/feedback/Banner";
import { Dialog } from "@/components/feedback/Dialog";
import { EmptyState } from "@/components/feedback/EmptyState";
import { StatusBadge } from "@/components/data/StatusBadge";
import { AvailabilityBar } from "@/components/data/AvailabilityBar";
import { Skeleton } from "@/components/data/Skeleton";
import { MemberRow } from "@/components/circulation/MemberRow";
import { useBook, useActivateBook, useDeactivateBook } from "@/lib/hooks/useBooks";
import { useUnreturnedAll } from "@/lib/hooks/useRecords";
import { useMembersRaw } from "@/lib/hooks/useMembers";
import { useToast } from "@/lib/hooks/useToast";
import { useMediaQuery } from "@/lib/hooks/useMediaQuery";
import { useAuthStore } from "@/lib/stores/auth";
import { isStaffRole } from "@/lib/utils/rbac";
import { daysOverdue as computeDaysOverdue, fmt, parseApiDate } from "@/lib/utils/date";

export default function BookDetailPage() {
  const params = useParams<{ id: string }>();
  const id = Number(params.id);
  const router = useRouter();
  const toast = useToast();

  // "Currently out" + activate/deactivate all touch @StaffOnly endpoints
  // (BACKEND_HANDOFF.md §3.6) — a MEMBER can view the book, nothing more.
  const isStaff = isStaffRole(useAuthStore((s) => s.user?.role));

  const { data: book, isLoading, isError } = useBook(id);
  const unreturned = useUnreturnedAll({ enabled: isStaff });
  const members = useMembersRaw({ enabled: isStaff });
  const isNarrow = useMediaQuery("(max-width:767px)");
  const activateBook = useActivateBook();
  const deactivateBook = useDeactivateBook();

  const [confirmOpen, setConfirmOpen] = useState(false);

  const lent = useMemo(() => {
    const emailById = new Map((members.data ?? []).map((m) => [m.id, m.email]));
    return (unreturned.data ?? [])
      .filter((r) => r.borrowedBookId === id)
      .map((r) => {
        const due = parseApiDate(r.dueDate);
        const overdue = computeDaysOverdue(due);
        return {
          memberId: r.memberId,
          memberName: r.memberName,
          email: emailById.get(r.memberId) ?? "",
          dueDateLabel: fmt(due),
          daysOverdue: overdue || undefined,
        };
      });
  }, [unreturned.data, members.data, id]);

  const handleDeactivate = async () => {
    setConfirmOpen(false);
    try {
      await deactivateBook.mutateAsync(id);
      toast.success(`"${book?.title}" deactivated.`);
    } catch (err) {
      toast.errorFrom(err);
    }
  };

  const handleActivate = async () => {
    try {
      await activateBook.mutateAsync(id);
      toast.success(`"${book?.title}" reactivated.`);
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

  if (isError || !book) {
    return (
      <EmptyState
        icon="book"
        headline="That book doesn't exist."
        body="It may have been removed, or the link is wrong."
        actionLabel="Back to books"
        onAction={() => router.push("/books")}
      />
    );
  }

  return (
    <div>
      <PageHeader
        back={{ label: "Back to books", onClick: (e) => { e.preventDefault(); router.push("/books"); } }}
        title={<span style={{ color: book.isActive ? "var(--ink)" : "var(--ink-subtle)" }}>{book.title}</span>}
        subtitle={book.author}
        meta={
          <>
            <StatusBadge status={book.isActive ? "active" : "inactive"} />
            <span style={{ font: "var(--type-mono)" }}>ID {book.id}</span>
          </>
        }
        action={
          isStaff ? (
          <>
            <Button variant="secondary" iconLeft="pencil" onClick={() => router.push(`/books/${book.id}/edit`)}>
              Edit
            </Button>
            {book.isActive ? (
              <Button variant="secondary" onClick={() => setConfirmOpen(true)}>
                Deactivate
              </Button>
            ) : (
              <Button loading={activateBook.isPending} onClick={handleActivate}>
                Reactivate
              </Button>
            )}
          </>
          ) : null
        }
      />

      {!book.isActive && isStaff ? (
        <div style={{ marginBottom: "var(--space-lg)" }}>
          <Banner
            tone="warning"
            action={
              <Button variant="secondary" size="sm" loading={activateBook.isPending} onClick={handleActivate}>
                Reactivate
              </Button>
            }
          >
            This book is inactive and can’t be lent.
          </Banner>
        </div>
      ) : null}

      <div style={{ display: "grid", gridTemplateColumns: isNarrow || !isStaff ? "1fr" : "260px 1fr", gap: "var(--space-lg)", alignItems: "start" }}>
        <Panel title="Copies">
          <div
            style={{
              font: "var(--type-display-md)",
              letterSpacing: "var(--track-display-md)",
              fontVariantNumeric: "tabular-nums",
              color: book.availableCopies ? "var(--ink)" : "var(--danger)",
            }}
          >
            {book.availableCopies}
          </div>
          <div style={{ marginTop: "var(--space-xxs)", font: "var(--type-body-sm)", color: "var(--ink-subtle)" }}>available of {book.totalCopies} total</div>
          <div style={{ marginTop: "var(--space-md)" }}>
            <AvailabilityBar available={book.availableCopies} total={book.totalCopies} width={228} />
          </div>
        </Panel>

        {isStaff ? (
          <Panel title={`Currently out${lent.length ? ` (${lent.length})` : ""}`} padded={!lent.length}>
            {lent.length ? (
              lent.map((r, i) => (
                <MemberRow
                  key={`${r.memberId}`}
                  member={{ id: r.memberId, name: r.memberName, email: r.email }}
                  style={{ borderTop: i ? "1px solid var(--hairline)" : "none" }}
                  action={
                    <span style={{ display: "flex", alignItems: "center", gap: "var(--space-xs)" }}>
                      <span style={{ font: "var(--type-mono)", color: r.daysOverdue ? "var(--danger)" : "var(--ink-muted)" }}>due {r.dueDateLabel}</span>
                      {r.daysOverdue ? <StatusBadge status="overdue">{r.daysOverdue} days over</StatusBadge> : null}
                    </span>
                  }
                />
              ))
            ) : (
              <EmptyState icon="circle-check" headline="Every copy is on the shelf." pattern={false} />
            )}
          </Panel>
        ) : null}
      </div>

      <Dialog
        open={confirmOpen}
        title="Deactivate this book?"
        confirmLabel="Deactivate book"
        destructive
        onCancel={() => setConfirmOpen(false)}
        onConfirm={handleDeactivate}
      >
        It stays in the catalogue and its history is kept, but it can’t be lent until you reactivate it. Books currently on loan must be returned first.
      </Dialog>
    </div>
  );
}
