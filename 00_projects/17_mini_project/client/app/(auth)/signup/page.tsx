"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { AuthCard } from "@/components/panels/AuthCard";
import { Label } from "@/components/forms/Label";
import { Input } from "@/components/forms/Input";
import { PasswordField } from "@/components/forms/PasswordField";
import { StrengthMeter } from "@/components/forms/StrengthMeter";
import { Button } from "@/components/forms/Button";
import { signupSchema, type SignupFormValues } from "@/lib/schemas/auth";
import { useSignup } from "@/lib/hooks/useAuth";
import { isNormalizedApiError } from "@/lib/api/client";
import { passwordChecks, passwordScore } from "@/lib/utils/password";

export default function SignupPage() {
  const router = useRouter();
  const signup = useSignup();

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<SignupFormValues>({
    resolver: zodResolver(signupSchema),
    defaultValues: { name: "", email: "", password: "", confirmPassword: "" },
  });

  const password = watch("password") || "";

  const onSubmit = handleSubmit(async (values) => {
    try {
      await signup.mutateAsync({ name: values.name, email: values.email, password: values.password });
      router.push("/dashboard");
    } catch {
      // surfaced via signup.error below
    }
  });

  const formError = signup.error && isNormalizedApiError(signup.error) ? signup.error.userMessage : null;

  return (
    <AuthCard
      title="Create your account"
      subtitle="You’ll use this to sign in to the console."
      error={formError}
      footer={
        <>
          Already have an account? <Link href="/login">Sign in</Link>
        </>
      }
    >
      <form onSubmit={onSubmit} style={{ display: "grid", gap: "var(--space-md)" }}>
        <div>
          <Label htmlFor="s-name">Full name</Label>
          <Input id="s-name" autoFocus invalid={!!errors.name} error={errors.name?.message} {...register("name")} />
        </div>
        <div>
          <Label htmlFor="s-email">Email</Label>
          <Input id="s-email" type="email" autoComplete="email" invalid={!!errors.email} error={errors.email?.message} {...register("email")} />
        </div>
        <div>
          <Label htmlFor="s-pw">Password</Label>
          <PasswordField id="s-pw" autoComplete="new-password" invalid={!!errors.password} error={errors.password?.message} {...register("password")} />
          <StrengthMeter score={passwordScore(password)} requirements={passwordChecks(password)} />
        </div>
        <div>
          <Label htmlFor="s-confirm">Confirm password</Label>
          <PasswordField
            id="s-confirm"
            autoComplete="new-password"
            invalid={!!errors.confirmPassword}
            error={errors.confirmPassword?.message}
            {...register("confirmPassword")}
          />
        </div>
        <Button type="submit" size="lg" fullWidth loading={isSubmitting || signup.isPending} loadingLabel="Creating your account…">
          Create account
        </Button>
      </form>
    </AuthCard>
  );
}
