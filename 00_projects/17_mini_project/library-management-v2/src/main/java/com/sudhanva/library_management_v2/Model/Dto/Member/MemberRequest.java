package com.sudhanva.library_management_v2.Model.Dto.Member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;


@Builder
public record MemberRequest(

    @NotBlank(message = "Member name is required")
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,
    
    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be greater than 0")
    Integer age,

    @NotNull(message = "isActive cannot be null")
    Boolean isActive
) {
    
}
