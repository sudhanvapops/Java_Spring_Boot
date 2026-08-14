import { useMemo } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import * as transactionsApi from "@/lib/api/services/transactions";
import type { BorrowTransactionRequestDto } from "@/lib/types/api";
import { deriveTransaction } from "@/lib/utils/derive";

/** ['transactions'], 60s — BorrowTransactionResponse nests full book
 * details, so cache aggressively rather than re-deriving. Derivation is
 * memoized on query.data — recomputing (and returning a new array/object)
 * on every render breaks any useMemo/useEffect a caller keys on `data`. */
export function useTransactions() {
  const query = useQuery({
    queryKey: ["transactions"],
    queryFn: transactionsApi.listAllTransactions,
    staleTime: 60_000,
  });
  const data = useMemo(() => query.data?.map(deriveTransaction), [query.data]);
  return { ...query, data };
}

export function useTransaction(id: number) {
  const query = useQuery({
    queryKey: ["transactions", id],
    queryFn: () => transactionsApi.getTransaction(id),
    staleTime: 60_000,
    enabled: Number.isFinite(id),
  });
  const data = useMemo(() => (query.data ? deriveTransaction(query.data) : undefined), [query.data]);
  return { ...query, data };
}

export function useTransactionsForMember(memberId: number) {
  const query = useQuery({
    queryKey: ["transactions", "member", memberId],
    queryFn: () => transactionsApi.listTransactionsForMember(memberId),
    staleTime: 60_000,
    enabled: Number.isFinite(memberId),
  });
  const data = useMemo(() => query.data?.map(deriveTransaction), [query.data]);
  return { ...query, data };
}

export function useBorrowBooks() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (payload: BorrowTransactionRequestDto) =>
      transactionsApi.borrowBooks(payload).then(deriveTransaction),
    onSuccess: () => {
      // Lending touches nearly everything — invalidate broadly.
      qc.invalidateQueries({ queryKey: ["books"] });
      qc.invalidateQueries({ queryKey: ["records"] });
      qc.invalidateQueries({ queryKey: ["transactions"] });
      qc.invalidateQueries({ queryKey: ["members"] });
    },
  });
}
