import { useMemo } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import * as settingsApi from "@/lib/api/services/settings";
import { isNormalizedApiError } from "@/lib/api/client";
import { deriveSettings } from "@/lib/utils/derive";
import type { SettingKey, SettingsResponseDto, SettingValueType } from "@/lib/types/api";

/** ['settings'], 5min stale — the 3 library rules change rarely. */
export function useSettingsRaw() {
  return useQuery({ queryKey: ["settings"], queryFn: settingsApi.listSettings, staleTime: 5 * 60_000 });
}

/** Parsed { MAX_BOOKS, MAX_BORROW_DAYS, FINE_PER_DAY }, null for any key not
 * yet configured. Memoized on query.data — deriveSettings would otherwise
 * return a new object every render, and callers that put this in a useMemo/
 * useEffect dependency array (e.g. useMember) would recompute on every
 * render, which for a useEffect that calls setState is an infinite loop. */
export function useSettings() {
  const query = useSettingsRaw();
  const data = useMemo(() => (query.data ? deriveSettings(query.data) : undefined), [query.data]);
  return { ...query, data };
}

/** Save independently per key: PUT if the key already exists, POST if not
 * (the create/update trap from uploads/03-endpoints.md "Library Settings"). */
export function useSaveSetting() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (args: {
      key: SettingKey;
      value: string;
      valueType: SettingValueType;
      configured: boolean;
      description?: string;
    }) => {
      if (args.configured) {
        try {
          return await settingsApi.updateSetting({ settingKey: args.key, settingValue: args.value });
        } catch (err) {
          // SETTING_NOT_FOUND despite us thinking it was configured — retry as create.
          if (isNormalizedApiError(err) && err.errorCode === "SETTING_NOT_FOUND") {
            return settingsApi.createSetting({
              settingKey: args.key,
              valueType: args.valueType,
              settingValue: args.value,
              description: args.description,
            });
          }
          throw err;
        }
      }
      try {
        return await settingsApi.createSetting({
          settingKey: args.key,
          valueType: args.valueType,
          settingValue: args.value,
          description: args.description,
        });
      } catch (err) {
        // SETTING_ALREADY_EXISTS — not shown to the user, silently retry as PUT.
        if (isNormalizedApiError(err) && err.errorCode === "SETTING_ALREADY_EXISTS") {
          return settingsApi.updateSetting({ settingKey: args.key, settingValue: args.value });
        }
        throw err;
      }
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ["settings"] }),
  });
}

export function useDeleteSetting() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (key: SettingKey) => settingsApi.deleteSetting(key),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["settings"] }),
  });
}

export function settingsByKey(dtos: SettingsResponseDto[] | undefined): Map<SettingKey, SettingsResponseDto> {
  return new Map((dtos ?? []).map((d) => [d.settingKey, d]));
}
