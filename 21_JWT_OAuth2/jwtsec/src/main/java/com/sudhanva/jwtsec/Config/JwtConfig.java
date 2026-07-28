package com.sudhanva.jwtsec.Config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;


@Component
public class JwtConfig {
    
    private final Dotenv dotenv = Dotenv.load();

    public String getSecret() {
        return dotenv.get("JWT_SECRET");
    }

    public long getExpiration() {
        return Long.parseLong(dotenv.get("JWT_EXPIRATION"));
    }    
}
