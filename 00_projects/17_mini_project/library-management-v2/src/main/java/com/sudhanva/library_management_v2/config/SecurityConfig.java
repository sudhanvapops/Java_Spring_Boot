package com.sudhanva.library_management_v2.config;


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
            // TODO: Enable CORS
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
