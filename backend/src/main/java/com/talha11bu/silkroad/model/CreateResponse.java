package com.talha11bu.silkroad.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Duration;

/**
 * Response payload returned after a session creation attempt.
 *
 * <p>Mirrors the shape of {@link JoinResponse} so the frontend can handle
 * create and join flows with a single response type.</p>
 *
 * @param success   {@code true} if the session was created successfully.
 * @param token     the signed JWT token authenticating the creator.
 * @param session   the full {@link Session} entity including initial user list and empty file list.
 * @param timeLeft  the configured session duration (identical to the requested duration at creation time).
 */
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
