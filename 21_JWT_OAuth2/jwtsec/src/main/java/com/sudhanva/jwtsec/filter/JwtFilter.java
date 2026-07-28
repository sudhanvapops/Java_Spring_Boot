package com.sudhanva.jwtsec.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sudhanva.jwtsec.service.JwtService;
import com.sudhanva.jwtsec.service.MyUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// to be called for every request
// accept in the request mapper
// OncePerRequestFilter is a convenience base class provided by Spring that guarantees your filter executes only once for a single HTTP request.
@Component
public class JwtFilter extends OncePerRequestFilter{


    private final JwtService jwtService;
    private final MyUserDetailsService userDetailsService;

    JwtFilter(
        JwtService jwtService,
        MyUserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, 
        HttpServletResponse response, 
        FilterChain filterChain
    ) throws ServletException, IOException {


        // Read the Authorization header.
        // Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Check whether the Authorization header exists
        // and whether it contains a Bearer token.
        if (authHeader != null && authHeader.startsWith("Bearer ")){
            // Remove "Bearer " and keep only the JWT.
            token = authHeader.substring(7);

            // Extract the username/email from the JWT payload.
            // This DOES NOT verify the token yet.
            username = jwtService.extractUsername(token);
        }



        // Authenticate only if:
        // 1. We successfully extracted a username.
        // 2. This request has not already been authenticated.
        if (
            // Does this request's thread already have an Authentication?
            // Has this request's thread already been authenticated earlier in the filter chain?
            username != null && SecurityContextHolder.getContext().getAuthentication() == null
        ){

            // Load the user from the database.
            // We need this to:
            // - compare usernames
            // - verify authorities
            // - validate the token
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);


            // Validate the token.
            // Usually checks:
            // - Signature
            // - Expiration
            // - Username matches
            if(jwtService.validateToken(token,userDetails)){

                // Create an Authentication object.
                // Principal      -> logged in user
                // Credentials    -> null (password not needed anymore)
                // Authorities    -> user's roles/permissions
                UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null,
                        userDetails.getAuthorities()
                    );

                // Optional but recommended.
                // Stores request-specific details such as
                // remote IP and session id.
                authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource()
                        .buildDetails(request)
                );


                // Store the Authentication inside the SecurityContext.
                // Since SecurityContextHolder uses ThreadLocal,
                // this Authentication belongs ONLY to this request.
                // stores the current request's authentication.
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            
        }


        // Continue the remaining filter chain.
        // Eventually the request reaches the controller.
        filterChain.doFilter(request, response);


    }
    
}
