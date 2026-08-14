"use client";

import { useState } from "react";
import Link from "next/link";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { AuthCard } from "@/components/panels/AuthCard";
import { Label } from "@/components/forms/Label";
import { Input } from "@/components/forms/Input";
import { Button } from "@/components/forms/Button";
import { Icon } from "@/components/core/Icon";
import { memberFormSchema, type MemberFormValues } from "@/lib/schemas/member";
import { useRegister } from "@/lib/hooks/useAuth";
import { isNormalizedApiError } from "@/lib/api/client";

/**
 * Public signup registers the caller as a Member (a library patron staff can
 * look up and lend books to) — not a User login account. Members have no
 * password, so this collects the same fields as the staff-only /members/new
 * form (name/email/age, via the shared memberFormSchema) and there's nowhere
 * to redirect to afterward — the confirmation screen is the end state.
 */
export default function SignupPage() {
  const [registeredName, setRegisteredName] = useState<string | null>(null);
  const register = useRegister();

  const {
    register: registerField,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<MemberFormValues>({
    resolver: zodResolver(memberFormSchema),
    defaultValues: { name: "", email: "", age: undefined as unknown as number },
  });

  const onSubmit = handleSubmit(async (values) => {
    try {
      const member = await register.mutateAsync(values);
      setRegisteredName(member.name);
    } catch {
      // surfaced via register.error below
    }
  });

  if (registeredName) {
    return (
      <AuthCard title="You're registered" subtitle={`Welcome, ${registeredName}.`}>
        <div style={{ display: "grid", placeItems: "center", padding: "var(--space-md) 0" }}>
          <Icon name="circle-check" size={40} color="var(--success)" />
        </div>
        <p style={{ margin: 0, font: "var(--type-body-sm)", color: "var(--ink-subtle)", textAlign: "center" }}>
          Bring this up next time you're at the library — a librarian can look you up to lend you books.
        </p>
      </AuthCard>
    );
  }

  const formError = register.error && isNormalizedApiError(register.error) ? register.error.userMessage : null;
  const emailTaken =
    register.error &&
    isNormalizedApiError(register.error) &&
    (register.error.errorCode === "MEMBER_EMAIL_ALREADY_EXISTS" || register.error.errorCode === "USER_EMAIL_ALREADY_EXISTS")
      ? register.error.userMessage
      : null;

  return (
    <AuthCard
      title="Join the library"
      subtitle="Register as a member — no password needed, just your details."
      error={emailTaken ? null : formError}
      footer={
        <>
          Staff account? <Link href="/login">Sign in</Link>
        </>
      }
    >
      <form onSubmit={onSubmit} style={{ display: "grid", gap: "var(--space-md)" }}>
        <div>
          <Label htmlFor="s-name">Full name</Label>
          <Input id="s-name" autoFocus invalid={!!errors.name} error={errors.name?.message} {...registerField("name")} />
        </div>
        <div>
          <Label htmlFor="s-email">Email</Label>
          <Input
            id="s-email"
            type="email"
            autoComplete="email"
            invalid={!!errors.email || !!emailTaken}
            error={errors.email?.message || emailTaken}
            {...registerField("email")}
          />
        </div>
        <div>
          <Label htmlFor="s-age">Age</Label>
          <Input
            id="s-age"
            type="number"
            min="1"
            width={160}
            invalid={!!errors.age}
            error={errors.age?.message}
            {...registerField("age", { valueAsNumber: true })}
          />
        </div>
        <Button type="submit" size="lg" fullWidth loading={isSubmitting || register.isPending} loadingLabel="Registering…">
          Register
        </Button>
      </form>
    </AuthCard>
  );
}
