import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import * as transactionsApi from "@/lib/api/services/transactions";
import type { BorrowTransactionRequestDto } from "@/lib/types/api";
import { deriveTransaction } from "@/lib/utils/derive";

/** ['transactions'], 60s — BorrowTransactionResponse nests full book
 * details, so cache aggressively rather than re-deriving. */
export function useTransactions() {
  const query = useQuery({
    queryKey: ["transactions"],
    queryFn: transactionsApi.listAllTransactions,
    staleTime: 60_000,
  });
  return { ...query, data: query.data?.map(deriveTransaction) };
}

export function useTransaction(id: number) {
  const query = useQuery({
    queryKey: ["transactions", id],
    queryFn: () => transactionsApi.getTransaction(id),
    staleTime: 60_000,
    enabled: Number.isFinite(id),
  });
  return { ...query, data: query.data ? deriveTransaction(query.data) : undefined };
}

export function useTransactionsForMember(memberId: number) {
  const query = useQuery({
    queryKey: ["transactions", "member", memberId],
    queryFn: () => transactionsApi.listTransactionsForMember(memberId),
    staleTime: 60_000,
    enabled: Number.isFinite(memberId),
  });
  return { ...query, data: query.data?.map(deriveTransaction) };
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
