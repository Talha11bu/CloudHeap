package com.talha11bu.silkroad.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Duration;

@Schema(description = "Response returned after successfully creating a session")
public record CreateResponse(
        @Schema(description = "Whether the session was created successfully", example = "true")
        boolean success,

        @Schema(description = "JWT token for the session creator", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,

        @Schema(description = "The full session object with users and files")
        Session session,

        @Schema(description = "Duration the session will remain active", example = "PT1H")
        Duration timeLeft
) {}
