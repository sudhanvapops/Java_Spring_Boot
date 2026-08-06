/**
 * errorCode -> user-facing message + treatment, per
 * stacks-design-system/project/uploads/09-error-codes.md. Branch on
 * errorCode, never on bare HTTP status — a 404 means different things on
 * different endpoints (see isEmptyStateError below).
 */
import type { ErrorCode } from "@/lib/types/api";

export type ErrorTreatment = "inline" | "toast" | "banner" | "empty" | "blocking";

export interface ErrorDefinition {
  /** Message template. `{name}` is replaced with a real name (member/book)
   * when the caller has one, per the spec's "use real names" rule. */
  message: string;
  treatment: ErrorTreatment;
}

export const ERROR_DEFINITIONS: Partial<Record<ErrorCode, ErrorDefinition>> = {
  MEMBER_NOT_FOUND: { message: "That member doesn't exist.", treatment: "blocking" },
  MEMBER_INACTIVE: {
    message: "{name}'s account is deactivated and can't borrow books.",
    treatment: "blocking",
  },
  MEMBER_EMAIL_ALREADY_EXISTS: { message: "That email is already registered.", treatment: "inline" },
  MEMBER_HAS_ACTIVE_BORROWS: {
    message: "{name} still has books out. Take them back before deactivating.",
    treatment: "toast",
  },
  BOOK_NOT_FOUND: { message: "That book doesn't exist.", treatment: "blocking" },
  BOOK_INACTIVE: { message: "This book is inactive and can't be lent.", treatment: "inline" },
  BOOK_NOT_AVAILABLE: { message: "No copies of this book are available.", treatment: "inline" },
  BOOK_ALREADY_EXISTS: {
    message: "A book with this title and author already exists.",
    treatment: "inline",
  },
  INVALID_BOOK_COPIES: {
    message: "Available copies can't be more than total copies.",
    treatment: "inline",
  },
  BOOK_CURRENTLY_BORROWED: {
    message: "This book is out on loan. It has to come back before you can deactivate it.",
    treatment: "toast",
  },
  BORROW_TRANSACTION_NOT_FOUND: { message: "That transaction doesn't exist.", treatment: "blocking" },
  NO_BORROW_TRANSACTIONS_FOUND: { message: "No transactions yet", treatment: "empty" },
  MAX_BOOK_LIMIT_EXCEEDED: {
    message: "That's over {name}'s limit. Remove one first.",
    treatment: "inline",
  },
  DUPLICATE_BOOK_REQUEST: { message: "Already in this basket.", treatment: "inline" },
  BOOK_ALREADY_BORROWED_BY_MEMBER: { message: "{name} already has this book out.", treatment: "inline" },
  BORROW_RECORD_NOT_FOUND: { message: "That borrow record doesn't exist.", treatment: "toast" },
  NO_BORROW_RECORDS_FOUND: { message: "No records yet", treatment: "empty" },
  NO_UNRETURNED_BOOKS_FOUND: { message: "Nothing is out", treatment: "empty" },
  NO_ACTIVE_BORROWED_BOOKS: { message: "{name} has no books out.", treatment: "blocking" },
  BOOK_NOT_BORROWED_BY_MEMBER: { message: "{name} doesn't have this book out.", treatment: "toast" },
  INVALID_RETURN_DATE: { message: "You can't return a book in the future.", treatment: "inline" },
  NO_LIBRARY_SETTINGS_AVAILABLE: { message: "Library rules aren't set up yet.", treatment: "empty" },
  SETTING_NOT_FOUND: { message: "That setting hasn't been created yet.", treatment: "empty" },
  INVALID_SETTING_VALUE: { message: "Enter a valid value for this setting.", treatment: "inline" },
  SETTING_ALREADY_EXISTS: { message: "", treatment: "inline" },
  VALIDATION_FAILED: {
    message: "Check the highlighted fields and try again.",
    treatment: "banner",
  },
  INTERNAL_SERVER_ERROR: {
    message: "Something went wrong on the server. Try again in a moment.",
    treatment: "toast",
  },

  // Auth — PROPOSED
  INVALID_CREDENTIALS: { message: "That email and password don't match.", treatment: "banner" },
  ACCOUNT_INACTIVE: {
    message: "This account is deactivated. Contact a librarian to reactivate it.",
    treatment: "blocking",
  },
  EMAIL_NOT_VERIFIED: { message: "Verify your email before signing in.", treatment: "blocking" },
  ACCOUNT_ALREADY_EXISTS: { message: "That email is already registered.", treatment: "inline" },
  TOKEN_EXPIRED: { message: "This link has expired. Request a new one.", treatment: "blocking" },
  TOKEN_INVALID: { message: "This link isn't valid. Request a new one.", treatment: "blocking" },
  TOKEN_ALREADY_USED: { message: "This link has already been used. Request a new one.", treatment: "blocking" },
  PASSWORD_TOO_WEAK: { message: "Choose a longer password.", treatment: "inline" },
  RATE_LIMIT_EXCEEDED: { message: "Too many attempts. Try again in a moment.", treatment: "banner" },
  UNAUTHORIZED: { message: "Sign in to continue.", treatment: "blocking" },
  FORBIDDEN: { message: "You don't have access to this page.", treatment: "blocking" },

  // Client-synthesized
  NETWORK_ERROR: {
    message: "Can't reach the server. Check that it's running and try again.",
    treatment: "banner",
  },
  TIMEOUT: { message: "That took too long. Try again.", treatment: "toast" },
  CANCELLED: { message: "", treatment: "toast" },
  MALFORMED_RESPONSE: { message: "Something didn't load correctly. Try again.", treatment: "toast" },
};

/** Codes that mean "nothing here", not "something broke". */
const EMPTY_STATE_PATTERN = /^NO_/;

export function isEmptyStateCode(code: string | null | undefined): boolean {
  if (!code) return false;
  return EMPTY_STATE_PATTERN.test(code) || code === "NO_LIBRARY_SETTINGS_AVAILABLE";
}

export function isNotFoundCode(code: string | null | undefined): boolean {
  if (!code) return false;
  return code.endsWith("_NOT_FOUND");
}

/** Fill {name} in a message template with a real name, falling back to a
 * generic phrase if none is supplied — never show the literal placeholder. */
export function formatErrorMessage(
  code: ErrorCode | string | null | undefined,
  name?: string,
): string {
  const def = code ? ERROR_DEFINITIONS[code as ErrorCode] : undefined;
  if (!def) return "Something went wrong. Try again.";
  return def.message.replace("{name}", name ?? "This");
}

export function getErrorTreatment(code: ErrorCode | string | null | undefined): ErrorTreatment {
  const def = code ? ERROR_DEFINITIONS[code as ErrorCode] : undefined;
  return def?.treatment ?? "toast";
}
