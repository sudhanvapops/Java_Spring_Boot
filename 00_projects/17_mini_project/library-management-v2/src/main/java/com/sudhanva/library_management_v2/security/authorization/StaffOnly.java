package com.sudhanva.library_management_v2.security.authorization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

// Restricts an endpoint to UserRoles.ADMIN or UserRoles.LIBRARIAN (library
// staff). Applies to methods and, when every handler in a controller is
// staff-only, the class itself.
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
public @interface StaffOnly {
}
