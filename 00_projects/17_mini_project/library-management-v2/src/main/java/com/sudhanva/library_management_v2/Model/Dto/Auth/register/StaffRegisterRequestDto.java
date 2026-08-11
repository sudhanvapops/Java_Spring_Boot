package com.sudhanva.library_management_v2.Model.Dto.Auth.register;

import com.sudhanva.library_management_v2.enums.User.UserRoles;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StaffRegisterRequestDto(
    @NotBlank(message = "Username is required")
    String username,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password,

    @NotBlank(message = "Confirm password is required")
    String confirmPassword,

    @NotNull(message = "Role is required")
    UserRoles role

) {

}
