"use client";

import { useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import { PageHeader } from "@/components/navigation/PageHeader";
import { Tabs } from "@/components/navigation/Tabs";
import { Input } from "@/components/forms/Input";
import { Button } from "@/components/forms/Button";
import { IconButton } from "@/components/forms/IconButton";
import { DataTable, type DataTableColumn, type DataTableSort } from "@/components/data/DataTable";
import { StatusBadge } from "@/components/data/StatusBadge";
import { EmptyState } from "@/components/feedback/EmptyState";
import { useMembers } from "@/lib/hooks/useMembers";
import type { Member } from "@/lib/types/domain";

type Filter = "all" | "active" | "inactive" | "out";

/** Ported from ui_kits/console/Members.jsx "/members". */
export default function MembersPage() {
  const router = useRouter();
  const { data: members, isLoading, isError, refetch } = useMembers();

  const [filter, setFilter] = useState<Filter>("all");
  const [q, setQ] = useState("");
  const [sort, setSort] = useState<DataTableSort>({ key: "name", dir: "asc" });

  const rows = useMemo(() => {
    let r = (members ?? []).filter((m) =>
      filter === "all" ? true : filter === "active" ? m.isActive : filter === "inactive" ? !m.isActive : m.booksOut > 0,
    );
    if (q) {
      const t = q.toLowerCase();
      r = r.filter((m) => m.name.toLowerCase().includes(t) || m.email.toLowerCase().includes(t));
    }
    const dir = sort.dir === "asc" ? 1 : -1;
    const key = sort.key as keyof Member;
    return [...r].sort((a, b) => {
      const av = a[key];
      const bv = b[key];
      return (typeof av === "number" && typeof bv === "number" ? av - bv : String(av).localeCompare(String(bv))) * dir;
    });
  }, [members, filter, q, sort]);

  const columns: DataTableColumn<Member>[] = [
    { key: "name", header: "Name", sortable: true },
    { key: "email", header: "Email", width: "220px", render: (m) => <span style={{ font: "var(--type-mono)", color: "var(--ink-subtle)" }}>{m.email}</span> },
    { key: "age", header: "Age", width: "64px", sortable: true, render: (m) => <span style={{ font: "var(--type-mono)" }}>{m.age}</span> },
    {
      key: "booksOut",
      header: "Books out",
      width: "96px",
      sortable: true,
      render: (m) => <span style={{ font: "var(--type-mono)", color: m.booksOut ? "var(--ink)" : "var(--ink-tertiary)" }}>{m.booksOut}</span>,
    },
    { key: "status", header: "Status", width: "104px", render: (m) => <StatusBadge status={m.isActive ? "active" : "inactive"} /> },
  ];

  const state = isError ? "error" : isLoading ? "loading" : rows.length ? "loaded" : "empty";

  return (
    <div>
      <PageHeader title="Members" subtitle="Everyone who can borrow." action={<Button iconLeft="plus" onClick={() => router.push("/members/new")}>Register a member</Button>} />
      <div style={{ display: "flex", flexWrap: "wrap", alignItems: "center", gap: "var(--space-sm) var(--space-md)", marginBottom: "var(--space-md)" }}>
        <Input iconLeft="search" placeholder="Search by name or email…" value={q} onChange={(e) => setQ(e.target.value)} width={280} />
        <Tabs
          value={filter}
          onChange={(v) => setFilter(v as Filter)}
          tabs={[
            { value: "all", label: "All" },
            { value: "active", label: "Active" },
            { value: "inactive", label: "Inactive" },
            { value: "out", label: "Has books out" },
          ]}
        />
        <span style={{ marginLeft: "auto", font: "var(--type-caption)", color: "var(--ink-subtle)", flex: "0 0 auto" }}>
          {rows.length} {rows.length === 1 ? "result" : "results"}
        </span>
      </div>
      <DataTable
        state={state}
        rows={rows}
        getRowKey={(m) => m.id}
        sort={sort}
        onSortChange={setSort}
        onRowClick={(m) => router.push(`/members/${m.id}`)}
        rowTone={(m) => (m.isActive ? undefined : "muted")}
        columns={columns}
        rowActions={(m) => (
          <>
            <IconButton
              icon="eye"
              label={`View ${m.name}`}
              onClick={(e) => {
                e.stopPropagation();
                router.push(`/members/${m.id}`);
              }}
            />
            <IconButton
              icon="pencil"
              label={`Edit ${m.name}`}
              onClick={(e) => {
                e.stopPropagation();
                router.push(`/members/${m.id}/edit`);
              }}
            />
          </>
        )}
        error={
          <EmptyState icon="server-off" headline="Can't load members." body="Check that the API is running and try again." actionLabel="Try again" onAction={() => refetch()} />
        }
        empty={
          q ? (
            <EmptyState icon="search" headline={`No members match "${q}"`} body="Try a different name or email." actionLabel="Clear search" onAction={() => setQ("")} />
          ) : filter !== "all" ? (
            <EmptyState icon="users" headline="Nobody in this filter" body="No members are in that state right now." actionLabel="Show all members" onAction={() => setFilter("all")} />
          ) : (
            <EmptyState icon="users" headline="No members yet" body="Register your first member to start lending." actionLabel="Register a member" onAction={() => router.push("/members/new")} />
          )
        }
      />
    </div>
  );
}
