package com.talha11bu.silkroad.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request payload sent by the frontend after a direct-to-R2 upload completes.
 *
 * <p>The client uploads the file binary directly to R2 using a pre-signed URL,
 * then calls the confirm endpoint with this payload so the server can register
 * the file in the database and broadcast a WebSocket notification.</p>
 *
 * @param fileName the user-facing original filename.
 * @param fileKey  the R2 object key returned during pre-signed URL generation.
 * @param fileSize the size of the uploaded file in bytes.
 */
@Schema(description = "Request body for confirming a file upload to R2")
public record UploadCompleteRequest(
        @Schema(description = "Name of the file", example = "report.pdf", requiredMode = Schema.RequiredMode.REQUIRED)
        String fileName,

        @Schema(description = "R2 Object key returned from the pre-signed URL generation", example = "SKR-123/uuid-report.pdf", requiredMode = Schema.RequiredMode.REQUIRED)
        String fileKey,

        @Schema(description = "Size of the file in bytes", example = "12345", requiredMode = Schema.RequiredMode.REQUIRED)
        long fileSize
) {}
