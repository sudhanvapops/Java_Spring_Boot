# Stacks Client — Documentation

Documentation for the Next.js frontend, written for someone who **can read
Next.js but wants to be able to write it again**. Every concept is explained
against real code in this repository, not toy examples — so when you finish a
page you can open the file it describes and recognise everything in it.

## How to use these docs

Two ways in:

**Relearning Next.js** — read in order. 01 → 02 → 03 gives you the framework
back (routing, layouts, the server/client split). 04 → 07 gives you the
application architecture (data, state, forms, types). 08 → 13 is depth and
reference.

**Working on a task** — jump straight to [12 Recipes](./12-recipes.md). It has
step-by-step "to add X, do Y" walkthroughs that link back to the concept docs
when you need the why.

## Reading order

| # | Doc | What you get | Read when |
|---|---|---|---|
| 01 | [Next.js refresher](./01-nextjs-refresher.md) | App Router, server vs client components, special files, navigation | First. This is the framework refresher. |
| 02 | [Project structure](./02-project-structure.md) | What every folder is for and what belongs in it | Right after 01 |
| 03 | [Routing](./03-routing.md) | Route groups, layouts, dynamic segments, the full URL table | When adding/changing a page |
| 04 | [Data layer](./04-data-layer.md) | Axios client, interceptors, service modules, TanStack Query | When touching anything that talks to the API |
| 05 | [State](./05-state.md) | The four Zustand stores; server vs client state decisions | When you need to store something |
| 06 | [Forms](./06-forms.md) | react-hook-form + Zod, field wiring, error display | When building a form |
| 07 | [Types & derive](./07-types-and-derive.md) | DTOs vs domain types, the derive layer | When adding a field or endpoint |
| 08 | [Auth & RBAC](./08-auth.md) | Tokens, refresh rotation, route protection, roles | When touching auth or permissions |
| 09 | [Styling & components](./09-styling-and-components.md) | Design tokens, component catalogue | When building UI |
| 10 | [Flows](./10-flows.md) | Click-to-database walkthroughs of real features | To see how the layers connect |
| 11 | [Function reference](./11-function-reference.md) | Every exported function, one line each | As a lookup table |
| 12 | [Recipes](./12-recipes.md) | Copy-paste-shaped task walkthroughs | When doing the work |
| 13 | [Gotchas](./13-gotchas.md) | Real bugs hit in this codebase | Before debugging something weird |

## The 60-second architecture

```
Browser
  │
  ├── app/(auth)/…        login, signup          — no shell, public
  └── app/(app)/…         the console            — shell + auth gate
        │
        │  page component ("use client")
        ▼
      lib/hooks/use*.ts           TanStack Query — caching, loading, refetch
        │
        ▼
      lib/api/services/*.ts       one module per backend resource
        │                          also converts DTO → domain shape
        ▼
      lib/api/client.ts           Axios: attaches token, unwraps envelope,
        │                          normalises errors, refreshes on 401
        ▼
      Spring Boot @ :8080
```

Alongside that pipeline:

- **`lib/stores/*`** — Zustand, for state the server doesn't own (session, lend
  basket, UI toggles).
- **`lib/types/*`** — `api.ts` mirrors the wire format exactly; `domain.ts` is
  the friendlier shape the UI uses.
- **`lib/utils/derive.ts`** — the translation between those two.
- **`components/*`** — presentational; they take props and render, and almost
  never fetch.

## Conventions used in these docs

- File paths are relative to `client/`, e.g. `lib/api/client.ts`.
- Links point at real files — click them.
- `▸ Why it's built this way` boxes explain a decision rather than the mechanics.
- `⚠️` marks something that has actually bitten this codebase.
