"use client";

import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { PageHeader } from "@/components/navigation/PageHeader";
import { Label } from "@/components/forms/Label";
import { Input } from "@/components/forms/Input";
import { Button } from "@/components/forms/Button";
import { bookFormSchema, type BookFormValues } from "@/lib/schemas/book";
import { useCreateBook } from "@/lib/hooks/useBooks";
import { useToast } from "@/lib/hooks/useToast";
import { isNormalizedApiError } from "@/lib/api/client";

export default function NewBookPage() {
  const router = useRouter();
  const toast = useToast();
  const createBook = useCreateBook();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<BookFormValues>({
    resolver: zodResolver(bookFormSchema),
    defaultValues: { name: "", author: "", totalCopies: 1 },
  });

  const onSubmit = handleSubmit(async (values) => {
    try {
      const book = await createBook.mutateAsync(values);
      toast.success(`"${book.title}" added.`);
      router.push(`/books/${book.id}`);
    } catch (err) {
      if (isNormalizedApiError(err) && err.errorCode === "BOOK_ALREADY_EXISTS") return; // shown inline below
      toast.errorFrom(err);
    }
  });

  const duplicateError = createBook.error && isNormalizedApiError(createBook.error) && createBook.error.errorCode === "BOOK_ALREADY_EXISTS"
    ? createBook.error.userMessage
    : null;

  return (
    <div style={{ maxWidth: "var(--form-max-width)" }}>
      <PageHeader
        back={{ label: "Back to books", onClick: (e) => { e.preventDefault(); router.push("/books"); } }}
        title="Add a book"
        subtitle="One entry per title. Copies are counted, not listed separately."
      />
      <form onSubmit={onSubmit} style={{ display: "grid", gap: "var(--space-lg)" }}>
        <div>
          <Label htmlFor="title" required>Title</Label>
          <Input
            id="title"
            invalid={!!errors.name || !!duplicateError}
            error={errors.name?.message || duplicateError}
            helper={errors.name || duplicateError ? null : "As printed on the spine."}
            placeholder="The Pragmatic Programmer"
            autoFocus
            {...register("name")}
          />
        </div>
        <div>
          <Label htmlFor="author" required>Author</Label>
          <Input
            id="author"
            invalid={!!errors.author || !!duplicateError}
            error={errors.author?.message}
            helper={errors.author ? null : "Separate several authors with commas."}
            placeholder="Andrew Hunt, David Thomas"
            {...register("author")}
          />
        </div>
        <div>
          <Label htmlFor="total" required>Total copies</Label>
          <Input
            id="total"
            type="number"
            min="1"
            width={160}
            invalid={!!errors.totalCopies}
            error={errors.totalCopies?.message}
            helper={errors.totalCopies ? null : "How many physical copies the library owns."}
            {...register("totalCopies", { valueAsNumber: true })}
          />
        </div>

        <div style={{ display: "flex", gap: "var(--space-xs)" }}>
          <Button type="submit" loading={isSubmitting || createBook.isPending} loadingLabel="Saving…">
            Add book
          </Button>
          <Button type="button" variant="tertiary" onClick={() => router.push("/books")}>
            Cancel
          </Button>
        </div>
      </form>
    </div>
  );
}
