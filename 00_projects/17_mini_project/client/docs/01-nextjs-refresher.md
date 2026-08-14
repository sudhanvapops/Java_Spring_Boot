# 01 — Next.js Refresher (App Router), taught from this codebase

Everything here is explained against a real file in this repo. Open the file
next to the explanation; that's the point.

This project uses the **App Router** (the `app/` directory). If you last learned
Next.js in the `pages/` era, the mental model changed in three big ways:

1. Routing moved from `pages/` to `app/`, and a folder is a route segment.
2. Components are **server-rendered by default**; you opt into the browser with
   `"use client"`.
3. Shared UI is expressed as nested **layouts**, not a single `_app.tsx`.

---

## 1. The filesystem is the router

Inside `app/`, **a folder makes a URL segment, and a `page.tsx` inside it makes
that URL real.** A folder with no `page.tsx` is just a container.

```
app/
  page.tsx                        →  /
  (app)/
    dashboard/page.tsx            →  /dashboard
    books/page.tsx                →  /books
    books/new/page.tsx            →  /books/new
    books/[id]/page.tsx           →  /books/123
    books/[id]/edit/page.tsx      →  /books/123/edit
    records/due-today/page.tsx    →  /records/due-today
    settings/staff/new/page.tsx   →  /settings/staff/new
```

Note `(app)` contributes **nothing** to the URL — that's a route group, covered
in §4.

A `page.tsx` must `export default` a component. That component is the page.

**Real example** — [`app/page.tsx`](../app/page.tsx), the entire file:

```tsx
import { redirect } from "next/navigation";

export default function RootPage() {
  redirect("/dashboard");
}
```

`/` immediately redirects to `/dashboard`. `redirect()` from `next/navigation`
works by throwing a special error Next catches, so nothing after it runs — you
don't return anything.

---

## 2. Special files

Certain filenames are reserved. Drop one into a route folder and Next wires it
up automatically.

| File | Purpose | In this repo |
|---|---|---|
| `page.tsx` | Makes the segment routable | 23 of them |
| `layout.tsx` | Wraps this segment **and everything below it**; preserved across navigation | [`app/layout.tsx`](../app/layout.tsx), [`app/(app)/layout.tsx`](../app/%28app%29/layout.tsx), [`app/(auth)/layout.tsx`](../app/%28auth%29/layout.tsx) |
| `error.tsx` | React error boundary for the segment. Must be a client component | [`app/error.tsx`](../app/error.tsx), [`app/(app)/error.tsx`](../app/%28app%29/error.tsx) |
| `not-found.tsx` | Rendered for `notFound()` / unmatched routes | [`app/not-found.tsx`](../app/not-found.tsx) |
| `loading.tsx` | Automatic Suspense fallback while the segment loads | *Not used here* — this app fetches client-side and renders its own `<Skeleton>` per page |
| `template.tsx` | Like a layout but remounts on every navigation | *Not used here* |
| `route.ts` | An API endpoint instead of a page | *Not used here* — the Spring backend serves the API |

### Layouts nest, and they persist

A layout receives `children` and wraps every route beneath it. Layouts **do not
re-render on navigation between their children** — that's what keeps the sidebar
from flashing when you move between `/books` and `/members`.

The chain for `/dashboard`:

```
app/layout.tsx            ← <html>, <body>, fonts, providers   (always)
  app/(app)/layout.tsx    ← auth gate + <AppShell> sidebar     (whole console)
    app/(app)/dashboard/page.tsx
```

[`app/layout.tsx`](../app/layout.tsx) is the **root layout**. It's mandatory,
and it's the only place `<html>` and `<body>` are allowed:

```tsx
export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html lang="en" className={`${geist.variable} ${inter.variable} ${jetbrainsMono.variable}`}>
      <body>
        <AppProviders>
          {children}
          <ToastHost />
        </AppProviders>
      </body>
    </html>
  );
}
```

Two things to notice:

- `LayoutProps<"/">` — Next 16 generates route-aware prop types for you. If
  they ever go stale, regenerate with `npx next typegen`.
- `<ToastHost />` sits **outside** `{children}`, inside the providers. Toasts
  are global and must survive page navigation, so they live in the root layout,
  not in any page.

### Error boundaries

`error.tsx` must be a client component (it uses React state internally) and
receives `error` plus a `reset()` function that retries the segment.

[`app/(app)/error.tsx`](../app/%28app%29/error.tsx) catches errors in console pages
— crucially, **the layout above it keeps rendering**, so the sidebar and topbar
stay up and only the content area shows the error. That's the practical reason
to put `error.tsx` at the segment level rather than only at the root.

---

## 3. Server Components vs Client Components — the concept that changed most

**Every component under `app/` is a Server Component by default.** It runs on
the server (at build time or per request), never ships its JavaScript to the
browser, and therefore cannot use state, effects, or browser APIs.

Add `"use client"` at the top of a file to make it a Client Component: it
hydrates in the browser and can use hooks, event handlers and browser APIs.

### `"use client"` marks a boundary, not just a file

This is the part people misremember. `"use client"` doesn't only affect the file
it's in — **everything that file imports becomes part of the client bundle too**.

So you only need the directive at the *top of the boundary*. In this repo,
[`app/(app)/dashboard/page.tsx`](../app/%28app%29/dashboard/page.tsx) has
`"use client"`, and it imports `StatCard`, `Panel`, `BookRow`… none of which
need their own directive to work as client components in that tree.

You'll still see `"use client"` on some components like
[`components/forms/Button.tsx`](../components/forms/Button.tsx) — that's
defensive, so the component is safe to import from a server component too.

### How to decide

| Needs… | Then it must be |
|---|---|
| `useState`, `useEffect`, `useRef`, any hook | Client |
| `onClick`, `onChange`, any event handler | Client |
| `window`, `localStorage`, `matchMedia` | Client |
| A Zustand store or TanStack Query | Client |
| Only props + rendering | Either — leave it server |

### What's actually server-rendered here

Almost every page in this app is a client component, because it fetches through
TanStack Query in the browser against a separate Spring backend. The genuine
server components are:

| File | Why it stays server |
|---|---|
| [`app/layout.tsx`](../app/layout.tsx) | Just structure + font setup |
| [`app/page.tsx`](../app/page.tsx) | Just a `redirect()` |
| [`app/not-found.tsx`](../app/not-found.tsx) | Static markup |
| [`app/(auth)/layout.tsx`](../app/%28auth%29/layout.tsx) | Static wrapper |
| [`app/(auth)/login/page.tsx`](../app/%28auth%29/login/page.tsx) | Suspense wrapper (see §6) |
| [`app/(app)/lend/page.tsx`](../app/%28app%29/lend/page.tsx) | Suspense wrapper |
| [`app/(app)/returns/page.tsx`](../app/%28app%29/returns/page.tsx) | Suspense wrapper |

> ▸ **Why this app is client-heavy.** Server Components shine when the server
> can fetch data directly (a database, or an API it can authenticate to). Here
> the access token lives **in browser memory only** (never a cookie readable by
> the Next server), so the server literally cannot make an authenticated call on
> the user's behalf. Fetching client-side is the correct trade for this
> architecture. A Next.js app with its own database would look very different.

---

## 4. Route groups: folders in `(parentheses)`

A folder wrapped in parentheses organises files **without adding a URL segment**.

```
app/(auth)/login/page.tsx   →  /login       (not /auth/login)
app/(app)/books/page.tsx    →  /books       (not /app/books)
```

The reason to use them is that **each group gets its own `layout.tsx`**:

- [`app/(auth)/layout.tsx`](../app/%28auth%29/layout.tsx) — centred card on a dark
  canvas with the ambient beam effect. No sidebar.
- [`app/(app)/layout.tsx`](../app/%28app%29/layout.tsx) — the auth gate plus the
  full console shell (sidebar, topbar, breadcrumbs).

Two completely different chromes, no URL prefix, no per-page conditionals. That
is exactly what route groups are for.

---

## 5. Dynamic segments

| Pattern | Matches | Example |
|---|---|---|
| `[id]` | one segment | `books/[id]/page.tsx` → `/books/7` |
| `[...slug]` | one or more segments | `(app)/%5B...notfound%5D/page.tsx` |
| `[[...slug]]` | zero or more | *not used here* |

Read the value with `useParams()` in a client component. From
[`app/(app)/books/%5Bid%5D/page.tsx`](../app/%28app%29/books/%5Bid%5D/page.tsx):

```tsx
const params = useParams<{ id: string }>();
const id = Number(params.id);
```

⚠️ **Params are always strings.** `Number(params.id)` on `/books/abc` gives
`NaN`. This codebase guards downstream with `enabled: Number.isFinite(id)` on
the query, so a junk URL shows "not found" instead of firing a bad request.

The catch-all [`app/(app)/%5B...notfound%5D/page.tsx`](../app/%28app%29/%5B...notfound%5D/page.tsx)
is a nice trick: any unmatched URL *inside the console* renders "page not found"
**within the shell**, so the user keeps their sidebar and can navigate away —
rather than being dumped on a bare 404 page.

---

## 6. Navigation

### Links

```tsx
import Link from "next/link";
<Link href="/books">Books</Link>
```

`<Link>` client-side navigates and prefetches. Use it for anything that is
semantically a link — real `<a>` behaviour (middle-click, open in new tab) comes
free, which a `useRouter().push()` on a `<div>` does not give you.

### Programmatic navigation

```tsx
import { useRouter } from "next/navigation";   // ⚠️ next/navigation, NOT next/router
const router = useRouter();
router.push("/books");      // adds a history entry
router.replace("/login");   // replaces it — use for redirects
```

⚠️ `next/router` is the **old Pages Router API**. In App Router it's
`next/navigation`. Wrong import is the single most common muscle-memory bug when
coming back to Next.js.

`replace` vs `push` matters: [`app/(app)/layout.tsx`](../app/%28app%29/layout.tsx)
uses `router.replace()` when bouncing a signed-out user to `/login`, so the back
button doesn't take them to the page they were just denied.

### Reading the current route

| Hook | Returns |
|---|---|
| `usePathname()` | `"/books/7/edit"` |
| `useParams()` | `{ id: "7" }` |
| `useSearchParams()` | Read-only `URLSearchParams` |

### `useSearchParams()` requires a Suspense boundary

This is the trap that explains three otherwise-odd files in this repo.

`useSearchParams()` forces the component into client-side rendering, and Next
requires it to be wrapped in `<Suspense>`. **The build fails otherwise.**

The fix used consistently here: keep `page.tsx` as a tiny server component that
provides the boundary, and put the real UI in a sibling client component.

[`app/(app)/lend/page.tsx`](../app/%28app%29/lend/page.tsx):

```tsx
import { Suspense } from "react";
import { LendWorkspace } from "./LendWorkspace";

export default function LendPage() {
  return (
    <Suspense>
      <LendWorkspace />     {/* "use client", calls useSearchParams() */}
    </Suspense>
  );
}
```

Exactly three components call `useSearchParams()`, and each has this wrapper:

| Uses `useSearchParams()` | Wrapped by |
|---|---|
| `app/(app)/lend/LendWorkspace.tsx` | `app/(app)/lend/page.tsx` |
| `app/(app)/returns/ReturnsWorkspace.tsx` | `app/(app)/returns/page.tsx` |
| `app/(auth)/login/LoginForm.tsx` | `app/(auth)/login/page.tsx` |

That's the whole reason those `*Workspace.tsx` / `LoginForm.tsx` files exist as
separate files. Now the split makes sense instead of looking arbitrary.

---

## 7. `proxy.ts` — middleware, renamed

Next 16 renamed the `middleware.ts` convention to **`proxy.ts`**, exporting a
`proxy()` function instead of `middleware()`. Same edge runtime, same
request/response objects.

[`proxy.ts`](../proxy.ts) sits at the project root (not in `app/`) and runs
before a request is handled:

```ts
export function proxy() {
  return NextResponse.next();
}

export const config = {
  matcher: ["/((?!_next/static|_next/image|favicon.ico).*)"],
};
```

`config.matcher` limits which paths it runs on — here, everything except static
assets.

This file is currently a **deliberate pass-through**, and the comment inside it
explains why: the backend sets its refresh cookie with `Path=/api/auth`, a path
that only exists on the Spring server. The browser therefore never attaches that
cookie to a Next.js request, so a cookie check here would see "signed out" for
*everyone* and redirect in a loop. Auth is enforced in the layout instead — see
[08 Auth](./08-auth.md).

> ▸ **Lesson worth keeping:** middleware can only see cookies the browser
> actually sends it. Cookie `Path` and `Domain` decide that, and a cookie scoped
> to another origin's path is invisible here no matter what you write.

---

## 8. Metadata

Export a `metadata` object from a layout or page — Next renders the tags.

```tsx
export const metadata: Metadata = {
  title: "Stacks — Library console",
  description: "Manage the catalogue, members, lending and returns.",
};
```

⚠️ Only works in **server** components. A `"use client"` page can't export
`metadata`; put it in the layout above instead. That's why this app declares it
once in the root layout.

---

## 9. Fonts

`next/font` self-hosts Google Fonts at build time — no render-blocking request,
no layout shift, no privacy leak to Google.

```tsx
const geist = Geist({ variable: "--font-geist-sans", subsets: ["latin"], weight: ["400","500","600","700"] });
```

`variable:` emits a CSS custom property. The layout puts the generated class on
`<html>`, and [`app/globals.css`](../app/globals.css) maps it into the design
system:

```css
--font-display: var(--font-geist-sans), "Geist", -apple-system, sans-serif;
```

So components reference `var(--font-display)` and never know a font loader was
involved.

---

## 10. Environment variables

| Prefix | Visible to | Use for |
|---|---|---|
| `NEXT_PUBLIC_*` | Browser **and** server | API base URL, feature flags |
| no prefix | Server only | Secrets |

⚠️ `NEXT_PUBLIC_` values are **inlined into the JS bundle at build time**. They
are not secret, and changing one requires a dev-server restart.

This app uses three, all necessarily public
([`.env.local`](../.env.local)): `NEXT_PUBLIC_API_BASE_URL`,
`NEXT_PUBLIC_CURRENCY_SYMBOL`, `NEXT_PUBLIC_DISABLE_AUTH`.

---

## 11. The `@/` import alias

[`tsconfig.json`](../tsconfig.json) maps `@/*` to the project root:

```json
"paths": { "@/*": ["./*"] }
```

So `@/lib/api/client` resolves from anywhere without `../../../`. Use it for
every cross-folder import; reserve relative paths for true siblings
(`./LendWorkspace`).

---

## 12. Quick reference

```tsx
// Routing
app/foo/page.tsx                 → /foo
app/foo/layout.tsx               → wraps /foo and everything under it
app/(group)/foo/page.tsx         → /foo   (group invisible in URL)
app/foo/[id]/page.tsx            → /foo/123
app/foo/[...rest]/page.tsx       → /foo/a/b/c

// Client component
"use client";                    // first line, before imports

// Navigation
import Link from "next/link";
import { useRouter, usePathname, useParams, useSearchParams } from "next/navigation";
import { redirect } from "next/navigation";   // server components

// useSearchParams needs <Suspense> above it
```

**Next:** [02 — Project structure](./02-project-structure.md)
