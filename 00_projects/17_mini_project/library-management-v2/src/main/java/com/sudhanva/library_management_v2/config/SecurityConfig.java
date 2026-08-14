package com.sudhanva.library_management_v2.config;


import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.sudhanva.library_management_v2.exceptions.ExceptionHandlers.CustomAccessDeniedHandler;
import com.sudhanva.library_management_v2.security.CustomAuthenticationEntryPoint;
import com.sudhanva.library_management_v2.security.JwtFilter;

import lombok.RequiredArgsConstructor;

// Role checks live on the endpoints themselves via @AdminOnly / @StaffOnly
// (see security.authorization), which @EnableMethodSecurity activates here.
// This filter chain only decides what's public vs. merely "authenticated".
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    private final JwtFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){

        http
            .authorizeHttpRequests(
                auth -> auth
                    .requestMatchers(
                        "/public/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                    ).permitAll()
                    // Only the actual public auth flows - NOT /api/auth/** as a
                    // whole, so newer admin-only endpoints under /api/auth
                    // (e.g. register-staff) still require authentication.
                    .requestMatchers(
                        "/api/auth/register",
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/api/auth/logout"
                    ).permitAll()
                    .anyRequest().authenticated()
            )
            .cors( cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf( csrf -> csrf.disable())
            .sessionManagement( session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling( exception ->
                exception
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            ;

        return http.build();
    }


    // The frontend (localhost:3000) and API (localhost:8080) are cross-origin.
    // withCredentials/cookies require the exact origin here — "*" is not
    // legal alongside allowCredentials(true).
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // X-Correlation-Id: lib/api/client.ts on the frontend adds it to every
        // apiClient request outside production for request tracing in the
        // console. "*" isn't honored here once allowCredentials is true (the
        // Fetch spec treats it as a literal header name, not a wildcard), so
        // it has to be listed explicitly like the rest.
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Correlation-Id"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }


    @Bean
    AuthenticationManager authenticationManager(
        AuthenticationConfiguration configuration
    ){
        return configuration.getAuthenticationManager();
    }

}
