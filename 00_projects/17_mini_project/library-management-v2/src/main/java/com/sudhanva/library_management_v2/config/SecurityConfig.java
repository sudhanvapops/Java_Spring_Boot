package com.sudhanva.library_management_v2.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.sudhanva.library_management_v2.Service.customUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    

    private final customUserDetailsService userDetailsService;

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
                    .requestMatchers("/api/auth/**").permitAll() // also logout and refresh
                    .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            // .formLogin(Customizer.withDefaults())
            // TODO: Enable CORS
            .csrf( csrf -> csrf.disable())
            .sessionManagement( session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // TODO: JWT Custom Filter 
            // .addFilterBefore(null, null);
            ;

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }


    // Class helps in authenticating user
    // Internally when authentication manger calls this activates by bean
    @Bean
    public AuthenticationProvider authenticationProvider(){

        DaoAuthenticationProvider provider =  new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;

    }


    @Bean
    AuthenticationManager authenticationManager(
        AuthenticationConfiguration configuration
    ){
        return configuration.getAuthenticationManager();
    }

}
