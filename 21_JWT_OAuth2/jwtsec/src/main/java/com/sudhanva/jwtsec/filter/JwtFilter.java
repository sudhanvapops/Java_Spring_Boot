package com.sudhanva.jwtsec.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// to be called for every request
// accept in the request mapper
// OncePerRequestFilter is a convenience base class provided by Spring that guarantees your filter executes only once for a single HTTP request.
public class JwtFilter extends OncePerRequestFilter{

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, 
        HttpServletResponse response, 
        FilterChain filterChain
    ) throws ServletException, IOException {

        

    }
    
}
