# 02 — Project Structure

What every folder is for, what belongs in it, and — just as usefully — what
does **not** belong in it.

## The whole tree

```
client/
├── app/                      Routes. Folder names become URLs.
│   ├── layout.tsx            Root layout: <html>, fonts, providers, ToastHost
│   ├── page.tsx              "/" → redirects to /dashboard
│   ├── error.tsx             Root error boundary
│   ├── not-found.tsx         Root 404
│   ├── globals.css           Tailwind import + token imports + theme mapping
│   ├── tokens/               Design tokens as CSS custom properties
│   ├── (auth)/               Public routes — centred card, no shell
│   │   ├── layout.tsx
│   │   ├── login/
│   │   └── signup/
│   └── (app)/                Console routes — auth-gated, full shell
│       ├── layout.tsx        Auth gate + role gate + <AppShell>
│       ├── error.tsx
│       ├── [...notfound]/
│       ├── dashboard/  books/  members/  lend/  returns/
│       ├── records/  transactions/  settings/
│
├── components/               Reusable UI. Props in, markup out.
│   ├── core/                 Icon, Motion primitives
│   ├── forms/                Button, Input, Select, Checkbox, PasswordField…
│   ├── data/                 DataTable, Badge, StatusBadge, Skeleton…
│   ├── feedback/             Toast, Dialog, Banner, EmptyState, ErrorPanel
│   ├── navigation/           Sidebar, Topbar, Tabs, DropdownMenu, CommandPalette
│   ├── panels/               Panel, StatCard, SettingCard, AuthCard
│   ├── circulation/          Domain-specific: BookRow, MemberRow, BasketPanel…
│   ├── shell/                AppShell, Breadcrumbs
│   └── providers/            AppProviders (TanStack Query client)
│
├── lib/                      Everything that isn't a React component
│   ├── api/
│   │   ├── client.ts         Axios instances + interceptors
│   │   └── services/         One module per backend resource
│   ├── hooks/                TanStack Query wrappers + small utilities
│   ├── stores/               Zustand client state
│   ├── schemas/              Zod validation schemas
│   ├── types/                api.ts (wire) + domain.ts (UI)
│   ├── utils/                Pure functions: dates, currency, errors, derive, rbac
│   └── config/               nav.ts — sidebar + breadcrumb config
│
├── docs/                     You are here
├── proxy.ts                  Next 16 middleware equivalent
├── next.config.ts            Next config (currently empty defaults)
├── tsconfig.json             TS config + the @/* path alias
├── eslint.config.mjs         Flat ESLint config
├── postcss.config.mjs        Tailwind v4 via PostCSS
└── .env.local                Environment (not committed)
```

---

## `app/` — routes only

**Belongs here:** `page.tsx`, `layout.tsx`, `error.tsx`, and page-specific
components that only that route uses (`LendWorkspace.tsx` lives beside
`lend/page.tsx` because nothing else renders it).

**Does not belong here:** anything reusable. The moment a second route wants it,
move it to `components/`.

### `app/tokens/`

Eight CSS files defining the visual language as custom properties: `colors.css`,
`typography.css`, `spacing.css`, `radius.css`, `elevation.css`, `motion.css`,
`fonts.css`, `base.css`. Imported by `globals.css`. See
[09 Styling](./09-styling-and-components.md).

---

## `components/` — presentational UI

Grouped by **what the component is for**, not by which page uses it.

| Folder | Contains | Rule of thumb |
|---|---|---|
| `core/` | `Icon`, `Motion` | Primitives everything else builds on |
| `forms/` | `Button`, `Input`, `Label`, `Select`, `Checkbox`, `IconButton`, `PasswordField`, `StrengthMeter` | Anything you type into or click to submit |
| `data/` | `DataTable`, `Badge`, `StatusBadge`, `Skeleton`, `AvailabilityBar`, `FineDisplay` | Displays values |
| `feedback/` | `Toast`, `ToastHost`, `Dialog`, `Banner`, `EmptyState`, `ErrorPanel` | Tells the user what happened |
| `navigation/` | `Sidebar`, `Topbar`, `Tabs`, `PageHeader`, `DropdownMenu`, `CommandPalette` | Moving around |
| `panels/` | `Panel`, `StatCard`, `SettingCard`, `AuthCard` | Containers/cards |
| `circulation/` | `BookRow`, `MemberRow`, `BasketPanel`, `SearchCombobox`, `UnreturnedList` | Knows about books/members — domain-aware |
| `shell/` | `AppShell`, `Breadcrumbs` | The console frame |
| `providers/` | `AppProviders` | Context providers |

> ▸ **The one architectural rule:** components take props and render. They don't
> call the API. The exception is `AppShell`, which needs live data (the
> due-today badge count, command-palette search results) and is a shell rather
> than a leaf. Everything else receives data from the page.

Every component here is styled with inline `style={{}}` referencing CSS
variables — see [09](./09-styling-and-components.md) for why.

---

## `lib/` — the non-React half

### `lib/api/`

`client.ts` exports two configured Axios instances and the shared refresh
routine. `services/` has one module per backend resource, each exporting plain
async functions. **Services are the only place a URL string appears.**

```
services/auth.ts          register, registerStaff, login, refresh, logout
services/books.ts         listBooks, getBook, createBook, updateBook, …
services/members.ts       listMembers, getMember, createMember, …
services/records.ts       listUnreturnedAll, listDueToday, returnBooks, …
services/transactions.ts  listAllTransactions, borrowBooks, …
services/settings.ts      listSettings, createSetting, updateSetting, …
```

Full detail: [04 Data layer](./04-data-layer.md).

### `lib/hooks/`

Two kinds of file:

- **Data hooks** (`useBooks`, `useMembers`, `useRecords`, `useTransactions`,
  `useSettings`, `useAuth`) — wrap services in TanStack Query, own the cache
  keys and invalidation.
- **Utility hooks** (`useMediaQuery`, `useClickOutside`, `useToast`) — small
  React helpers.

Pages import hooks, never services directly.

### `lib/stores/`

Four Zustand stores. See [05 State](./05-state.md).

| Store | Holds | Persisted? |
|---|---|---|
| `auth.ts` | Session user, access token, status | No — deliberately |
| `lendBasket.ts` | The in-progress lend on `/lend` | No |
| `returnSelection.ts` | Selected books on `/returns` | No |
| `ui.ts` | Sidebar collapsed, command palette, toasts | Only `sidebarCollapsed` |

### `lib/schemas/`

Zod schemas, one file per form family: `auth.ts`, `book.ts`, `member.ts`,
`settings.ts`. Each exports the schema and its inferred TypeScript type. See
[06 Forms](./06-forms.md).

### `lib/types/`

| File | Contains |
|---|---|
| `api.ts` | Wire shapes exactly as the backend sends them — `*Dto` names, `ErrorCode` union, the response envelope |
| `domain.ts` | The shapes the UI actually wants — `Book`, `Member`, `LoanRecord`, `Account` |

Why both: [07 Types & derive](./07-types-and-derive.md).

### `lib/utils/`

Pure, testable, no React.

| File | Purpose |
|---|---|
| `derive.ts` | DTO → domain conversion and computed fields |
| `date.ts` | Parse/format, overdue and due-today maths |
| `currency.ts` | Fine formatting (`"no fine"` vs `"₹40.00"`) |
| `errors.ts` | `errorCode` → user-facing message + treatment |
| `rbac.ts` | Role predicates and per-role landing pages |
| `password.ts` | Advisory strength meter checks |
| `url.ts` | `safeNextPath()` — validates `?next=` redirect targets |

### `lib/config/`

`nav.ts` — the sidebar structure per role, the static breadcrumb map, and
`sectionFor()` which decides that `/books/7/edit` highlights **Books**.

---

## Where does my new code go?

| I'm writing… | Put it in |
|---|---|
| A new screen | `app/(app)/<name>/page.tsx` |
| A button/input/card used twice | `components/<category>/` |
| A call to a new backend endpoint | `lib/api/services/<resource>.ts` |
| Caching/loading around that call | `lib/hooks/use<Resource>.ts` |
| Form validation rules | `lib/schemas/<name>.ts` |
| A pure calculation | `lib/utils/<name>.ts` |
| Cross-page client state | `lib/stores/<name>.ts` |
| A backend response shape | `lib/types/api.ts` |
| A UI-friendly shape | `lib/types/domain.ts` + a `derive*` function |

**Next:** [03 — Routing](./03-routing.md)
