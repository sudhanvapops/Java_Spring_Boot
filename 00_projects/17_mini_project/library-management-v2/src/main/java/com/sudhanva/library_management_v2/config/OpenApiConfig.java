package com.sudhanva.library_management_v2.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

// Declares the "bearerAuth" scheme referenced via @SecurityRequirement on
// protected controllers, so Swagger UI shows an Authorize button that sends
// the JWT as "Authorization: Bearer <token>".
@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig {

}
