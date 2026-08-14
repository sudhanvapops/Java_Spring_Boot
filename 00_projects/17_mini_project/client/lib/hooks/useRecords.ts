import { useMemo } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import * as recordsApi from "@/lib/api/services/records";
import type { BookReturnRequestDto } from "@/lib/types/api";
import { deriveAllRecords, deriveDueToday, deriveReturnResult, deriveUnreturnedRecord } from "@/lib/utils/derive";

/** ['records','unreturned'], 30s — the workhorse behind the dashboard, book
 * detail, members list, /records/unreturned and the /lend guard rails.
 * BorrowRecordController is @StaffOnly — pass `enabled: false` for a MEMBER
 * session so the shell doesn't fire a request that can only 403. */
export function useUnreturnedAll(opts?: { enabled?: boolean }) {
  return useQuery({
    queryKey: ["records", "unreturned"],
    queryFn: recordsApi.listUnreturnedAll,
    staleTime: 30_000,
    enabled: opts?.enabled ?? true,
  });
}

export function useUnreturnedRecords() {
  const query = useUnreturnedAll();
  const data = useMemo(() => query.data?.map((d) => deriveUnreturnedRecord(d)), [query.data]);
  return { ...query, data };
}

export function useUnreturnedForMember(memberId: number) {
  return useQuery({
    queryKey: ["records", "unreturned", memberId],
    queryFn: () => recordsApi.listUnreturnedForMember(memberId),
    staleTime: 30_000,
    enabled: Number.isFinite(memberId),
  });
}

export function useUnreturnedForMemberDerived(memberId: number) {
  const query = useUnreturnedForMember(memberId);
  const data = useMemo(() => query.data?.map((d) => deriveUnreturnedRecord(d)), [query.data]);
  return { ...query, data };
}

/** A member's full ledger (returned + still out), status inferred the same
 * way as useAllRecords but scoped to one member. Powers the "All records"
 * tab on /members/[id]. */
export function useRecordsForMember(memberId: number) {
  const all = useQuery({
    queryKey: ["records", "member", memberId],
    queryFn: () => recordsApi.listRecordsForMember(memberId),
    staleTime: 60_000,
    enabled: Number.isFinite(memberId),
  });
  const unreturned = useUnreturnedForMember(memberId);
  const data = useMemo(
    () => (all.data && unreturned.data ? deriveAllRecords(all.data, unreturned.data) : undefined),
    [all.data, unreturned.data],
  );
  return {
    isLoading: all.isLoading || unreturned.isLoading,
    isError: all.isError || unreturned.isError,
    data,
  };
}

/** ['records','due-today'], 30s — 200 [] always, never 404.
 * BorrowRecordController is @StaffOnly — pass `enabled: false` for a MEMBER
 * session so the shell doesn't fire a request that can only 403. */
export function useDueToday(opts?: { enabled?: boolean }) {
  const query = useQuery({
    queryKey: ["records", "due-today"],
    queryFn: recordsApi.listDueToday,
    staleTime: 30_000,
    enabled: opts?.enabled ?? true,
  });
  const data = useMemo(() => query.data?.map(deriveDueToday), [query.data]);
  return { ...query, data };
}

/** ['records','all'], 60s — status (out/due-today/overdue/returned) is
 * inferred by diffing against unreturned/all, since /all has no flag. */
export function useAllRecords() {
  const all = useQuery({ queryKey: ["records", "all"], queryFn: recordsApi.listAllRecords, staleTime: 60_000 });
  const unreturned = useUnreturnedAll();
  const data = useMemo(
    () => (all.data && unreturned.data ? deriveAllRecords(all.data, unreturned.data) : undefined),
    [all.data, unreturned.data],
  );
  return {
    isLoading: all.isLoading || unreturned.isLoading,
    isError: all.isError || unreturned.isError,
    error: all.error ?? unreturned.error,
    data,
    refetch: () => Promise.all([all.refetch(), unreturned.refetch()]),
  };
}

export function useReturnBooks() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (payload: BookReturnRequestDto) => recordsApi.returnBooks(payload).then(deriveReturnResult),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["books"] });
      qc.invalidateQueries({ queryKey: ["records"] });
      qc.invalidateQueries({ queryKey: ["members"] });
    },
  });
}
