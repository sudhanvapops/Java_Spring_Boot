"use client";

import { useSyncExternalStore } from "react";

/** useSyncExternalStore avoids the classic useState+useEffect matchMedia
 * pattern (which trips react-hooks/set-state-in-effect, since the whole
 * point of that pattern is to setState synchronously in an effect on
 * mount) — the initial value is read synchronously in getSnapshot instead. */
export function useMediaQuery(query: string): boolean {
  return useSyncExternalStore(
    (onChange) => {
      const mq = window.matchMedia(query);
      mq.addEventListener("change", onChange);
      return () => mq.removeEventListener("change", onChange);
    },
    () => (typeof window !== "undefined" ? window.matchMedia(query).matches : false),
    () => false,
  );
}
