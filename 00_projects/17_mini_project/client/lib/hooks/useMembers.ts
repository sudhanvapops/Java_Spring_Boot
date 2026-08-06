import { useMemo } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import * as membersApi from "@/lib/api/services/members";
import type { MemberFormValues } from "@/lib/schemas/member";
import { deriveMember, deriveOverdueMemberIds, groupBooksOutByMember } from "@/lib/utils/derive";
import { useUnreturnedAll } from "./useRecords";
import { useSettings } from "./useSettings";

export function useMembersRaw() {
  return useQuery({ queryKey: ["members"], queryFn: membersApi.listMembers, staleTime: 60_000 });
}

export function useMemberRaw(id: number) {
  return useQuery({
    queryKey: ["members", id],
    queryFn: () => membersApi.getMember(id),
    staleTime: 60_000,
    enabled: Number.isFinite(id),
  });
}

/** Members with booksOut/remainingAllowance/hasOverdue derived from
 * unreturned/all + the MAX_BOOKS setting — never stored, always computed. */
export function useMembers() {
  const membersQuery = useMembersRaw();
  const unreturnedQuery = useUnreturnedAll();
  const settingsQuery = useSettings();

  const data = useMemo(() => {
    if (!membersQuery.data) return undefined;
    const unreturned = unreturnedQuery.data ?? [];
    const grouped = groupBooksOutByMember(unreturned);
    const overdueIds = deriveOverdueMemberIds(unreturned);
    return membersQuery.data.map((m) => deriveMember(m, grouped, settingsQuery.data?.MAX_BOOKS ?? null, overdueIds));
  }, [membersQuery.data, unreturnedQuery.data, settingsQuery.data]);

  return {
    data,
    isLoading: membersQuery.isLoading || unreturnedQuery.isLoading,
    isError: membersQuery.isError || unreturnedQuery.isError,
    error: membersQuery.error ?? unreturnedQuery.error,
    refetch: () => Promise.all([membersQuery.refetch(), unreturnedQuery.refetch()]),
  };
}

export function useMember(id: number) {
  const memberQuery = useMemberRaw(id);
  const unreturnedQuery = useUnreturnedAll();
  const settingsQuery = useSettings();

  const data = useMemo(() => {
    if (!memberQuery.data) return undefined;
    const unreturned = unreturnedQuery.data ?? [];
    const grouped = groupBooksOutByMember(unreturned);
    const overdueIds = deriveOverdueMemberIds(unreturned);
    return deriveMember(memberQuery.data, grouped, settingsQuery.data?.MAX_BOOKS ?? null, overdueIds);
  }, [memberQuery.data, unreturnedQuery.data, settingsQuery.data]);

  return {
    data,
    isLoading: memberQuery.isLoading || unreturnedQuery.isLoading,
    isError: memberQuery.isError,
    error: memberQuery.error,
  };
}

export function useCreateMember() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (values: MemberFormValues) => membersApi.createMember(values),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["members"] }),
  });
}

export function useUpdateMember(id: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (args: { values: MemberFormValues; isActive: boolean }) =>
      membersApi.updateMember(id, args.values, args.isActive),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["members"] }),
  });
}

export function useDeactivateMember() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => membersApi.deactivateMember(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["members"] }),
  });
}

export function useActivateMember() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => membersApi.activateMember(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["members"] }),
  });
}
