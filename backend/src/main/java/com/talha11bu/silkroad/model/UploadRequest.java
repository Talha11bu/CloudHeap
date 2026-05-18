package com.talha11bu.silkroad.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "Request body for uploading a file to a session")
public record UploadRequest(
        @Schema(description = "ID of the session to upload to", example = "SKR-7X9K2M")
        String sessionId,

        @Schema(description = "Password of the session", example = "mySecret123")
        String password,

        @Schema(description = "The file to upload")
        MultipartFile file
) {}
