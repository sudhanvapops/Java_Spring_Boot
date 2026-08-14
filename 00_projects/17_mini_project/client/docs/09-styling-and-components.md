# 09 — Styling & Component Catalogue

## The approach

This app styles with **CSS custom properties (design tokens) applied through
inline `style={{}}`**, not utility classes.

```tsx
<div style={{
  padding: "var(--space-md)",
  background: "var(--surface-1)",
  border: "1px solid var(--hairline)",
  borderRadius: "var(--radius-card)",
}}>
```

Tailwind v4 **is** installed and wired up ([`postcss.config.mjs`](../postcss.config.mjs),
`@import "tailwindcss"` in [`globals.css`](../app/globals.css)), and the tokens
are mapped into its theme so `bg-canvas` / `p-md` / `rounded-xl` resolve to the
same variables. New layout code may use utilities. But the existing components
are token-and-inline-style throughout — **match the file you're editing** rather
than mixing both in one component.

> ▸ **Why inline styles here.** The components were ported from a reference
> design system where every value came from a token. Inline styles make the
> token reference explicit and impossible to drift; there's no second file to
> keep in sync, and dynamic values (`borderColor` from an `invalid` prop)
> compose naturally.

---

## Design tokens

Defined in [`app/tokens/`](../app/tokens/), imported by
[`app/globals.css`](../app/globals.css). Dark-first — there is no light mode by
design.

### Colour — [`tokens/colors.css`](../app/tokens/colors.css)

| Group | Tokens | Use |
|---|---|---|
| Accent | `--primary`, `--primary-hover`, `--primary-focus` | Interaction only, **never decoration** |
| Canvas | `--canvas` | Page background |
| Surfaces | `--surface-1` … `--surface-4` | Layered panels; higher = closer to the viewer |
| Hairlines | `--hairline`, `--hairline-strong`, `--hairline-tertiary` | Borders/dividers |
| Ink | `--ink`, `--ink-muted`, `--ink-subtle`, `--ink-tertiary` | Text, decreasing emphasis |
| Semantic | `--success`, `--warning`, `--danger`, `--info` | **Status only** |
| Tints | `--success-tint`, `--warning-tint`, `--danger-tint`, `--primary-tint` | Low-weight status backgrounds |

The discipline that keeps a dark UI clean: accent means "you can interact with
this", semantic colours mean "this is a state". Neither is decorative.

### Spacing — [`tokens/spacing.css`](../app/tokens/spacing.css)

```
--space-xxs 4    --space-xs 8     --space-sm 12    --space-md 16
--space-lg  24   --space-xl 32    --space-xxl 48   --space-section 96
```

Nothing outside the scale. Layout constants live here too:
`--sidebar-width: 240px`, `--topbar-height: 56px`, `--auth-card-width: 420px`,
`--form-max-width: 560px`, `--touch-min: 44px`.

### Radius — [`tokens/radius.css`](../app/tokens/radius.css)

`--radius-xs` 4 → `--radius-xxl` 24, plus `--radius-pill`. Use the **semantic
aliases**, not the raw sizes:

```
--radius-control  (inputs, buttons)     --radius-card   (cards)
--radius-panel    (panels)              --radius-badge  (badges)
```

### Typography, elevation, motion

`--type-display-md`, `--type-headline`, `--type-body`, `--type-body-sm`,
`--type-caption`, `--type-mono`, `--type-eyebrow` — these are **`font` shorthand**
values, so they set size, weight and line-height together:

```tsx
<span style={{ font: "var(--type-body-sm)", color: "var(--ink-subtle)" }}>
```

Also `--shadow-popover`, `--focus-ring`, `--duration-fast/medium`,
`--ease-out`, `--transition-hover`.

---

## Component catalogue

### `core/`

| Component | Props | Notes |
|---|---|---|
| [`Icon`](../components/core/Icon.tsx) | `name`, `size=16`, `strokeWidth=1.5`, `color`, `label` | Lucide paths vendored into a `GLYPHS` map — no icon package. Unknown name warns in console. Add a glyph by pasting the SVG inner markup into `GLYPHS`. |
| [`Motion`](../components/core/Motion.tsx) | — | Framer Motion wrappers: `MotionDiv`, `Presence`, `EASE`, `RISE`, `STAGGER`, `stagger()`, `usePrefersReducedMotion()` |

### `forms/`

| Component | Key props |
|---|---|
| [`Button`](../components/forms/Button.tsx) | `variant` (primary/secondary/tertiary/danger), `size`, `loading`, `loadingLabel`, `iconLeft`, `fullWidth` |
| [`IconButton`](../components/forms/IconButton.tsx) | `icon`, `label` (required — it's the accessible name), `tone`, `size` |
| [`Input`](../components/forms/Input.tsx) | `invalid`, `error`, `helper`, `iconLeft`, `prefix`, `unit`, `width`, `size` |
| [`PasswordField`](../components/forms/PasswordField.tsx) | Same as Input + show/hide toggle |
| [`Select`](../components/forms/Select.tsx) | `options: {value,label}[]`, `value`, `onChange`, `width` |
| [`Checkbox`](../components/forms/Checkbox.tsx) | `checked`, `onChange`, `label`, `description`, `indeterminate` |
| [`Label`](../components/forms/Label.tsx) | `htmlFor`, `required`, `hint` |
| [`StrengthMeter`](../components/forms/StrengthMeter.tsx) | `score` 0–3, `requirements[]` |

### `data/`

| Component | Purpose |
|---|---|
| [`DataTable<T>`](../components/data/DataTable.tsx) | Generic table: `columns`, `rows`, `getRowKey`, `sort`, `onSortChange`, `onRowClick`, `rowActions`, `rowTone`, and `state` = `loading \| error \| empty \| loaded` with `empty`/`error` slots |
| [`Badge`](../components/data/Badge.tsx) | Small count/label pill, `tone` |
| [`StatusBadge`](../components/data/StatusBadge.tsx) | Domain statuses: active, inactive, out, returned, due-today, overdue, librarian, admin, member |
| [`Skeleton`](../components/data/Skeleton.tsx) | Loading placeholder; `SkeletonRows` for tables |
| [`AvailabilityBar`](../components/data/AvailabilityBar.tsx) | "3 of 5" plus a proportional bar |
| [`FineDisplay`](../components/data/FineDisplay.tsx) | Money with overdue context; `estimate` mode |

`DataTable`'s `state` prop is the pattern worth copying — one prop drives
loading/error/empty/loaded so pages never hand-roll that branching:

```tsx
const state = isError ? "error" : isLoading ? "loading" : rows.length ? "loaded" : "empty";
<DataTable state={state} rows={rows} columns={columns} empty={<EmptyState … />} error={<EmptyState … />} />
```

### `feedback/`

| Component | Purpose |
|---|---|
| [`Toast`](../components/feedback/Toast.tsx) / [`ToastHost`](../components/feedback/ToastHost.tsx) | Transient confirmations. `ToastHost` is mounted once in the root layout; push via `useToast()` |
| [`Dialog`](../components/feedback/Dialog.tsx) | Confirm/cancel modal. `destructive` styles the confirm red |
| [`Banner`](../components/feedback/Banner.tsx) | Inline contextual message with optional action; `tone` |
| [`EmptyState`](../components/feedback/EmptyState.tsx) | "Nothing here" with icon, headline, body, action |
| [`ErrorPanel`](../components/feedback/ErrorPanel.tsx) | "Something broke" with a retry and optional reference id |

Empty ≠ error. `EmptyState` means "nothing to show"; `ErrorPanel` means "we
failed". Using the wrong one makes a working empty list look broken.

### `navigation/`

| Component | Purpose |
|---|---|
| [`Sidebar`](../components/navigation/Sidebar.tsx) | Grouped nav, `collapsed` rail mode, account footer, count badges |
| [`Topbar`](../components/navigation/Topbar.tsx) | Breadcrumb + search trigger + right slot |
| [`PageHeader`](../components/navigation/PageHeader.tsx) | Title, subtitle, `back`, `action`, `meta` — every page starts with one |
| [`Tabs`](../components/navigation/Tabs.tsx) | Filter/segment switcher |
| [`DropdownMenu`](../components/navigation/DropdownMenu.tsx) | Menu with `separator` and `tone: "danger"` items |
| [`CommandPalette`](../components/navigation/CommandPalette.tsx) | ⌘K search over books, members and screens |

### `panels/`

| Component | Purpose |
|---|---|
| [`Panel`](../components/panels/Panel.tsx) | Titled container. `step` renders a numbered badge (used by `/lend`), `padded={false}` for flush rows, `footer` slot |
| [`StatCard`](../components/panels/StatCard.tsx) | Dashboard metric; `animate` counts up; `tone` |
| [`SettingCard`](../components/panels/SettingCard.tsx) | One library rule with inline save |
| [`AuthCard`](../components/panels/AuthCard.tsx) | Login/signup card. Also exports `BackgroundBeams` |

### `circulation/` — domain-aware

| Component | Purpose |
|---|---|
| [`BookRow`](../components/circulation/BookRow.tsx) / [`MemberRow`](../components/circulation/MemberRow.tsx) | One entity as a list row, with `meta` and `action` slots |
| [`SearchCombobox<T>`](../components/circulation/SearchCombobox.tsx) | Generic search+select base |
| [`BookSearchCombobox`](../components/circulation/BookSearchCombobox.tsx) | Book picker; each option can carry a `reason` explaining why it's disabled |
| [`MemberSearchCombobox`](../components/circulation/MemberSearchCombobox.tsx) | Member picker; `MemberSummary` shows the chosen one |
| [`BasketPanel`](../components/circulation/BasketPanel.tsx) | The lend basket with due-date preview |
| [`UnreturnedList`](../components/circulation/UnreturnedList.tsx) | Selectable list of books out |

`BookSearchCombobox`'s `reason` is a nice UX detail: instead of silently
disabling a book, it says *why* — "None available", "Priya already has this
book", "That's over Priya's limit of 5 books."

---

## Animation

Framer Motion, wrapped in [`components/core/Motion.tsx`](../components/core/Motion.tsx)
so timings stay consistent:

```tsx
import { MotionDiv, EASE, RISE, STAGGER, usePrefersReducedMotion } from "@/components/core/Motion";

<MotionDiv initial={RISE.hidden} animate={RISE.visible} transition={EASE.entrance}>
```

| Export | Purpose |
|---|---|
| `EASE` | `fast`, `medium`, `entrance` transition presets |
| `RISE` | Standard fade-up `hidden`/`visible` variants |
| `STAGGER` / `stagger(step)` | Stagger children |
| `Presence` | `AnimatePresence` wrapper for exit animations |
| `usePrefersReducedMotion()` | Respect the OS setting |

Always honour reduced motion — the dashboard does:

```tsx
const reduced = usePrefersReducedMotion();
<motion.div initial={reduced ? undefined : "hidden"} animate={reduced ? undefined : "visible"} …>
```

---

## Responsive

No CSS breakpoints in components — [`useMediaQuery`](../lib/hooks/useMediaQuery.ts)
returns a boolean and layout branches in JS:

```tsx
const isNarrow = useMediaQuery("(max-width:767px)");
const columns = isNarrow ? "1fr" : "1fr 1fr";
```

Built on `useSyncExternalStore`, so the first render already has the right value
— no `useState` + `useEffect` flash.

Breakpoints used: `767px` (mobile), `1023px` (tablet — collapses the sidebar to
a rail).

---

## Content rules worth keeping

Encoded in the components, and they're good habits generally:

- **Zero is a word when zero is good news.** `formatFine(0)` → `"no fine"`, not
  `"₹0.00"` ([`lib/utils/currency.ts`](../lib/utils/currency.ts)).
- **Use real names.** "Priya still has 2 books out", not "This member has 2
  books out." Error templates take a `{name}` placeholder for exactly this.
- **Say what to do next.** "Check that the API is running and try again", not
  "Request failed".
- **Never show a raw error code** to a user. `formatErrorMessage()` maps codes
  to sentences.

**Next:** [10 — Flows](./10-flows.md)
