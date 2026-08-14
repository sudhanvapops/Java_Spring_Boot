# Stacks — Library Console (Next.js client)

The frontend for `library-management-v2`, a Spring Boot library management API.
A staff console: librarians and admins manage the catalogue, members, lending
and returns. Members of the public can self-register as library patrons.

**New here? Read [`docs/`](./docs/README.md) — it documents this app folder by
folder, function by function, flow by flow, and doubles as a Next.js App Router
refresher.**

---

## Stack

| Piece | Choice | Why |
|---|---|---|
| Framework | Next.js 16.3 (App Router, Turbopack) | File-based routing, layouts, server/client split |
| UI | React 19.2 | — |
| Language | TypeScript (strict) | — |
| Server state | TanStack Query v5 | Caching, refetch, invalidation for API data |
| Client state | Zustand v5 | Auth session, lend basket, return selection, UI |
| HTTP | Axios | Interceptors for auth headers + token refresh |
| Forms | react-hook-form + Zod | Uncontrolled inputs, schema validation |
| Dates | date-fns | Parsing/formatting/overdue maths |
| Animation | Framer Motion | Entrances, toasts, dialogs |
| Styling | CSS custom properties (design tokens) + inline styles, Tailwind v4 available | See [docs/09](./docs/09-styling-and-components.md) |

## Prerequisites

- Node.js 20+
- The backend running at `http://localhost:8080` (see `../library-management-v2`)
- PostgreSQL running (the backend needs it)

## Getting started

```bash
npm install
npm run dev          # http://localhost:3000
```

Start the backend first, or every request 500s/fails:

```bash
cd ../library-management-v2
./mvnw spring-boot:run
```

### Scripts

| Command | Does |
|---|---|
| `npm run dev` | Dev server with hot reload on :3000 |
| `npm run build` | Production build (also runs a full typecheck) |
| `npm start` | Serve the production build |
| `npm run lint` | ESLint |
| `npx tsc --noEmit` | Typecheck only — fastest correctness check |

## Environment

`.env.local`:

```bash
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
NEXT_PUBLIC_CURRENCY_SYMBOL=₹
NEXT_PUBLIC_DISABLE_AUTH=false
```

`NEXT_PUBLIC_` prefix means the value is inlined into the browser bundle — never
put a secret behind it. `NEXT_PUBLIC_DISABLE_AUTH=true` bypasses the login gate
for local UI work; leave it `false` normally.

## Signing in

There is no public staff signup. The backend seeds one admin on first run
against an empty `users` table:

```
email:    admin@library.local
password: Admin@12345
```

Sign in as that admin, then create more staff accounts at **Settings → Add staff**
(`/settings/staff/new`, admin-only).

`/signup` is public but registers a **library member** (a patron record: name,
email, age — no password, cannot sign in), not a staff login account.

## Roles

| Role | Can do |
|---|---|
| `ADMIN` | Everything, including creating staff accounts and writing library settings |
| `LIBRARIAN` | Books, members, lending, returns, records; read settings |
| `MEMBER` | Browse the book catalogue only |

Roles are enforced **server-side** by Spring `@PreAuthorize`. The frontend
mirrors them for UX (hiding links and buttons), which is not security — see
[docs/08](./docs/08-auth.md).

## Project map

```
app/            Routes (App Router). URLs come from folder names.
  (auth)/       Login + public member signup — no app shell
  (app)/        The console — sidebar/topbar shell, auth-gated
components/     Reusable UI, grouped by purpose
lib/
  api/          Axios client + one service module per backend resource
  hooks/        TanStack Query wrappers + small React utilities
  stores/       Zustand client state
  schemas/      Zod form validation
  types/        API DTOs (wire shapes) and domain types (UI shapes)
  utils/        Pure helpers — dates, currency, errors, derive, RBAC
  config/       Navigation + breadcrumb config
docs/           Full documentation — start at docs/README.md
proxy.ts        Next 16's middleware equivalent
```

## Documentation

| Doc | Covers |
|---|---|
| [docs/README.md](./docs/README.md) | Index + suggested reading order |
| [01 Next.js refresher](./docs/01-nextjs-refresher.md) | App Router concepts, taught from this codebase |
| [02 Project structure](./docs/02-project-structure.md) | Every folder, what belongs in it |
| [03 Routing](./docs/03-routing.md) | Route groups, layouts, dynamic routes, every URL |
| [04 Data layer](./docs/04-data-layer.md) | Axios client, services, TanStack Query hooks |
| [05 State](./docs/05-state.md) | Zustand stores + which state goes where |
| [06 Forms](./docs/06-forms.md) | react-hook-form + Zod |
| [07 Types & derive](./docs/07-types-and-derive.md) | DTOs vs domain types |
| [08 Auth & RBAC](./docs/08-auth.md) | Tokens, refresh, route protection, roles |
| [09 Styling & components](./docs/09-styling-and-components.md) | Design tokens + component catalogue |
| [10 Flows](./docs/10-flows.md) | End-to-end walkthroughs |
| [11 Function reference](./docs/11-function-reference.md) | Every exported function |
| [12 Recipes](./docs/12-recipes.md) | "How do I add a page / endpoint / form" |
| [13 Gotchas](./docs/13-gotchas.md) | Real bugs hit here and how to avoid them |
