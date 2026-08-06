import { apiClient, isNormalizedApiError } from "@/lib/api/client";
import type { BorrowTransactionRequestDto, BorrowTransactionResponseDto } from "@/lib/types/api";

/** uploads/03-endpoints.md "Borrow Transactions". */

export async function listAllTransactions(): Promise<BorrowTransactionResponseDto[]> {
  try {
    const res = await apiClient.get<BorrowTransactionResponseDto[]>("/api/borrow-transactions/all");
    return res.data;
  } catch (err) {
    if (isNormalizedApiError(err) && err.isEmptyState) return [];
    throw err;
  }
}

export async function getTransaction(id: number): Promise<BorrowTransactionResponseDto> {
  const res = await apiClient.get<BorrowTransactionResponseDto>(`/api/borrow-transactions/${id}`);
  return res.data;
}

/** 404 is ambiguous — missing member vs. no transactions. */
export async function listTransactionsForMember(
  memberId: number,
): Promise<BorrowTransactionResponseDto[]> {
  try {
    const res = await apiClient.get<BorrowTransactionResponseDto[]>(
      `/api/borrow-transactions/member-id/${memberId}`,
    );
    return res.data;
  } catch (err) {
    if (isNormalizedApiError(err) && err.errorCode === "MEMBER_NOT_FOUND") throw err;
    if (isNormalizedApiError(err) && err.status === 404) return [];
    throw err;
  }
}

/**
 * Also 404s when the required library settings aren't configured yet —
 * looks like member-not-found by status alone, so /lend checks settings on
 * mount rather than relying on this error to disambiguate.
 */
export async function borrowBooks(
  payload: BorrowTransactionRequestDto,
): Promise<BorrowTransactionResponseDto> {
  const res = await apiClient.post<BorrowTransactionResponseDto>(
    "/api/borrow-transactions/borrow",
    payload,
  );
  return res.data;
}
