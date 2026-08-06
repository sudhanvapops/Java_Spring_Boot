"use client";

import { useEffect, type RefObject } from "react";

/** Fires onOutside for a pointerdown outside the given ref — used to close
 * the account dropdown and the mobile sidebar sheet. The listener callback
 * runs async on a later event, so calling the handler here isn't the
 * synchronous-setState-in-effect pattern the stricter lint rule flags. */
export function useClickOutside(ref: RefObject<HTMLElement | null>, onOutside: () => void, active: boolean) {
  useEffect(() => {
    if (!active) return;
    const handler = (e: PointerEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) onOutside();
    };
    document.addEventListener("pointerdown", handler);
    return () => document.removeEventListener("pointerdown", handler);
  }, [ref, onOutside, active]);
}
