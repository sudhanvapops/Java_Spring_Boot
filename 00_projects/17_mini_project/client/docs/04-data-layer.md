# 04 — Data Layer

How data gets from the Spring backend into a React component, and back.

## The three layers

Every API interaction passes through the same stack. Each layer has one job.

```
Component
   │   calls a hook, gets { data, isLoading, isError }
   ▼
lib/hooks/use*.ts          ← caching, loading state, refetch, invalidation
   │   calls a service function
   ▼
lib/api/services/*.ts      ← the URL, the HTTP verb, DTO → domain conversion
   │   calls apiClient.get/post/…
   ▼
lib/api/client.ts          ← auth header, envelope unwrap, error normalise, 401 refresh
   │
   ▼
http://localhost:8080
```

**Never skip a layer.** A component calling `axios` directly loses caching, auth
and error normalisation all at once.

---

## Layer 1 — `lib/api/client.ts`

Two Axios instances, deliberately different.

```ts
export const bareClient: AxiosInstance = axios.create({ baseURL, timeout: 15000, withCredentials: true, … });
export const apiClient:  AxiosInstance = axios.create({ baseURL, timeout: 15000, withCredentials: true, … });
```

| Instance | Gets | Used by |
|---|---|---|
| `apiClient` | auth header, envelope unwrap, error normalise, **401 → refresh → retry** | Everything |
| `bareClient` | envelope unwrap, error normalise. **No refresh retry** | `/auth/login` and `/auth/refresh` only |

> ▸ **Why two.** If `/auth/refresh` itself 401s and the client retried by
> calling refresh again, you'd get infinite recursion. `bareClient` exists to
> break that loop.

`withCredentials: true` makes the browser send the httpOnly `refreshToken`
cookie — required for refresh and logout to work cross-origin.

### Request interceptor — attach the token

```ts
apiClient.interceptors.request.use((config) => {
  const { accessToken } = getAuthState();
  if (accessToken) config.headers.set("Authorization", `Bearer ${accessToken}`);
  …
});
```

`getAuthState()` reads the Zustand store **outside React** — interceptors aren't
components and can't call hooks. In dev it also stamps an `X-Correlation-Id` and
logs the request, which is why you see `[api] -> GET /api/book (a1b2c3d4)` in the
console.

### Response interceptor — unwrap the envelope

The backend wraps every success in `{ success, message, data }`. Rather than
writing `res.data.data` everywhere, the interceptor unwraps once:

```ts
function unwrapEnvelope(response: AxiosResponse) {
  const body = response.data as unknown;
  if (body && typeof body === "object" && "success" in body) {
    return { ...response, data: (body as { data: unknown }).data };
  }
  return response;
}
```

So `res.data` in a service is already the payload.

### Error interceptor — normalise every failure

Whatever goes wrong — HTTP error, network down, timeout, cancellation — callers
receive the same shape:

```ts
interface NormalizedApiError {
  status: number | null;              // null = never reached the server
  errorCode: ErrorCode | string | null;
  message: string | null;             // raw backend message
  userMessage: string;                // ready to show a human
  isEmptyState: boolean;              // 404 that means "nothing here", not "broken"
  raw: unknown;
}
```

Check for it with the type guard:

```ts
import { isNormalizedApiError } from "@/lib/api/client";

catch (err) {
  if (isNormalizedApiError(err) && err.errorCode === "MEMBER_EMAIL_ALREADY_EXISTS") {
    // show inline on the email field
  }
}
```

`isEmptyState` deserves attention: this backend returns **404 for empty lists**
on some endpoints (`NO_BORROW_RECORDS_FOUND` etc.). That's "no rows", not an
error. [`isEmptyStateCode()`](../lib/utils/errors.ts) detects the `NO_`-prefixed
codes so services can turn them into `[]`.

### The 401 → refresh → retry flow

```ts
apiClient.interceptors.response.use(unwrapEnvelope, async (error) => {
  if (status === 401 && config && !config._retried) {
    config._retried = true;                    // only ever retry once
    const token = await refreshAccessToken();
    if (token) {
      config.headers.set("Authorization", `Bearer ${token}`);
      return apiClient(config);                // replay the original request
    }
  }
  return normalizeAxiosError(error);
});
```

`refreshAccessToken()` is **single-flight**: a module-level promise means ten
concurrent 401s all await one refresh call rather than firing ten.

```ts
let refreshPromise: Promise<string | null> | null = null;

export async function refreshAccessToken(): Promise<string | null> {
  if (!refreshPromise) {
    refreshPromise = (async () => {
      try {
        const res = await bareClient.post<RefreshResponseDto>("/api/auth/refresh");
        useAuthStore.getState().setSession({ id: res.data.userId, email: res.data.email, role: res.data.role }, res.data.accessToken);
        return res.data.accessToken;
      } catch {
        useAuthStore.getState().clearAuth();
        return null;
      } finally {
        refreshPromise = null;
      }
    })();
  }
  return refreshPromise;
}
```

> ▸ **Why single-flight matters here.** Refresh tokens **rotate** — each refresh
> revokes the previous token. Parallel refreshes would revoke each other and log
> the user out. The shared promise *is* the queue.

---

## Layer 2 — `lib/api/services/`

One module per backend resource. Each function: takes typed input, calls the
client, returns typed output. No React, no caching, no state.

```ts
// lib/api/services/books.ts
export async function listBooks(): Promise<Book[]> {
  try {
    const res = await apiClient.get<BookResponseDto[]>("/api/book");
    return res.data.map(deriveBook);              // DTO → domain
  } catch (err) {
    if (isNormalizedApiError(err) && err.isEmptyState) return [];
    throw err;
  }
}
```

Three responsibilities:

1. **Own the URL.** `/api/book` appears here and nowhere else.
2. **Convert DTO → domain** (see [07](./07-types-and-derive.md)).
3. **Absorb backend quirks** so hooks and pages don't have to.

### Quirks absorbed here

Documenting these is half the value of the layer:

| Quirk | Where | Handling |
|---|---|---|
| Empty list returns 404 | books, records, transactions, settings | `isEmptyState` → return `[]` |
| Read is `/api/book/id/{id}`, write is `/api/book/{id}` | `books.ts` | Both encoded correctly |
| `activateBook` 500s if already active | `books.ts` | Rewrites to a plain user message |
| 404 is ambiguous — missing member vs. no records | `records.ts`, `transactions.ts` | Rethrow on `MEMBER_NOT_FOUND`, else `[]` |
| Settings update key is in the **body**, not the path | `settings.ts` | `updateSetting(payload)` |
| `/unreturned/all` resolves before `/{memberId}` | `records.ts` | Documented; a member id can never be `"all"` |

---

## Layer 3 — `lib/hooks/`

TanStack Query wrappers. This is what components import.

### Queries (reads)

```ts
export function useBooks() {
  return useQuery({ queryKey: ["books"], queryFn: booksApi.listBooks, staleTime: 60_000 });
}

export function useBook(id: number) {
  return useQuery({
    queryKey: ["books", id],
    queryFn: () => booksApi.getBook(id),
    staleTime: 60_000,
    enabled: Number.isFinite(id),      // don't fire on /books/abc
  });
}
```

`queryKey` is the cache identity. Same key → same cached data, deduplicated
across every component that asks. `staleTime` is how long it's considered fresh
before a background refetch.

### Cache keys and stale times used here

| Key | Stale | Why |
|---|---|---|
| `["books"]` | 60s | Changes rarely |
| `["books", id]` | 60s | |
| `["members"]` | 60s | |
| `["records", "unreturned"]` | 30s | The workhorse — dashboard, book detail, members list, `/lend` guards |
| `["records", "due-today"]` | 30s | Drives the sidebar badge |
| `["records", "all"]` | 60s | |
| `["transactions"]` | 60s | |
| `["settings"]` | 5min | Three values that almost never change |

### Mutations (writes)

```ts
export function useCreateBook() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (values: BookFormValues) => booksApi.createBook(values),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["books"] }),
  });
}
```

`invalidateQueries` marks cached data stale so it refetches — that's how the
books list updates after you add a book, with no manual state juggling.

Lending touches nearly everything, so it invalidates broadly
([`useTransactions.ts`](../lib/hooks/useTransactions.ts)):

```ts
onSuccess: () => {
  qc.invalidateQueries({ queryKey: ["books"] });        // availableCopies changed
  qc.invalidateQueries({ queryKey: ["records"] });      // new unreturned rows
  qc.invalidateQueries({ queryKey: ["transactions"] }); // new transaction
  qc.invalidateQueries({ queryKey: ["members"] });      // booksOut changed
}
```

### Calling a mutation

```ts
const createBook = useCreateBook();

// with try/catch — you want the result or the error
try {
  const book = await createBook.mutateAsync(values);
  router.push(`/books/${book.id}`);
} catch (err) {
  toast.errorFrom(err);
}

// fire and forget
createBook.mutate(values, { onSuccess: () => router.push("/books") });
```

Also available: `createBook.isPending` for button spinners, `createBook.error`
for inline messages.

### ⚠️ Memoise derived data

Several hooks reshape query data before returning it. That derivation **must**
be wrapped in `useMemo`:

```ts
export function useDueToday(opts?: { enabled?: boolean }) {
  const query = useQuery({ … });
  const data = useMemo(() => query.data?.map(deriveDueToday), [query.data]);
  return { ...query, data };
}
```

Without `useMemo`, `.map()` returns a **new array every render**. Anything using
that array in a dependency array re-runs forever. This exact bug shipped here
once — full story in [13 Gotchas](./13-gotchas.md).

### Retry policy

Set once in [`AppProviders.tsx`](../components/providers/AppProviders.tsx):

```ts
function shouldRetryQuery(failureCount: number, error: unknown) {
  if (!err || err.status == null) return failureCount < 2;   // network/timeout → 2 retries
  if (err.status >= 500) return failureCount < 1;            // server error → 1 retry
  return false;                                              // 4xx → never
}
```

Mutations **never** retry. A timed-out lend request may have already succeeded
server-side; retrying could double-lend.

---

## The `enabled` flag as a permission gate

`MEMBER`-role users get 403 on staff endpoints. Rather than fire doomed requests
and swallow errors, the shell disables those queries:

```ts
// components/shell/AppShell.tsx
const isStaff = isStaffRole(user?.role);
const { data: dueToday } = useDueToday({ enabled: isStaff });
const { data: members }  = useMembersRaw({ enabled: isStaff });
```

`enabled: false` means the query never runs and stays idle.

**Next:** [05 — State](./05-state.md)
