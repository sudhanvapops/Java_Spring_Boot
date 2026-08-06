"use client";

import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { PageHeader } from "@/components/navigation/PageHeader";
import { Panel } from "@/components/panels/Panel";
import { Label } from "@/components/forms/Label";
import { PasswordField } from "@/components/forms/PasswordField";
import { StrengthMeter } from "@/components/forms/StrengthMeter";
import { Button } from "@/components/forms/Button";
import { StatusBadge } from "@/components/data/StatusBadge";
import { changePasswordSchema, type ChangePasswordFormValues } from "@/lib/schemas/auth";
import { useChangePassword, useLogout } from "@/lib/hooks/useAuth";
import { useAuthStore } from "@/lib/stores/auth";
import { useToast } from "@/lib/hooks/useToast";
import { isNormalizedApiError } from "@/lib/api/client";
import { passwordChecks, passwordScore } from "@/lib/utils/password";

/** Ported from ui_kits/console/Settings.jsx "/settings/account". */
export default function AccountSettingsPage() {
  const router = useRouter();
  const toast = useToast();
  const user = useAuthStore((s) => s.user);
  const changePassword = useChangePassword();
  const logout = useLogout();

  const {
    register,
    handleSubmit,
    watch,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<ChangePasswordFormValues>({
    resolver: zodResolver(changePasswordSchema),
    defaultValues: { currentPassword: "", newPassword: "", confirmPassword: "" },
  });

  const newPassword = watch("newPassword") || "";

  const onSubmit = handleSubmit(async (values) => {
    try {
      await changePassword.mutateAsync(values);
      reset({ currentPassword: "", newPassword: "", confirmPassword: "" });
      toast.success("Password updated. You’ve been signed out everywhere else.");
    } catch {
      // surfaced via changePassword.error below
    }
  });

  const handleSignOut = () => {
    logout.mutate(undefined, { onSuccess: () => router.push("/login") });
  };

  const formError = changePassword.error && isNormalizedApiError(changePassword.error) ? changePassword.error.userMessage : null;

  return (
    <div style={{ maxWidth: "var(--form-max-width)" }}>
      <PageHeader title="My account" subtitle="Your sign-in details for the console." />
      <div style={{ display: "grid", gap: "var(--space-lg)" }}>
        <Panel title="Profile">
          <div style={{ display: "grid", gap: "var(--space-md)" }}>
            <Field label="Name" value={user?.name ?? "—"} />
            <Field label="Email" value={user?.email ?? "—"} mono />
            <div>
              <div style={{ font: "var(--type-caption)", color: "var(--ink-subtle)", marginBottom: "var(--space-xxs)" }}>Role</div>
              <StatusBadge status="librarian" />
            </div>
          </div>
          <p style={{ margin: "var(--space-md) 0 0", font: "var(--type-caption)", color: "var(--ink-subtle)" }}>
            Name and email can’t be changed here yet. Ask an administrator.
          </p>
        </Panel>

        <Panel title="Password">
          {formError ? (
            <div
              role="alert"
              style={{ marginBottom: "var(--space-md)", padding: "var(--space-sm) var(--space-md)", background: "var(--danger-tint)", border: "1px solid var(--danger)", borderRadius: "var(--radius-md)", font: "var(--type-body-sm)", color: "var(--ink)" }}
            >
              {formError}
            </div>
          ) : null}
          <form onSubmit={onSubmit} style={{ display: "grid", gap: "var(--space-md)" }}>
            <div>
              <Label htmlFor="current">Current password</Label>
              <PasswordField id="current" autoComplete="current-password" invalid={!!errors.currentPassword} error={errors.currentPassword?.message} {...register("currentPassword")} />
            </div>
            <div>
              <Label htmlFor="next">New password</Label>
              <PasswordField id="next" autoComplete="new-password" invalid={!!errors.newPassword} error={errors.newPassword?.message} {...register("newPassword")} />
              <StrengthMeter score={passwordScore(newPassword)} requirements={passwordChecks(newPassword)} />
            </div>
            <div>
              <Label htmlFor="confirm">Confirm new password</Label>
              <PasswordField id="confirm" autoComplete="new-password" invalid={!!errors.confirmPassword} error={errors.confirmPassword?.message} {...register("confirmPassword")} />
            </div>
            <div>
              <Button type="submit" loading={isSubmitting || changePassword.isPending} loadingLabel="Updating…">
                Update password
              </Button>
            </div>
          </form>
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
