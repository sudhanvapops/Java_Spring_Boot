# 10 — End-to-End Flows

Click-to-database walkthroughs. Read one of these and the layers stop being
abstract.

---

## Flow 1 — App start (signed out → dashboard)

```
1. Browser → http://localhost:3000/
2. app/page.tsx (server)          redirect("/dashboard")
3. Layouts mount:
     app/layout.tsx               fonts, AppProviders (QueryClient), ToastHost
     app/(app)/layout.tsx         "use client" — the gate
4. status === "idle" → useHydrateAuth() → status "loading"
     → POST /api/auth/refresh with the httpOnly cookie
5a. No cookie → 400 NO_REFRESH_TOKEN_EXISTS
     → clearAuth() → status "unauthenticated"
     → router.replace("/login?next=%2Fdashboard")
5b. Valid cookie → 200 { accessToken, userId, email, role }
     → setSession(...) → status "authenticated"
     → <AppShell> renders, dashboard queries fire
```

Until step 5 resolves the layout renders `<FullPageSkeleton />` — which is why
you never see the login screen flash for a signed-in user.

⚠️ The 400 in 5a is **normal**. "No cookie" means "not signed in", not an error.

---

## Flow 2 — Login

```
1. /login → app/(auth)/login/page.tsx (server) wraps LoginForm in <Suspense>
2. LoginForm ("use client") — RHF + zodResolver(loginSchema)
3. Submit → handleSubmit validates client-side
4. useLogin().mutateAsync({ email, password })
     → authApi.login()  →  bareClient.post("/api/auth/login")
5. Backend: authenticate → access token (30s) + Set-Cookie refreshToken (7d, Path=/api/auth)
6. Interceptor unwraps the envelope → res.data is LoginResponseDto
7. onSuccess → setSession({ id: userId, email, role }, accessToken) → status "authenticated"
8. router.push(safeNextPath(searchParams.get("next"), homeFor(data.role)))
     ADMIN/LIBRARIAN → /dashboard      other → /books
```

Failure path: 401 `UNAUTHORIZED` → normalised → `login.error` → `AuthCard`
renders "That email and password don't match." above the form, never on a field.

---

## Flow 3 — Loading the books list

```
1. /books → app/(app)/books/page.tsx ("use client")
2. useBooks() → useQuery({ queryKey: ["books"], queryFn: listBooks, staleTime: 60s })
3. Cache miss → listBooks()
     → apiClient.get("/api/book")
        → request interceptor adds Authorization: Bearer <token>
4. 200 { success, message, data: BookResponseDto[] }
     → unwrapEnvelope → res.data is the array
     → res.data.map(deriveBook) → Book[]  (title, onLoan, canBeLent)
5. Query caches under ["books"]; component re-renders with data
6. Page filters/sorts client-side (useMemo) and renders <DataTable>
```

Navigate away and back within 60s: **no request**. The cache answers.

Empty-list path: backend 404s with `NO_BOOKS_FOUND`-style code → interceptor
sets `isEmptyState: true` → `listBooks` catches and returns `[]` → the page
shows `EmptyState`, not an error.

---

## Flow 4 — Lending books (the most complex)

```
1. /lend → page.tsx (server, <Suspense>) → LendWorkspace ("use client")
2. Parallel queries: useMembers(), useBooks(), useSettings()
     useMembers() itself composes members + unreturned records + settings,
     deriving booksOut / remainingAllowance / hasOverdue per member
3. Settings guard: MAX_BOOKS or MAX_BORROW_DAYS missing
     → EmptyState "Library rules aren't set up yet" → hard stop
4. ?member=7 present → preselect that member into useLendBasketStore
5. Librarian picks a member → selectMember(member)
     → useUnreturnedForMember(member.id) fires
6. Book picker computes a per-row `reason`:
     !isActive                      → "Inactive"
     availableCopies === 0          → "None available"
     already out to this member     → "Priya already has this book"
     already in basket              → "Already in this basket"
     items.length >= allowance      → "That's over Priya's limit of 5 books."
   Any reason → option disabled, reason shown
7. addBook(book) → store (duplicates ignored)
8. Confirm → useBorrowBooks().mutateAsync({ memberId, books: [{bookId}] })
     → POST /api/borrow-transactions/borrow
9. onSuccess invalidates FOUR caches:
     ["books"]        availableCopies changed
     ["records"]      new unreturned rows
     ["transactions"] new transaction
     ["members"]      booksOut changed
10. toast.success(...) + render the <Receipt> with a printable transaction
```

Two things worth noticing:

- The guard rails are **client-side UX**; the server enforces the same rules and
  returns `MAX_BOOK_LIMIT_EXCEEDED` / `BOOK_ALREADY_BORROWED_BY_MEMBER` if
  bypassed.
- Step 9 is why "derive, don't store" pays off: invalidate four keys and every
  derived number across the app corrects itself.

---

## Flow 5 — Returning books

```
1. /returns → ReturnsWorkspace ("use client"), ?member=7 optional
2. Pick member → useUnreturnedForMemberDerived(memberId)
     → LoanRecord[] with status + daysOverdue computed from dueDate
3. Select books → useReturnSelectionStore holds a Set<number> of bookIds
     (ids only — the records stay in the query cache)
4. Return date defaults to today; a future date is blocked client-side
     (isFutureDate) and server-side (INVALID_RETURN_DATE)
5. Submit → useReturnBooks().mutateAsync({ memberId, books, returnDate })
     → POST /api/borrowrecord/return
6. Response → deriveReturnResult → ReturnResult with per-book fines + total
7. Invalidate ["books"], ["records"], ["members"]
8. Render the outcome, formatting money with formatFine()
     0 → "no fine"     40 → "₹40.00"
```

---

## Flow 6 — Editing a member

```
1. /members/7/edit → useParams() → id = Number("7")
2. useMember(7) → composes member + unreturned + settings, derives Member
3. Form mounts with empty defaults (data hasn't arrived)
4. Data arrives → useEffect([member, reset]) → reset({ name, email, age })
5. Submit → useUpdateMember(7).mutateAsync({ values, isActive })
     → PUT /api/member/7
6. onSuccess → invalidate ["members"] → list and detail refresh
7. toast.success + router.push(`/members/7`)
```

⚠️ Step 4 is where the infinite-loop bug lived. `member` came from a hook that
re-derived a **new object every render**, so `[member, reset]` never stabilised
and the effect re-ran forever. Fixed by memoising in the hook —
[13 Gotchas](./13-gotchas.md).

---

## Flow 7 — Public member signup

```
1. /signup (public, inside (auth) — no shell, no auth gate)
2. Form: name, email, age — NO password (a member isn't a login account)
     validated by memberFormSchema, shared with /members/new
3. useRegister().mutateAsync(values)
     → POST /api/auth/register   (public)
4. Backend AuthService.register() → MemberService.addMember()
     → INSERT into `member`, NOT `users`
5. Response is a MemberResponse
6. No session, no redirect — render "You're registered" and stop
```

The absence of a redirect is the point: there's nothing to log into. A member
record is a patron a librarian can look up.

---

## Flow 8 — Creating a staff account (admin only)

```
1. Sidebar "Add staff" — only rendered when role === "ADMIN" (navGroupsFor)
2. /settings/staff/new — page guard re-checks role for direct URL entry
3. Form: username, email, role (ADMIN|LIBRARIAN), password, confirmPassword
4. useRegisterStaff().mutateAsync(values)
     → apiClient.post("/api/auth/register-staff")   ← apiClient: needs the Bearer token
5. Backend @AdminOnly verifies the caller really is an admin
6. Success → toast, form resets, stays on the page (bulk creation is common)
```

Both the nav hiding and the page guard are UX. `@AdminOnly` is the security.

---

## Flow 9 — A 401 mid-session

```
1. User clicks something after the 30s access token expired
2. apiClient.get("/api/member") → 401
3. Response interceptor: status 401 && !config._retried
     → config._retried = true
     → refreshAccessToken()  (single-flight — concurrent 401s share one call)
4a. Refresh succeeds → new token in store → replay the original request → user sees nothing
4b. Refresh fails → clearAuth() → status "unauthenticated"
     → the (app) layout effect fires → router.replace("/login?next=…")
```

In practice 4a rarely triggers, because `useProactiveRefresh()` refreshes every
15s. The interceptor is the safety net for a laptop waking from sleep.

---

## Flow 10 — Toasts

```
1. Anywhere: const toast = useToast(); toast.success("Book saved.")
2. → useUiStore.pushToast({ tone: "success", message })
3. Store assigns crypto.randomUUID(), appends to toasts[]
4. Success tone → setTimeout(dismissToast, 4000). Danger → never auto-dismiss.
5. <ToastHost /> (mounted once in the root layout) subscribes and renders
6. Because it's in the ROOT layout, a toast survives page navigation
```

`toast.errorFrom(err)` pulls `userMessage` off a `NormalizedApiError`, with a
generic fallback — so you never have to unwrap the error yourself.

**Next:** [11 — Function reference](./11-function-reference.md)
