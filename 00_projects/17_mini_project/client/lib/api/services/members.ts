import { apiClient } from "@/lib/api/client";
import type { MemberRequestDto, MemberResponseDto } from "@/lib/types/api";
import type { MemberFormValues } from "@/lib/schemas/member";

/** uploads/03-endpoints.md "Members" — one of only two well-behaved list
 * endpoints (200 [] on empty, never 404). */
export async function listMembers(): Promise<MemberResponseDto[]> {
  const res = await apiClient.get<MemberResponseDto[]>("/api/member");
  return res.data;
}

export async function getMember(id: number): Promise<MemberResponseDto> {
  const res = await apiClient.get<MemberResponseDto>(`/api/member/${id}`);
  return res.data;
}

/** Not signup — no password. Registers a borrower record only. */
export async function createMember(values: MemberFormValues): Promise<MemberResponseDto> {
  const payload: MemberRequestDto = {
    name: values.name,
    email: values.email,
    age: values.age,
    isActive: true,
  };
  const res = await apiClient.post<MemberResponseDto>("/api/member", payload);
  return res.data;
}

export async function updateMember(
  id: number,
  values: MemberFormValues,
  isActive: boolean,
): Promise<MemberResponseDto> {
  const payload: MemberRequestDto = {
    name: values.name,
    email: values.email,
    age: values.age,
    isActive,
  };
  const res = await apiClient.put<MemberResponseDto>(`/api/member/${id}`, payload);
  return res.data;
}

/** Soft delete. 409 if already inactive or has active borrows. */
export async function deactivateMember(id: number): Promise<void> {
  await apiClient.delete(`/api/member/${id}`);
}

export async function activateMember(id: number): Promise<void> {
  await apiClient.patch(`/api/member/${id}/activate`);
}
