# 13 — Gotchas

Real bugs this codebase hit, and the general lesson from each. These are worth
reading *before* you spend an afternoon debugging one of them again.

---

## 1. Unmemoised derived data → infinite render loop

**The symptom**

```
Maximum update depth exceeded. This can happen when a component calls setState
inside useEffect, but useEffect either doesn't have a dependency array, or one
of the dependencies changes on every render.

app/(app)/members/[id]/edit/page.tsx (43:17) @ EditMemberPage.useEffect
```

**The code it pointed at** — a completely standard "populate the edit form once
data arrives" effect:

```tsx
useEffect(() => {
  if (member) reset({ name: member.name, email: member.email, age: member.age });
}, [member, reset]);
```

**The actual cause, two layers away.** `useSettings()` derived its return value
on every render without memoising:

```ts
// before — broken
export function useSettings() {
  const query = useSettingsRaw();
  return { ...query, data: query.data ? deriveSettings(query.data) : undefined };
}
```

`deriveSettings()` builds a **new object every call**. That object fed
`useMember()`'s `useMemo` dependency array, so the memo never hit — which
produced a **new `member` object every render** — so `[member, reset]` never
settled and the effect re-ran forever.

**The fix** — memoise at the source:

```ts
// after
export function useSettings() {
  const query = useSettingsRaw();
  const data = useMemo(() => (query.data ? deriveSettings(query.data) : undefined), [query.data]);
  return { ...query, data };
}
```

The same latent bug existed in eight sibling hooks (`useDueToday`,
`useUnreturnedRecords`, `useAllRecords`, `useTransactions`, …). All fixed the
same way.

> ▸ **The rule:** if a hook reshapes query data with `.map()`, `.filter()` or an
> object literal, wrap it in `useMemo` keyed on the source. A new array or object
> identity every render breaks every consumer that puts it in a dependency array.

**How to diagnose this class of bug:** the error points at the `useEffect` that
*loops*, but the cause is usually the *unstable dependency*. Walk up the chain —
where does that value come from, and is it referentially stable?

---

## 2. `useSearchParams()` without `<Suspense>` fails the build

Dev server: fine. `npm run build`: fails.

Fix — split into a server `page.tsx` that provides the boundary and a client
component that reads the params:

```tsx
export default function LendPage() {
  return <Suspense><LendWorkspace /></Suspense>;
}
```

That is the entire reason `LendWorkspace.tsx`, `ReturnsWorkspace.tsx` and
`LoginForm.tsx` exist as separate files. Always run `npm run build` before
committing.

---

## 3. Middleware can't see a cookie scoped to another path

The obvious edge-side auth check:

```ts
// proxy.ts — looks right, cannot work here
const hasSession = request.cookies.has("refreshToken");
if (!hasSession) return NextResponse.redirect(new URL("/login", request.url));
```

The backend sets that cookie with `Path=/api/auth` — a path that only exists on
the Spring server at `:8080`. The browser never attaches it to a Next.js request
for `/dashboard`, so `has()` is `false` for **every** visitor and everyone gets
redirect-looped to `/login`.

[`proxy.ts`](../proxy.ts) is a documented pass-through as a result; the auth gate
lives in [`app/(app)/layout.tsx`](../app/%28app%29/layout.tsx), which calls
`/api/auth/refresh` — a request that *does* hit `/api/auth`, so the cookie
applies.

> ▸ **Lesson:** cookie `Path` and `Domain` decide who can see a cookie. Before
> writing a middleware auth check, verify the cookie is actually sent to that
> origin and path.

---

## 4. CORS: `Access-Control-Allow-Headers` must list every custom header

Login worked. Every other request failed with:

```
Request header field x-correlation-id is not allowed by
Access-Control-Allow-Headers in preflight response.
```

`apiClient` adds an `X-Correlation-Id` in development. Login used `bareClient`
(no such header) so it passed; everything else tripped preflight.

Backend fix:

```java
config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Correlation-Id"));
```

⚠️ `"*"` does **not** work once `allowCredentials(true)` is set — the Fetch spec
treats it as a literal header name. List every header explicitly. Same rule for
`Access-Control-Allow-Origin`: an exact origin, never `*`, when sending
credentials.

---

## 5. `@CookieValue` without a default → 500 instead of 400

A missing `refreshToken` cookie made Spring throw
`MissingRequestCookieException` *before* the controller ran, which fell through
to the generic handler as a **500**.

But "no cookie" is the normal state for a signed-out visitor — it happens on
every first page load. A 500 makes routine sign-out look like a server crash.

Fixed with an explicit handler returning 400 `NO_REFRESH_TOKEN_EXISTS`.

> ▸ **Lesson:** distinguish "expected absence" from "something broke". Frontend
> code branches on that, and dashboards alert on it.

---

## 6. 403 is not 401 — never redirect it to login

- **401** — you aren't authenticated. Refresh, then sign out. Redirecting to
  login is right.
- **403** — you *are* authenticated, you just aren't allowed. Redirecting to
  login is wrong: the user signs in again, lands back, gets 403 again. An
  infinite loop that looks like broken auth but is a permissions issue.

Show the message and offer somewhere they *can* go.

---

## 7. Copying server data into `useState`

```tsx
// wrong
const { data: books } = useBooks();
const [localBooks, setLocalBooks] = useState<Book[]>([]);
useEffect(() => { if (books) setLocalBooks(books); }, [books]);
```

You now own a second copy that goes stale, an extra render, and a dependency
that must stay referentially stable (see gotcha 1).

Read from the query cache. Derive with `useMemo` if you need a different shape.

There's one legitimate exception in this codebase —
[`app/(app)/settings/library/page.tsx`](../app/%28app%29/settings/library/page.tsx)
holds an editable input value that must **not** be clobbered mid-typing. It uses
React's documented "adjust state during render" pattern rather than an effect:

```tsx
const [prevCurrentValue, setPrevCurrentValue] = useState(currentValue);
if (currentValue !== prevCurrentValue) {
  setPrevCurrentValue(currentValue);
  if (currentValue != null) setValue(currentValue);
}
```

---

## 8. `next/router` vs `next/navigation`

```tsx
import { useRouter } from "next/router";      // ❌ Pages Router
import { useRouter } from "next/navigation";  // ✅ App Router
```

The APIs differ too — App Router's has no `router.query` (use `useParams()` /
`useSearchParams()`) and no `router.events`.

---

## 9. `metadata` doesn't work in client components

```tsx
"use client";
export const metadata = { title: "Books" };   // ❌ silently ignored
```

Move it to a server layout above the page. This app declares it once in
[`app/layout.tsx`](../app/layout.tsx).

---

## 10. Route params are strings

```tsx
const { id } = useParams<{ id: string }>();
useBook(id);           // ❌ passing "7", not 7
useBook(Number(id));   // ✅
```

And `Number("abc")` is `NaN`, so guard downstream:

```ts
enabled: Number.isFinite(id)
```

Without it you fire `/api/book/id/NaN` and get a confusing 400/500 instead of a
clean "not found".

---

## 11. A 404 doesn't always mean "missing"

This backend returns **404 for empty lists** on several endpoints, with a `NO_`
prefixed code (`NO_BORROW_RECORDS_FOUND`, `NO_UNRETURNED_BOOKS_FOUND`). That's
"no rows", not an error.

Services translate it:

```ts
catch (err) {
  if (isNormalizedApiError(err) && err.isEmptyState) return [];
  throw err;
}
```

⚠️ On member-scoped endpoints a 404 is **ambiguous** — missing member vs. no
records. Disambiguate on `errorCode`, not status:

```ts
if (isNormalizedApiError(err) && err.errorCode === "MEMBER_NOT_FOUND") throw err;
if (isNormalizedApiError(err) && err.status === 404) return [];
```

---

## 12. Mutations must not auto-retry

A timed-out lend may have **already succeeded** server-side. Retrying
double-lends. Configured once in
[`AppProviders.tsx`](../components/providers/AppProviders.tsx):

```ts
mutations: { retry: false }
```

Queries retry (network twice, 5xx once, 4xx never) because reads are idempotent.

---

## 13. Forgetting `queryClient.clear()` on logout

Without it, the next user to sign in on that browser briefly sees the previous
user's cached books and members before refetch. A privacy bug, not just a
glitch.

```ts
onSettled: () => { clearAuth(); qc.clear(); }
```

`onSettled`, not `onSuccess` — clear locally even if the server call fails.

---

## 14. Spread `register()` last

```tsx
<Input {...register("name")} id="name" />   // ❌ can clobber register's props
<Input id="name" {...register("name")} />   // ✅
```

`register()` returns `name`, `onChange`, `onBlur`, `ref`. Anything spread after
it silently wins.

---

## 15. `z.coerce.number()` vs `valueAsNumber`

Inside `useForm`, prefer:

```tsx
{...register("totalCopies", { valueAsNumber: true })}
```
```ts
totalCopies: z.number().int().min(1)
```

`z.coerce`'s input type is `unknown`, which makes the form's input and output
types diverge and fights `useForm`'s generics. `z.coerce` is fine **outside**
`useForm` — [`lib/schemas/settings.ts`](../lib/schemas/settings.ts) uses it for
standalone `safeParse` calls.

---

## 16. `.superRefine()` breaks `.extend()`

```ts
const base = z.object({ … }).superRefine(…);
base.extend({ role: … });          // ❌ ZodEffects has no .extend()
```

Keep the shared fields as a plain object and spread:

```ts
const accountFields = { username: …, email: …, password: … };
export const staffRegisterSchema = z.object({ ...accountFields, role: … }).superRefine(…);
```

---

## 17. Environment variables are baked in at build time

`NEXT_PUBLIC_*` values are inlined into the bundle. Changing `.env.local`
requires a **dev server restart** — a hot reload won't pick it up. And they are
not secret; anyone can read them in DevTools.

---

## 18. Stale `.next` type errors

If `tsc` complains about routes that no longer exist:

```
Cannot find module '../../../app/(auth)/forgot-password/page.js'
```

The generated route types are stale after deleting a route. Fix:

```bash
npx next typegen
# or nuke it
rm -rf .next
```

---

## Quick diagnostic table

| Symptom | Look at |
|---|---|
| "Maximum update depth exceeded" | Unmemoised derived data in a hook (gotcha 1) |
| Build fails, dev works | Missing `<Suspense>` around `useSearchParams()` (2) |
| Everyone redirected to /login | Cookie path / auth gate (3) |
| CORS preflight error | Backend allow-list missing a header or origin (4) |
| 500 on `/api/auth/refresh` when signed out | Missing-cookie handler (5) |
| Redirect loop after signing in | 403 treated as 401 (6) |
| Data stale after a mutation | Missing `invalidateQueries` ([04](./04-data-layer.md)) |
| Next user sees previous user's data | Missing `qc.clear()` on logout (13) |
| Form field ignores input | `register()` spread order (14) |
| Number field fails validation | Missing `valueAsNumber` (15) |
| Env var change has no effect | Restart the dev server (17) |
| `tsc` references deleted routes | `npx next typegen` (18) |

**Back to:** [docs index](./README.md)
