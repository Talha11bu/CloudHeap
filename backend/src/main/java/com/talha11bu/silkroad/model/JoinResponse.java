package com.talha11bu.silkroad.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Duration;

@Schema(description = "Response returned after successfully joining or rejoining a session")
public record JoinResponse(
        @Schema(description = "Whether the join was successful", example = "true")
        boolean success,

        @Schema(description = "JWT token for the authenticated user", example = "eyJhbGciOiJIUzI1NiJ9.eyJzZXNzaW9uSWQiOiJTS1ItN1g5SzJNIiwidXNlcm5hbWUiOiJib2IifQ.xyz789")
        String token,

        @Schema(description = "The full session object with users and files")
        Session session,

        @Schema(description = "Time remaining before the session expires", example = "PT45M12S")
        Duration timeLeft
) {}
