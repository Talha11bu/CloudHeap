package com.talha11bu.silkroad.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for confirming a file upload to R2")
public record UploadCompleteRequest(
        @Schema(description = "Name of the file", example = "report.pdf", requiredMode = Schema.RequiredMode.REQUIRED)
        String fileName,

        @Schema(description = "R2 Object key returned from the pre-signed URL generation", example = "SKR-123/uuid-report.pdf", requiredMode = Schema.RequiredMode.REQUIRED)
        String fileKey,

        @Schema(description = "Size of the file in bytes", example = "12345", requiredMode = Schema.RequiredMode.REQUIRED)
        long fileSize
) {}
