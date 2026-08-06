"use client";

import { useRouter, useSearchParams } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { AuthCard } from "@/components/panels/AuthCard";
import { Button } from "@/components/forms/Button";
import { Icon } from "@/components/core/Icon";
import * as authApi from "@/lib/api/services/auth";

export function VerifyEmailStatus() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const token = searchParams.get("token");

  const verify = useQuery({
    queryKey: ["auth", "verify-email", token],
    queryFn: () => authApi.verifyEmail(token as string),
    enabled: !!token,
    retry: false,
    staleTime: Infinity,
  });

  if (!token || verify.isError) {
    return (
      <AuthCard title="That link has expired" subtitle="Links last an hour. Ask for a new one and try again.">
        <div style={{ display: "grid", placeItems: "center", padding: "var(--space-md) 0" }}>
          <Icon name="triangle-alert" size={40} color="var(--warning)" />
        </div>
        <Button size="lg" fullWidth onClick={() => router.push("/login")}>
          Back to sign in
        </Button>
      </AuthCard>
    );
  }

  if (verify.isPending) {
    return (
      <AuthCard title="Checking your link…" subtitle="This only takes a moment.">
        <div style={{ display: "grid", placeItems: "center", padding: "var(--space-md) 0" }}>
          <Icon name="clock" size={40} color="var(--ink-subtle)" />
        </div>
      </AuthCard>
    );
  }

  return (
    <AuthCard title="Email verified" subtitle="Your account is ready. Sign in to get started.">
      <div style={{ display: "grid", placeItems: "center", padding: "var(--space-md) 0" }}>
        <Icon name="circle-check" size={40} color="var(--success)" />
      </div>
      <Button size="lg" fullWidth onClick={() => router.push("/login")}>
        Go to sign in
      </Button>
    </AuthCard>
  );
}
