import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import * as booksApi from "@/lib/api/services/books";
import type { BookFormValues } from "@/lib/schemas/book";

/** uploads/08-api-client.md cache keys/stale-times: ['books'], 60s. */
export function useBooks() {
  return useQuery({ queryKey: ["books"], queryFn: booksApi.listBooks, staleTime: 60_000 });
}

export function useBook(id: number) {
  return useQuery({
    queryKey: ["books", id],
    queryFn: () => booksApi.getBook(id),
    staleTime: 60_000,
    enabled: Number.isFinite(id),
  });
}

export function useBookSearch(query: string, mode: "name" | "author") {
  return useQuery({
    queryKey: ["books", "search", mode, query],
    queryFn: () => (mode === "name" ? booksApi.searchBooksByName(query) : booksApi.searchBooksByAuthor(query)),
    enabled: query.trim().length > 0,
    staleTime: 30_000,
  });
}

export function useCreateBook() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (values: BookFormValues) => booksApi.createBook(values),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["books"] }),
  });
}

export function useUpdateBook(id: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (args: { values: BookFormValues; availableCopies: number; isActive: boolean }) =>
      booksApi.updateBook(id, args.values, args.availableCopies, args.isActive),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["books"] }),
  });
}

export function useDeactivateBook() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => booksApi.deactivateBook(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["books"] }),
  });
}

export function useActivateBook() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => booksApi.activateBook(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["books"] }),
  });
}
