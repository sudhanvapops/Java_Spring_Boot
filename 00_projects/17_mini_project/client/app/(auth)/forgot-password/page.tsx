"use client";

import { useState } from "react";
import Link from "next/link";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { AuthCard } from "@/components/panels/AuthCard";
import { Label } from "@/components/forms/Label";
import { Input } from "@/components/forms/Input";
import { Button } from "@/components/forms/Button";
import { forgotPasswordSchema, type ForgotPasswordFormValues } from "@/lib/schemas/auth";
import { useForgotPassword } from "@/lib/hooks/useAuth";

export default function ForgotPasswordPage() {
  const [sentTo, setSentTo] = useState<string | null>(null);
  const forgotPassword = useForgotPassword();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<ForgotPasswordFormValues>({
    resolver: zodResolver(forgotPasswordSchema),
    defaultValues: { email: "" },
  });

  const onSubmit = handleSubmit(async (values) => {
    // Always shows the "check your email" state regardless of outcome —
    // uploads/05-auth-spec.md: identical response whether or not the
    // account exists, so the screen can't leak which emails are registered.
    try {
      await forgotPassword.mutateAsync(values);
    } catch {
      // deliberately ignored — see above
    }
    setSentTo(values.email);
  });

  if (sentTo) {
    return (
      <AuthCard
        title="Check your email"
        subtitle={`If an account exists for ${sentTo}, a reset link is on its way. It expires in an hour.`}
        footer={<Link href="/login">Back to sign in</Link>}
      >
        <Button variant="secondary" fullWidth onClick={() => setSentTo(null)}>
          Use a different email
        </Button>
      </AuthCard>
    );
  }

  return (
    <AuthCard
      title="Reset your password"
      subtitle="We’ll email you a link to set a new one."
      footer={<Link href="/login">Back to sign in</Link>}
    >
      <form onSubmit={onSubmit} style={{ display: "grid", gap: "var(--space-md)" }}>
        <div>
          <Label htmlFor="f-email">Email</Label>
          <Input id="f-email" type="email" autoComplete="email" autoFocus invalid={!!errors.email} error={errors.email?.message} {...register("email")} />
        </div>
        <Button type="submit" size="lg" fullWidth loading={isSubmitting} loadingLabel="Sending…">
          Send reset link
        </Button>
      </form>
    </AuthCard>
  );
}
