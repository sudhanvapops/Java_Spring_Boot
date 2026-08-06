import { apiClient, isNormalizedApiError } from "@/lib/api/client";
import type {
  BookReturnRequestDto,
  BookReturnResponseDto,
  BorrowTransactionItemResponseDto,
  DueTodayResponseDto,
} from "@/lib/types/api";

/** uploads/03-endpoints.md "Borrow Records". Spring resolves the literal
 * `/unreturned/all` before `{memberId}` — a member id must never literally
 * be the string "all". */

export async function listAllRecords(): Promise<BorrowTransactionItemResponseDto[]> {
  try {
    const res = await apiClient.get<BorrowTransactionItemResponseDto[]>("/api/borrowrecord/all");
    return res.data;
  } catch (err) {
    if (isNormalizedApiError(err) && err.isEmptyState) return [];
    throw err;
  }
}

/** 404 here is ambiguous — could mean the member doesn't exist, or they just
 * have no records. Disambiguate on errorCode rather than assuming empty. */
export async function listRecordsForMember(memberId: number): Promise<BorrowTransactionItemResponseDto[]> {
  try {
    const res = await apiClient.get<BorrowTransactionItemResponseDto[]>(
      `/api/borrowrecord/member-id/${memberId}`,
    );
    return res.data;
  } catch (err) {
    if (isNormalizedApiError(err) && err.errorCode === "MEMBER_NOT_FOUND") throw err;
    if (isNormalizedApiError(err) && err.status === 404) return [];
    throw err;
  }
}

/** The workhorse — powers the dashboard, book detail, members list,
 * /records/unreturned and the /lend guard rails. Fetch once, cache, share. */
export async function listUnreturnedAll(): Promise<BorrowTransactionItemResponseDto[]> {
  try {
    const res = await apiClient.get<BorrowTransactionItemResponseDto[]>(
      "/api/borrowrecord/unreturned/all",
    );
    return res.data;
  } catch (err) {
    if (isNormalizedApiError(err) && err.isEmptyState) return [];
    throw err;
  }
}

export async function listUnreturnedForMember(
  memberId: number,
): Promise<BorrowTransactionItemResponseDto[]> {
  try {
    const res = await apiClient.get<BorrowTransactionItemResponseDto[]>(
      `/api/borrowrecord/unreturned/${memberId}`,
    );
    return res.data;
  } catch (err) {
    if (isNormalizedApiError(err) && err.errorCode === "MEMBER_NOT_FOUND") throw err;
    if (isNormalizedApiError(err) && err.status === 404) return [];
    throw err;
  }
}

/** Only response type with memberEmail. Always 200 [], never 404. */
export async function listDueToday(): Promise<DueTodayResponseDto[]> {
  const res = await apiClient.get<DueTodayResponseDto[]>("/api/borrowrecord/due-today");
  return res.data;
}

export async function returnBooks(payload: BookReturnRequestDto): Promise<BookReturnResponseDto> {
  const res = await apiClient.post<BookReturnResponseDto>("/api/borrowrecord/return", payload);
  return res.data;
}
