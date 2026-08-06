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
  // Auth — PROPOSED
  | "INVALID_CREDENTIALS"
  | "ACCOUNT_INACTIVE"
  | "EMAIL_NOT_VERIFIED"
  | "ACCOUNT_ALREADY_EXISTS"
  | "TOKEN_EXPIRED"
  | "TOKEN_INVALID"
  | "TOKEN_ALREADY_USED"
  | "PASSWORD_TOO_WEAK"
  | "PASSWORD_MISMATCH"
  | "RATE_LIMIT_EXCEEDED"
  | "UNAUTHORIZED"
  | "FORBIDDEN"
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

// ---- Auth — PROPOSED, none of this exists in the backend yet ----------

export type AccountRole = "LIBRARIAN" | "MEMBER";

export interface AccountDto {
  id: number;
  name: string;
  email: string;
  role: AccountRole;
  isActive: boolean;
  emailVerified: boolean;
  memberId: number | null;
  createdAt: ISODateString;
}

export interface AuthResponseDto {
  accessToken: string;
  expiresIn: number;
  user: {
    id: number;
    name: string;
    email: string;
    role: AccountRole;
    memberId: number | null;
  };
}

export interface SignupRequestDto {
  name: string;
  email: string;
  password: string;
}

export interface LoginRequestDto {
  email: string;
  password: string;
}

export interface ForgotPasswordRequestDto {
  email: string;
}

export interface ResetPasswordRequestDto {
  token: string;
  password: string;
}

export interface ChangePasswordRequestDto {
  currentPassword: string;
  newPassword: string;
}
