package com.sudhanva.library_management_v2.Model.Dto.Settings;

import com.sudhanva.library_management_v2.enums.Setting.SettingKey;
import com.sudhanva.library_management_v2.enums.Setting.SettingValueType;

import lombok.Builder;

@Builder
public record SettingsResponseDto(
    SettingKey  settingKey,
    String  settingValue,
    String description,
    SettingValueType valueType
) {}
