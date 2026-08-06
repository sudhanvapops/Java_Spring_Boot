import { addDays, differenceInCalendarDays, format, isAfter, parseISO, startOfDay } from "date-fns";

/** Parse an ISO date string from the API into a Date. */
export function parseApiDate(value: string): Date {
  return parseISO(value);
}

/** "4 Aug 2026" — used wherever a librarian reads a date at a glance. */
export function fmt(date: Date): string {
  return format(date, "d MMM yyyy");
}

/** "4 August 2026" — used in longer-form copy (receipts, confirmations). */
export function fmtLong(date: Date): string {
  return format(date, "d MMMM yyyy");
}

/** Due date for a loan starting today, given the MAX_BORROW_DAYS setting. */
export function dueDateFor(borrowDate: Date, maxBorrowDays: number): Date {
  return addDays(borrowDate, maxBorrowDays);
}

/** Whole days overdue, 0 if not overdue (never negative). */
export function daysOverdue(dueDate: Date, reference: Date = new Date()): number {
  const diff = differenceInCalendarDays(startOfDay(reference), startOfDay(dueDate));
  return diff > 0 ? diff : 0;
}

export function isDueToday(dueDate: Date, reference: Date = new Date()): boolean {
  return differenceInCalendarDays(startOfDay(dueDate), startOfDay(reference)) === 0;
}

export function isOverdue(dueDate: Date, reference: Date = new Date()): boolean {
  return isAfter(startOfDay(reference), startOfDay(dueDate));
}

/** True if the given date is strictly after today — used to block
 * backdating a return into the future (INVALID_RETURN_DATE). */
export function isFutureDate(date: Date, reference: Date = new Date()): boolean {
  return isAfter(startOfDay(date), startOfDay(reference));
}

/** yyyy-MM-dd for date inputs / API payloads. */
export function toDateInputValue(date: Date): string {
  return format(date, "yyyy-MM-dd");
}
