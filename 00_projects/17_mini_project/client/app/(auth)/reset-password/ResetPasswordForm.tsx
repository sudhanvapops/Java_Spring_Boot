"use client";

import { useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { AuthCard } from "@/components/panels/AuthCard";
import { Label } from "@/components/forms/Label";
import { PasswordField } from "@/components/forms/PasswordField";
import { StrengthMeter } from "@/components/forms/StrengthMeter";
import { Button } from "@/components/forms/Button";
import { Icon } from "@/components/core/Icon";
import { resetPasswordSchema, type ResetPasswordFormValues } from "@/lib/schemas/auth";
import { useResetPassword, useValidateResetToken } from "@/lib/hooks/useAuth";
import { isNormalizedApiError } from "@/lib/api/client";
import { passwordChecks, passwordScore } from "@/lib/utils/password";

export function ResetPasswordForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const token = searchParams.get("token");
  const [done, setDone] = useState(false);

  const validate = useValidateResetToken(token);
  const resetPassword = useResetPassword();

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<ResetPasswordFormValues>({
    resolver: zodResolver(resetPasswordSchema),
    defaultValues: { token: token ?? "", password: "", confirmPassword: "" },
  });

  const password = watch("password") || "";

  const onSubmit = handleSubmit(async (values) => {
    try {
      await resetPassword.mutateAsync({ token: values.token, password: values.password });
      setDone(true);
    } catch {
      // surfaced via resetPassword.error below
    }
  });

  if (done) {
    return (
      <AuthCard title="Password set" subtitle="You can sign in with your new password now.">
        <Button size="lg" fullWidth onClick={() => router.push("/login")}>
          Go to sign in
        </Button>
      </AuthCard>
    );
  }

  if (!token || validate.isError || validate.data === false) {
    return (
      <AuthCard title="This link isn’t valid" subtitle="Password reset links expire after 30 minutes and only work once.">
        <div style={{ display: "grid", placeItems: "center", padding: "var(--space-md) 0" }}>
          <Icon name="triangle-alert" size={40} color="var(--warning)" />
        </div>
        <Button size="lg" fullWidth onClick={() => router.push("/forgot-password")}>
          Request a new link
        </Button>
      </AuthCard>
    );
  }

  if (validate.isPending) {
    return (
      <AuthCard title="Checking your link…" subtitle="This only takes a moment.">
        <div style={{ display: "grid", placeItems: "center", padding: "var(--space-md) 0" }}>
          <Icon name="clock" size={40} color="var(--ink-subtle)" />
        </div>
      </AuthCard>
    );
  }

  const formError = resetPassword.error && isNormalizedApiError(resetPassword.error) ? resetPassword.error.userMessage : null;

  return (
    <AuthCard title="Set a new password" subtitle="Pick something you haven’t used here before." error={formError}>
      <form onSubmit={onSubmit} style={{ display: "grid", gap: "var(--space-md)" }}>
        <div>
          <Label htmlFor="r-pw">New password</Label>
          <PasswordField id="r-pw" autoComplete="new-password" autoFocus invalid={!!errors.password} error={errors.password?.message} {...register("password")} />
          <StrengthMeter score={passwordScore(password)} requirements={passwordChecks(password)} />
        </div>
        <div>
          <Label htmlFor="r-confirm">Confirm password</Label>
          <PasswordField
            id="r-confirm"
            autoComplete="new-password"
            invalid={!!errors.confirmPassword}
            error={errors.confirmPassword?.message}
            {...register("confirmPassword")}
          />
        </div>
        <Button type="submit" size="lg" fullWidth loading={isSubmitting || resetPassword.isPending} loadingLabel="Saving…">
          Set password
        </Button>
      </form>
    </AuthCard>
  );
}
