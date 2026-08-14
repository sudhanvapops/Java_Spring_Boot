# 07 — Types & the Derive Layer

Why there are two type files, and what happens between them.

## Two vocabularies

| File | Represents | Named |
|---|---|---|
| [`lib/types/api.ts`](../lib/types/api.ts) | Exactly what the backend sends and expects | `*Dto` |
| [`lib/types/domain.ts`](../lib/types/domain.ts) | What the UI wants to work with | `Book`, `Member`, `LoanRecord` |

The gap between them is real, not ceremony:

```ts
// api.ts — the wire
interface BookResponseDto {
  id: number;
  name: string;             // "name"
  author: string;
  availableCopies: number;
  totalCopies: number;
  isActive: boolean;
}

// domain.ts — the UI
interface Book {
  id: number;
  title: string;            // "title" — books have titles
  author: string;
  availableCopies: number;
  totalCopies: number;
  isActive: boolean;
  onLoan: number;           // computed: totalCopies - availableCopies
  canBeLent: boolean;       // computed: isActive && availableCopies > 0
}
```

> ▸ **Why bother.** Three payoffs. (1) A backend rename touches one `derive`
> function, not forty components. (2) Computed values are defined once instead
> of being re-derived inconsistently across pages. (3) Components read in
> domain language — `book.canBeLent` beats
> `book.isActive && book.availableCopies > 0` scattered everywhere.

---

## `lib/types/api.ts`

### The envelope

Every successful response is wrapped:

```ts
interface ApiSuccessEnvelope<T> { success: true; message: string; data: T; }
interface ApiErrorEnvelope { success: false; errorCode?: ErrorCode | string; message: string; timestamp?: string; }
```

You rarely reference these — the Axios interceptor unwraps `data` before your
code sees it ([04 Data layer](./04-data-layer.md)).

### `ErrorCode`

A union of every code the backend can emit, mirroring its Java `ErrorCode` enum,
plus four the client synthesises (`NETWORK_ERROR`, `TIMEOUT`, `CANCELLED`,
`MALFORMED_RESPONSE`).

Keeping it a union rather than `string` means `ERROR_DEFINITIONS` in
[`lib/utils/errors.ts`](../lib/utils/errors.ts) is type-checked — a typo in a
code name is a compile error.

### `NormalizedApiError`

Not a backend shape — the client-side shape every failure is converted into.
See [04](./04-data-layer.md).

### DTOs by resource

| Resource | Request | Response |
|---|---|---|
| Books | `BookRequestDto` | `BookResponseDto` |
| Members | `MemberRequestDto` | `MemberResponseDto` |
| Transactions | `BorrowTransactionRequestDto` | `BorrowTransactionResponseDto`, `BorrowTransactionItemResponseDto` |
| Records | `BookReturnRequestDto` | `BookReturnResponseDto`, `BorrowReturnItemResponseDto`, `DueTodayResponseDto` |
| Settings | `CreateSettingRequestDto`, `SettingsRequestDto` | `SettingsResponseDto` |
| Auth | `RegisterRequestDto`, `StaffRegisterRequestDto`, `LoginRequestDto` | `RegisterResponseDto`, `StaffRegisterResponseDto`, `LoginResponseDto`, `RefreshResponseDto`, `LogoutResponseDto` |

Two worth calling out:

**`BorrowTransactionItemResponseDto` is dual-purpose.** The same shape backs
`/borrowrecord/all`, `/borrowrecord/unreturned/*` and transaction items. It has
no record id, no return date and no `returned` flag — so "is this returned?"
**cannot be read from the row**. It's inferred from *which endpoint returned it*
(see `deriveAllRecords` below).

**`RegisterResponseDto = MemberResponseDto`.** Public signup registers a library
patron, not a login account, so `/api/auth/register` returns a member. See
[08 Auth](./08-auth.md).

---

## `lib/types/domain.ts`

| Type | Adds over the DTO |
|---|---|
| `Book` | `title` (renamed), `onLoan`, `canBeLent` |
| `Member` | `booksOut`, `remainingAllowance`, `hasOverdue` |
| `LoanRecord` | `bookId` (renamed), `dueDate` as `Date`, `status`, `daysOverdue` |
| `DueTodayRecord` | `bookId` renamed, `dueDate` parsed |
| `Transaction` | `memberId` lifted from `books[0]`, dates parsed |
| `ReturnResult` / `ReturnItem` | dates parsed |
| `LibrarySettings` | three string values parsed to `number \| null` |
| `Account` | The signed-in user: `id`, `email`, `role` |

`LoanStatus` is `"out" | "due-today" | "overdue" | "returned"` — computed, never
sent by the API.

---

## `lib/utils/derive.ts` — the translation layer

Pure functions, no React, no network. Called by **services** (on the way in) so
hooks and components only ever see domain types.

### Simple mapping

```ts
export function deriveBook(dto: BookResponseDto): Book {
  const onLoan = dto.totalCopies - dto.availableCopies;
  return {
    id: dto.id,
    title: dto.name,                                        // rename
    author: dto.author,
    availableCopies: dto.availableCopies,
    totalCopies: dto.totalCopies,
    isActive: dto.isActive,
    onLoan,                                                 // computed
    canBeLent: dto.isActive && dto.availableCopies > 0,     // computed
  };
}
```

### Derivation needing several sources

A member's `booksOut` isn't on `MemberResponseDto` — it's counted from the
unreturned-records list:

```ts
export function deriveMember(
  dto: MemberResponseDto,
  booksOutByMember: Map<number, number>,
  maxBooks: number | null,
  overdueMemberIds?: Set<number>,
): Member {
  const booksOut = booksOutByMember.get(dto.id) ?? 0;
  return {
    …,
    booksOut,
    remainingAllowance: maxBooks != null ? Math.max(0, maxBooks - booksOut) : 0,
    hasOverdue: overdueMemberIds?.has(dto.id) ?? false,
  };
}
```

Which is why [`useMembers()`](../lib/hooks/useMembers.ts) composes three queries
— members, unreturned records, settings — and derives inside a `useMemo`.

### Derivation from *which endpoint* answered

The cleverest one. `/borrowrecord/all` has no returned flag, so status comes
from diffing against the unreturned list on a composite key:

```ts
export function deriveAllRecords(all, unreturned, reference = new Date()): LoanRecord[] {
  const outKeys = new Set(unreturned.map((u) => `${u.memberId}:${u.borrowedBookId}:${u.dueDate}`));
  return all.map((dto) => {
    const key = `${dto.memberId}:${dto.borrowedBookId}:${dto.dueDate}`;
    if (outKeys.has(key)) return deriveUnreturnedRecord(dto, reference);   // still out
    return { …, status: "returned" as const };                             // must be back
  });
}
```

### Time-dependent derivation takes an injectable `reference`

```ts
export function deriveUnreturnedRecord(dto, reference: Date = new Date()): LoanRecord {
  const dueDate = parseApiDate(dto.dueDate);
  const overdue = isOverdue(dueDate, reference);
  return {
    …,
    status: overdue ? "overdue" : isDueToday(dueDate, reference) ? "due-today" : "out",
    daysOverdue: overdue ? daysOverdue(dueDate, reference) : undefined,
  };
}
```

Defaulting to `new Date()` but allowing an override keeps the function pure and
testable — you can assert overdue behaviour without mocking the clock.

### Full list

| Function | Converts |
|---|---|
| `deriveBook` | `BookResponseDto` → `Book` |
| `deriveMember` | `MemberResponseDto` + counts → `Member` |
| `deriveUnreturnedRecord` | item DTO → `LoanRecord` (out/due-today/overdue) |
| `deriveAllRecords` | all + unreturned → `LoanRecord[]` with returned status |
| `deriveDueToday` | `DueTodayResponseDto` → `DueTodayRecord` |
| `deriveTransaction` | `BorrowTransactionResponseDto` → `Transaction` |
| `deriveReturnResult` | `BookReturnResponseDto` → `ReturnResult` |
| `deriveSettings` | `SettingsResponseDto[]` → `LibrarySettings` |
| `groupBooksOutByMember` | items → `Map<memberId, count>` |
| `groupOutByBook` | items → `Map<bookId, items[]>` |
| `deriveOverdueMemberIds` | items → `Set<memberId>` |

---

## "Derive, don't store"

The governing rule: **computed values are never persisted, in state or on the
server.** `booksOut` is counted from records every time; `status` is computed
from `dueDate` every time.

Stored derived data goes stale the moment its source changes. Computing is cheap
here — these are tens of rows, not millions.

⚠️ The cost: derivation runs on every render, so it must be **memoised** when
its result feeds a dependency array. See [13 Gotchas](./13-gotchas.md).

---

## Adding a field end to end

Say the backend adds `isbn` to books:

1. **`lib/types/api.ts`** — add `isbn: string` to `BookResponseDto` (and
   `BookRequestDto` if writable).
2. **`lib/types/domain.ts`** — add `isbn: string` to `Book`.
3. **`lib/utils/derive.ts`** — map it in `deriveBook`.
4. **`lib/schemas/book.ts`** — add a validation rule if it's on the form.
5. **The service** — include it in the request payload if writable.
6. **Components** — render `book.isbn`.

TypeScript walks you through 1–5: add the field to `Book` and every incomplete
`derive` call becomes a compile error. Let the compiler drive.

**Next:** [08 — Auth & RBAC](./08-auth.md)
