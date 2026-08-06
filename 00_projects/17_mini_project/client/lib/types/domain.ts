/**
 * Frontend-shaped types: renamed at the API boundary (book.name -> title,
 * borrowedBookId -> bookId), dates parsed to Date, and derived fields
 * (booksOut, isOverdue, status, ...) computed rather than duplicated.
 * See lib/utils/derive.ts for the derivation logic and
 * stacks-design-system/project/uploads/04-data-models.md "Frontend type notes".
 */
import type { AccountRole } from "./api";

export interface Book {
  id: number;
  title: string;
  author: string;
  availableCopies: number;
  totalCopies: number;
  isActive: boolean;
  /** totalCopies - availableCopies */
  onLoan: number;
  /** isActive && availableCopies > 0 */
  canBeLent: boolean;
}

export interface Member {
  id: number;
  name: string;
  email: string;
  age: number;
  isActive: boolean;
  /** From unreturned/all, grouped by memberId. */
  booksOut: number;
  /** MAX_BOOKS - booksOut, floored at 0. */
  remainingAllowance: number;
  hasOverdue: boolean;
}

/**
 * A single borrowed-book row. `status` is NOT sent by the API — the same
 * BorrowTransactionItemResponseDto shape backs /borrowrecord/all,
 * /borrowrecord/unreturned/*, and transaction items, so status is inferred
 * from *which* endpoint the row came from (see deriveRecordStatus).
 */
export type LoanStatus = "out" | "due-today" | "overdue" | "returned";

export interface LoanRecord {
  memberId: number;
  memberName: string;
  bookId: number;
  bookName: string;
  author: string;
  dueDate: Date;
  status: LoanStatus;
  daysOverdue?: number;
}

export interface DueTodayRecord {
  bookId: number;
  bookName: string;
  author: string;
  dueDate: Date;
  memberId: number;
  memberName: string;
  memberEmail: string;
}

export interface Transaction {
  id: number;
  /** Not on the API response root — read from books[0].memberId (see
   * uploads/04-data-models.md's note on BorrowTransactionResponse). */
  memberId: number;
  memberName: string;
  borrowDate: Date;
  books: {
    bookId: number;
    bookName: string;
    author: string;
    dueDate: Date;
  }[];
}

export interface ReturnItem {
  bookId: number;
  bookName: string;
  fine: number;
  returnDate: Date;
}

export interface ReturnResult {
  memberId: number;
  memberName: string;
  totalFine: number;
  books: ReturnItem[];
}

export interface LibrarySettings {
  MAX_BOOKS: number | null;
  MAX_BORROW_DAYS: number | null;
  FINE_PER_DAY: number | null;
}

export interface Account {
  id: number;
  name: string;
  email: string;
  role: AccountRole;
  memberId: number | null;
}
