package com.sudhanva.library_management_v2.Model.Dto.Exception;

import java.time.LocalDateTime;

import com.sudhanva.library_management_v2.enums.Error.ErrorCode;

import lombok.Builder;

@Builder
public record ErrorResponseDto(
    boolean success,
    ErrorCode errorCode,
    String message,
    LocalDateTime timestamp
) {} 