package com.plagiarism.engine;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PlagiarismEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlagiarismEngineApplication.class, args);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Semantic Code Plagiarism Detection Engine API")
                        .version("1.0.0")
                        .description("Production-ready API for semantic code parsing, fingerprinting, and similarity computation."))
                .addSecurityItem(new SecurityRequirement().addList("ApiKeyAuth"))
                .components(new Components()
                        .addSecuritySchemes("ApiKeyAuth", new SecurityScheme()
                                .name("X-API-KEY")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .description("Custom header for api key authentication")));
    }
}
