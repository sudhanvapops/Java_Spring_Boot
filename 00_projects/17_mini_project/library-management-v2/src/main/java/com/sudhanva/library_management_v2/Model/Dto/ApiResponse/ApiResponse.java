package com.sudhanva.library_management_v2.Model.Dto.ApiResponse;

public record ApiResponse<T>(
    boolean success,
    String message,
    T data
) {
    
}
