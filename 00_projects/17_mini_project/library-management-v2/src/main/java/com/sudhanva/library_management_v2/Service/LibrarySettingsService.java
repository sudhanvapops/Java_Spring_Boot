package com.sudhanva.library_management_v2.Service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sudhanva.library_management_v2.Model.LibrarySettings;
import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.Settings.CreateSettingRequestDto;
import com.sudhanva.library_management_v2.Model.Dto.Settings.SettingsRequestDto;
import com.sudhanva.library_management_v2.Model.Dto.Settings.SettingsResponseDto;
import com.sudhanva.library_management_v2.enums.Setting.SettingKey;
import com.sudhanva.library_management_v2.enums.Setting.SettingValueType;
import com.sudhanva.library_management_v2.exceptions.LibraryExceptions.InvalidSettingValueException;
import com.sudhanva.library_management_v2.exceptions.LibraryExceptions.NoLibrarySettingsAvailableException;
import com.sudhanva.library_management_v2.exceptions.LibraryExceptions.SettingAlreadyExistsException;
import com.sudhanva.library_management_v2.exceptions.LibraryExceptions.SettingNotFoundException;
import com.sudhanva.library_management_v2.repo.LibrarySettingsRepo;

@Service
public class LibrarySettingsService {

    final LibrarySettingsRepo librarySettingsRepo;

    public LibrarySettingsService(LibrarySettingsRepo librarySettingsRepo) {
        this.librarySettingsRepo = librarySettingsRepo;
    }

    // Utitly Method

    private SettingsResponseDto mapToSettingsResponseDto(LibrarySettings setting) {

        return SettingsResponseDto.builder()
                .settingValue(setting.getSettingValue())
                .settingKey(setting.getSettingKey())
                .description(setting.getDescription())
                .valueType(setting.getValueType())
                .build();
    }

    private void validateSettingValue(
            SettingKey key,
            SettingValueType valueType,
            String value) {

        switch (valueType) {

            case INTEGER ->
                Integer.parseInt(value);

            case DECIMAL ->
                new BigDecimal(value);

            case BOOLEAN -> {

                if (!value.equalsIgnoreCase("true")
                        && !value.equalsIgnoreCase("false")) {

                    throw new InvalidSettingValueException(
                            key,
                            valueType,
                            value);
                }
            }

            case STRING -> {
                // Nothing to validate

            }
        }

    }

    // Service Methods

    // Get All Settings
    @Transactional(readOnly = true)
    public ApiResponse<List<SettingsResponseDto>> getAllSettings() {

        List<LibrarySettings> allSettings = librarySettingsRepo.findAll();

        if (allSettings.isEmpty()) {
            throw new NoLibrarySettingsAvailableException();
        }

        List<SettingsResponseDto> settings = allSettings.stream().map(
                setting -> mapToSettingsResponseDto(setting)).toList();

        return new ApiResponse<>(
                true,
                "All Settings:",
                settings);
    }

    // Get Setting
    @Transactional(readOnly = true)
    public ApiResponse<SettingsResponseDto> getSettingByKey(SettingKey key) {

        LibrarySettings setting = librarySettingsRepo.findBySettingKey(key)
                .orElseThrow(() -> SettingNotFoundException.byKey(key));

        return new ApiResponse<>(
                true,
                "Setting Found: " + key,
                mapToSettingsResponseDto(setting));
    }

    // Create Setting
    @Transactional
    public ApiResponse<SettingsResponseDto> createSetting(CreateSettingRequestDto createSettingRequestDto) {

        LibrarySettings existingSetting = librarySettingsRepo
                .findBySettingKey(createSettingRequestDto.settingKey())
                .orElse(null);

        if (existingSetting != null) {
            throw new SettingAlreadyExistsException(createSettingRequestDto.settingKey());
        }

        validateSettingValue(
                createSettingRequestDto.settingKey(),
                createSettingRequestDto.valueType(),
                createSettingRequestDto.settingValue());

        LibrarySettings setting = LibrarySettings.builder()
                .settingKey(createSettingRequestDto.settingKey())
                .settingValue(createSettingRequestDto.settingValue())
                .valueType(createSettingRequestDto.valueType())
                .description(createSettingRequestDto.description())
                .build();

        LibrarySettings savedSetting = librarySettingsRepo.save(setting);

        return new ApiResponse<>(
                true,
                "Setting Created: " + createSettingRequestDto.settingKey(),
                mapToSettingsResponseDto(savedSetting));
    }

    // Set Setting (Update Only)
    @Transactional
    public ApiResponse<SettingsResponseDto> setSetting(SettingsRequestDto settingsRequestDto) {

        LibrarySettings setting = librarySettingsRepo
                .findBySettingKey(settingsRequestDto.settingKey())
                .orElseThrow(() -> SettingNotFoundException.byKey(settingsRequestDto.settingKey()));

        validateSettingValue(
                settingsRequestDto.settingKey(),
                setting.getValueType(),
                settingsRequestDto.settingValue());

        setting.setSettingValue(settingsRequestDto.settingValue());

        LibrarySettings savedSetting = librarySettingsRepo.save(setting);

        return new ApiResponse<>(
                true,
                "Setting Updated: " + settingsRequestDto.settingKey(),
                mapToSettingsResponseDto(savedSetting));
    }

    // Delete Setting
    @Transactional
    public ApiResponse<SettingsResponseDto> deleteSetting(SettingKey key) {

        LibrarySettings setting = librarySettingsRepo.findBySettingKey(key)
                .orElseThrow(() -> SettingNotFoundException.byKey(key));

        librarySettingsRepo.delete(setting);

        return new ApiResponse<>(
                true,
                "Setting Deleted: " + key,
                mapToSettingsResponseDto(setting));
    }

    // Used by other services to resolve an integer-valued setting (e.g. MAX_BOOKS)
    @Transactional(readOnly = true)
    public int getIntSetting(SettingKey key) {

        LibrarySettings setting = librarySettingsRepo.findBySettingKey(key)
                .orElseThrow(() -> SettingNotFoundException.byKey(key));

        return Integer.parseInt(setting.getSettingValue());
    }

    // Used by other services to resolve a decimal-valued setting (e.g.
    // FINE_PER_DAY)
    @Transactional(readOnly = true)
    public BigDecimal getDecimalSetting(SettingKey key) {

        LibrarySettings setting = librarySettingsRepo.findBySettingKey(key)
                .orElseThrow(() -> SettingNotFoundException.byKey(key));

        return new BigDecimal(setting.getSettingValue());
    }

}
