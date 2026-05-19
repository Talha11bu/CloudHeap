package com.talha11bu.silkroad.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload returned after a file has been successfully uploaded and registered.
 *
 * @param filename the original name of the uploaded file.
 * @param fileType the MIME type of the file (may be empty for pre-signed uploads).
 * @param fileSize the size of the file in bytes.
 */
@Schema(description = "Response returned after a successful file upload")
public record UploadResponse(
        @Schema(description = "Name of the uploaded file", example = "quarterly-report.pdf")
        String filename,

        @Schema(description = "MIME type of the uploaded file", example = "application/pdf")
        String fileType,

        @Schema(description = "Size of the uploaded file in bytes", example = "2458624")
        long fileSize
) {}