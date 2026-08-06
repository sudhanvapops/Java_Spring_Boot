import { apiClient, isNormalizedApiError } from "@/lib/api/client";
import type { BookRequestDto, BookResponseDto } from "@/lib/types/api";
import type { Book } from "@/lib/types/domain";
import type { BookFormValues } from "@/lib/schemas/book";
import { deriveBook } from "@/lib/utils/derive";

/**
 * uploads/03-endpoints.md "Books". Note the path asymmetry: reads go through
 * /api/book/id/{id}, writes through /api/book/{id}.
 */
export async function listBooks(): Promise<Book[]> {
  try {
    const res = await apiClient.get<BookResponseDto[]>("/api/book");
    return res.data.map(deriveBook);
  } catch (err) {
    if (isNormalizedApiError(err) && err.isEmptyState) return [];
    throw err;
  }
}

export async function getBook(id: number): Promise<Book> {
  const res = await apiClient.get<BookResponseDto>(`/api/book/id/${id}`);
  return deriveBook(res.data);
}

export async function searchBooksByName(name: string): Promise<Book[]> {
  try {
    const res = await apiClient.get<BookResponseDto[]>("/api/book/name", { params: { name } });
    return res.data.map(deriveBook);
  } catch (err) {
    if (isNormalizedApiError(err) && err.status === 404) return [];
    throw err;
  }
}

export async function searchBooksByAuthor(author: string): Promise<Book[]> {
  try {
    const res = await apiClient.get<BookResponseDto[]>("/api/book/author", { params: { author } });
    return res.data.map(deriveBook);
  } catch (err) {
    if (isNormalizedApiError(err) && err.status === 404) return [];
    throw err;
  }
}

/** availableCopies and isActive are never on the create form — sent silently
 * as totalCopies and true, per uploads/02-pages-required.md "/books/new". */
export async function createBook(values: BookFormValues): Promise<Book> {
  const payload: BookRequestDto = {
    name: values.name,
    author: values.author,
    totalCopies: values.totalCopies,
    availableCopies: values.totalCopies,
    isActive: true,
  };
  const res = await apiClient.post<BookResponseDto>("/api/book", payload);
  return deriveBook(res.data);
}

export async function updateBook(
  id: number,
  values: BookFormValues,
  availableCopies: number,
  isActive: boolean,
): Promise<Book> {
  const payload: BookRequestDto = {
    name: values.name,
    author: values.author,
    totalCopies: values.totalCopies,
    availableCopies,
    isActive,
  };
  const res = await apiClient.put<BookResponseDto>(`/api/book/${id}`, payload);
  return deriveBook(res.data);
}

/** Soft delete. 409 if already inactive or currently borrowed. */
export async function deactivateBook(id: number): Promise<void> {
  await apiClient.delete(`/api/book/${id}`);
}

/** Undocumented gap: returns 500 when the book is already active — callers
 * should disable the control when isActive is already true, and this
 * rewrites that 500 into a plain user message as a second line of defence. */
export async function activateBook(id: number): Promise<void> {
  try {
    await apiClient.patch(`/api/book/${id}/activate`);
  } catch (err) {
    if (isNormalizedApiError(err) && err.status === 500) {
      throw { ...err, userMessage: "This book is already active." };
    }
    throw err;
  }
}
