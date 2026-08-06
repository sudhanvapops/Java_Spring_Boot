import { create } from "zustand";
import type { Member, ReturnResult } from "@/lib/types/domain";

/**
 * uploads/07-state-management.md "Store 3 — returnSelection". Drives
 * /returns. `selected` holds bookIds only — the loan records themselves stay
 * in the TanStack Query cache. Not persisted; cleared on logout and after a
 * successful submit.
 */
interface ReturnSelectionState {
  member: Member | null;
  selected: Set<number>;
  returnDate: Date;
  submitting: boolean;
  result: ReturnResult | null;
  selectMember: (member: Member) => void;
  toggle: (bookId: number) => void;
  selectAll: (bookIds: number[]) => void;
  clearSelection: () => void;
  setReturnDate: (date: Date) => void;
  setSubmitting: (submitting: boolean) => void;
  setResult: (result: ReturnResult | null) => void;
  reset: () => void;
}

export const useReturnSelectionStore = create<ReturnSelectionState>((set, get) => ({
  member: null,
  selected: new Set<number>(),
  returnDate: new Date(),
  submitting: false,
  result: null,
  selectMember: (member) => set({ member, selected: new Set(), result: null }),
  toggle: (bookId) => {
    const next = new Set(get().selected);
    if (next.has(bookId)) next.delete(bookId);
    else next.add(bookId);
    set({ selected: next });
  },
  selectAll: (bookIds) => set({ selected: new Set(bookIds) }),
  clearSelection: () => set({ selected: new Set() }),
  setReturnDate: (returnDate) => set({ returnDate }),
  setSubmitting: (submitting) => set({ submitting }),
  setResult: (result) => set({ result }),
  reset: () => set({ member: null, selected: new Set(), returnDate: new Date(), submitting: false, result: null }),
}));
