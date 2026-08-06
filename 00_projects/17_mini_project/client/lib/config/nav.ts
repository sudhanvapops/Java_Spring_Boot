import type { SidebarGroup } from "@/components/navigation/Sidebar";

/** Static shell of the sidebar nav — mirrors ui_kits/console/data.js's `nav`.
 * The "Due today" count is filled in at render time from live data. */
export const NAV_GROUPS: SidebarGroup[] = [
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
