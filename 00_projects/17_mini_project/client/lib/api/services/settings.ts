import { apiClient, isNormalizedApiError } from "@/lib/api/client";
import type { CreateSettingRequestDto, SettingKey, SettingsRequestDto, SettingsResponseDto } from "@/lib/types/api";

/** uploads/03-endpoints.md "Library Settings". Read-first pattern required:
 * GET /all on mount to know which of the 3 keys already exist, then a card
 * uses PUT if it exists, POST if it doesn't (see SETTING_ALREADY_EXISTS /
 * SETTING_NOT_FOUND handling below). */

export async function listSettings(): Promise<SettingsResponseDto[]> {
  try {
    const res = await apiClient.get<SettingsResponseDto[]>("/api/settings/all");
    return res.data;
  } catch (err) {
    if (isNormalizedApiError(err) && err.isEmptyState) return [];
    throw err;
  }
}

export async function getSetting(key: SettingKey): Promise<SettingsResponseDto> {
  const res = await apiClient.get<SettingsResponseDto>(`/api/settings/${key}`);
  return res.data;
}

export async function createSetting(payload: CreateSettingRequestDto): Promise<SettingsResponseDto> {
  const res = await apiClient.post<SettingsResponseDto>("/api/settings", payload);
  return res.data;
}

/** No path param — the key is in the body. Fails 404 if the key doesn't
 * exist yet (caller should fall back to createSetting). */
export async function updateSetting(payload: SettingsRequestDto): Promise<SettingsResponseDto> {
  const res = await apiClient.put<SettingsResponseDto>("/api/settings", payload);
  return res.data;
}

export async function deleteSetting(key: SettingKey): Promise<void> {
  await apiClient.delete(`/api/settings/${key}`);
}
