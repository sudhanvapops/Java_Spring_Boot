package com.sudhanva.library_management_v2.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sudhanva.library_management_v2.Service.CustomUserDetailsService;
import com.sudhanva.library_management_v2.exceptions.UserExceptions.UserNotFoundException;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter{


    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, 
        HttpServletResponse response, 
        FilterChain filterChain
    ) throws ServletException, IOException {
        


        String authHeader =  request.getHeader("Authorization");
        String token = null;
        String email = null;


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // validate token
        // Get Token
        token = authHeader.split("Bearer ")[1];

        try {
            // Get Username
            email = jwtService.getUsername(token);

            // Check whether this request is already authenticated
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null){

                // load user since i am doing it with email in loadByUserame
                // load from db
                UserDetails user = userDetailsService.loadUserByUsername(email);

                // create Authentication
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                    );
         

                // Attach request details
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );


                // Put athentication into Security Context
                SecurityContextHolder.getContext().setAuthentication(authentication);


            }
        } catch (JwtException | IllegalArgumentException | UserNotFoundException exception) {
            // Invalid/expired/malformed token or unknown user -> leave request unauthenticated.
            // CustomAuthenticationEntryPoint reads this attribute and returns a proper
            // ErrorResponseDto instead of a raw 500 (exceptions thrown here run before
            // DispatcherServlet, so @RestControllerAdvice can never see them).
            SecurityContextHolder.clearContext();
            request.setAttribute(CustomAuthenticationEntryPoint.AUTH_ERROR_ATTRIBUTE, exception.getMessage());
        }

        filterChain.doFilter(request, response);

    }
    

}
