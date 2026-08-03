package com.sudhanva.library_management_v2.Model.Dto.Settings;

import com.sudhanva.library_management_v2.enums.Setting.SettingKey;
import com.sudhanva.library_management_v2.enums.Setting.SettingValueType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateSettingRequestDto(
    @NotNull(message = "Setting key is required")
    SettingKey settingKey,

    @NotNull(message = "Setting value type is required")
    SettingValueType valueType,

    @NotBlank(message = "Setting value cannot be blank")
    @Size(max = 255, message = "Setting value cannot exceed 255 characters")
    String settingValue,

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    String description

) {

}
