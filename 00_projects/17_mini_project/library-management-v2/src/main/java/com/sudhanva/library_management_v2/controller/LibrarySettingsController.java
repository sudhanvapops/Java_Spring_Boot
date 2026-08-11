package com.sudhanva.library_management_v2.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.Model.Dto.Settings.CreateSettingRequestDto;
import com.sudhanva.library_management_v2.Model.Dto.Settings.SettingsRequestDto;
import com.sudhanva.library_management_v2.Model.Dto.Settings.SettingsResponseDto;
import com.sudhanva.library_management_v2.Service.LibrarySettingsService;
import com.sudhanva.library_management_v2.enums.Setting.SettingKey;
import com.sudhanva.library_management_v2.security.authorization.AdminOnly;
import com.sudhanva.library_management_v2.security.authorization.StaffOnly;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/settings")
@Tag(name = "Library Settings", description = "Endpoints for managing configurable library settings (e.g. max books, max borrow days, fine per day)")
@SecurityRequirement(name = "bearerAuth")
// Default for the whole controller is staff-read; mutating endpoints below
// tighten this to @AdminOnly since they change library-wide policy.
@StaffOnly
public class LibrarySettingsController {


    final LibrarySettingsService librarySettingsService;

    public LibrarySettingsController(LibrarySettingsService librarySettingsService) {
        this.librarySettingsService = librarySettingsService;
    }

    // Get All Settings
    @Operation(summary = "Get all library settings")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Settings found",
        content = @Content(schema = @Schema(implementation = SettingsResponseDto.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No settings exist",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<SettingsResponseDto>>> getAllSettings(){

        ApiResponse<List<SettingsResponseDto>> response = librarySettingsService.getAllSettings();

        return ResponseEntity.ok(response);
    }

    // Get Setting
    @Operation(summary = "Get a single setting by key")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Setting found",
        content = @Content(schema = @Schema(implementation = SettingsResponseDto.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No setting exists with the given key",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping("/{key}")
    public ResponseEntity<ApiResponse<SettingsResponseDto>> getSettingByKey(
        @Parameter(description = "Setting key") @PathVariable SettingKey key
    ){

        ApiResponse<SettingsResponseDto> response = librarySettingsService.getSettingByKey(key);

        return ResponseEntity.ok(response);
    }

    // Create Setting
    @Operation(summary = "Create a new setting", description = "Fails if a setting already exists for the given key; use PUT to update an existing setting instead")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Setting created",
        content = @Content(schema = @Schema(implementation = SettingsResponseDto.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request failed validation, or the setting value does not match the given value type")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "A setting already exists with the given key",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @AdminOnly
    @PostMapping
    public ResponseEntity<ApiResponse<SettingsResponseDto>> createSetting(
        @Valid @RequestBody CreateSettingRequestDto createSettingRequestDto
    ){

        ApiResponse<SettingsResponseDto> response = librarySettingsService.createSetting(createSettingRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Set Setting
    @Operation(summary = "Update the value of an existing setting", description = "Fails if no setting exists for the given key; use POST to create a new setting instead")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Setting updated",
        content = @Content(schema = @Schema(implementation = SettingsResponseDto.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request failed validation, or the new value does not match the setting's existing value type")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No setting exists with the given key",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @AdminOnly
    @PutMapping
    public ResponseEntity<ApiResponse<SettingsResponseDto>> setSetting(
        @Valid @RequestBody SettingsRequestDto settingsRequestDto
    ){

        ApiResponse<SettingsResponseDto> response = librarySettingsService.setSetting(settingsRequestDto);

        return ResponseEntity.ok(response);
    }

    // Delete Setting
    @Operation(summary = "Delete a setting by key")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Setting deleted",
        content = @Content(schema = @Schema(implementation = SettingsResponseDto.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No setting exists with the given key",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @AdminOnly
    @DeleteMapping("/{key}")
    public ResponseEntity<ApiResponse<SettingsResponseDto>> deleteSetting(
        @Parameter(description = "Setting key") @PathVariable SettingKey key
    ){

        ApiResponse<SettingsResponseDto> response = librarySettingsService.deleteSetting(key);

        return ResponseEntity.ok(response);
    }

}
