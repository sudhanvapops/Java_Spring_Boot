/**
 * Raw shapes exactly as the Spring Boot API sends/expects them (see
 * stacks-design-system/project/uploads/03-endpoints.md and 04-data-models.md).
 * Nothing here is renamed or derived — that happens in lib/api/services/*
 * and lib/utils/derive.ts on the way to lib/types/domain.ts shapes.
 */

export type ISODateString = string;

// ---- Envelope --------------------------------------------------------

export interface ApiSuccessEnvelope<T> {
  success: true;
  message: string;
  data: T;
}

export interface ApiErrorEnvelope {
  success: false;
  errorCode?: ErrorCode | string;
  message: string;
  timestamp?: string;
}

// ---- Error codes (uploads/09-error-codes.md) --------------------------

export type ErrorCode =
  | "MEMBER_NOT_FOUND"
  | "MEMBER_INACTIVE"
  | "MEMBER_EMAIL_ALREADY_EXISTS"
  | "MEMBER_HAS_ACTIVE_BORROWS"
  | "BOOK_NOT_FOUND"
  | "BOOK_INACTIVE"
  | "BOOK_NOT_AVAILABLE"
  | "BOOK_ALREADY_EXISTS"
  | "INVALID_BOOK_COPIES"
  | "BOOK_CURRENTLY_BORROWED"
  | "BORROW_TRANSACTION_NOT_FOUND"
  | "NO_BORROW_TRANSACTIONS_FOUND"
  | "MAX_BOOK_LIMIT_EXCEEDED"
  | "DUPLICATE_BOOK_REQUEST"
  | "BOOK_ALREADY_BORROWED_BY_MEMBER"
  | "BORROW_RECORD_NOT_FOUND"
  | "NO_BORROW_RECORDS_FOUND"
  | "NO_UNRETURNED_BOOKS_FOUND"
  | "NO_ACTIVE_BORROWED_BOOKS"
  | "BOOK_NOT_BORROWED_BY_MEMBER"
  | "INVALID_RETURN_DATE"
  | "NO_LIBRARY_SETTINGS_AVAILABLE"
  | "SETTING_NOT_FOUND"
  | "INVALID_SETTING_VALUE"
  | "SETTING_ALREADY_EXISTS"
  | "VALIDATION_FAILED"
  | "INTERNAL_SERVER_ERROR"
  // Auth (enums/Error/ErrorCode.java — see BACKEND_HANDOFF.md §3.10)
  | "PASSWORD_AND_CONFIRM_PASSWORD_DOESNT_MATCH"
  | "USER_EMAIL_ALREADY_EXISTS"
  | "USERNAME_ALREADY_EXISTS_EXCEPTION"
  | "UNAUTHORIZED"
  | "FORBIDDEN"
  | "INVALID_STAFF_ROLE"
  // Refresh
  | "NO_REFRESH_TOKEN_EXISTS"
  | "NOT_REFRESH_TOKEN"
  | "NO_REFRESH_TOKEN_RECORD_EXISTS"
  | "TOKEN_REVOKED"
  | "TOKEN_EXPIRED"
  | "TOKEN_SUBJECT_MISMATCH"
  | "INVALID_REFRESH_TOKEN"
  // Client-synthesized
  | "NETWORK_ERROR"
  | "TIMEOUT"
  | "CANCELLED"
  | "MALFORMED_RESPONSE";

/** Normalized shape every service/hook/component actually consumes on failure. */
export interface NormalizedApiError {
  status: number | null;
  errorCode: ErrorCode | string | null;
  message: string | null;
  userMessage: string;
  isEmptyState: boolean;
  raw: unknown;
}

// ---- Books --------------------------------------------------------

export interface BookRequestDto {
  name: string;
  author: string;
  totalCopies: number;
  availableCopies: number;
  isActive: boolean;
}

export interface BookResponseDto {
  id: number;
  name: string;
  author: string;
  availableCopies: number;
  totalCopies: number;
  isActive: boolean;
}

// ---- Members --------------------------------------------------------

export interface MemberRequestDto {
  name: string;
  email: string;
  age: number;
  isActive: boolean;
}

export interface MemberResponseDto {
  id: number;
  name: string;
  email: string;
  age: number;
  isActive: boolean;
  /** Always literally "MEMBER" — MemberService hardcodes it; a DTO artifact
   * since Member and User aren't linked (see BACKEND_HANDOFF.md §3.2/§4). */
  role: "MEMBER";
}

// ---- Borrow transactions --------------------------------------------

export interface BorrowTransactionRequestDto {
  memberId: number;
  books: { bookId: number }[];
}

/** Dual-purpose: also the item shape for /borrowrecord/* endpoints. No record
 * id, no returnDate, no `returned` flag — status is inferred from which
 * endpoint returned the row (see lib/utils/derive.ts). */
export interface BorrowTransactionItemResponseDto {
  memberId: number;
  memberName: string;
  borrowedBookId: number;
  bookName: string;
  author: string;
  dueDate: ISODateString;
}

export interface BorrowTransactionResponseDto {
  transactionId: number;
  memberName: string;
  borrowDate: ISODateString;
  books: BorrowTransactionItemResponseDto[];
}

export interface DueTodayResponseDto {
  borrowedBookId: number;
  bookName: string;
  author: string;
  dueDate: ISODateString;
  memberId: number;
  memberName: string;
  memberEmail: string;
}

// ---- Borrow records / returns ----------------------------------------

export interface BookReturnRequestDto {
  memberId: number;
  books: { bookId: number }[];
  returnDate?: ISODateString;
}

export interface BorrowReturnItemResponseDto {
  bookId: number;
  bookName: string;
  fine: number;
  returnDate: ISODateString;
}

export interface BookReturnResponseDto {
  memberId: number;
  memberName: string;
  totalFine: number;
  books: BorrowReturnItemResponseDto[];
}

// ---- Settings --------------------------------------------------------

export type SettingKey = "MAX_BOOKS" | "MAX_BORROW_DAYS" | "FINE_PER_DAY";
export type SettingValueType = "STRING" | "INTEGER" | "BOOLEAN" | "DECIMAL";

export interface SettingsResponseDto {
  settingKey: SettingKey;
  settingValue: string;
  description?: string;
  valueType: SettingValueType;
}

export interface CreateSettingRequestDto {
  settingKey: SettingKey;
  valueType: SettingValueType;
  settingValue: string;
  description?: string;
}

export interface SettingsRequestDto {
  settingKey: SettingKey;
  settingValue: string;
}

// ---- Auth — real, implemented contract (BACKEND_HANDOFF.md §3) --------

export type AccountRole = "MEMBER" | "ADMIN" | "LIBRARIAN";

/** Public signup registers the caller as a Member (a library patron staff
 * can look up and lend books to), not a User login account — Members have
 * no password, and User accounts are staff-only (created via
 * /register-staff). Same shape as MemberRequestDto minus `isActive`, which
 * the backend always sets true for a new signup. */
export interface RegisterRequestDto {
  name: string;
  email: string;
  age: number;
}

/** Identical to MemberResponseDto — /api/auth/register delegates straight
 * to the same MemberService the staff-only /api/member endpoints use. */
export type RegisterResponseDto = MemberResponseDto;

/** POST /api/auth/register-staff — @AdminOnly. Same shape as register plus
 * a required role. */
export interface StaffRegisterRequestDto {
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
  role: "ADMIN" | "LIBRARIAN";
}

export interface StaffRegisterResponseDto {
  username: string;
  email: string;
  role: "ADMIN" | "LIBRARIAN";
}

export interface LoginRequestDto {
  email: string;
  password: string;
}

/** `refreshToken` arrives in the body AND via Set-Cookie — both real. There
 * is no `username`/`name` field anywhere in this response. */
export interface LoginResponseDto {
  accessToken: string;
  accessTokenType: "Bearer";
  refreshToken: string;
  refreshTokenType: "Cookie";
  userId: number;
  email: string;
  role: AccountRole;
}

/** POST /api/auth/refresh — no body, reads the `refreshToken` cookie.
 * Carries full identity, so this response alone rehydrates a session on
 * app load; there is no separate /me endpoint. */
export interface RefreshResponseDto {
  accessToken: string;
  accessTokenType: "Bearer";
  userId: number;
  email: string;
  role: AccountRole;
}

export interface LogoutResponseDto {
  user: string;
}
