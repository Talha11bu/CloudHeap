package com.talha11bu.silkroad.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Duration;

/**
 * Inbound request payload for creating a new file-sharing session.
 *
 * @param username the display name of the session creator.
 * @param password the password that protects the session from unauthorized access.
 * @param duration the ISO-8601 duration the session should remain active (e.g., {@code PT1H} for one hour).
 */
@Schema(description = "Request body for creating a new file-sharing session")
public record CreateRequest(
        @Schema(description = "Username of the session creator", example = "alice", requiredMode = Schema.RequiredMode.REQUIRED)
        String username,

        @Schema(description = "Password to protect the session", example = "mySecret123", requiredMode = Schema.RequiredMode.REQUIRED)
        String password,

        @Schema(description = "Duration for which the session remains active (ISO-8601 format)", example = "PT1H", requiredMode = Schema.RequiredMode.REQUIRED)
        Duration duration
) {}
