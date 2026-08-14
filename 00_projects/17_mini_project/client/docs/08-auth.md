# 08 — Auth & RBAC

JWT with rotating refresh tokens, plus role-based access control. This is the
most intricate part of the app; it's worth reading end to end once.

## Two account types — the thing to understand first

The backend has **two unrelated tables**, and conflating them causes confusion:

| | `users` | `member` |
|---|---|---|
| What | A login account | A library patron record |
| Fields | username, email, password, role | name, email, age, isActive |
| Can sign in? | **Yes** | **No** — has no password |
| Roles | `ADMIN`, `LIBRARIAN` | — |
| Created by | An admin, via `/settings/staff/new` | Public `/signup`, or staff via `/members/new` |
| Used for | Operating the console | Borrowing books |

**There is no foreign key between them.** A patron is not a user; a user is not
a patron.

So:

- `/signup` (public) creates a **member**. No password, no login.
- `/settings/staff/new` (admin only) creates a **user** with `ADMIN` or
  `LIBRARIAN`.
- There's no public route that creates a login account, by design.

`MEMBER` still exists as a `UserRoles` value and the frontend handles it, but no
current flow creates a user with that role.

---

## The five auth endpoints

| Method | Path | Auth | Purpose |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register a library **member** (name, email, age) |
| `POST` | `/api/auth/register-staff` | Admin (Bearer) | Create an `ADMIN`/`LIBRARIAN` **user** |
| `POST` | `/api/auth/login` | Public | Email + password → access token + refresh cookie |
| `POST` | `/api/auth/refresh` | Refresh cookie | Rotate refresh token, issue new access token |
| `POST` | `/api/auth/logout` | Refresh cookie | Revoke the refresh token, clear the cookie |

Wrapped in [`lib/api/services/auth.ts`](../lib/api/services/auth.ts). There is
**no `/me` endpoint** — `/refresh` returns full identity (`userId`, `email`,
`role`), so it doubles as session rehydration.

---

## Tokens

| | Access token | Refresh token |
|---|---|---|
| Sent as | `Authorization: Bearer <token>` | httpOnly cookie `refreshToken` |
| Lifetime | **30 seconds** | 7 days |
| Stored | Zustand, **memory only** | Cookie — JS can never read it |
| Rotates | Per refresh | Per refresh; the old one is revoked |
| Cookie path | — | `/api/auth`, `sameSite=strict` |

> ▸ **30 seconds is a dev value**, but the frontend is built as if it's real —
> which is the right instinct. A token that short is unusable without both a
> reactive 401-retry *and* a proactive refresh, and building both makes the app
> correct at any lifetime.

### Why memory-only

A token in `localStorage` is readable by any XSS payload. A module-scoped
variable isn't. The trade-off — losing it on refresh — costs nothing, because
the httpOnly cookie restores the session silently on mount.

---

## Flow 1 — Login

```
LoginForm submit
  └─ useLogin().mutateAsync({ email, password })
       └─ authApi.login()  →  bareClient.post("/api/auth/login")
            └─ Backend: verify, issue access token + Set-Cookie: refreshToken
       └─ onSuccess: setSession({ id: userId, email, role }, accessToken)
  └─ router.push(safeNextPath(searchParams.get("next"), homeFor(data.role)))
```

`bareClient`, not `apiClient` — login must never trigger the 401-refresh
interceptor.

Landing page depends on role: `homeFor()` sends staff to `/dashboard`, everyone
else to `/books`. And `?next=` is filtered through
[`safeNextPath()`](../lib/utils/url.ts), which rejects protocol-relative and
absolute URLs so a crafted link can't redirect a freshly-authenticated user
off-site.

---

## Flow 2 — Session rehydration on load

A page refresh wipes the in-memory token. On mount,
[`app/(app)/layout.tsx`](../app/%28app%29/layout.tsx) calls `/refresh`; the browser
attaches the httpOnly cookie automatically.

```
Page load → status: "idle"
  └─ useHydrateAuth() → status: "loading"
       └─ refreshAccessToken()
            ├─ 200 → setSession(...)   → status: "authenticated" → render app
            └─ 400/401 → clearAuth()   → status: "unauthenticated" → /login
```

This is why `AuthStatus` has four values. Rendering is gated on
`status === "authenticated"`, and `"idle"`/`"loading"` show a skeleton — so a
signed-in user never sees the login screen flash on refresh.

⚠️ A 400 here is **normal**, not an error: no cookie means "not signed in". You
will see one 400 on `/api/auth/refresh` in the console on every signed-out page
load. Expected.

---

## Flow 3 — Requests and the 401 retry

```
apiClient request
  └─ interceptor attaches Bearer token
  └─ 401? (token expired mid-flight)
       ├─ refreshAccessToken()  (single-flight)
       │    ├─ success → replay original request with the new token
       │    └─ failure → clearAuth() → layout redirects to /login
       └─ not 401 → normalise the error and reject
```

Guarded by `config._retried` so a request is only ever replayed once.

## Flow 4 — Proactive refresh

Waiting for a 401 on a 30-second token means constant retry churn. So
[`useProactiveRefresh()`](../lib/hooks/useAuth.ts) refreshes ahead of expiry:

```ts
const PROACTIVE_REFRESH_MS = 15_000;

export function useProactiveRefresh() {
  const status = useAuthStore((s) => s.status);
  useEffect(() => {
    if (status !== "authenticated") return;
    const id = setInterval(() => { refreshAccessToken(); }, PROACTIVE_REFRESH_MS);
    return () => clearInterval(id);
  }, [status]);
}
```

Mounted once, in the `(app)` layout. The cleanup matters — without
`clearInterval` you'd stack a new timer on every status change.

## Flow 5 — Logout

```
useLogout().mutate()
  └─ authApi.logout()   → backend revokes the token, clears the cookie
  └─ onSettled:  clearAuth()  +  queryClient.clear()
  └─ router.push("/login")
```

`onSettled`, not `onSuccess` — local state must be cleared even if the network
call fails. And `qc.clear()` is essential: without it the next user to sign in
on that browser briefly sees the previous user's cached data.

---

## Route protection — three layers, one of which is real

| Layer | Where | Enforces? |
|---|---|---|
| 1. Edge middleware | [`proxy.ts`](../proxy.ts) | **Removed** — see below |
| 2. Layout guard | [`app/(app)/layout.tsx`](../app/%28app%29/layout.tsx) | UX only |
| 3. Server `@PreAuthorize` | Spring backend | **The only real security** |

### Why layer 1 is gone

The obvious edge check — "does the request have a `refreshToken` cookie?" —
**cannot work here**. The backend sets that cookie with `Path=/api/auth`, a path
that exists only on the Spring server at `:8080`. The browser therefore never
attaches it to a Next.js request for `/dashboard`.

`request.cookies.has("refreshToken")` would be `false` for *every* visitor,
signed in or not, and every protected route would redirect to `/login` forever.
`proxy.ts` is a documented pass-through as a result.

> ▸ **Transferable lesson:** middleware only sees cookies the browser chooses to
> send it, and `Path`/`Domain` decide that. A cookie scoped to another origin's
> path is invisible to your middleware regardless of what you write.

### Layer 2 — the layout guard

```tsx
const status = useAuthStore((s) => s.status);
const role = useAuthStore((s) => s.user?.role);

useEffect(() => { if (status === "idle") hydrate(); }, [status, hydrate]);

useEffect(() => {
  if (status === "unauthenticated") router.replace(`/login?next=${encodeURIComponent(pathname)}`);
}, [status, pathname, router]);

useEffect(() => {
  if (status === "authenticated" && role === "MEMBER" && !isAllowedForMember(pathname)) {
    router.replace(MEMBER_HOME);
  }
}, [status, role, pathname, router]);

if (status !== "authenticated") return <FullPageSkeleton />;
```

Three effects: hydrate, bounce if signed out, bounce if the role can't be here.
`router.replace` (not `push`) keeps the denied page out of history.

### Layer 3 — the server

Spring's `@StaffOnly` / `@AdminOnly` annotations. Everything the frontend does
is cosmetic; deleting the entire frontend guard would change **nothing** about
what the API permits.

---

## RBAC

### The matrix

| Area | Who |
|---|---|
| Login / register / refresh / logout | Public |
| Create staff account | `ADMIN` |
| Browse books (`GET /api/book*`) | Any authenticated |
| Create/edit/deactivate books | `ADMIN`, `LIBRARIAN` |
| Members, transactions, records | `ADMIN`, `LIBRARIAN` |
| Read library settings | `ADMIN`, `LIBRARIAN` |
| Write library settings | `ADMIN` |

### The frontend helpers

[`lib/utils/rbac.ts`](../lib/utils/rbac.ts):

```ts
export function isStaffRole(role) { return role === "ADMIN" || role === "LIBRARIAN"; }

export function isAllowedForMember(pathname: string): boolean {
  if (pathname === "/settings/account") return true;
  const segments = pathname.split("/").filter(Boolean);
  if (segments[0] !== "books") return false;
  if (segments.length === 1) return true;                    // /books
  return segments.length === 2 && segments[1] !== "new";     // /books/{id}, not /books/new
}

export const MEMBER_HOME = "/books";
export const STAFF_HOME = "/dashboard";
export function homeFor(role) { return isStaffRole(role) ? STAFF_HOME : MEMBER_HOME; }
```

### Applied in four places

**1. Sidebar** — [`navGroupsFor(role)`](../lib/config/nav.ts) returns a
different nav tree per role; the admin-only "Add staff" link is appended only
for `ADMIN`.

**2. Buttons** — staff-only affordances are conditionally rendered:

```tsx
const isStaff = isStaffRole(useAuthStore((s) => s.user?.role));
<PageHeader action={isStaff ? <Button …>Add a book</Button> : undefined} />
```

**3. Queries** — don't fire requests that can only 403:

```tsx
const { data: members } = useMembersRaw({ enabled: isStaff });
```

**4. Page guards** — for direct URL entry:

```tsx
if (currentRole !== "ADMIN") {
  return <EmptyState icon="lock" headline="You do not have permission to access this resource." … />;
}
```

---

## Error codes

| Code | Status | Means | Do |
|---|---|---|---|
| `UNAUTHORIZED` | 401 | Bad credentials, or missing/expired token | On login: "That email and password don't match." Elsewhere: refresh, then sign out |
| `FORBIDDEN` | 403 | Signed in, wrong role | Show the message. ⚠️ **Never redirect to login** |
| `NO_REFRESH_TOKEN_EXISTS` | 400 | No cookie — not signed in | Treat as signed out, not an error |
| `TOKEN_REVOKED` / `TOKEN_EXPIRED` / `INVALID_REFRESH_TOKEN` | 401 | Session over | Sign out |
| `USER_EMAIL_ALREADY_EXISTS` | 409 | Duplicate | Inline on the email field |
| `USERNAME_ALREADY_EXISTS_EXCEPTION` | 409 | Duplicate | Inline on the username field |

The 401-vs-403 distinction matters: redirecting a 403 to login is a classic bug
that makes a permissions problem look like a session problem.

---

## Local development

```
email:    admin@library.local
password: Admin@12345
```

Seeded by the backend only when `users` is empty. It's the only way to reach an
admin session initially; use it, then create real staff accounts.

`NEXT_PUBLIC_DISABLE_AUTH=true` bypasses the layout gate for UI work. It cannot
bypass the server, so API calls still 401.

**Next:** [09 — Styling & components](./09-styling-and-components.md)
