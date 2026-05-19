package com.talha11bu.silkroad.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Inbound request payload for joining an existing session.
 *
 * @param sessionId the unique identifier of the session to join (e.g., {@code SKR-7X9K2M}).
 * @param password  the session password for authentication.
 * @param username  the display name the joining user will use.
 */
@Schema(description = "Request body for joining an existing session")
public record JoinRequest(
        @Schema(description = "ID of the session to join", example = "SKR-7X9K2M", requiredMode = Schema.RequiredMode.REQUIRED)
        String sessionId,

        @Schema(description = "Password of the session", example = "mySecret123", requiredMode = Schema.RequiredMode.REQUIRED)
        String password,

        @Schema(description = "Username of the joining user", example = "bob", requiredMode = Schema.RequiredMode.REQUIRED)
        String username
) {}
