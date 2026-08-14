import type { AccountRole } from "@/lib/types/api";

/**
 * Mirrors the real server-side @PreAuthorize checks (BACKEND_HANDOFF.md
 * §3.2/§3.6) — this is UX only, the server is the actual enforcement. A
 * MEMBER account today can only browse the book catalogue: every other
 * controller is @StaffOnly or stricter, and there's no /my/* endpoint
 * scoped to a member's own borrows.
 */
export function isStaffRole(role: AccountRole | null | undefined): boolean {
  return role === "ADMIN" || role === "LIBRARIAN";
}

/** Paths a MEMBER session may render. Book mutations (new/edit) are
 * @StaffOnly, so only the list and a read-only detail view are allowed. */
export function isAllowedForMember(pathname: string): boolean {
  if (pathname === "/settings/account") return true;
  const segments = pathname.split("/").filter(Boolean);
  if (segments[0] !== "books") return false;
  if (segments.length === 1) return true; // /books
  return segments.length === 2 && segments[1] !== "new"; // /books/{id}, not /books/new
}

export const MEMBER_HOME = "/books";
export const STAFF_HOME = "/dashboard";

export function homeFor(role: AccountRole | null | undefined): string {
  return isStaffRole(role) ? STAFF_HOME : MEMBER_HOME;
}
