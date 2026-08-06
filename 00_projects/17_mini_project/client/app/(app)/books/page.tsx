"use client";

import { useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import { PageHeader } from "@/components/navigation/PageHeader";
import { Tabs } from "@/components/navigation/Tabs";
import { Input } from "@/components/forms/Input";
import { Button } from "@/components/forms/Button";
import { IconButton } from "@/components/forms/IconButton";
import { DataTable, type DataTableColumn, type DataTableSort } from "@/components/data/DataTable";
import { AvailabilityBar } from "@/components/data/AvailabilityBar";
import { StatusBadge } from "@/components/data/StatusBadge";
import { EmptyState } from "@/components/feedback/EmptyState";
import { useBooks } from "@/lib/hooks/useBooks";
import type { Book } from "@/lib/types/domain";

type Filter = "all" | "available" | "unavailable" | "inactive";

/** Ported from ui_kits/console/Books.jsx "/books" — fetch-once, filter and
 * sort client-side (uploads/02-pages-required.md recommends this over the
 * two 404-prone search endpoints). */
export default function BooksPage() {
  const router = useRouter();
  const { data: books, isLoading, isError, refetch } = useBooks();

  const [filter, setFilter] = useState<Filter>("all");
  const [q, setQ] = useState("");
  const [sort, setSort] = useState<DataTableSort>({ key: "title", dir: "asc" });

  const rows = useMemo(() => {
    let r = (books ?? []).filter((b) =>
      filter === "all"
        ? true
        : filter === "available"
          ? b.isActive && b.availableCopies > 0
          : filter === "unavailable"
            ? b.isActive && b.availableCopies === 0
            : !b.isActive,
    );
    if (q) {
      const t = q.toLowerCase();
      r = r.filter((b) => b.title.toLowerCase().includes(t) || b.author.toLowerCase().includes(t));
    }
    const dir = sort.dir === "asc" ? 1 : -1;
    const key = sort.key as keyof Book;
    return [...r].sort((a, b) => String(a[key]).localeCompare(String(b[key])) * dir);
  }, [books, filter, q, sort]);

  const columns: DataTableColumn<Book>[] = [
    { key: "title", header: "Title", sortable: true },
    { key: "author", header: "Author", width: "200px", sortable: true },
    {
      key: "copies",
      header: "Available / Total",
      width: "120px",
      render: (b) => <AvailabilityBar available={b.availableCopies} total={b.totalCopies} />,
    },
    {
      key: "status",
      header: "Status",
      width: "104px",
      render: (b) => <StatusBadge status={b.isActive ? "active" : "inactive"} />,
    },
  ];

  const state = isError ? "error" : isLoading ? "loading" : rows.length ? "loaded" : "empty";

  return (
    <div>
      <PageHeader title="Books" subtitle="Everything the library owns." action={<Button iconLeft="plus" onClick={() => router.push("/books/new")}>Add a book</Button>} />
      <div style={{ display: "flex", flexWrap: "wrap", alignItems: "center", gap: "var(--space-sm) var(--space-md)", marginBottom: "var(--space-md)" }}>
        <Input iconLeft="search" placeholder="Search by title or author…" value={q} onChange={(e) => setQ(e.target.value)} width={280} />
        <Tabs
          value={filter}
          onChange={(v) => setFilter(v as Filter)}
          tabs={[
            { value: "all", label: "All" },
            { value: "available", label: "Available" },
            { value: "unavailable", label: "Unavailable" },
            { value: "inactive", label: "Inactive" },
          ]}
        />
        <span style={{ marginLeft: "auto", font: "var(--type-caption)", color: "var(--ink-subtle)", flex: "0 0 auto" }}>
          {rows.length} {rows.length === 1 ? "result" : "results"}
        </span>
      </div>
      <DataTable
        state={state}
        rows={rows}
        getRowKey={(b) => b.id}
        sort={sort}
        onSortChange={setSort}
        onRowClick={(b) => router.push(`/books/${b.id}`)}
        rowTone={(b) => (b.isActive ? undefined : "muted")}
        columns={columns}
        rowActions={(b) => (
          <>
            <IconButton
              icon="eye"
              label={`View ${b.title}`}
              onClick={(e) => {
                e.stopPropagation();
                router.push(`/books/${b.id}`);
              }}
            />
            <IconButton
              icon="pencil"
              label={`Edit ${b.title}`}
              onClick={(e) => {
                e.stopPropagation();
                router.push(`/books/${b.id}/edit`);
              }}
            />
          </>
        )}
        error={
          <EmptyState icon="server-off" headline="Can't load the catalogue." body="Check that the API is running and try again." actionLabel="Try again" onAction={() => refetch()} />
        }
        empty={
          q ? (
            <EmptyState icon="search" headline={`No books match "${q}"`} body="Try a different title or author." actionLabel="Clear search" onAction={() => setQ("")} />
          ) : filter !== "all" ? (
            <EmptyState icon="book" headline="Nothing in this filter" body="No books are in that state right now." actionLabel="Show all books" onAction={() => setFilter("all")} />
          ) : (
            <EmptyState icon="book" headline="No books yet" body="Add the first title to your catalogue." actionLabel="Add a book" onAction={() => router.push("/books/new")} />
          )
        }
      />
    </div>
  );
}
