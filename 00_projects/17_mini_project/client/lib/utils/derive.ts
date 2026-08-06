/**
 * "Derive, don't store" — the same rule the reference mock dataset
 * (ui_kits/console/data.js) follows: availability, booksOut, and loan status
 * are computed from raw API responses, never duplicated in state. See
 * uploads/04-data-models.md "Frontend type notes".
 */
import type {
  BookResponseDto,
  BorrowTransactionItemResponseDto,
  BorrowTransactionResponseDto,
  BookReturnResponseDto,
  DueTodayResponseDto,
  MemberResponseDto,
  SettingsResponseDto,
} from "@/lib/types/api";
import type {
  Book,
  DueTodayRecord,
  LibrarySettings,
  LoanRecord,
  Member,
  ReturnResult,
  Transaction,
} from "@/lib/types/domain";
import { daysOverdue, isDueToday, isOverdue, parseApiDate } from "./date";

export function deriveBook(dto: BookResponseDto): Book {
  const onLoan = dto.totalCopies - dto.availableCopies;
  return {
    id: dto.id,
    title: dto.name,
    author: dto.author,
    availableCopies: dto.availableCopies,
    totalCopies: dto.totalCopies,
    isActive: dto.isActive,
    onLoan,
    canBeLent: dto.isActive && dto.availableCopies > 0,
  };
}

/** memberId -> count of books currently out, from /borrowrecord/unreturned/all. */
export function groupBooksOutByMember(
  unreturned: BorrowTransactionItemResponseDto[],
): Map<number, number> {
  const map = new Map<number, number>();
  for (const item of unreturned) {
    map.set(item.memberId, (map.get(item.memberId) ?? 0) + 1);
  }
  return map;
}

/** bookId -> count currently on loan, for a "who has it" style lookup. */
export function groupOutByBook(
  unreturned: BorrowTransactionItemResponseDto[],
): Map<number, BorrowTransactionItemResponseDto[]> {
  const map = new Map<number, BorrowTransactionItemResponseDto[]>();
  for (const item of unreturned) {
    const list = map.get(item.borrowedBookId) ?? [];
    list.push(item);
    map.set(item.borrowedBookId, list);
  }
  return map;
}

export function deriveOverdueMemberIds(
  unreturned: BorrowTransactionItemResponseDto[],
  reference: Date = new Date(),
): Set<number> {
  const ids = new Set<number>();
  for (const item of unreturned) {
    if (isOverdue(parseApiDate(item.dueDate), reference)) ids.add(item.memberId);
  }
  return ids;
}

export function deriveMember(
  dto: MemberResponseDto,
  booksOutByMember: Map<number, number>,
  maxBooks: number | null,
  overdueMemberIds?: Set<number>,
): Member {
  const booksOut = booksOutByMember.get(dto.id) ?? 0;
  const remaining = maxBooks != null ? Math.max(0, maxBooks - booksOut) : 0;
  return {
    id: dto.id,
    name: dto.name,
    email: dto.email,
    age: dto.age,
    isActive: dto.isActive,
    booksOut,
    remainingAllowance: remaining,
    hasOverdue: overdueMemberIds?.has(dto.id) ?? false,
  };
}

/** A row still out (from an /unreturned endpoint): status is out / due-today
 * / overdue, computed from dueDate vs. today. */
export function deriveUnreturnedRecord(
  dto: BorrowTransactionItemResponseDto,
  reference: Date = new Date(),
): LoanRecord {
  const dueDate = parseApiDate(dto.dueDate);
  const overdue = isOverdue(dueDate, reference);
  return {
    memberId: dto.memberId,
    memberName: dto.memberName,
    bookId: dto.borrowedBookId,
    bookName: dto.bookName,
    author: dto.author,
    dueDate,
    status: overdue ? "overdue" : isDueToday(dueDate, reference) ? "due-today" : "out",
    daysOverdue: overdue ? daysOverdue(dueDate, reference) : undefined,
  };
}

/**
 * /borrowrecord/all has no `returned` flag, so "still out" vs "returned" is
 * inferred by diffing against /borrowrecord/unreturned/all on the
 * (memberId, borrowedBookId) composite key — see uploads/02-pages-required.md
 * "/records" and 04-data-models.md's note on BorrowTransactionItemResponse.
 */
export function deriveAllRecords(
  all: BorrowTransactionItemResponseDto[],
  unreturned: BorrowTransactionItemResponseDto[],
  reference: Date = new Date(),
): LoanRecord[] {
  const outKeys = new Set(unreturned.map((u) => `${u.memberId}:${u.borrowedBookId}:${u.dueDate}`));
  return all.map((dto) => {
    const key = `${dto.memberId}:${dto.borrowedBookId}:${dto.dueDate}`;
    if (outKeys.has(key)) {
      return deriveUnreturnedRecord(dto, reference);
    }
    const dueDate = parseApiDate(dto.dueDate);
    return {
      memberId: dto.memberId,
      memberName: dto.memberName,
      bookId: dto.borrowedBookId,
      bookName: dto.bookName,
      author: dto.author,
      dueDate,
      status: "returned" as const,
    };
  });
}

export function deriveDueToday(dto: DueTodayResponseDto): DueTodayRecord {
  return {
    bookId: dto.borrowedBookId,
    bookName: dto.bookName,
    author: dto.author,
    dueDate: parseApiDate(dto.dueDate),
    memberId: dto.memberId,
    memberName: dto.memberName,
    memberEmail: dto.memberEmail,
  };
}

export function deriveTransaction(dto: BorrowTransactionResponseDto): Transaction {
  return {
    id: dto.transactionId,
    // No memberId at the response root — handle an empty books array
    // defensively per uploads/04-data-models.md's note on this type.
    memberId: dto.books[0]?.memberId ?? 0,
    memberName: dto.memberName,
    borrowDate: parseApiDate(dto.borrowDate),
    books: dto.books.map((b) => ({
      bookId: b.borrowedBookId,
      bookName: b.bookName,
      author: b.author,
      dueDate: parseApiDate(b.dueDate),
    })),
  };
}

export function deriveReturnResult(dto: BookReturnResponseDto): ReturnResult {
  return {
    memberId: dto.memberId,
    memberName: dto.memberName,
    totalFine: dto.totalFine,
    books: dto.books.map((b) => ({
      bookId: b.bookId,
      bookName: b.bookName,
      fine: b.fine,
      returnDate: parseApiDate(b.returnDate),
    })),
  };
}

/** Settings values are always strings on the wire, typed by valueType.
 * A missing key stays null so callers can tell "not configured" apart from
 * "configured as zero". */
export function deriveSettings(dtos: SettingsResponseDto[]): LibrarySettings {
  const byKey = new Map(dtos.map((d) => [d.settingKey, d.settingValue]));
  const maxBooks = byKey.get("MAX_BOOKS");
  const maxDays = byKey.get("MAX_BORROW_DAYS");
  const finePerDay = byKey.get("FINE_PER_DAY");
  return {
    MAX_BOOKS: maxBooks != null ? parseInt(maxBooks, 10) : null,
    MAX_BORROW_DAYS: maxDays != null ? parseInt(maxDays, 10) : null,
    FINE_PER_DAY: finePerDay != null ? parseFloat(finePerDay) : null,
  };
}
