package com.talha11bu.silkroad.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Duration;

@Schema(description = "Response returned after successfully creating a session")
public record CreateResponse(
        @Schema(description = "Whether the session was created successfully", example = "true")
        boolean success,

        @Schema(description = "Unique identifier for the created session", example = "SKR-7X9K2M")
        String sessionId,

        @Schema(description = "Username of the session creator", example = "alice")
        String userName,

        @Schema(description = "Password set for the session", example = "mySecret123")
        String password,

        @Schema(description = "JWT token for the session creator", example = "eyJhbGciOiJIUzI1NiJ9.eyJzZXNzaW9uSWQiOiJTS1ItN1g5SzJNIiwidXNlcm5hbWUiOiJhbGljZSJ9.abc123")
        String token,

        @Schema(description = "Duration the session will remain active", example = "PT1H")
        Duration duration
) {}
