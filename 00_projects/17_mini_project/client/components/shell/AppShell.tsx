"use client";

import { useCallback, useMemo, useRef, useState, type ReactNode } from "react";
import { usePathname, useRouter } from "next/navigation";
import { Sidebar, type SidebarGroup } from "@/components/navigation/Sidebar";
import { Topbar } from "@/components/navigation/Topbar";
import { DropdownMenu } from "@/components/navigation/DropdownMenu";
import { CommandPalette, type CommandItem } from "@/components/navigation/CommandPalette";
import { IconButton } from "@/components/forms/IconButton";
import { MotionDiv, Presence, EASE } from "@/components/core/Motion";
import { navGroupsFor, sectionFor } from "@/lib/config/nav";
import { useAuthStore } from "@/lib/stores/auth";
import { useUiStore } from "@/lib/stores/ui";
import { useLogout } from "@/lib/hooks/useAuth";
import { useBooks } from "@/lib/hooks/useBooks";
import { useMembersRaw } from "@/lib/hooks/useMembers";
import { useDueToday } from "@/lib/hooks/useRecords";
import { useMediaQuery } from "@/lib/hooks/useMediaQuery";
import { useClickOutside } from "@/lib/hooks/useClickOutside";
import { isStaffRole } from "@/lib/utils/rbac";
import { useBreadcrumbs } from "./Breadcrumbs";

function withDueTodayCount(groups: SidebarGroup[], count: number): SidebarGroup[] {
  return groups.map((g) => ({
    ...g,
    items: g.items.map((it) => (it.href === "/records/due-today" ? { ...it, count: count || undefined, countTone: "warning" as const } : it)),
  }));
}

/**
 * Ported from ui_kits/console/AppShell.jsx. The hand-rolled `go()`/route prop
 * plumbing is replaced by next/navigation; everything else — the breadcrumb
 * and command-palette composition — follows the reference closely.
 */
export function AppShell({ children }: { children: ReactNode }) {
  const pathname = usePathname();
  const router = useRouter();
  const user = useAuthStore((s) => s.user);
  const isStaff = isStaffRole(user?.role);

  const sidebarCollapsedPref = useUiStore((s) => s.sidebarCollapsed);
  const commandPaletteOpen = useUiStore((s) => s.commandPaletteOpen);
  const setCommandPaletteOpen = useUiStore((s) => s.setCommandPaletteOpen);

  const isTablet = useMediaQuery("(max-width:1023px)");
  const isMobile = useMediaQuery("(max-width:767px)");
  const collapsed = isTablet ? true : sidebarCollapsedPref;

  const [accountOpen, setAccountOpen] = useState(false);
  const [mobileSidebarOpen, setMobileSidebarOpen] = useState(false);
  const [query, setQuery] = useState("");

  const accountRef = useRef<HTMLDivElement>(null);
  useClickOutside(accountRef, () => setAccountOpen(false), accountOpen);

  const breadcrumb = useBreadcrumbs(pathname);
  const { data: dueToday } = useDueToday({ enabled: isStaff });
  const navGroups = useMemo(
    () => withDueTodayCount(navGroupsFor(user?.role), dueToday?.length ?? 0),
    [user?.role, dueToday],
  );

  const { data: books } = useBooks();
  const { data: members } = useMembersRaw({ enabled: isStaff });
  const logout = useLogout();

  const goTo = useCallback((href: string) => router.push(href), [router]);
  const openSearch = useCallback(() => setCommandPaletteOpen(true), [setCommandPaletteOpen]);

  const groups = useMemo(() => {
    const t = query.trim().toLowerCase();
    const hit = (s: string) => s.toLowerCase().includes(t);
    const bookResults = (t ? (books ?? []).filter((b) => hit(b.title) || hit(b.author)) : (books ?? [])).slice(0, 5);
    const memberResults = (t ? (members ?? []).filter((m) => hit(m.name) || hit(m.email)) : []).slice(0, 5);
    const screenItems = navGroups
      .flatMap((g) => g.items)
      .filter((it) => !t || hit(it.label))
      .slice(0, t ? 5 : 4);
    return [
      {
        label: "Books",
        items: bookResults.map((b) => ({
          id: `b${b.id}`,
          label: b.title,
          meta: b.author,
          icon: "book",
          trailing: b.availableCopies ? `${b.availableCopies} of ${b.totalCopies}` : "none available",
          href: `/books/${b.id}`,
        })),
      },
      {
        label: "Members",
        items: memberResults.map((m) => ({
          id: `m${m.id}`,
          label: m.name,
          meta: m.email,
          icon: "users",
          href: `/members/${m.id}`,
        })),
      },
      {
        label: t ? "Screens" : "Go to",
        items: screenItems.map((it) => ({ id: it.href, label: it.label, icon: it.icon, href: it.href })),
      },
    ];
  }, [books, members, navGroups, query]);

  const pick = useCallback(
    (it: CommandItem & { group: string }) => {
      setCommandPaletteOpen(false);
      setQuery("");
      if (it.href) router.push(it.href);
    },
    [router, setCommandPaletteOpen],
  );

  const handleSignOut = useCallback(() => {
    setAccountOpen(false);
    logout.mutate(undefined, { onSuccess: () => router.push("/login") });
  }, [logout, router]);

  const ROLE_LABEL: Record<string, string> = { ADMIN: "Admin", LIBRARIAN: "Librarian", MEMBER: "Member" };
  // No display name comes back from login/refresh — see lib/types/domain.ts.
  const account = user ? { name: user.email, role: ROLE_LABEL[user.role] ?? user.role } : undefined;

  return (
    <div style={{ display: "flex", minHeight: "100vh", background: "var(--canvas)" }}>
      {!isMobile ? <Sidebar groups={navGroups} active={sectionFor(pathname)} onNavigate={goTo} account={account} collapsed={collapsed} /> : null}

      {isMobile ? (
        <MobileSidebarSheet open={mobileSidebarOpen} onClose={() => setMobileSidebarOpen(false)}>
          <Sidebar
            groups={navGroups}
            active={sectionFor(pathname)}
            onNavigate={(href) => {
              setMobileSidebarOpen(false);
              goTo(href);
            }}
            account={account}
            collapsed={false}
          />
        </MobileSidebarSheet>
      ) : null}

      <div style={{ flex: 1, minWidth: 0, display: "flex", flexDirection: "column" }}>
        <Topbar
          breadcrumb={isMobile ? [] : breadcrumb}
          onNavigate={goTo}
          onSearch={openSearch}
          right={
            <>
              {isMobile ? <IconButton icon="list" label="Open menu" onClick={() => setMobileSidebarOpen(true)} /> : null}
              <div ref={accountRef} style={{ position: "relative" }}>
                <IconButton icon="circle-user-round" label="Account menu" onClick={() => setAccountOpen((o) => !o)} />
                <DropdownMenu
                  open={accountOpen}
                  items={[
                    {
                      label: "My account",
                      icon: "circle-user-round",
                      onClick: () => {
                        setAccountOpen(false);
                        goTo("/settings/account");
                      },
                    },
                    // LibrarySettingsController is @StaffOnly to read — hide
                    // the link rather than send a MEMBER into a 403.
                    ...(isStaff
                      ? [
                          {
                            label: "Library rules",
                            icon: "settings",
                            onClick: () => {
                              setAccountOpen(false);
                              goTo("/settings/library");
                            },
                          },
                        ]
                      : []),
                    { separator: true },
                    { label: "Sign out", icon: "log-out", tone: "danger" as const, onClick: handleSignOut },
                  ]}
                />
              </div>
            </>
          }
        />
        <main style={{ flex: 1, padding: "var(--space-xl)", overflow: "auto" }}>{children}</main>
      </div>

      <CommandPalette
        open={commandPaletteOpen}
        onRequestOpen={openSearch}
        onClose={() => {
          setCommandPaletteOpen(false);
          setQuery("");
        }}
        query={query}
        onQueryChange={setQuery}
        placeholder="Search books, members and screens…"
        emptyMessage={`Nothing matches "${query}".`}
        groups={groups}
        onSelect={pick}
      />
    </div>
  );
}

function MobileSidebarSheet({ open, onClose, children }: { open: boolean; onClose: () => void; children: ReactNode }) {
  return (
    <Presence>
      {open ? (
        <MotionDiv
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          transition={EASE.fast}
          onClick={onClose}
          style={{ position: "fixed", inset: 0, background: "rgba(5,5,6,.72)", zIndex: 70 }}
        >
          <MotionDiv
            initial={{ x: -240 }}
            animate={{ x: 0 }}
            exit={{ x: -240 }}
            transition={EASE.medium}
            onClick={(e) => e.stopPropagation()}
            style={{ height: "100%" }}
          >
            {children}
          </MotionDiv>
        </MotionDiv>
      ) : null}
    </Presence>
  );
}
