package com.talha11bu.silkroad.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

/**
 * Legacy request payload for server-proxied file uploads.
 *
 * <p>This was used before the direct-to-R2 pre-signed URL flow was implemented.
 * Retained for backwards compatibility.</p>
 *
 * @param sessionId the ID of the session to upload the file to.
 * @param password  the session password for authorization.
 * @param file      the multipart file binary.
 */
@Schema(description = "Request body for uploading a file to a session")
public record UploadRequest(
        @Schema(description = "ID of the session to upload to", example = "SKR-7X9K2M")
        String sessionId,

        @Schema(description = "Password of the session", example = "mySecret123")
        String password,

        @Schema(description = "The file to upload")
        MultipartFile file
) {}
