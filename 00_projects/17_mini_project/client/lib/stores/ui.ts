import { create } from "zustand";
import { persist } from "zustand/middleware";

export type ToastTone = "success" | "danger" | "info";

export interface Toast {
  id: string;
  tone: ToastTone;
  message: string;
  action?: { label: string; onClick: () => void };
}

/**
 * uploads/07-state-management.md "Store 4 — ui". Only sidebarCollapsed is
 * persisted (localStorage); toasts are cleared on logout. Success toasts
 * auto-dismiss after 4s, failure toasts never do — the person needs to
 * actually read what went wrong.
 */
interface UiState {
  sidebarCollapsed: boolean;
  commandPaletteOpen: boolean;
  toasts: Toast[];
  setSidebarCollapsed: (collapsed: boolean) => void;
  toggleSidebar: () => void;
  setCommandPaletteOpen: (open: boolean) => void;
  pushToast: (toast: Omit<Toast, "id">) => string;
  dismissToast: (id: string) => void;
  clearToasts: () => void;
}

export const useUiStore = create<UiState>()(
  persist(
    (set, get) => ({
      sidebarCollapsed: false,
      commandPaletteOpen: false,
      toasts: [],
      setSidebarCollapsed: (sidebarCollapsed) => set({ sidebarCollapsed }),
      toggleSidebar: () => set({ sidebarCollapsed: !get().sidebarCollapsed }),
      setCommandPaletteOpen: (commandPaletteOpen) => set({ commandPaletteOpen }),
      pushToast: (toast) => {
        const id = crypto.randomUUID();
        set({ toasts: [...get().toasts, { ...toast, id }] });
        if (toast.tone === "success") {
          setTimeout(() => get().dismissToast(id), 4000);
        }
        return id;
      },
      dismissToast: (id) => set({ toasts: get().toasts.filter((t) => t.id !== id) }),
      clearToasts: () => set({ toasts: [] }),
    }),
    {
      name: "stacks-ui",
      partialize: (state) => ({ sidebarCollapsed: state.sidebarCollapsed }),
    },
  ),
);
