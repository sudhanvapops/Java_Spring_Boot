"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { PageHeader } from "@/components/navigation/PageHeader";
import { Label } from "@/components/forms/Label";
import { Input } from "@/components/forms/Input";
import { Button } from "@/components/forms/Button";
import { Banner } from "@/components/feedback/Banner";
import { Dialog } from "@/components/feedback/Dialog";
import { Skeleton } from "@/components/data/Skeleton";
import { EmptyState } from "@/components/feedback/EmptyState";
import { bookFormSchema, type BookFormValues } from "@/lib/schemas/book";
import { useBook, useUpdateBook, useDeactivateBook } from "@/lib/hooks/useBooks";
import { useToast } from "@/lib/hooks/useToast";
import { isNormalizedApiError } from "@/lib/api/client";

export default function EditBookPage() {
  const params = useParams<{ id: string }>();
  const id = Number(params.id);
  const router = useRouter();
  const toast = useToast();

  const { data: book, isLoading, isError } = useBook(id);
  const updateBook = useUpdateBook(id);
  const deactivateBook = useDeactivateBook();
  const [confirmOpen, setConfirmOpen] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<BookFormValues>({
    resolver: zodResolver(bookFormSchema),
    defaultValues: { name: "", author: "", totalCopies: 1 },
  });

  useEffect(() => {
    if (book) reset({ name: book.title, author: book.author, totalCopies: book.totalCopies });
  }, [book, reset]);

  const totalCopies = watch("totalCopies");
  const lowering = book != null && Number(totalCopies) < book.onLoan;

  const onSubmit = handleSubmit(async (values) => {
    if (!book) return;
    try {
      await updateBook.mutateAsync({ values, availableCopies: book.availableCopies, isActive: book.isActive });
      toast.success(`"${values.name}" saved.`);
      router.push(`/books/${book.id}`);
    } catch (err) {
      if (isNormalizedApiError(err) && (err.errorCode === "BOOK_ALREADY_EXISTS" || err.errorCode === "INVALID_BOOK_COPIES")) return;
      toast.errorFrom(err);
    }
  });

  const handleDeactivate = async () => {
    if (!book) return;
    setConfirmOpen(false);
    try {
      await deactivateBook.mutateAsync(book.id);
      toast.success(`"${book.title}" deactivated.`);
      router.push("/books");
    } catch (err) {
      toast.errorFrom(err);
    }
  };

  if (isLoading) {
    return (
      <div style={{ maxWidth: "var(--form-max-width)", display: "grid", gap: "var(--space-md)" }}>
        <Skeleton width={200} height={28} />
        <Skeleton height={38} />
        <Skeleton height={38} />
        <Skeleton height={38} width={160} />
      </div>
    );
  }

  if (isError || !book) {
    return (
      <EmptyState icon="book" headline="That book doesn't exist." body="It may have been removed, or the link is wrong." actionLabel="Back to books" onAction={() => router.push("/books")} />
    );
  }

  const mutationError = updateBook.error && isNormalizedApiError(updateBook.error) ? updateBook.error : null;
  const duplicateError = mutationError?.errorCode === "BOOK_ALREADY_EXISTS" ? mutationError.userMessage : null;
  const copiesError = mutationError?.errorCode === "INVALID_BOOK_COPIES" ? mutationError.userMessage : null;

  return (
    <div style={{ maxWidth: "var(--form-max-width)" }}>
      <PageHeader
        back={{ label: "Back to books", onClick: (e) => { e.preventDefault(); router.push(`/books/${book.id}`); } }}
        title="Edit book"
      />
      <form onSubmit={onSubmit} style={{ display: "grid", gap: "var(--space-lg)" }}>
        <div>
          <Label htmlFor="title" required>Title</Label>
          <Input id="title" invalid={!!errors.name || !!duplicateError} error={errors.name?.message || duplicateError} autoFocus {...register("name")} />
        </div>
        <div>
          <Label htmlFor="author" required>Author</Label>
          <Input id="author" invalid={!!errors.author || !!duplicateError} error={errors.author?.message} {...register("author")} />
        </div>
        <div>
          <Label htmlFor="total" required>Total copies</Label>
          <Input
            id="total"
            type="number"
            min="1"
            width={160}
            invalid={!!errors.totalCopies || !!copiesError}
            error={errors.totalCopies?.message || copiesError}
            {...register("totalCopies", { valueAsNumber: true })}
          />
        </div>

        <div
          style={{
            padding: "var(--space-sm) var(--space-md)",
            background: "var(--surface-2)",
            border: "1px solid var(--hairline)",
            borderRadius: "var(--radius-md)",
            font: "var(--type-body-sm)",
            color: "var(--ink-subtle)",
          }}
        >
          <span style={{ font: "var(--type-mono)", color: "var(--ink-muted)" }}>
            {book.availableCopies} available of {book.totalCopies}
          </span>
          {" · adjusted automatically when books are lent and returned."}
        </div>

        {lowering ? (
          <Banner tone="warning">
            {book.onLoan} {book.onLoan === 1 ? "copy is" : "copies are"} on loan right now. Lowering the total below that will be rejected until they come back.
          </Banner>
        ) : null}

        <div style={{ display: "flex", gap: "var(--space-xs)" }}>
          <Button type="submit" loading={isSubmitting || updateBook.isPending} loadingLabel="Saving…">
            Save book
          </Button>
          <Button type="button" variant="tertiary" onClick={() => router.push(`/books/${book.id}`)}>
            Cancel
          </Button>
        </div>
      </form>

      {book.isActive ? (
        <div style={{ marginTop: "var(--space-xxl)", paddingTop: "var(--space-lg)", borderTop: "1px solid var(--hairline)" }}>
          <div style={{ font: "var(--type-body-sm)", fontWeight: "var(--weight-medium)", color: "var(--ink)" }}>Deactivate this book</div>
          <p style={{ margin: "4px 0 var(--space-md)", font: "var(--type-body-sm)", color: "var(--ink-subtle)", maxWidth: 420 }}>
            It stays in the catalogue and keeps its history, but it can’t be lent until you reactivate it.
          </p>
          <Button variant="danger" onClick={() => setConfirmOpen(true)}>
            Deactivate book
          </Button>
        </div>
      ) : null}

      <Dialog open={confirmOpen} title="Deactivate this book?" confirmLabel="Deactivate book" destructive onCancel={() => setConfirmOpen(false)} onConfirm={handleDeactivate}>
        It stays in the catalogue and its history is kept, but it can’t be lent until you reactivate it. Books currently on loan must be returned first.
      </Dialog>
    </div>
  );
}
