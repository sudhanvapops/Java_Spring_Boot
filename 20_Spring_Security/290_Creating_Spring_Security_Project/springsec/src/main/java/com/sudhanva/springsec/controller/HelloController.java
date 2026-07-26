package com.sudhanva.springsec.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class HelloController {
    

    @GetMapping("/hello")
    public String greet(HttpServletRequest request){
        // Give me the current session. If one doesn't exist, create one.
        // if not used false
        return "Hello World Session Id: "+request.getSession().getId();
    }

    @GetMapping("/csrf")
    public CsrfToken getCsrfToken(HttpServletRequest req){
        return (CsrfToken) req.getAttribute("_csrf");
    }

}
