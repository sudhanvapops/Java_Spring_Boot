/**
 * uploads/05-auth-spec.md security checklist: the `?next=` redirect target
 * must be validated as an internal path — a single `/` prefix, never `//`
 * (protocol-relative) or an absolute URL — before it's used, or a crafted
 * link could redirect a signed-in user off-site.
 */
export function safeNextPath(next: string | null | undefined, fallback = "/dashboard"): string {
  if (!next) return fallback;
  if (!next.startsWith("/") || next.startsWith("//")) return fallback;
  try {
    // Reject anything that parses as an absolute URL (belt and suspenders
    // against scheme-relative or backslash tricks some browsers normalize).
    const url = new URL(next, "http://internal.invalid");
    if (url.origin !== "http://internal.invalid") return fallback;
  } catch {
    return fallback;
  }
  return next;
}
