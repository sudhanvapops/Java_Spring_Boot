package com.sudhanva.jwtsec.Config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


// This class defines Spring beans.
@Configuration
// Enables/configures Spring Security's web security support
// optional in modern spring boot for deafullt settings
@EnableWebSecurity
public class SecurityConfig {


    private final UserDetailsService userDetailsService;

    SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Bean
    // SecurityFilterChain is the sequence of security filters every HTTP request passes through.
    // HttpSecurity is the builder used to configure that filter chain.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
            // Ensures that any request to our application requires the user to be authenticated through next methods chained to it
            // Authorization = "What are you allowed to do?"
            // Every request must come from an authenticated user.↓
            .authorizeHttpRequests( request -> request
                .requestMatchers("/login", "/register", "/public/**").permitAll()
                // only alow authenticated requests
                .anyRequest().authenticated()
            )
            // these how you get authentiated
            // Lets users authenticate with form-based login
            .formLogin(Customizer.withDefaults())
            // Lets users use http basic authnetication
            .httpBasic(Customizer.withDefaults())
            // CSRF is disabled
            .csrf(customizer -> customizer.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
            ;

        return http.build(); // creates the SecurityFilterChain Object
    }



    // Authentication Data


    // Password Enocder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }


    @Bean
    public AuthenticationProvider authProvider(){

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(
            // tells how to retrive the Data from database to SecurityContext
            userDetailsService
        );

        // Which encoder to use for Veryfing/authenticating user
        provider.setPasswordEncoder(passwordEncoder());

        return provider;

    }


}
