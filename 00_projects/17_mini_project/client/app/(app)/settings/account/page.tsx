"use client";

import { useRouter } from "next/navigation";
import { PageHeader } from "@/components/navigation/PageHeader";
import { Panel } from "@/components/panels/Panel";
import { Button } from "@/components/forms/Button";
import { StatusBadge, type Status } from "@/components/data/StatusBadge";
import { useLogout } from "@/lib/hooks/useAuth";
import { useAuthStore } from "@/lib/stores/auth";

const ROLE_STATUS: Record<string, Status> = { ADMIN: "admin", LIBRARIAN: "librarian", MEMBER: "member" };

/** Ported from ui_kits/console/Settings.jsx "/settings/account". The
 * password-change section is gone — no endpoint for it exists
 * (BACKEND_HANDOFF.md §3.3), and name/username aren't returned by login or
 * refresh, so this is a read-only identity summary plus sign-out. */
export default function AccountSettingsPage() {
  const router = useRouter();
  const user = useAuthStore((s) => s.user);
  const logout = useLogout();

  const handleSignOut = () => {
    logout.mutate(undefined, { onSuccess: () => router.push("/login") });
  };

  return (
    <div style={{ maxWidth: "var(--form-max-width)" }}>
      <PageHeader title="My account" subtitle="Your sign-in details for the console." />
      <div style={{ display: "grid", gap: "var(--space-lg)" }}>
        <Panel title="Profile">
          <div style={{ display: "grid", gap: "var(--space-md)" }}>
            <Field label="Email" value={user?.email ?? "—"} mono />
            <div>
              <div style={{ font: "var(--type-caption)", color: "var(--ink-subtle)", marginBottom: "var(--space-xxs)" }}>Role</div>
              <StatusBadge status={(user && ROLE_STATUS[user.role]) || "member"} />
            </div>
          </div>
          <p style={{ margin: "var(--space-md) 0 0", font: "var(--type-caption)", color: "var(--ink-subtle)" }}>
            Account details and passwords can’t be changed here yet. Ask an administrator.
          </p>
        </Panel>

        <div style={{ paddingTop: "var(--space-md)", borderTop: "1px solid var(--hairline)" }}>
          <Button variant="secondary" iconLeft="log-out" onClick={handleSignOut}>
            Sign out
          </Button>
        </div>
      </div>
    </div>
  );
}

function Field({ label, value, mono }: { label: string; value: string; mono?: boolean }) {
  return (
    <div>
      <div style={{ font: "var(--type-caption)", color: "var(--ink-subtle)", marginBottom: "var(--space-xxs)" }}>{label}</div>
      <div style={{ font: mono ? "var(--type-mono)" : "var(--type-body-sm)", color: "var(--ink)" }}>{value}</div>
    </div>
  );
}
