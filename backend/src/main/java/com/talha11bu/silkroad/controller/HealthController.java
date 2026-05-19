package com.talha11bu.silkroad.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Simple health check controller for the SilkRoad API.
 *
 * <p>Exposes an unauthenticated endpoint at {@code /sessions/health}
 * that returns {@code "ALIVE"} to confirm the service is running.</p>
 */
@RestController
@RequestMapping("/sessions")
@Tag(name = "Health")
public class HealthController {

    @Operation(
            summary = "Health check",
            description = "Returns a simple health status to verify the API is running. " +
                    "No authentication required.",
            security = {}  // Override global security — no auth needed
    )
    @ApiResponse(
            responseCode = "200",
            description = "Service is alive and healthy",
            content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(value = "ALIVE")
            )
    )
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("ALIVE");
    }
}
