package com.talha11bu.silkroad.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI silkroadOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SilkRoad API")
                        .description("""
                                SilkRoad is a real-time file sharing platform that enables users to create \
                                temporary sessions, upload/download files, and collaborate via WebSocket \
                                notifications. Sessions are password-protected and auto-expire after a \
                                configurable duration.
                                
                                ## Authentication
                                Most endpoints require a JWT Bearer token obtained after creating or joining a session.
                                
                                ## WebSocket
                                Real-time notifications are available via STOMP over WebSocket at `/ws`. \
                                Subscribe to `/topic/sessions/{sessionId}` for session events.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("talha11bu")
                                .url("https://github.com/talha11bu")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development"),
                        new Server().url("https://silkroad.example.com").description("Production")))
                .tags(List.of(
                        new Tag().name("Session Management")
                                .description("Create, join, rejoin, leave, and end file-sharing sessions"),
                        new Tag().name("File Operations")
                                .description("Upload, download, and delete files within a session"),
                        new Tag().name("Health")
                                .description("Application health check endpoints")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token obtained from /sessions/create or /sessions/join")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
