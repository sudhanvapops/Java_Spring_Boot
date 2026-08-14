# 03 — Routing

Every URL in the app, how the layouts compose, and the routing patterns used
here.

## Every route

Taken from `next build` output — this is the complete, authoritative list.

### Public — `app/(auth)/`

| URL | File | Notes |
|---|---|---|
| `/login` | `(auth)/login/page.tsx` → `LoginForm.tsx` | Server page wraps client form in `<Suspense>` |
| `/signup` | `(auth)/signup/page.tsx` | Registers a **library member** (patron), not a login account |

### Console — `app/(app)/` (auth-gated)

| URL | File | Role |
|---|---|---|
| `/dashboard` | `(app)/dashboard/page.tsx` | Staff |
| `/books` | `(app)/books/page.tsx` | Any signed-in user |
| `/books/new` | `(app)/books/new/page.tsx` | Staff |
| `/books/[id]` | `(app)/books/%5Bid%5D/page.tsx` | Any signed-in user |
| `/books/%5Bid%5D/edit` | `(app)/books/%5Bid%5D/edit/page.tsx` | Staff |
| `/members` | `(app)/members/page.tsx` | Staff |
| `/members/new` | `(app)/members/new/page.tsx` | Staff |
| `/members/[id]` | `(app)/members/[id]/page.tsx` | Staff |
| `/members/[id]/edit` | `(app)/members/[id]/edit/page.tsx` | Staff |
| `/lend` | `(app)/lend/page.tsx` → `LendWorkspace.tsx` | Staff |
| `/returns` | `(app)/returns/page.tsx` → `ReturnsWorkspace.tsx` | Staff |
| `/records` | `(app)/records/page.tsx` | Staff |
| `/records/due-today` | `(app)/records/due-today/page.tsx` | Staff |
| `/records/unreturned` | `(app)/records/unreturned/page.tsx` | Staff |
| `/transactions` | `(app)/transactions/page.tsx` | Staff |
| `/transactions/[id]` | `(app)/transactions/[id]/page.tsx` | Staff |
| `/settings/library` | `(app)/settings/library/page.tsx` | Staff read, **admin** write |
| `/settings/account` | `(app)/settings/account/page.tsx` | Any signed-in user |
| `/settings/staff/new` | `(app)/settings/staff/new/page.tsx` | **Admin only** |

### Special

| URL | File | Notes |
|---|---|---|
| `/` | `app/page.tsx` | `redirect("/dashboard")` |
| anything unmatched **inside** the console | `(app)/%5B...notfound%5D/page.tsx` | 404 rendered inside the shell |
| anything unmatched outside both groups | `app/not-found.tsx` | Bare 404 |

---

## Layout composition

Which layouts wrap which routes:

```
/login, /signup
──────────────────────────────────────────
app/layout.tsx              <html>, fonts, AppProviders, ToastHost
└── app/(auth)/layout.tsx   centred card, BackgroundBeams
    └── page


/dashboard, /books, /members, … (the whole console)
──────────────────────────────────────────
app/layout.tsx              <html>, fonts, AppProviders, ToastHost
└── app/(app)/layout.tsx    auth gate → role gate → <AppShell>
    │                         AppShell = Sidebar + Topbar + <main>
    └── page                (also guarded by app/(app)/error.tsx)
```

The consequence worth internalising: **navigating between two console pages does
not re-run the auth gate or remount the sidebar.** Layouts persist. Only the
`page` swaps.

---

## Route groups

`(auth)` and `(app)` are route groups — parentheses mean "organise, don't
appear in the URL".

```
app/(auth)/login/page.tsx  →  /login      not  /auth/login
app/(app)/books/page.tsx   →  /books      not  /app/books
```

They exist so the two halves of the app can have completely different layouts:

| Group | Layout gives it |
|---|---|
| `(auth)` | Full-screen centred card, ambient beams, no chrome |
| `(app)` | Auth gate, role gate, sidebar, topbar, breadcrumbs, command palette |

Without groups you'd need one layout with `if (isAuthPage)` branching inside it
— which is exactly the mess route groups remove.

---

## Dynamic routes

Three resources use `[id]`: books, members, transactions.

The pattern is identical in each. From
[`app/(app)/books/%5Bid%5D/page.tsx`](../app/%28app%29/books/%5Bid%5D/page.tsx):

```tsx
"use client";
import { useParams } from "next/navigation";

export default function BookDetailPage() {
  const params = useParams<{ id: string }>();
  const id = Number(params.id);           // params are strings
  const { data: book, isLoading, isError } = useBook(id);

  if (isLoading) return <Skeleton … />;
  if (isError || !book) return <EmptyState headline="That book doesn't exist." … />;
  return …;
}
```

Three things this pattern always does:

1. `Number(params.id)` — URL params are strings, always.
2. The hook guards with `enabled: Number.isFinite(id)`, so `/books/abc` never
   fires a request (see [`lib/hooks/useBooks.ts`](../lib/hooks/useBooks.ts)).
3. Loading and error are handled **before** the happy path, so the JSX below can
   assume `book` exists.

### The catch-all

[`app/(app)/%5B...notfound%5D/page.tsx`](../app/%28app%29/%5B...notfound%5D/page.tsx)
matches any unmatched path inside the console. Because it lives inside `(app)`,
it renders **inside the shell** — the user keeps the sidebar and can navigate
out, instead of hitting a dead-end 404.

⚠️ A catch-all has the lowest priority, so it never shadows a real route. But
be careful adding one at a level where you also have dynamic segments — order
of specificity gets subtle fast.

---

## The `page.tsx` + `Workspace.tsx` split

Three routes split into a server `page.tsx` and a client component:

| Route | Server file | Client file |
|---|---|---|
| `/lend` | `page.tsx` | `LendWorkspace.tsx` |
| `/returns` | `page.tsx` | `ReturnsWorkspace.tsx` |
| `/login` | `page.tsx` | `LoginForm.tsx` |

The reason is **exactly one thing**: those three client components call
`useSearchParams()`, which Next requires to sit under a `<Suspense>` boundary.
The server page provides it:

```tsx
export default function LendPage() {
  return (
    <Suspense>
      <LendWorkspace />
    </Suspense>
  );
}
```

Miss the boundary and `next build` fails. Every other page in the app is a
single `page.tsx`, because it doesn't read search params.

### What the search params are for

| Route | Param | Meaning |
|---|---|---|
| `/lend` | `?member=7` | Preselect a member (linked from the member detail page) |
| `/returns` | `?member=7` | Same |
| `/login` | `?next=/books` | Where to go after signing in |

⚠️ `?next=` is attacker-controllable. It's always passed through
[`safeNextPath()`](../lib/utils/url.ts), which rejects anything that isn't an
internal path — otherwise a crafted link could bounce a freshly-signed-in user
to another site.

---

## Navigation in practice

```tsx
// Declarative — prefer this
<Link href={`/books/${book.id}`}>{book.title}</Link>

// Imperative — after an action completes
const router = useRouter();
await createBook.mutateAsync(values);
router.push(`/books/${book.id}`);

// Redirect (no history entry) — for guards
router.replace("/login");

// Server component only
import { redirect } from "next/navigation";
redirect("/dashboard");
```

### Highlighting the active nav item

`/books/7/edit` should light up **Books** in the sidebar.
[`sectionFor()`](../lib/config/nav.ts) maps any pathname to its section:

```ts
export function sectionFor(pathname: string): string {
  if (pathname.startsWith("/books")) return "/books";
  if (pathname.startsWith("/members")) return "/members";
  if (pathname.startsWith("/transactions")) return "/transactions";
  if (pathname === "/records") return "/records";
  if (pathname.startsWith("/settings")) return "/settings/library";
  return pathname;
}
```

### Breadcrumbs

[`useBreadcrumbs()`](../components/shell/Breadcrumbs.tsx) builds the trail:
static routes come from a map in `nav.ts`; dynamic routes (`/books/7`) resolve
the title from the **TanStack Query cache** the detail page already populated —
so it costs no extra request and degrades to a plain "Book" placeholder while
loading.

**Next:** [04 — Data layer](./04-data-layer.md)
