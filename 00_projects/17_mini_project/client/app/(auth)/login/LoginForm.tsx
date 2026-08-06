"use client";

import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { AuthCard } from "@/components/panels/AuthCard";
import { Label } from "@/components/forms/Label";
import { Input } from "@/components/forms/Input";
import { PasswordField } from "@/components/forms/PasswordField";
import { Checkbox } from "@/components/forms/Checkbox";
import { Button } from "@/components/forms/Button";
import { loginSchema, type LoginFormValues } from "@/lib/schemas/auth";
import { useLogin } from "@/lib/hooks/useAuth";
import { isNormalizedApiError } from "@/lib/api/client";
import { safeNextPath } from "@/lib/utils/url";

export function LoginForm() {
    const router = useRouter();
    const searchParams = useSearchParams();
    const login = useLogin();

    const {
        register,
        handleSubmit,
        setValue,
        watch,
        formState: { errors, isSubmitting },
    } = useForm<LoginFormValues>({
        resolver: zodResolver(loginSchema),
        defaultValues: { email: "", password: "", rememberMe: true },
    });

    const onSubmit = handleSubmit(async (values) => {
        try {
            await login.mutateAsync({ email: values.email, password: values.password });
            router.push(safeNextPath(searchParams.get("next")));
        } catch {
            // handled by login.error below — credential failures sit above the
            // form, never on a field, per uploads/09-error-codes.md INVALID_CREDENTIALS.
        }
    });

    const formError = login.error && isNormalizedApiError(login.error) ? login.error.userMessage : null;

    return (
        <AuthCard
            title="Welcome back"
            subtitle="Sign in to the library console."
            error={formError}
            footer={
                <>
                    New here? <Link href="/signup">Create an account</Link>
                </>
            }
        >
            <form onSubmit={onSubmit} style={{ display: "grid", gap: "var(--space-md)" }}>
                <div>
                    <Label htmlFor="email">Email</Label>
                    <Input id="email" type="email" autoComplete="email" autoFocus invalid={!!errors.email} error={errors.email?.message} {...register("email")} />
                </div>
                <div>
                    <Label htmlFor="password" hint={<Link href="/forgot-password">Forgot password?</Link>}>
                        Password
                    </Label>
                    <PasswordField
                        id="password"
                        autoComplete="current-password"
                        invalid={!!errors.password}
                        error={errors.password?.message}
                        {...register("password")}
                    />
                </div>
                <Checkbox id="keep" checked={watch("rememberMe")} onChange={(e) => setValue("rememberMe", e.target.checked)} label="Keep me signed in" />
                <Button type="submit" size="lg" fullWidth loading={isSubmitting || login.isPending} loadingLabel="Signing in…">
                    Sign in
                </Button>
            </form>
        </AuthCard>
    );
}
