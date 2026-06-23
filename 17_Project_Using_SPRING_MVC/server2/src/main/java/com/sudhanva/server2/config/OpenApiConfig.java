package com.sudhanva.server2.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Server2 API")
                        .version("1.0")
                        .description("API documentation for Spring MVC project")
                        .contact(new Contact()
                                .name("Sudhanva")
                                .email("sudhanva@example.com")));
    }
}