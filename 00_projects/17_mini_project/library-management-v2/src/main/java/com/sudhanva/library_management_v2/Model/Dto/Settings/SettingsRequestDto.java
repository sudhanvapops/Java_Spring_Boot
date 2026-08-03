package com.sudhanva.library_management_v2.Model.Dto.Settings;

import com.sudhanva.library_management_v2.enums.Setting.SettingKey;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SettingsRequestDto(
    @NotNull(message = "Setting key is required")
    SettingKey settingKey,

    @NotBlank(message = "Setting value cannot be blank")
    @Size(max = 255, message = "Setting value cannot exceed 255 characters")
    String settingValue


) {
    
}
