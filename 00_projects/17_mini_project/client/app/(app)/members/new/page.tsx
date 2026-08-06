"use client";

import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { PageHeader } from "@/components/navigation/PageHeader";
import { Label } from "@/components/forms/Label";
import { Input } from "@/components/forms/Input";
import { Button } from "@/components/forms/Button";
import { memberFormSchema, type MemberFormValues } from "@/lib/schemas/member";
import { useCreateMember } from "@/lib/hooks/useMembers";
import { useToast } from "@/lib/hooks/useToast";
import { isNormalizedApiError } from "@/lib/api/client";

export default function NewMemberPage() {
  const router = useRouter();
  const toast = useToast();
  const createMember = useCreateMember();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<MemberFormValues>({
    resolver: zodResolver(memberFormSchema),
    defaultValues: { name: "", email: "", age: undefined as unknown as number },
  });

  const onSubmit = handleSubmit(async (values) => {
    try {
      const member = await createMember.mutateAsync(values);
      toast.success(`${member.name} registered.`);
      router.push(`/members/${member.id}`);
    } catch (err) {
      if (isNormalizedApiError(err) && err.errorCode === "MEMBER_EMAIL_ALREADY_EXISTS") return; // shown inline below
      toast.errorFrom(err);
    }
  });

  const duplicateError =
    createMember.error && isNormalizedApiError(createMember.error) && createMember.error.errorCode === "MEMBER_EMAIL_ALREADY_EXISTS"
      ? createMember.error.userMessage
      : null;

  return (
    <div style={{ maxWidth: "var(--form-max-width)" }}>
      <PageHeader
        back={{ label: "Back to members", onClick: (e) => { e.preventDefault(); router.push("/members"); } }}
        title="Register a member"
        subtitle="The details the library keeps on file."
      />
      <form onSubmit={onSubmit} style={{ display: "grid", gap: "var(--space-lg)" }}>
        <div>
          <Label htmlFor="name" required>Full name</Label>
          <Input id="name" invalid={!!errors.name} error={errors.name?.message} placeholder="Priya Nair" autoFocus {...register("name")} />
        </div>
        <div>
          <Label htmlFor="email" required>Email</Label>
          <Input
            id="email"
            type="email"
            invalid={!!errors.email || !!duplicateError}
            error={errors.email?.message || duplicateError}
            helper={errors.email || duplicateError ? null : "Used for due-date reminders."}
            placeholder="priya@example.com"
            {...register("email")}
          />
        </div>
        <div>
          <Label htmlFor="age" required>Age</Label>
          <Input
            id="age"
            type="number"
            min="1"
            width={160}
            invalid={!!errors.age}
            error={errors.age?.message}
            helper={errors.age ? null : "Used for age-restricted titles."}
            {...register("age", { valueAsNumber: true })}
          />
        </div>
        <div style={{ display: "flex", gap: "var(--space-xs)" }}>
          <Button type="submit" loading={isSubmitting || createMember.isPending} loadingLabel="Saving…">
            Register member
          </Button>
          <Button type="button" variant="tertiary" onClick={() => router.push("/members")}>
            Cancel
          </Button>
        </div>
      </form>
    </div>
  );
}
