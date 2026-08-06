import { create } from "zustand";
import type { Book, Member, Transaction } from "@/lib/types/domain";

/**
 * uploads/07-state-management.md "Store 2 — lendBasket". Drives /lend,
 * client-only until submit. Not persisted — a stale basket surviving a
 * refresh is a trap, not a convenience. Cleared on successful submit and on
 * logout.
 */
interface LendBasketState {
  member: Member | null;
  items: Book[];
  submitting: boolean;
  result: Transaction | null;
  selectMember: (member: Member) => void;
  clearMember: () => void;
  addBook: (book: Book) => void;
  removeBook: (bookId: number) => void;
  clear: () => void;
  setSubmitting: (submitting: boolean) => void;
  setResult: (result: Transaction | null) => void;
}

export const useLendBasketStore = create<LendBasketState>((set) => ({
  member: null,
  items: [],
  submitting: false,
  result: null,
  selectMember: (member) => set({ member }),
  // Changing member invalidates the basket — allowances/duplicates are per-member.
  clearMember: () => set({ member: null, items: [] }),
  addBook: (book) =>
    set((state) => (state.items.some((b) => b.id === book.id) ? state : { items: [...state.items, book] })),
  removeBook: (bookId) => set((state) => ({ items: state.items.filter((b) => b.id !== bookId) })),
  clear: () => set({ member: null, items: [], submitting: false, result: null }),
  setSubmitting: (submitting) => set({ submitting }),
  setResult: (result) => set({ result }),
}));
