# 05 — State Management

Where each kind of state lives, and how to decide.

## The decision tree

```
Does the server own this data?
├── YES → TanStack Query          books, members, records, settings
│         Never copy it into useState. Read it from the cache.
│
└── NO → Is it needed by more than one component?
    ├── NO  → useState             a dialog's open flag, a local search box
    │
    └── YES → Does it survive a page navigation?
        ├── NO  → useState lifted to the nearest common parent
        └── YES → Zustand store    session, lend basket, toasts
```

⚠️ **The most common mistake:** copying server data into `useState`. The moment
you do, you own the staleness problem forever. Read from the query cache instead
— it's already shared and already fresh.

---

## The four Zustand stores

Zustand is a small store: create it once, read it with a hook, update via
actions. No provider, no reducers, no boilerplate.

| Store | File | Holds | Persisted |
|---|---|---|---|
| `useAuthStore` | [`lib/stores/auth.ts`](../lib/stores/auth.ts) | Session user, access token, status | **No** |
| `useLendBasketStore` | [`lib/stores/lendBasket.ts`](../lib/stores/lendBasket.ts) | In-progress lend | No |
| `useReturnSelectionStore` | [`lib/stores/returnSelection.ts`](../lib/stores/returnSelection.ts) | Selected books to return | No |
| `useUiStore` | [`lib/stores/ui.ts`](../lib/stores/ui.ts) | Sidebar, palette, toasts | `sidebarCollapsed` only |

---

### 1. `useAuthStore` — the session

```ts
export type AuthStatus = "idle" | "loading" | "authenticated" | "unauthenticated";

interface AuthState {
  user: Account | null;
  accessToken: string | null;
  status: AuthStatus;
  error: string | null;
  setSession: (user: Account, accessToken: string) => void;  // all three at once
  clearAuth: () => void;
  …
}
```

Two design decisions worth understanding:

**The token is in memory only.** Never `localStorage`. A token in
`localStorage` is readable by any XSS payload on the page; a variable in a
module closure is not. Cost: a full page refresh loses it — which is fine,
because the httpOnly refresh cookie silently restores the session on mount.

**`status` starts at `"idle"`, not `"unauthenticated"`.** Four states, not a
boolean, so the app can distinguish "we haven't checked yet" from "we checked
and you're signed out". That's what lets the layout show a skeleton instead of
flashing the login screen on every page load.

Usage:

```tsx
// In a component — subscribe to one slice, not the whole store
const user = useAuthStore((s) => s.user);
const status = useAuthStore((s) => s.status);

// Outside React (Axios interceptors)
import { getAuthState } from "@/lib/stores/auth";
const { accessToken } = getAuthState();
```

> ▸ **Always select a slice.** `useAuthStore((s) => s.user)` re-renders only when
> `user` changes. `useAuthStore()` re-renders on *any* store change.

---

### 2. `useLendBasketStore` — the lend workspace

`/lend` is a multi-step workspace, not a form: pick a member, add books, confirm.
That intermediate state has to live somewhere until submit.

```ts
interface LendBasketState {
  member: Member | null;
  items: Book[];
  selectMember: (member: Member) => void;
  clearMember: () => void;          // also empties items — see below
  addBook: (book: Book) => void;    // ignores duplicates
  removeBook: (bookId: number) => void;
  clear: () => void;
  …
}
```

Two behaviours encoded in the store rather than the page:

```ts
// Changing member invalidates the basket — allowances and duplicate rules are per-member
clearMember: () => set({ member: null, items: [] }),

// Adding a duplicate is a no-op, not an error
addBook: (book) => set((state) =>
  state.items.some((b) => b.id === book.id) ? state : { items: [...state.items, book] }),
```

**Not persisted, deliberately.** A half-finished basket restored after a refresh
is a trap: the librarian doesn't remember it, availability may have changed, and
they might lend the wrong thing.

---

### 3. `useReturnSelectionStore` — the returns workspace

Same shape of problem for `/returns`.

```ts
interface ReturnSelectionState {
  member: Member | null;
  selected: Set<number>;      // bookIds only
  returnDate: Date;
  …
}
```

Note `selected` holds **only ids**. The loan records themselves stay in the
TanStack Query cache — storing them here would duplicate server data and risk
going stale. Store the reference, not the copy.

---

### 4. `useUiStore` — chrome and toasts

The only store with persistence, and only for one field:

```ts
export const useUiStore = create<UiState>()(
  persist(
    (set, get) => ({ … }),
    {
      name: "stacks-ui",
      partialize: (state) => ({ sidebarCollapsed: state.sidebarCollapsed }),
    },
  ),
);
```

`partialize` selects what reaches `localStorage`. Persisting `toasts` would
resurrect week-old error messages on next visit; persisting sidebar preference
is genuinely useful.

Toast lifetime encodes a UX rule:

```ts
pushToast: (toast) => {
  const id = crypto.randomUUID();
  set({ toasts: [...get().toasts, { ...toast, id }] });
  if (toast.tone === "success") setTimeout(() => get().dismissToast(id), 4000);
  return id;
},
```

Success toasts auto-dismiss after 4s. **Failure toasts never do** — the person
needs time to actually read what went wrong.

Most code doesn't touch the store directly; it uses the
[`useToast()`](../lib/hooks/useToast.ts) wrapper:

```tsx
const toast = useToast();
toast.success("Book saved.");
toast.errorFrom(err);        // pulls userMessage off a NormalizedApiError
```

---

## Writing a new store

```ts
import { create } from "zustand";

interface CounterState {
  count: number;
  increment: () => void;
  reset: () => void;
}

export const useCounterStore = create<CounterState>((set, get) => ({
  count: 0,
  increment: () => set({ count: get().count + 1 }),
  reset: () => set({ count: 0 }),
}));
```

- `set(partial)` merges at the top level.
- `set((state) => …)` when the next value depends on the previous.
- `get()` reads current state inside an action.
- `useStore.getState()` reads outside React.

To persist:

```ts
import { persist } from "zustand/middleware";

export const useThingStore = create<ThingState>()(
  persist((set) => ({ … }), {
    name: "stacks-thing",
    partialize: (s) => ({ onlyThisField: s.onlyThisField }),
  }),
);
```

---

## Clearing state on logout

Logout must wipe both halves — Zustand session *and* the query cache
([`lib/hooks/useAuth.ts`](../lib/hooks/useAuth.ts)):

```ts
export function useLogout() {
  const clearAuth = useAuthStore((s) => s.clearAuth);
  const qc = useQueryClient();
  return useMutation({
    mutationFn: () => authApi.logout(),
    onSettled: () => {
      clearAuth();
      qc.clear();          // ⚠️ without this the next user sees cached data
    },
  });
}
```

`onSettled`, not `onSuccess` — the local session must be cleared even if the
server call fails, or a user with a dead token could get stuck signed in.

**Next:** [06 — Forms](./06-forms.md)
