# 11 — Function Reference

Every exported function, hook, store and component in `lib/` and `components/`,
one line each. Use it as a lookup table.

---

## `lib/api/client.ts`

| Export | Signature | Does |
|---|---|---|
| `apiClient` | `AxiosInstance` | Main HTTP client: attaches the Bearer token, unwraps the envelope, normalises errors, refreshes on 401 and replays |
| `bareClient` | `AxiosInstance` | Same minus the 401-refresh retry. Used **only** by login/refresh to avoid recursion |
| `refreshAccessToken` | `() => Promise<string \| null>` | Single-flight refresh. Updates the auth store on success, clears it on failure. Returns the new token or `null` |
| `isNormalizedApiError` | `(error: unknown) => error is NormalizedApiError` | Type guard — narrows an unknown catch value so you can read `.errorCode` / `.userMessage` |

## `lib/api/services/auth.ts`

| Function | Signature | Endpoint |
|---|---|---|
| `register` | `(payload: RegisterRequestDto) => Promise<RegisterResponseDto>` | `POST /api/auth/register` — public; creates a **member** (patron), not a login account |
| `registerStaff` | `(payload: StaffRegisterRequestDto) => Promise<StaffRegisterResponseDto>` | `POST /api/auth/register-staff` — admin only; creates an `ADMIN`/`LIBRARIAN` user |
| `login` | `(payload: LoginRequestDto) => Promise<LoginResponseDto>` | `POST /api/auth/login` — via `bareClient` |
| `refresh` | `() => Promise<RefreshResponseDto>` | `POST /api/auth/refresh` — no body; reads the httpOnly cookie |
| `logout` | `() => Promise<LogoutResponseDto>` | `POST /api/auth/logout` — revokes the refresh token |

## `lib/api/services/books.ts`

| Function | Signature | Notes |
|---|---|---|
| `listBooks` | `() => Promise<Book[]>` | `GET /api/book`. Empty-state 404 → `[]` |
| `getBook` | `(id: number) => Promise<Book>` | `GET /api/book/id/{id}` — note the `/id/` segment on reads |
| `searchBooksByName` | `(name: string) => Promise<Book[]>` | 404 → `[]` |
| `searchBooksByAuthor` | `(author: string) => Promise<Book[]>` | 404 → `[]` |
| `createBook` | `(values: BookFormValues) => Promise<Book>` | Sends `availableCopies = totalCopies`, `isActive = true` |
| `updateBook` | `(id, values, availableCopies, isActive) => Promise<Book>` | `PUT /api/book/{id}` — no `/id/` on writes |
| `deactivateBook` | `(id: number) => Promise<void>` | Soft delete. 409 if currently borrowed |
| `activateBook` | `(id: number) => Promise<void>` | Rewrites the backend's 500-when-already-active into a readable message |

## `lib/api/services/members.ts`

| Function | Signature | Notes |
|---|---|---|
| `listMembers` | `() => Promise<MemberResponseDto[]>` | Well-behaved: `200 []` when empty |
| `getMember` | `(id: number) => Promise<MemberResponseDto>` | |
| `createMember` | `(values: MemberFormValues) => Promise<MemberResponseDto>` | Patron record only — no password |
| `updateMember` | `(id, values, isActive) => Promise<MemberResponseDto>` | |
| `deactivateMember` | `(id: number) => Promise<void>` | 409 if they have active borrows |
| `activateMember` | `(id: number) => Promise<void>` | |

## `lib/api/services/records.ts`

| Function | Signature | Notes |
|---|---|---|
| `listAllRecords` | `() => Promise<BorrowTransactionItemResponseDto[]>` | Full ledger. No returned flag — status is inferred |
| `listRecordsForMember` | `(memberId) => Promise<…[]>` | Rethrows `MEMBER_NOT_FOUND`; other 404s → `[]` |
| `listUnreturnedAll` | `() => Promise<…[]>` | The workhorse — dashboard, book detail, members list, `/lend` guards |
| `listUnreturnedForMember` | `(memberId) => Promise<…[]>` | |
| `listDueToday` | `() => Promise<DueTodayResponseDto[]>` | Always `200 []`. Only response carrying `memberEmail` |
| `returnBooks` | `(payload: BookReturnRequestDto) => Promise<BookReturnResponseDto>` | Returns per-book fines + total |

## `lib/api/services/transactions.ts`

| Function | Signature | Notes |
|---|---|---|
| `listAllTransactions` | `() => Promise<BorrowTransactionResponseDto[]>` | |
| `getTransaction` | `(id: number) => Promise<BorrowTransactionResponseDto>` | |
| `listTransactionsForMember` | `(memberId) => Promise<…[]>` | Rethrows `MEMBER_NOT_FOUND` |
| `borrowBooks` | `(payload: BorrowTransactionRequestDto) => Promise<BorrowTransactionResponseDto>` | Also 404s when settings are unconfigured — `/lend` checks settings first |

## `lib/api/services/settings.ts`

| Function | Signature | Notes |
|---|---|---|
| `listSettings` | `() => Promise<SettingsResponseDto[]>` | Read-first: tells you which keys exist |
| `getSetting` | `(key: SettingKey) => Promise<SettingsResponseDto>` | |
| `createSetting` | `(payload: CreateSettingRequestDto) => Promise<SettingsResponseDto>` | POST for keys that don't exist |
| `updateSetting` | `(payload: SettingsRequestDto) => Promise<SettingsResponseDto>` | PUT; **key is in the body**, not the path |
| `deleteSetting` | `(key: SettingKey) => Promise<void>` | |

---

## `lib/hooks/useAuth.ts`

| Hook | Returns | Does |
|---|---|---|
| `useLogin()` | mutation | Logs in and calls `setSession` on success |
| `useRegister()` | mutation | Public member signup. No session side-effects |
| `useRegisterStaff()` | mutation | Admin-only staff creation. Doesn't touch the caller's session |
| `useLogout()` | mutation | Revokes server-side, then `clearAuth()` + `queryClient.clear()` in `onSettled` |
| `useHydrateAuth()` | `() => Promise<void>` | Call on mount: sets status loading → refresh → authenticated/unauthenticated |
| `useProactiveRefresh()` | `void` | Refreshes every 15s while authenticated (access token lives 30s) |

## `lib/hooks/useBooks.ts`

| Hook | Cache key | Does |
|---|---|---|
| `useBooks()` | `["books"]` | All books, 60s stale |
| `useBook(id)` | `["books", id]` | One book; disabled when `id` isn't finite |
| `useBookSearch(query, mode)` | `["books","search",mode,query]` | Server-side search by `"name"` or `"author"`; disabled on empty query |
| `useCreateBook()` | — | Invalidates `["books"]` |
| `useUpdateBook(id)` | — | Invalidates `["books"]` |
| `useDeactivateBook()` | — | Invalidates `["books"]` |
| `useActivateBook()` | — | Invalidates `["books"]` |

## `lib/hooks/useMembers.ts`

| Hook | Does |
|---|---|
| `useMembersRaw(opts?)` | Raw DTOs, `["members"]`. `opts.enabled` gates it for non-staff |
| `useMemberRaw(id)` | Raw DTO for one member |
| `useMembers()` | **Composed**: members + unreturned + settings → `Member[]` with `booksOut`, `remainingAllowance`, `hasOverdue`. Memoised |
| `useMember(id)` | Same composition for one member. Memoised |
| `useCreateMember()` | Invalidates `["members"]` |
| `useUpdateMember(id)` | Invalidates `["members"]` |
| `useDeactivateMember()` | Invalidates `["members"]` |
| `useActivateMember()` | Invalidates `["members"]` |

## `lib/hooks/useRecords.ts`

| Hook | Does |
|---|---|
| `useUnreturnedAll(opts?)` | Raw unreturned rows, `["records","unreturned"]`, 30s. `opts.enabled` for non-staff |
| `useUnreturnedRecords()` | Same, derived to `LoanRecord[]` with status. **Memoised** |
| `useUnreturnedForMember(memberId)` | Raw, scoped to a member |
| `useUnreturnedForMemberDerived(memberId)` | Derived version. **Memoised** |
| `useRecordsForMember(memberId)` | Full ledger for one member (returned + out). **Memoised** |
| `useDueToday(opts?)` | `DueTodayRecord[]`, 30s. Drives the sidebar badge. **Memoised** |
| `useAllRecords()` | Whole ledger with returned/out inferred by diffing. **Memoised** |
| `useReturnBooks()` | Return mutation; invalidates books, records, members |

## `lib/hooks/useTransactions.ts`

| Hook | Does |
|---|---|
| `useTransactions()` | All transactions, derived. **Memoised** |
| `useTransaction(id)` | One transaction, derived. **Memoised** |
| `useTransactionsForMember(memberId)` | Scoped list, derived. **Memoised** |
| `useBorrowBooks()` | Lend mutation; invalidates books, records, transactions, members |

## `lib/hooks/useSettings.ts`

| Export | Does |
|---|---|
| `useSettingsRaw()` | Raw `SettingsResponseDto[]`, `["settings"]`, 5min stale |
| `useSettings()` | Parsed `LibrarySettings` (`number \| null` per key). **Memoised** |
| `useSaveSetting()` | PUT if configured else POST, with fallback both ways on `SETTING_NOT_FOUND` / `SETTING_ALREADY_EXISTS` |
| `useDeleteSetting()` | Deletes a key; invalidates `["settings"]` |
| `settingsByKey(dtos)` | `Map<SettingKey, SettingsResponseDto>` — helper, not a hook |

## Utility hooks

| Hook | Signature | Does |
|---|---|---|
| `useToast()` | → `{ success, info, error, errorFrom }` | Push toasts. `errorFrom(err)` extracts `userMessage` |
| `useMediaQuery(query)` | `(string) => boolean` | Reactive media query via `useSyncExternalStore` — correct on first render |
| `useClickOutside(ref, onOutside, active)` | `void` | Fires on pointerdown outside `ref`, only while `active` |
| `useBreadcrumbs(pathname)` | `(string) => Crumb[]` | Breadcrumb trail; resolves dynamic titles from the query cache |

---

## `lib/stores/`

| Store | Selected fields | Actions |
|---|---|---|
| `useAuthStore` | `user`, `accessToken`, `status`, `error` | `setUser`, `setAccessToken`, `setStatus`, `setError`, `setSession`, `clearAuth` |
| `useLendBasketStore` | `member`, `items`, `submitting`, `result` | `selectMember`, `clearMember`, `addBook`, `removeBook`, `clear`, `setSubmitting`, `setResult` |
| `useReturnSelectionStore` | `member`, `selected` (Set), `returnDate`, `submitting`, `result` | `selectMember`, `toggle`, `selectAll`, `clearSelection`, `setReturnDate`, `setSubmitting`, `setResult`, `reset` |
| `useUiStore` | `sidebarCollapsed`, `commandPaletteOpen`, `toasts` | `setSidebarCollapsed`, `toggleSidebar`, `setCommandPaletteOpen`, `pushToast`, `dismissToast`, `clearToasts` |

| Helper | Does |
|---|---|
| `getAuthState()` | Reads auth state outside React (used by Axios interceptors) |

---

## `lib/utils/derive.ts`

| Function | Does |
|---|---|
| `deriveBook(dto)` | → `Book`; renames `name`→`title`, computes `onLoan`, `canBeLent` |
| `deriveMember(dto, booksOutMap, maxBooks, overdueIds?)` | → `Member`; computes `booksOut`, `remainingAllowance`, `hasOverdue` |
| `deriveUnreturnedRecord(dto, reference?)` | → `LoanRecord`; status `out`/`due-today`/`overdue` + `daysOverdue` |
| `deriveAllRecords(all, unreturned, reference?)` | → `LoanRecord[]`; infers `returned` by diffing on `memberId:bookId:dueDate` |
| `deriveDueToday(dto)` | → `DueTodayRecord` |
| `deriveTransaction(dto)` | → `Transaction`; lifts `memberId` from `books[0]`, parses dates |
| `deriveReturnResult(dto)` | → `ReturnResult`; parses dates |
| `deriveSettings(dtos)` | → `LibrarySettings`; string values → `number \| null` |
| `groupBooksOutByMember(unreturned)` | → `Map<memberId, count>` |
| `groupOutByBook(unreturned)` | → `Map<bookId, items[]>` |
| `deriveOverdueMemberIds(unreturned, reference?)` | → `Set<memberId>` |

## `lib/utils/date.ts`

| Function | Does |
|---|---|
| `parseApiDate(value)` | ISO string → `Date` |
| `fmt(date)` | `"4 Aug 2026"` — tables and rows |
| `fmtLong(date)` | `"4 August 2026"` — receipts and prose |
| `dueDateFor(borrowDate, maxBorrowDays)` | Adds the loan length |
| `daysOverdue(dueDate, reference?)` | Whole days late, never negative |
| `isDueToday(dueDate, reference?)` | |
| `isOverdue(dueDate, reference?)` | |
| `isFutureDate(date, reference?)` | Blocks backdating a return into the future |
| `toDateInputValue(date)` | `yyyy-MM-dd` for `<input type="date">` |

## `lib/utils/currency.ts`

| Export | Does |
|---|---|
| `CURRENCY` | Symbol from `NEXT_PUBLIC_CURRENCY_SYMBOL`, default `₹` |
| `formatFine(amount)` | `0` → `"no fine"`, else `"₹40.00"` |
| `formatAmount(amount)` | Always numeric, even at zero — for editable fields |

## `lib/utils/errors.ts`

| Export | Does |
|---|---|
| `ERROR_DEFINITIONS` | `errorCode` → `{ message, treatment }` for every known code |
| `formatErrorMessage(code, name?)` | Code → user sentence; fills the `{name}` placeholder |
| `getErrorTreatment(code)` | → `inline \| toast \| banner \| empty \| blocking` |
| `isEmptyStateCode(code)` | True for `NO_`-prefixed codes — "nothing here", not "broken" |
| `isNotFoundCode(code)` | True for `*_NOT_FOUND` |

## `lib/utils/rbac.ts`

| Export | Does |
|---|---|
| `isStaffRole(role)` | `ADMIN` or `LIBRARIAN` |
| `isAllowedForMember(pathname)` | Whether a `MEMBER` session may render that path |
| `MEMBER_HOME` / `STAFF_HOME` | `/books` / `/dashboard` |
| `homeFor(role)` | Landing page for a role |

## `lib/utils/password.ts` · `lib/utils/url.ts`

| Export | Does |
|---|---|
| `passwordChecks(password)` | Advisory checklist: 8+ chars, a number, a symbol |
| `passwordScore(password)` | `0–3` for the strength meter |
| `safeNextPath(next, fallback?)` | Validates a `?next=` target is an internal path; else the fallback |

## `lib/config/nav.ts`

| Export | Does |
|---|---|
| `navGroupsFor(role)` | Sidebar tree for the role; appends admin-only "Add staff" for `ADMIN` |
| `staticCrumbsFor(pathname)` | Breadcrumb parts for a static route, or `null` |
| `sectionFor(pathname)` | Which sidebar item to highlight (`/books/7/edit` → `/books`) |

---

## Components

Props are listed in [09 — Styling & components](./09-styling-and-components.md).

| Folder | Exports |
|---|---|
| `core/` | `Icon`, `GLYPHS`, `MotionDiv`, `Presence`, `EASE`, `RISE`, `STAGGER`, `stagger()`, `usePrefersReducedMotion()` |
| `forms/` | `Button`, `IconButton`, `Input`, `PasswordField`, `Select`, `Checkbox`, `Label`, `StrengthMeter` |
| `data/` | `DataTable`, `Badge`, `StatusBadge`, `Skeleton`, `SkeletonRows`, `AvailabilityBar`, `FineDisplay` |
| `feedback/` | `Toast`, `ToastHost`, `Dialog`, `Banner`, `EmptyState`, `ErrorPanel` |
| `navigation/` | `Sidebar`, `Topbar`, `PageHeader`, `Tabs`, `DropdownMenu`, `CommandPalette` |
| `panels/` | `Panel`, `StatCard`, `SettingCard`, `AuthCard`, `BackgroundBeams` |
| `circulation/` | `BookRow`, `MemberRow`, `SearchCombobox`, `BookSearchCombobox`, `MemberSearchCombobox`, `MemberSummary`, `BasketPanel`, `UnreturnedList` |
| `shell/` | `AppShell`, `useBreadcrumbs` |
| `providers/` | `AppProviders` |

**Next:** [12 — Recipes](./12-recipes.md)
