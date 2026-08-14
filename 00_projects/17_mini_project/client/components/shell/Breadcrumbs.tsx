"use client";

import { useMemo } from "react";
import type { Crumb } from "@/components/navigation/Topbar";
import { staticCrumbsFor } from "@/lib/config/nav";
import { useBook } from "@/lib/hooks/useBooks";
import { useMemberRaw } from "@/lib/hooks/useMembers";
import { useAuthStore } from "@/lib/stores/auth";
import { homeFor } from "@/lib/utils/rbac";

const BOOK_RE = /^\/books\/(\d+)$/;
const BOOK_EDIT_RE = /^\/books\/(\d+)\/edit$/;
const MEMBER_RE = /^\/members\/(\d+)$/;
const MEMBER_EDIT_RE = /^\/members\/(\d+)\/edit$/;
const TRANSACTION_RE = /^\/transactions\/(\d+)$/;

/** Builds the topbar breadcrumb trail for the current pathname. Static
 * routes come straight from lib/config/nav.ts; detail/edit routes for a
 * book or member resolve the title from the same TanStack Query cache the
 * detail page itself populates, so this costs nothing extra once that page
 * has loaded (and shows a plain "Book"/"Member" placeholder until it has). */
export function useBreadcrumbs(pathname: string): Crumb[] {
  const bookMatch = pathname.match(BOOK_RE) ?? pathname.match(BOOK_EDIT_RE);
  const memberMatch = pathname.match(MEMBER_RE) ?? pathname.match(MEMBER_EDIT_RE);
  const bookId = bookMatch ? Number(bookMatch[1]) : NaN;
  const memberId = memberMatch ? Number(memberMatch[1]) : NaN;

  const book = useBook(bookId);
  const member = useMemberRaw(memberId);
  const role = useAuthStore((s) => s.user?.role);

  return useMemo(() => {
    const parts = resolveParts(pathname, book.data?.title, member.data?.name);
    return parts.map((label, i) => ({ label, href: i === 0 ? homeFor(role) : undefined }));
  }, [pathname, book.data?.title, member.data?.name, role]);
}

function resolveParts(pathname: string, bookTitle?: string, memberName?: string): string[] {
  const staticParts = staticCrumbsFor(pathname);
  if (staticParts) return staticParts;

  let m: RegExpMatchArray | null;
  if ((m = pathname.match(BOOK_RE))) return ["Console", "Books", bookTitle || "Book"];
  if ((m = pathname.match(BOOK_EDIT_RE))) return ["Console", "Books", bookTitle || "Book", "Edit"];
  if ((m = pathname.match(MEMBER_RE))) return ["Console", "Members", memberName || "Member"];
  if ((m = pathname.match(MEMBER_EDIT_RE))) return ["Console", "Members", memberName || "Member", "Edit"];
  if ((m = pathname.match(TRANSACTION_RE))) return ["Console", "Transactions", `TXN ${m[1]}`];
  return ["Console"];
}
