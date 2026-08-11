package com.sudhanva.library_management_v2.exceptions.UserExceptions;

import com.sudhanva.library_management_v2.enums.User.UserRoles;

// Thrown by AuthService.registerStaff() when the requested role isn't a
// staff role (ADMIN/LIBRARIAN). MEMBER accounts go through /api/auth/register.
public class InvalidStaffRoleException extends RuntimeException {

    public InvalidStaffRoleException(UserRoles role) {
        super("Role must be ADMIN or LIBRARIAN, got: " + role);
    }

}
