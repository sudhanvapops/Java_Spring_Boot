"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { PageHeader } from "@/components/navigation/PageHeader";
import { Label } from "@/components/forms/Label";
import { Input } from "@/components/forms/Input";
import { Button } from "@/components/forms/Button";
import { Banner } from "@/components/feedback/Banner";
import { Dialog } from "@/components/feedback/Dialog";
import { Skeleton } from "@/components/data/Skeleton";
import { EmptyState } from "@/components/feedback/EmptyState";
import { memberFormSchema, type MemberFormValues } from "@/lib/schemas/member";
import { useMember, useUpdateMember, useDeactivateMember } from "@/lib/hooks/useMembers";
import { useToast } from "@/lib/hooks/useToast";
import { isNormalizedApiError } from "@/lib/api/client";

export default function EditMemberPage() {
  const params = useParams<{ id: string }>();
  const id = Number(params.id);
  const router = useRouter();
  const toast = useToast();

  const { data: member, isLoading, isError } = useMember(id);
  const updateMember = useUpdateMember(id);
  const deactivateMember = useDeactivateMember();
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [blocked, setBlocked] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<MemberFormValues>({
    resolver: zodResolver(memberFormSchema),
    defaultValues: { name: "", email: "", age: undefined as unknown as number },
  });

  useEffect(() => {
    if (member) reset({ name: member.name, email: member.email, age: member.age });
  }, [member, reset]);

  const onSubmit = handleSubmit(async (values) => {
    if (!member) return;
    try {
      await updateMember.mutateAsync({ values, isActive: member.isActive });
      toast.success(`${values.name} saved.`);
      router.push(`/members/${member.id}`);
    } catch (err) {
      if (isNormalizedApiError(err) && err.errorCode === "MEMBER_EMAIL_ALREADY_EXISTS") return;
      toast.errorFrom(err);
    }
  });

  const tryDeactivate = async () => {
    if (!member) return;
    if (member.booksOut) {
      setConfirmOpen(false);
      setBlocked(true);
      return;
    }
    setConfirmOpen(false);
    try {
      await deactivateMember.mutateAsync(member.id);
      toast.success(`${member.name} deactivated.`);
      router.push("/members");
    } catch (err) {
      toast.errorFrom(err);
    }
  };

  if (isLoading) {
    return (
      <div style={{ maxWidth: "var(--form-max-width)", display: "grid", gap: "var(--space-md)" }}>
        <Skeleton width={200} height={28} />
        <Skeleton height={38} />
        <Skeleton height={38} />
        <Skeleton height={38} width={160} />
      </div>
    );
  }

  if (isError || !member) {
    return (
      <EmptyState icon="users" headline="That member doesn't exist." body="They may have been removed, or the link is wrong." actionLabel="Back to members" onAction={() => router.push("/members")} />
    );
  }

  const duplicateError =
    updateMember.error && isNormalizedApiError(updateMember.error) && updateMember.error.errorCode === "MEMBER_EMAIL_ALREADY_EXISTS"
      ? updateMember.error.userMessage
      : null;

  return (
    <div style={{ maxWidth: "var(--form-max-width)" }}>
      <PageHeader
        back={{ label: "Back to members", onClick: (e) => { e.preventDefault(); router.push(`/members/${member.id}`); } }}
        title="Edit member"
      />

      {blocked ? (
        <div style={{ marginBottom: "var(--space-lg)" }}>
          <Banner tone="danger" action={<Button variant="secondary" size="sm" onClick={() => router.push(`/returns?member=${member.id}`)}>Go to returns</Button>}>
            {member.name.split(" ")[0]} still has {member.booksOut} {member.booksOut === 1 ? "book" : "books"} out. Take them back before deactivating.
          </Banner>
        </div>
      ) : null}

      <form onSubmit={onSubmit} style={{ display: "grid", gap: "var(--space-lg)" }}>
        <div>
          <Label htmlFor="name" required>Full name</Label>
          <Input id="name" invalid={!!errors.name} error={errors.name?.message} autoFocus {...register("name")} />
        </div>
        <div>
          <Label htmlFor="email" required>Email</Label>
          <Input
            id="email"
            type="email"
            invalid={!!errors.email || !!duplicateError}
            error={errors.email?.message || duplicateError}
            helper={errors.email || duplicateError ? null : "Used for due-date reminders."}
            {...register("email")}
          />
        </div>
        <div>
          <Label htmlFor="age" required>Age</Label>
          <Input id="age" type="number" min="1" width={160} invalid={!!errors.age} error={errors.age?.message} {...register("age", { valueAsNumber: true })} />
        </div>
        <div style={{ display: "flex", gap: "var(--space-xs)" }}>
          <Button type="submit" loading={isSubmitting || updateMember.isPending} loadingLabel="Saving…">
            Save member
          </Button>
          <Button type="button" variant="tertiary" onClick={() => router.push(`/members/${member.id}`)}>
            Cancel
          </Button>
        </div>
      </form>

      {member.isActive ? (
        <div style={{ marginTop: "var(--space-xxl)", paddingTop: "var(--space-lg)", borderTop: "1px solid var(--hairline)" }}>
          <div style={{ font: "var(--type-body-sm)", fontWeight: "var(--weight-medium)", color: "var(--ink)" }}>Deactivate this member</div>
          <p style={{ margin: "4px 0 var(--space-md)", font: "var(--type-body-sm)", color: "var(--ink-subtle)", maxWidth: 420 }}>
            Their record and history are kept. They won’t be able to borrow until reactivated.
          </p>
          <Button variant="danger" onClick={() => setConfirmOpen(true)}>
            Deactivate member
          </Button>
        </div>
      ) : null}

      <Dialog open={confirmOpen} title="Deactivate this member?" confirmLabel="Deactivate member" destructive onCancel={() => setConfirmOpen(false)} onConfirm={tryDeactivate}>
        Their record and history are kept. They won’t be able to borrow until reactivated. Members with books still out can’t be deactivated — take those back first.
      </Dialog>
    </div>
  );
}
