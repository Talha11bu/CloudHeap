package com.talha11bu.silkroad.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Duration;

/**
 * Response payload returned after a join or rejoin attempt.
 *
 * <p>Contains everything the frontend needs to initialize the session view:
 * a JWT for subsequent API calls, the full session state, and the remaining
 * time until expiration for the countdown timer.</p>
 *
 * @param success  {@code true} if the join was successful.
 * @param token    the signed JWT token authenticating the user within this session.
 * @param session  the full {@link Session} entity including current users and uploaded files.
 * @param timeLeft the remaining time before the session expires, as an ISO-8601 {@link java.time.Duration}.
 */
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
