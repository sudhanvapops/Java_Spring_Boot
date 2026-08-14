import type { SidebarGroup } from "@/components/navigation/Sidebar";
import type { AccountRole } from "@/lib/types/api";
import { isStaffRole } from "@/lib/utils/rbac";

/** Static shell of the sidebar nav — mirrors ui_kits/console/data.js's `nav`.
 * The "Due today" count is filled in at render time from live data. Staff
 * only: everything but the book catalogue is @StaffOnly or stricter server-
 * side (BACKEND_HANDOFF.md §3.2/§3.6). */
const STAFF_NAV_GROUPS: SidebarGroup[] = [
  { items: [{ label: "Overview", href: "/dashboard", icon: "layout-dashboard" }] },
  {
    label: "Catalogue",
    items: [
      { label: "Books", href: "/books", icon: "book" },
      { label: "Members", href: "/members", icon: "users" },
    ],
  },
  {
    label: "Circulation",
    items: [
      { label: "Lend", href: "/lend", icon: "arrow-right-left" },
      { label: "Returns", href: "/returns", icon: "undo-2" },
      { label: "Due today", href: "/records/due-today", icon: "calendar-clock" },
      { label: "Currently out", href: "/records/unreturned", icon: "clock" },
    ],
  },
  {
    label: "History",
    items: [
      { label: "Transactions", href: "/transactions", icon: "history" },
      { label: "Records", href: "/records", icon: "list" },
    ],
  },
  { items: [{ label: "Settings", href: "/settings/library", icon: "settings" }] },
];

/** register-staff is @AdminOnly server-side — a LIBRARIAN would get a 403,
 * so the link only appears for ADMIN (§3.3/§3.6). */
const ADMIN_ONLY_SETTINGS_ITEM = { label: "Add staff", href: "/settings/staff/new", icon: "user-plus" };

/** A MEMBER account can only browse the catalogue today — no /my/*
 * endpoint exists, so there's nothing else to link to (§3.2). */
const MEMBER_NAV_GROUPS: SidebarGroup[] = [{ items: [{ label: "Books", href: "/books", icon: "book" }] }];

export function navGroupsFor(role: AccountRole | null | undefined): SidebarGroup[] {
  if (!isStaffRole(role)) return MEMBER_NAV_GROUPS;
  if (role !== "ADMIN") return STAFF_NAV_GROUPS;
  return STAFF_NAV_GROUPS.map((g) =>
    g.items.some((it) => it.href === "/settings/library") ? { ...g, items: [...g.items, ADMIN_ONLY_SETTINGS_ITEM] } : g,
  );
}

const CRUMBS: Record<string, string[]> = {
  "/dashboard": ["Console", "Overview"],
  "/books": ["Console", "Books"],
  "/books/new": ["Console", "Books", "Add a book"],
  "/members": ["Console", "Members"],
  "/members/new": ["Console", "Members", "Register a member"],
  "/lend": ["Console", "Lend books"],
  "/returns": ["Console", "Take books back"],
  "/records": ["Console", "Records"],
  "/records/unreturned": ["Console", "Records", "Currently out"],
  "/records/due-today": ["Console", "Records", "Due today"],
  "/transactions": ["Console", "Transactions"],
  "/settings/library": ["Console", "Settings", "Library rules"],
  "/settings/account": ["Console", "Settings", "My account"],
  "/settings/staff/new": ["Console", "Settings", "Add staff"],
};

/** Static breadcrumb parts for a pathname — dynamic segments (book/member
 * titles) are resolved separately by the caller, which has query-cache
 * access this pure function doesn't need. */
export function staticCrumbsFor(pathname: string): string[] | null {
  return CRUMBS[pathname] ?? null;
}

/** Which sidebar item lights up for a nested route. */
export function sectionFor(pathname: string): string {
  if (pathname.startsWith("/books")) return "/books";
  if (pathname.startsWith("/members")) return "/members";
  if (pathname.startsWith("/transactions")) return "/transactions";
  if (pathname === "/records") return "/records";
  if (pathname.startsWith("/settings")) return "/settings/library";
  return pathname;
}
