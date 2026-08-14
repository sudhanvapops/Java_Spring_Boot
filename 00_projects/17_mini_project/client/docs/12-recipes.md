# 12 — Recipes

Step-by-step walkthroughs for the tasks you'll actually do. These are the docs
to use while writing code.

---

## Recipe 1 — Add a new page

Say `/reports`.

**1. Create the route file**

```
app/(app)/reports/page.tsx
```

`(app)` gives it the shell and the auth gate. `(auth)` would give it the bare
centred layout instead.

**2. Write the page**

```tsx
"use client";

import { PageHeader } from "@/components/navigation/PageHeader";

export default function ReportsPage() {
  return (
    <div>
      <PageHeader title="Reports" subtitle="What the library has been up to." />
      {/* … */}
    </div>
  );
}
```

`"use client"` because virtually every page here uses hooks. Start every page
with a `PageHeader`.

**3. Add it to the sidebar** — [`lib/config/nav.ts`](../lib/config/nav.ts):

```ts
{
  label: "History",
  items: [
    { label: "Transactions", href: "/transactions", icon: "history" },
    { label: "Records", href: "/records", icon: "list" },
    { label: "Reports", href: "/reports", icon: "chart-bar" },   // new
  ],
},
```

⚠️ The icon must exist in `GLYPHS` in
[`components/core/Icon.tsx`](../components/core/Icon.tsx). If it doesn't, copy
the inner markup from `lucide-static/icons/<name>.svg` into the map — otherwise
you get a console warning and an empty box.

**4. Add breadcrumbs** — same file:

```ts
const CRUMBS: Record<string, string[]> = {
  …,
  "/reports": ["Console", "Reports"],
};
```

**5. Highlight the right nav item for sub-routes** — only if you add
`/reports/something`:

```ts
export function sectionFor(pathname: string): string {
  …
  if (pathname.startsWith("/reports")) return "/reports";
  return pathname;
}
```

**6. If the page is staff-only**, put it in the staff nav group only, and
consider whether `isAllowedForMember()` in
[`lib/utils/rbac.ts`](../lib/utils/rbac.ts) needs updating (it defaults to
denying anything that isn't `/books`).

---

## Recipe 2 — Call a new backend endpoint

Say `GET /api/report/monthly`.

**1. Types** — [`lib/types/api.ts`](../lib/types/api.ts):

```ts
export interface MonthlyReportResponseDto {
  month: string;
  booksLent: number;
  booksReturned: number;
  finesCollected: number;
}
```

**2. Domain type** (only if the UI wants a different shape) —
[`lib/types/domain.ts`](../lib/types/domain.ts):

```ts
export interface MonthlyReport {
  month: Date;
  booksLent: number;
  booksReturned: number;
  finesCollected: number;
}
```

**3. Derive function** — [`lib/utils/derive.ts`](../lib/utils/derive.ts):

```ts
export function deriveMonthlyReport(dto: MonthlyReportResponseDto): MonthlyReport {
  return { ...dto, month: parseApiDate(dto.month) };
}
```

**4. Service** — `lib/api/services/reports.ts`:

```ts
import { apiClient, isNormalizedApiError } from "@/lib/api/client";
import { deriveMonthlyReport } from "@/lib/utils/derive";
import type { MonthlyReportResponseDto } from "@/lib/types/api";
import type { MonthlyReport } from "@/lib/types/domain";

export async function listMonthlyReports(): Promise<MonthlyReport[]> {
  try {
    const res = await apiClient.get<MonthlyReportResponseDto[]>("/api/report/monthly");
    return res.data.map(deriveMonthlyReport);
  } catch (err) {
    if (isNormalizedApiError(err) && err.isEmptyState) return [];
    throw err;
  }
}
```

**5. Hook** — `lib/hooks/useReports.ts`:

```ts
import { useQuery } from "@tanstack/react-query";
import * as reportsApi from "@/lib/api/services/reports";

export function useMonthlyReports() {
  return useQuery({
    queryKey: ["reports", "monthly"],
    queryFn: reportsApi.listMonthlyReports,
    staleTime: 5 * 60_000,
  });
}
```

**6. Use it**

```tsx
const { data, isLoading, isError } = useMonthlyReports();
```

⚠️ If you reshape `data` after the query (`.map`, `.filter`), wrap it in
`useMemo` keyed on `query.data`. See [13 Gotchas](./13-gotchas.md).

---

## Recipe 3 — Add a form

**1. Schema** — `lib/schemas/report.ts`:

```ts
import { z } from "zod";

export const reportFilterSchema = z.object({
  from: z.string().min(1, "Pick a start date."),
  to: z.string().min(1, "Pick an end date."),
  minFine: z.number({ error: "Enter an amount." }).min(0, "Enter 0 or more."),
});

export type ReportFilterValues = z.infer<typeof reportFilterSchema>;
```

**2. Form component**

```tsx
"use client";

const { register, handleSubmit, formState: { errors, isSubmitting } } =
  useForm<ReportFilterValues>({
    resolver: zodResolver(reportFilterSchema),
    defaultValues: { from: "", to: "", minFine: 0 },
  });

const onSubmit = handleSubmit(async (values) => {
  try {
    await runReport.mutateAsync(values);
    toast.success("Report ready.");
  } catch (err) {
    toast.errorFrom(err);
  }
});

<form onSubmit={onSubmit} style={{ display: "grid", gap: "var(--space-lg)" }}>
  <div>
    <Label htmlFor="from" required>From</Label>
    <Input id="from" type="date" invalid={!!errors.from} error={errors.from?.message} {...register("from")} />
  </div>
  <div>
    <Label htmlFor="minFine" required>Minimum fine</Label>
    <Input id="minFine" type="number" prefix="₹"
      invalid={!!errors.minFine} error={errors.minFine?.message}
      {...register("minFine", { valueAsNumber: true })} />
  </div>
  <Button type="submit" loading={isSubmitting || runReport.isPending} loadingLabel="Running…">
    Run report
  </Button>
</form>
```

Checklist: `defaultValues` always; `valueAsNumber` on numbers; `register()` last
in the spread; `id` matching `htmlFor`. Full detail in [06 Forms](./06-forms.md).

---

## Recipe 4 — Add a mutation with cache invalidation

```ts
export function useCreateReport() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (values: ReportFilterValues) => reportsApi.createReport(values),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["reports"] });   // prefix — invalidates all report queries
    },
  });
}
```

Invalidation keys are **prefix matches**: `["reports"]` also invalidates
`["reports","monthly"]`.

Ask "what else did this change on the server?" A lend changes books, records,
transactions *and* members — invalidate all four
([`useTransactions.ts`](../lib/hooks/useTransactions.ts)).

---

## Recipe 5 — Add a table

```tsx
const columns: DataTableColumn<MonthlyReport>[] = [
  { key: "month", header: "Month", sortable: true, render: (r) => fmt(r.month) },
  { key: "booksLent", header: "Lent", width: "100px", sortable: true },
  { key: "finesCollected", header: "Fines", width: "120px", render: (r) => formatAmount(r.finesCollected) },
];

const state = isError ? "error" : isLoading ? "loading" : rows.length ? "loaded" : "empty";

<DataTable
  state={state}
  rows={rows}
  columns={columns}
  getRowKey={(r) => r.month.toISOString()}
  sort={sort}
  onSortChange={setSort}
  onRowClick={(r) => router.push(`/reports/${r.month}`)}
  empty={<EmptyState icon="inbox" headline="No reports yet" body="They appear once books start moving." />}
  error={<EmptyState icon="server-off" headline="Can't load reports." body="Check the API is running." actionLabel="Try again" onAction={() => refetch()} />}
/>
```

The `state` prop drives all four rendering modes — don't branch by hand.

---

## Recipe 6 — Add a confirmation dialog

```tsx
const [confirmOpen, setConfirmOpen] = useState(false);

const handleDelete = async () => {
  setConfirmOpen(false);
  try {
    await deleteReport.mutateAsync(id);
    toast.success("Report deleted.");
  } catch (err) {
    toast.errorFrom(err);
  }
};

<Button variant="danger" onClick={() => setConfirmOpen(true)}>Delete report</Button>

<Dialog
  open={confirmOpen}
  title="Delete this report?"
  confirmLabel="Delete report"
  destructive
  onCancel={() => setConfirmOpen(false)}
  onConfirm={handleDelete}
>
  This can't be undone. The underlying records aren't affected.
</Dialog>
```

Title asks a question; `confirmLabel` names the action ("Delete report", never
"OK"); the body says what actually happens.

---

## Recipe 7 — Gate something by role

**Hide a button:**

```tsx
import { isStaffRole } from "@/lib/utils/rbac";
const isStaff = isStaffRole(useAuthStore((s) => s.user?.role));

<PageHeader action={isStaff ? <Button …>Add</Button> : undefined} />
```

**Skip a query that would 403:**

```tsx
const { data } = useMembersRaw({ enabled: isStaff });
```

**Guard a whole page** (direct URL entry):

```tsx
const role = useAuthStore((s) => s.user?.role);

if (role !== "ADMIN") {
  return (
    <EmptyState
      icon="lock"
      headline="You do not have permission to access this resource."
      body="Only admins can do this."
      actionLabel="Back to settings"
      onAction={() => router.push("/settings/library")}
    />
  );
}
```

**Hide it from the sidebar** — [`navGroupsFor()`](../lib/config/nav.ts).

⚠️ All four are UX. The server enforces the real rule. Never rely on hiding a
button as security.

---

## Recipe 8 — Add an icon

Icons are vendored Lucide path data, not a package.

1. Find the SVG at `lucide-static/icons/<name>.svg` (or lucide.dev).
2. Copy the **inner** markup (`<path …/>`, `<circle …/>` — not the `<svg>`).
3. Add to `GLYPHS` in [`components/core/Icon.tsx`](../components/core/Icon.tsx):

```ts
"chart-bar": '<path d="M3 3v18h18"/><rect x="7" y="10" width="3" height="8"/><rect x="14" y="6" width="3" height="12"/>',
```

4. Use it: `<Icon name="chart-bar" size={16} />`

---

## Recipe 9 — Add a new error code

When the backend adds one:

1. **`lib/types/api.ts`** — add it to the `ErrorCode` union.
2. **`lib/utils/errors.ts`** — add a definition:

```ts
REPORT_GENERATION_FAILED: {
  message: "That report couldn't be generated. Try a shorter date range.",
  treatment: "toast",
},
```

Treatments: `inline` (a field), `toast` (transient), `banner` (persistent
context), `empty` (nothing here), `blocking` (can't continue).

3. Handle it specially only if it needs field-level placement:

```ts
catch (err) {
  if (isNormalizedApiError(err) && err.errorCode === "REPORT_GENERATION_FAILED") return; // inline
  toast.errorFrom(err);
}
```

---

## Recipe 10 — Add a Zustand store

```ts
// lib/stores/reportFilters.ts
import { create } from "zustand";

interface ReportFiltersState {
  from: string;
  to: string;
  setRange: (from: string, to: string) => void;
  reset: () => void;
}

export const useReportFiltersStore = create<ReportFiltersState>((set) => ({
  from: "",
  to: "",
  setRange: (from, to) => set({ from, to }),
  reset: () => set({ from: "", to: "" }),
}));
```

Use it — **always select a slice**:

```tsx
const from = useReportFiltersStore((s) => s.from);
const setRange = useReportFiltersStore((s) => s.setRange);
```

Before adding one, re-read the decision tree in [05 State](./05-state.md). If
the server owns the data, it belongs in TanStack Query, not here.

---

## Recipe 11 — Read a query/search param

Any component calling `useSearchParams()` **must** sit under `<Suspense>`.

```tsx
// app/(app)/reports/page.tsx  — server component
import { Suspense } from "react";
import { ReportsView } from "./ReportsView";

export default function ReportsPage() {
  return (
    <Suspense>
      <ReportsView />
    </Suspense>
  );
}
```

```tsx
// app/(app)/reports/ReportsView.tsx  — client component
"use client";
import { useSearchParams } from "next/navigation";

export function ReportsView() {
  const searchParams = useSearchParams();
  const month = searchParams.get("month");
  …
}
```

Skip the boundary and `next build` fails.

⚠️ If the param is a redirect target, run it through
[`safeNextPath()`](../lib/utils/url.ts) first.

---

## Recipe 12 — Debug an API call

1. **Console** — dev requests log as `[api] -> GET /api/book (a1b2c3d4)`. No log
   means your code never fired the request (often `enabled: false`).
2. **Network tab** — check `Authorization: Bearer …` is present. Missing means
   no session.
3. **Status:**
   - `401` → token expired; the interceptor should refresh and retry. Repeated
     401s mean refresh is failing.
   - `403` → wrong role. **Not** a login problem.
   - `404` → may be an empty list. Check `errorCode` for a `NO_` prefix.
   - `500` → backend. Read the Spring console.
   - CORS error → the backend's allow-list is missing your origin **or header**
     (see [13 Gotchas](./13-gotchas.md)).
4. **Try it without the frontend:**

```bash
curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@library.local","password":"Admin@12345"}'
```

If curl works and the browser doesn't, it's CORS, cookies, or your request code
— not the endpoint.

---

## Recipe 13 — Before you commit

```bash
npx tsc --noEmit     # fastest real check
npm run lint
npm run build        # catches Suspense boundaries + prerender errors
```

`build` catches a class of bug the dev server tolerates — especially missing
`<Suspense>` around `useSearchParams()`.

**Next:** [13 — Gotchas](./13-gotchas.md)
