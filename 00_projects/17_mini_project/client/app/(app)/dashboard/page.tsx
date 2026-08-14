"use client";

import { useMemo } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { motion } from "framer-motion";
import { StatCard } from "@/components/panels/StatCard";
import { Panel } from "@/components/panels/Panel";
import { BookRow } from "@/components/circulation/BookRow";
import { Button } from "@/components/forms/Button";
import { EmptyState } from "@/components/feedback/EmptyState";
import { ErrorPanel } from "@/components/feedback/ErrorPanel";
import { Skeleton } from "@/components/data/Skeleton";
import { EASE, RISE, STAGGER, usePrefersReducedMotion } from "@/components/core/Motion";
import { useAuthStore } from "@/lib/stores/auth";
import { useDueToday, useUnreturnedRecords } from "@/lib/hooks/useRecords";
import { useMembersRaw } from "@/lib/hooks/useMembers";
import { useMediaQuery } from "@/lib/hooks/useMediaQuery";
import { fmtLong } from "@/lib/utils/date";

/** Ported from ui_kits/console/Dashboard.jsx — 4 parallel queries derive
 * every stat client-side, since no summary endpoint exists
 * (uploads/03-endpoints.md quirk #10). */
export default function DashboardPage() {
  const router = useRouter();
  const user = useAuthStore((s) => s.user);
  const reduced = usePrefersReducedMotion();
  // Layout rules: dashboard goes 2-col below 768px so stat cards stay
  // readable instead of squeezing 4 into a 375px-wide screen.
  const isNarrow = useMediaQuery("(max-width:767px)");
  const statColumns = isNarrow ? "repeat(2,1fr)" : "repeat(4,1fr)";

  const out = useUnreturnedRecords();
  const due = useDueToday();
  const members = useMembersRaw();

  const overdueCount = useMemo(() => out.data?.filter((r) => r.status === "overdue").length ?? 0, [out.data]);
  const activeMembersCount = useMemo(() => members.data?.filter((m) => m.isActive).length ?? 0, [members.data]);

  const isLoading = out.isLoading || due.isLoading || members.isLoading;
  const isError = out.isError || due.isError || members.isError;

  const hour = new Date().getHours();
  const greeting = hour < 12 ? "Good morning" : hour < 18 ? "Good afternoon" : "Good evening";
  // Login/refresh never return a display name (lib/types/domain.ts) — the
  // email's local part is the closest thing to one.
  const firstName = user?.email?.split("@")[0] ?? "there";

  const dueList = due.data ?? [];

  const stats = [
    { label: "Out on loan", value: out.data?.length ?? 0 },
    { label: "Due today", value: dueList.length },
    { label: "Overdue", value: overdueCount, tone: overdueCount ? ("danger" as const) : ("default" as const) },
    { label: "Active members", value: activeMembersCount },
  ];

  return (
    <div style={{ display: "grid", gap: "var(--space-lg)" }}>
      <div style={{ display: "flex", alignItems: "flex-start", justifyContent: "space-between", gap: "var(--space-lg)" }}>
        <div>
          <h1 style={{ margin: 0, font: "var(--type-headline)", letterSpacing: "var(--track-headline)", color: "var(--ink)" }}>
            {greeting}, {firstName}
          </h1>
          <p style={{ margin: "var(--space-xxs) 0 0", font: "var(--type-body-sm)", color: "var(--ink-subtle)" }}>
            {isError
              ? "Couldn't load today's numbers."
              : isLoading
                ? "Loading today's numbers…"
                : dueList.length
                  ? `${dueList.length} ${dueList.length === 1 ? "book is" : "books are"} due back today.`
                  : "Nothing is due back today."}
          </p>
        </div>
        <span style={{ font: "var(--type-mono)", color: "var(--ink-subtle)" }}>{fmtLong(new Date())}</span>
      </div>

      {isError ? (
        <ErrorPanel headline="Can't load the dashboard." body="Check that the API is running and try again." onAction={() => { out.refetch(); due.refetch(); members.refetch(); }} />
      ) : (
        <motion.div
          initial={reduced ? undefined : "hidden"}
          animate={reduced ? undefined : "visible"}
          variants={reduced ? undefined : STAGGER}
          style={{ display: "grid", gridTemplateColumns: statColumns, gap: "var(--space-md)" }}
        >
          {isLoading
            ? Array.from({ length: 4 }).map((_, i) => <Skeleton key={i} height={104} radius="var(--radius-card)" />)
            : stats.map((s) => (
                <motion.div key={s.label} variants={reduced ? undefined : RISE} transition={EASE.entrance}>
                  <StatCard label={s.label} value={s.value} tone={s.tone} animate />
                </motion.div>
              ))}
        </motion.div>
      )}

      {isError ? null : (
      <Panel
        title="Due back today"
        padded={!dueList.length}
        action={
          dueList.length ? <Link href="/records/due-today">View all →</Link> : null
        }
      >
        {isLoading ? null : dueList.length ? (
          dueList.map((r, i) => (
            <BookRow
              key={`${r.memberId}-${r.bookId}`}
              book={{ id: r.bookId, title: r.bookName, author: r.author }}
              showAvailability={false}
              style={{ borderTop: i ? "1px solid var(--hairline)" : "none" }}
              meta={
                <>
                  {r.memberName}
                  <span style={{ color: "var(--ink-tertiary)" }}> · </span>
                  <span style={{ font: "var(--type-mono)" }}>due today</span>
                </>
              }
              action={
                <Button size="sm" variant="secondary" onClick={() => router.push(`/returns?member=${r.memberId}`)}>
                  Take it back
                </Button>
              }
            />
          ))
        ) : (
          <EmptyState
            icon="circle-check"
            headline="Nothing due today."
            body={`The shelves are quiet. ${out.data?.length ?? 0} books are out on loan and none are due back yet.`}
            pattern={false}
          />
        )}
      </Panel>
      )}

      <Panel title="Quick actions">
        <div style={{ display: "flex", gap: "var(--space-xs)" }}>
          <Button onClick={() => router.push("/lend")}>Lend books</Button>
          <Button variant="secondary" onClick={() => router.push("/returns")}>
            Take books back
          </Button>
          <Button variant="secondary" iconLeft="plus" onClick={() => router.push("/books/new")}>
            Add a book
          </Button>
        </div>
      </Panel>
    </div>
  );
}
