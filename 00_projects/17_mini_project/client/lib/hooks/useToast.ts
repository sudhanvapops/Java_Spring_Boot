import { useUiStore } from "@/lib/stores/ui";
import { isNormalizedApiError } from "@/lib/api/client";

/** Toasts confirm every write; failure toasts never auto-dismiss
 * (lib/stores/ui.ts). Thin convenience wrapper over pushToast. */
export function useToast() {
  const pushToast = useUiStore((s) => s.pushToast);
  return {
    success: (message: string) => pushToast({ tone: "success", message }),
    info: (message: string) => pushToast({ tone: "info", message }),
    error: (message: string) => pushToast({ tone: "danger", message }),
    /** Extracts userMessage from a NormalizedApiError, or a generic fallback. */
    errorFrom: (err: unknown, fallback = "Something went wrong. Try again.") =>
      pushToast({ tone: "danger", message: isNormalizedApiError(err) ? err.userMessage : fallback }),
  };
}
