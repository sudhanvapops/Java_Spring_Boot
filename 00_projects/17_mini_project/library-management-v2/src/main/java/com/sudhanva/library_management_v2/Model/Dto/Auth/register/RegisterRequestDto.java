package com.sudhanva.library_management_v2.Model.Dto.Auth.register;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Public self-registration creates a Member (library patron), not a User
// (login account) - User accounts are staff-only and provisioned exclusively
// via /register-staff by an existing admin. A Member has no password, so
// this request carries none.
public record RegisterRequestDto(
    @NotBlank(message = "Name is required")
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be greater than 0")
    Integer age

) {


}
