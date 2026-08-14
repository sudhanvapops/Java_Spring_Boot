"use client";

import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { PageHeader } from "@/components/navigation/PageHeader";
import { Label } from "@/components/forms/Label";
import { Input } from "@/components/forms/Input";
import { Select } from "@/components/forms/Select";
import { PasswordField } from "@/components/forms/PasswordField";
import { StrengthMeter } from "@/components/forms/StrengthMeter";
import { Button } from "@/components/forms/Button";
import { EmptyState } from "@/components/feedback/EmptyState";
import { staffRegisterSchema, type StaffRegisterFormValues } from "@/lib/schemas/auth";
import { useRegisterStaff } from "@/lib/hooks/useAuth";
import { useAuthStore } from "@/lib/stores/auth";
import { useToast } from "@/lib/hooks/useToast";
import { isNormalizedApiError } from "@/lib/api/client";
import { passwordChecks, passwordScore } from "@/lib/utils/password";

/** @AdminOnly server-side (BACKEND_HANDOFF.md §3.3) — this is how new
 * librarian/admin accounts get created day-to-day. The nav link is already
 * hidden from non-admins (lib/config/nav.ts); this guard covers direct
 * navigation to the URL. */
export default function RegisterStaffPage() {
  const router = useRouter();
  const toast = useToast();
  const currentRole = useAuthStore((s) => s.user?.role);
  const registerStaff = useRegisterStaff();

  const {
    register,
    handleSubmit,
    watch,
    setValue,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<StaffRegisterFormValues>({
    resolver: zodResolver(staffRegisterSchema),
    defaultValues: { username: "", email: "", password: "", confirmPassword: "", role: "LIBRARIAN" },
  });

  const password = watch("password") || "";

  const onSubmit = handleSubmit(async (values) => {
    try {
      const account = await registerStaff.mutateAsync(values);
      toast.success(`${account.username} can now sign in as ${account.role === "ADMIN" ? "an admin" : "a librarian"}.`);
      reset({ username: "", email: "", password: "", confirmPassword: "", role: "LIBRARIAN" });
    } catch {
      // surfaced via registerStaff.error below
    }
  });

  if (currentRole !== "ADMIN") {
    return (
      <EmptyState
        icon="lock"
        headline="You do not have permission to access this resource."
        body="Only admins can create staff accounts."
        actionLabel="Back to settings"
        onAction={() => router.push("/settings/library")}
      />
    );
  }

  const formError = registerStaff.error && isNormalizedApiError(registerStaff.error) ? registerStaff.error.userMessage : null;
  const usernameTaken =
    registerStaff.error && isNormalizedApiError(registerStaff.error) && registerStaff.error.errorCode === "USERNAME_ALREADY_EXISTS_EXCEPTION"
      ? registerStaff.error.userMessage
      : null;
  const emailTaken =
    registerStaff.error && isNormalizedApiError(registerStaff.error) && registerStaff.error.errorCode === "USER_EMAIL_ALREADY_EXISTS"
      ? registerStaff.error.userMessage
      : null;

  return (
    <div style={{ maxWidth: "var(--form-max-width)" }}>
      <PageHeader
        back={{ label: "Back to settings", onClick: (e) => { e.preventDefault(); router.push("/settings/library"); } }}
        title="Add a staff account"
        subtitle="Creates an admin or librarian sign-in. Members register themselves at /signup."
      />
      {formError && !usernameTaken && !emailTaken ? (
        <div
          role="alert"
          style={{
            marginBottom: "var(--space-md)",
            padding: "var(--space-sm) var(--space-md)",
            background: "var(--danger-tint)",
            border: "1px solid var(--danger)",
            borderRadius: "var(--radius-md)",
            font: "var(--type-body-sm)",
            color: "var(--ink)",
          }}
        >
          {formError}
        </div>
      ) : null}
      <form onSubmit={onSubmit} style={{ display: "grid", gap: "var(--space-lg)" }}>
        <div>
          <Label htmlFor="username" required>Username</Label>
          <Input id="username" autoFocus invalid={!!errors.username || !!usernameTaken} error={errors.username?.message || usernameTaken} {...register("username")} />
        </div>
        <div>
          <Label htmlFor="email" required>Email</Label>
          <Input id="email" type="email" invalid={!!errors.email || !!emailTaken} error={errors.email?.message || emailTaken} {...register("email")} />
        </div>
        <div>
          <Label htmlFor="role" required>Role</Label>
          <Select
            id="role"
            width={200}
            options={[
              { value: "LIBRARIAN", label: "Librarian" },
              { value: "ADMIN", label: "Admin" },
            ]}
            value={watch("role")}
            onChange={(e) => setValue("role", e.target.value as StaffRegisterFormValues["role"])}
          />
        </div>
        <div>
          <Label htmlFor="password" required>Password</Label>
          <PasswordField id="password" autoComplete="new-password" invalid={!!errors.password} error={errors.password?.message} {...register("password")} />
          <StrengthMeter score={passwordScore(password)} requirements={passwordChecks(password)} />
        </div>
        <div>
          <Label htmlFor="confirmPassword" required>Confirm password</Label>
          <PasswordField id="confirmPassword" autoComplete="new-password" invalid={!!errors.confirmPassword} error={errors.confirmPassword?.message} {...register("confirmPassword")} />
        </div>
        <div style={{ display: "flex", gap: "var(--space-xs)" }}>
          <Button type="submit" loading={isSubmitting || registerStaff.isPending} loadingLabel="Creating…">
            Create staff account
          </Button>
          <Button type="button" variant="tertiary" onClick={() => router.push("/settings/library")}>
            Cancel
          </Button>
        </div>
      </form>
    </div>
  );
}
